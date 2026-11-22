
package com.app.productmanagementapp.core.dialog

import androidx.annotation.StringRes


sealed class DialogState {

    data object Idle : DialogState()

    data class Message(
        val type: DialogType,
        @StringRes val titleRes: Int,
        @StringRes val messageRes: Int,
        @StringRes val positiveButtonRes: Int
    ) : DialogState()


    data class Confirmation(
        @StringRes val titleRes: Int,
        @StringRes val messageRes: Int,
        @StringRes val positiveButtonRes: Int,
        @StringRes val negativeButtonRes: Int,
        val onConfirm: () -> Unit
    ) : DialogState()
}
