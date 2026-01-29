package com.faithconnect.core.network

/**
 * Result wrapper for API operations
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val exception: Throwable) : ApiResult<Nothing>()
    
    inline fun <R> map(transform: (T) -> R): ApiResult<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(exception)
        }
    }
    
    inline fun onSuccess(action: (T) -> Unit): ApiResult<T> {
        if (this is Success) action(data)
        return this
    }
    
    inline fun onError(action: (Throwable) -> Unit): ApiResult<T> {
        if (this is Error) action(exception)
        return this
    }
    
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
}
