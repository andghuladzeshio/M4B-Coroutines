package com.example.testproj

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val onRefresh = MutableSharedFlow<RequestCode>()
    private val onInit get() = flow { emit(RequestCode.RC_INIT) }
    private val dataSource = DataSource()
    val dataLiveData = MutableLiveData<Result<String>>()
    val requestCodeDataLiveData = MutableLiveData<RequestedResult<String>>()
//    val loopFlow = flow {
//        var old = 0
//
//        while (true) {
//            emit(old)
//            old++
//            delay(1000)
//        }
//    }
//    private var stopFlag = false

    init {
//        onStopRequest()
//        callWithFlow()
//        callWithoutFlow()
//        callWithFlowRequested()
//        callWithoutFlowRequested()
    }

//    private fun onStopRequest() {
//        loopFlow.onEach {
//            dataLiveData.postValue(Result.Success(it.toString()))
//        }.takeWhile { !stopFlag }.launchIn(viewModelScope)
//    }

    private fun callWithFlow() { // requested res
        // oninit onrefresh
        merge(
            onInit,
            onRefresh
        ).flatMapLatest {
            dataSource.getDataFlow()
                .handleLoading(dataLiveData)
                .handleError(dataLiveData)
                .handleResult(dataLiveData)
        }.launchIn(viewModelScope)
    }

    private fun callWithoutFlow() {
        merge(onInit, onRefresh).onEach {
            networkExecutor {
                execute { dataSource.getData() }

                onResult { result ->
                    dataLiveData.value = result
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun callWithFlowRequested() {
        merge(
            onInit,
            onRefresh
        ).flatMapLatest { rc ->
            dataSource.getDataFlow()
                .handleRequestedLoading(requestCodeDataLiveData, rc)
                .handleRequestedError(requestCodeDataLiveData, rc)
                .handleRequestedResult(requestCodeDataLiveData, rc)
        }.launchIn(viewModelScope)
    }

    private fun callWithoutFlowRequested() {
        merge(onInit, onRefresh).onEach { rc ->
            networkExecutor {
                execute { dataSource.getData() }

                onRequestedResult(rc) { result ->
                    requestCodeDataLiveData.postValue(result)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onRefresh(requestCode: RequestCode) {
        viewModelScope.launch {
            onRefresh.emit(requestCode)
        }
    }

//    fun onStop() {
//        viewModelScope.launch {
//            stopFlag = !stopFlag
//        }
//    }
}

enum class RequestCode {
    RC_INIT,
    RC_RELOAD
}

class DataSource {
    suspend fun getData(): String {
        delay(1500L)
        return "suspend data"
    }

    fun getDataFlow(): Flow<String> = flow {
        delay(1500L)
        emit("data from flow")
    }
}
