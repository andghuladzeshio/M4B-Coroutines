package com.example.testproj

import android.view.PixelCopy.Request
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class NetworkExecutor<T : Any> internal constructor(parentJob: Job) {
    private val executorScope = CoroutineScope(Dispatchers.IO + SupervisorJob(parentJob))

    private var onResult: (suspend (Result<T>) -> Unit)? = null
    private var executeBlock: (suspend CoroutineScope.() -> T)? = null
    private var loadingBlock: (() -> Unit)? = null
    private var onSuccessBlock: ((T) -> Unit)? = null
    private var onErrorBlock: ((Exception) -> Unit)? = null
    private var onStartBlock: (() -> Unit)? = null
    private var onFinishBlock: (() -> Unit)? = null

    internal fun makeNetworkCall() = executorScope.launch {
        onStartBlock?.invoke()
        loadingBlock?.invoke()
        onResult?.invoke(Result.Loading)

        val result = try {
            executeBlock?.invoke(executorScope) ?: throw Exception("unknown network error")
        } catch (e: Exception) {
            if (e is CancellationException) throw e

            onErrorBlock?.invoke(e)
            onResult?.invoke(Result.Error(e))

            null
        }

        result?.let { r ->
            withContext(Dispatchers.Main) {
                onSuccessBlock?.invoke(r)
                onResult?.invoke(Result.Success(r))
            }
        }

        onFinishBlock?.invoke()
    }

    fun execute(block: suspend CoroutineScope.() -> T) {
        executeBlock = block
    }

    fun loading(block: () -> Unit) {
        loadingBlock = block
    }

    fun success(block: (T) -> Unit) {
        onSuccessBlock = block
    }

    fun error(block: (Exception) -> Unit) {
        onErrorBlock = block
    }

    fun onStart(block: () -> Unit) {
        onStartBlock = block
    }

    fun onFinish(block: () -> Unit) {
        onFinishBlock = block
    }

    fun onResult(block: suspend (Result<T>) -> Unit) {
        onResult = block
    }

    fun onRequestedResult(requestCode: RequestCode, block: (RequestedResult<T>) -> Unit) {
        onResult { result ->
            block(result.toRequestedResult(requestCode))
        }
    }
}

fun <T : Any> ViewModel.networkExecutor(block: NetworkExecutor<T>.() -> Unit) =
    NetworkExecutor<T>(viewModelScope.coroutineContext.job).apply(block).makeNetworkCall()

sealed class Result<out T> {

    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    data object Loading : Result<Nothing>()

    fun toRequestedResult(requestCode: RequestCode) = when (this) {
        is Success -> RequestedResult.Success(data, requestCode)
        is Error -> RequestedResult.Error(exception, requestCode, null)
        is Loading -> RequestedResult.Loading(requestCode)
    }
}

sealed class RequestedResult<out R>(open val requestCode: RequestCode) {

    data class Success<out T>(
        val data: T,
        override val requestCode: RequestCode
    ) : RequestedResult<T>(requestCode)

    data class Error(
        val exception: Throwable,
        override val requestCode: RequestCode,
        val default: Any?,
    ) : RequestedResult<Nothing>(requestCode) {

        @Suppress("UNCHECKED_CAST")
        fun <T> requireDefault(): T = requireNotNull(default as? T)
    }

    data class Loading(override val requestCode: RequestCode) :
        RequestedResult<Nothing>(requestCode)

    fun <T> map(mapFn: (R) -> T): RequestedResult<T> {
        return when (this) {
            is Success -> Success(data = mapFn(data), requestCode = requestCode)
            is Error -> this
            is Loading -> this
        }
    }

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data,requestCode=$requestCode]"
            is Error -> "Error[exception=$exception,requestCode=$requestCode]"
            is Loading -> "Loading[requestCode=$requestCode]"
        }
    }
}