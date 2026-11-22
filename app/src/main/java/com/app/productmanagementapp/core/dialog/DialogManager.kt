package com.app.productmanagementapp.core.dialog

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.app.productmanagementapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject

/**
 * Centralized Dialog Manager.
 *
 * Responsible only for creating, displaying and dismissing dialogs.
 * It does not manage application state or hold Activity/Fragment references.
 */
class DialogManager @Inject constructor() {

    fun showSuccessDialog(
        context: Context,
        @StringRes titleRes: Int = R.string.dialog_success_title,
        @StringRes messageRes: Int
    ) {
        showMessageDialog(context, titleRes, messageRes)
    }

    fun showErrorDialog(
        context: Context,
        @StringRes titleRes: Int = R.string.dialog_error_title,
        @StringRes messageRes: Int
    ) {
        showMessageDialog(context, titleRes, messageRes)
    }

    fun showInfoDialog(
        context: Context,
        @StringRes titleRes: Int = R.string.dialog_info_title,
        @StringRes messageRes: Int
    ) {
        showMessageDialog(context, titleRes, messageRes)
    }

    private fun showMessageDialog(
        context: Context,
        @StringRes titleRes: Int,
        @StringRes messageRes: Int
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(titleRes)
            .setMessage(messageRes)
            .setPositiveButton(R.string.action_ok, null)
            .show()
    }

    fun showConfirmationDialog(
        context: Context,
        @StringRes titleRes: Int,
        @StringRes messageRes: Int,
        @StringRes positiveButtonRes: Int,
        @StringRes negativeButtonRes: Int,
        onPositiveClick: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(titleRes)
            .setMessage(messageRes)
            .setPositiveButton(positiveButtonRes) { _, _ ->
                onPositiveClick()
            }
            .setNegativeButton(negativeButtonRes, null)
            .show()
    }

    fun showProgressDialog(
        context: Context,
        title: String,
        message: String
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .create()
            .also { it.show() }
    }
}