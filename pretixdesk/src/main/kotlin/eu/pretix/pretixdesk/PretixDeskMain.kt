package eu.pretix.pretixdesk

import eu.pretix.libpretixsync.DummySentryImplementation
import eu.pretix.libpretixsync.api.DefaultHttpClientFactory
import eu.pretix.libpretixsync.api.PretixApi
import eu.pretix.libpretixsync.check.AsyncCheckProvider
import eu.pretix.libpretixsync.check.OnlineCheckProvider
import eu.pretix.libpretixsync.check.TicketCheckProvider
import eu.pretix.libpretixsync.db.Migrations
import eu.pretix.libpretixsync.db.Models
import eu.pretix.pretixdesk.ui.MainView
import eu.pretix.pretixdesk.ui.style.MainStyleSheet
import io.requery.BlockingEntityStore
import io.requery.Persistable
import io.requery.cache.EntityCacheBuilder
import io.requery.sql.ConfigurationBuilder
import io.requery.sql.EntityDataStore
import it.sauronsoftware.junique.AlreadyLockedException
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import javafx.stage.Stage
import net.harawata.appdirs.AppDirsFactory
import org.sqlite.SQLiteConfig
import org.sqlite.SQLiteDataSource
import tornadofx.*
import java.io.File
import java.util.*


val VERSION = "0.3.3"
val VERSION_CODE = 1
val APP_ID = "eu.pretix.pretixdesk"

class PretixDeskMain : App(MainView::class, MainStyleSheet::class) {
    private var dataStore: BlockingEntityStore<Persistable>? = null
    private val appDirs = AppDirsFactory.getInstance()!!
    // Keep version argument at 1, we do not want new folders for every new version for now.
    val dataDir = appDirs.getUserDataDir("pretixdesk", "1", "pretix")
    val cacheDir = appDirs.getUserCacheDir("pretixdesk", "1", "pretix")
    val configStore = PretixDeskConfig(dataDir)
    private var apiClient: PretixApi? = null
    var stage: Stage? = null
    var parameters_handled = false
    var _provider: TicketCheckProvider? = null

    private val _messages: SimpleObjectProperty<ResourceBundle> = object : SimpleObjectProperty<ResourceBundle>() {
        override fun get(): ResourceBundle? {
            if (super.get() == null) {
                try {
                    val bundle = ResourceBundle.getBundle(this@PretixDeskMain.javaClass.name, FX.locale, FXResourceBundleControl)
                    (bundle as? FXPropertyResourceBundle)?.inheritFromGlobal()
                    set(bundle)
                } catch (ex: Exception) {
                    FX.log.fine("No Messages found for ${javaClass.name} in locale ${FX.locale}, using global bundle")
                    set(FX.messages)
                }
            }
            return super.get()
        }
    }

    var messages: ResourceBundle
        get() = _messages.get()
        set(value) = _messages.set(value)

    override fun start(stage: Stage) {
        this.stage = stage

        Renderer.registerFonts(this)

        try {
            acquireLock(APP_ID, fun(message: String): String {
                return "ok"
            })
        } catch (e: AlreadyLockedException) {
            val alert = Alert(AlertType.INFORMATION)
            alert.title = messages["running_already_title"]
            alert.headerText = messages["running_already_title"]
            alert.contentText = messages["running_already"]

            val buttonTypeCancel = ButtonType(messages["alert_cancel"], ButtonBar.ButtonData.OK_DONE)
            val buttonTypeIgnore = ButtonType(messages["alert_ignore"], ButtonBar.ButtonData.OTHER)
            alert.buttonTypes.setAll(buttonTypeIgnore, buttonTypeCancel)

            val res = alert.showAndWait()
            if (res.get() == buttonTypeCancel) {
                System.exit(1)
            }
        }

        stage.icons += Image(PretixDeskMain::class.java.getResourceAsStream("icon.png"))
        stage.isMaximized = true
        stage.minHeight = 600.0
        stage.minWidth = 800.0
        super.start(stage)

        val stylesheets = stage.scene.getStylesheets()
        stylesheets.addAll(PretixDeskMain::class.java.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                PretixDeskMain::class.java.getResource("/css/jfoenix-design.css").toExternalForm())
    }


    fun data(): BlockingEntityStore<Persistable> {
        if (dataStore == null) {
            File(dataDir).mkdirs()
            val dbFile = File(dataDir + "/data.sqlite")
            val dbIsNew = !dbFile.exists()

            val dataSource = SQLiteDataSource()
            dataSource.url = "jdbc:sqlite:" + dbFile.absolutePath
            val config = SQLiteConfig()
            config.setDateClass("TEXT")
            dataSource.config = config
            dataSource.setEnforceForeignKeys(true)
            val model = Models.DEFAULT

            Migrations.migrate(dataSource, dbIsNew)

            val configuration = ConfigurationBuilder(dataSource, model)
                    .useDefaultLogging()
                    .setEntityCache(EntityCacheBuilder(model)
                            .useReferenceCache(false)
                            .useSerializableCache(false)
                            .build())
                    .build()

            dataStore = EntityDataStore<Persistable>(configuration)
        }
        return dataStore!!
    }

    fun api(): PretixApi {
        if (apiClient == null) {
            apiClient = PretixApi.fromConfig(configStore, DefaultHttpClientFactory());
        }
        return apiClient!!
    }

    val provider: TicketCheckProvider
        get() {
            if (_provider == null) {
                reloadCheckProvider();
            }
            return _provider!!
        }

    fun reloadCheckProvider() {
        _provider = newCheckProvider();
    }

    fun newCheckProvider(): TicketCheckProvider {
        val p: TicketCheckProvider
        if (configStore.asyncModeEnabled) {
            p = AsyncCheckProvider(configStore, data())
        } else {
            p = OnlineCheckProvider(configStore, DefaultHttpClientFactory(), data(), configStore.checkInListId)
        }
        p.setSentry(DummySentryImplementation())
        apiClient = PretixApi.fromConfig(configStore, DefaultHttpClientFactory());
        return p
    }
}
