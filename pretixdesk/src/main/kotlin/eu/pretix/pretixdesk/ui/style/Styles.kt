package eu.pretix.pretixdesk.ui.style

import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import tornadofx.*

val STYLE_BACKGROUND_COLOR = "#f6f1f9"
val STYLE_INPUT_BACKGROUND_COLOR = "#ffffff"
val STYLE_SHADOW_COLOR_COLOR = "#4E315D"
val STYLE_INPUT_PROMPT_COLOR = "#9D91A3"
val STYLE_INPUT_PROMPT_FOCUSED_COLOR = "#C5BDC9"
val STYLE_CARD_BACKGROUND_COLOR = "#ffffff"
val STYLE_STATE_VALID_COLOR = "#2E7D32"
val STYLE_STATE_REPEAT_COLOR = "#f0ad4e"
val STYLE_STATE_ERROR_COLOR = "#d9534f"
val STYLE_STATE_TEXT_COLOR = "#FFFFFF"
val STYLE_PRIMARY_DARK_COLOR = "#3b1c4a"
val STYLE_TOOLBAR_TEXT_COLOR = "#ffffff"
val STYLE_ATTENTION_COLOR = "#3B1C4A"
val STYLE_ATTENTION_ALTERNATE_COLOR = "#ffee58"

class MainStyleSheet : Stylesheet() {

    companion object {
        val mainSearchField by cssclass()
        val card by cssclass()
        val cardBody by cssclass()
        val resultHolder by cssclass()
        val cardHeaderValid by cssclass()
        val cardHeaderRepeat by cssclass()
        val cardHeaderError by cssclass()
        val cardHeaderErrorNoMessage by cssclass()
        val cardHeaderLabel by cssclass()
        val cardFooterAttention by cssclass()
        val cardFooterAttentionBlink by cssclass()
        val toolBar by cssclass()
    }

    init {
        label {
            fontFamily = "Roboto"
        }

        textInput {
            and(focused) {
                promptTextFill = c(STYLE_INPUT_PROMPT_FOCUSED_COLOR)
                effect = DropShadow(5.0, 0.0, 2.0, c(STYLE_SHADOW_COLOR_COLOR, 0.2))
            }

            promptTextFill = c(STYLE_INPUT_PROMPT_COLOR)
            backgroundColor += c(STYLE_INPUT_BACKGROUND_COLOR)
            borderWidth += box(0.px)
            effect = DropShadow(5.0, 0.0, 2.0, c(STYLE_SHADOW_COLOR_COLOR, 0.1))
            borderRadius += box(4.px)
        }

        mainSearchField {
            fontSize = 24.px
            minWidth = 480.px
            maxWidth = 480.px
            fontFamily = "Roboto"
        }

        resultHolder {
            minWidth = 480.px
            maxWidth = 480.px
            minHeight = 200.px
        }

        card {
            backgroundColor += c(STYLE_CARD_BACKGROUND_COLOR)
            effect = DropShadow(5.0, 0.0, 2.0, c(STYLE_SHADOW_COLOR_COLOR, 0.1))
            backgroundRadius += box(4.px)
        }

        cardHeaderLabel {
            textFill = c(STYLE_STATE_TEXT_COLOR)
            fontSize = 24.px
        }

        select("JFXListView") {
            borderWidth += box(0.px)
            borderRadius += box(4.px)
            backgroundColor += c(STYLE_CARD_BACKGROUND_COLOR)
        }

        cardHeaderValid {
            backgroundColor += c(STYLE_STATE_VALID_COLOR)
            backgroundRadius += box(4.px, 4.px, 0.px, 0.px)
            alignment = Pos.CENTER
        }
        cardHeaderError {
            backgroundColor += c(STYLE_STATE_ERROR_COLOR)
            backgroundRadius += box(4.px, 4.px, 0.px, 0.px)
            alignment = Pos.CENTER
        }
        cardHeaderErrorNoMessage {
            backgroundColor += c(STYLE_STATE_ERROR_COLOR)
            backgroundRadius += box(4.px, 4.px, 4.px, 4.px)
            alignment = Pos.CENTER
        }
        cardHeaderRepeat {
            backgroundColor += c(STYLE_STATE_REPEAT_COLOR)
            backgroundRadius += box(4.px, 4.px, 0.px, 0.px)
            alignment = Pos.CENTER
        }
        cardFooterAttention {
            backgroundColor += c(STYLE_ATTENTION_COLOR)
            backgroundRadius += box(0.px, 0.px, 4.px, 4.px)
            alignment = Pos.CENTER
            label {
                textFill = c("#ffffff")
            }
        }
        cardFooterAttentionBlink {
            backgroundColor += c(STYLE_ATTENTION_ALTERNATE_COLOR)
            backgroundRadius += box(0.px, 0.px, 4.px, 4.px)
            alignment = Pos.CENTER
            label {
                textFill = c(STYLE_ATTENTION_COLOR)
            }
        }

        cardBody {
            padding = box(15.px)
            label {
                fontSize = 18.px
            }
        }

        select("JFXSpinner .arc") {
            stroke = c(STYLE_PRIMARY_DARK_COLOR)
            strokeWidth = 4.px
        }

        toolBar {
            alignment = Pos.CENTER
            backgroundColor += c(STYLE_PRIMARY_DARK_COLOR)
            minHeight = 48.px
            maxHeight = 48.px

            select("JFXToggleButton") {
                select("LabeledText") {
                    fill = c(STYLE_TOOLBAR_TEXT_COLOR)
                    fontFamily = "Roboto"
                }
            }
            select("JFXButton") {
                select("LabeledText") {
                    fill = c(STYLE_TOOLBAR_TEXT_COLOR)
                }
            }
            label {
                textFill = c(STYLE_TOOLBAR_TEXT_COLOR)
            }
        }
    }
}