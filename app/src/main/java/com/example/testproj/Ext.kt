package com.example.testproj

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlin.coroutines.cancellation.CancellationException

fun <T> Flow<T>.handleLoading(liveData: MutableLiveData<Result<T>>) = this.onStart {
    liveData.value = Result.Loading
}

fun <T> Flow<T>.handleError(liveData: MutableLiveData<Result<T>>) = this.catch { error ->
    liveData.value = Result.Error(error)
}

fun <T> Flow<T>.handleResult(liveData: MutableLiveData<Result<T>>) = this.onEach { result ->
    liveData.value = Result.Success(result)
}

fun <T> Flow<T>.handleRequestedLoading(liveData: MutableLiveData<RequestedResult<T>>, requestCode: RequestCode) = this.onStart {
    liveData.value = RequestedResult.Loading(requestCode)
}

fun <T> Flow<T>.handleRequestedError(liveData: MutableLiveData<RequestedResult<T>>, requestCode: RequestCode) = this.catch { error ->
    liveData.value = RequestedResult.Error(error, requestCode, null)
}

fun <T> Flow<T>.handleRequestedResult(liveData: MutableLiveData<RequestedResult<T>>, requestCode: RequestCode) = this.onEach { result ->
    liveData.value = RequestedResult.Success(result, requestCode)
}
