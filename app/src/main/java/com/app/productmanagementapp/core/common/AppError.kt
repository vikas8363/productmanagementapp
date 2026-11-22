package com.app.productmanagementapp.core.common
import androidx.annotation.StringRes
import com.app.productmanagementapp.R

sealed class AppError(

    @StringRes
    val messageRes: Int,

    val cause: Throwable? = null

) {

    data class DatabaseError(
        val exception: Throwable? = null
    ) : AppError(
        messageRes = R.string.error_database_operation,
        cause = exception
    )


    data object ProductNotFound : AppError(
        messageRes = R.string.error_product_not_found
    )

    data class UnexpectedError(
        val exception: Throwable? = null
    ) : AppError(
        messageRes = R.string.error_unexpected,
        cause = exception
    )
}
