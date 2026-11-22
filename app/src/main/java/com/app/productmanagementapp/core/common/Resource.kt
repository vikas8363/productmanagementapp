package com.app.productmanagementapp.core.common

sealed class Resource<out T> {

    data object Loading : Resource<Nothing>()

    data class Success<T>(
        val data: T
    ) : Resource<T>()

    data class Error(
        val error: AppError
    ) : Resource<Nothing>()
}
