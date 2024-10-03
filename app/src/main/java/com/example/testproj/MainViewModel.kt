package com.example.testproj

import android.provider.ContactsContract.Data
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testproj.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class MainViewModel : ViewModel() {
    private val onRefresh = MutableSharedFlow<RequestCode>()
    val dataFlow = MutableSharedFlow<Result<String>>()

    init {
        onRefresh.mapLatest {
            networkExecutor {
                execute {
                    val a = async {
                        delay(1500)
                        "a"
                    }

                    val b = async {
                        delay(3000)
                        "b"
                    }

                    a.await() + b.await()
                }

                onResult { result -> dataFlow.emit(result) }
            }
        }.launchIn(viewModelScope)
    }

    fun onRefresh() {
        viewModelScope.launch {
            onRefresh.emit(RequestCode.RC_INIT)
        }
    }
}

//class MainViewModel : ViewModel() {
//    private val onRefresh = MutableSharedFlow<RequestCode>()
//    val dataFlow = MutableSharedFlow<Result<String>>()
//
//    val flowA = flow {
//        delay(1500)
//        emit("a")
//    }
//
//    val flowB = flow {
//        delay(3000)
//        emit("b")
//    }
//
//    init {
//        onRefresh.flatMapLatest {
//            flowA.zip(flowB) { a, b ->
//                a + b
//            }.handleLoading(dataFlow)
//                .handleError(dataFlow)
//                .handleResult(dataFlow)
//        }.launchIn(viewModelScope)
//    }
//
//    fun onRefresh() {
//        viewModelScope.launch {
//            onRefresh.emit(RequestCode.RC_INIT)
//        }
//    }
//}

//class MainViewModel : ViewModel() {
//    private val onRefresh = MutableSharedFlow<RequestCode>()
//    val dataFlow = MutableSharedFlow<Result<String>>()
//
//    init {
//        onRefresh.flatMapLatest {
//            flow {
//                delay(3000L)
//                emit("Some Value")
//
//                currentCoroutineContext().cancel()
//            }.handleLoading(dataFlow)
//                .handleError(dataFlow)
//                .handleResult(dataFlow)
//        }.launchIn(viewModelScope)
//
//        onRefresh.flatMapLatest {
//            flow {
//                delay(3000L)
//                emit("Some Value without cancel")
//            }.handleLoading(dataFlow)
//                .handleError(dataFlow)
//                .handleResult(dataFlow)
//        }.launchIn(viewModelScope)
//    }
//
//    fun onRefresh() {
//        viewModelScope.launch {
//            onRefresh.emit(RequestCode.RC_INIT)
//        }
//    }
//}

//class MainViewModel : ViewModel() {
//    private val onRefresh = MutableSharedFlow<RequestCode>()
//    private val onInit get() = flow { emit(RequestCode.RC_INIT) }
//    private val dataSource = DataSource()
//
//    val dataFlow = MutableSharedFlow<Result<String>>(replay = 1)
//
//    val productActivatedSubject = MutableSharedFlow<Unit>()
//
//    init {
//        merge(
//            onInit,
//            onRefresh,
//            productActivatedSubject
//        ).mapLatest {
//            networkExecutor {
//                execute { dataSource.getData() }
//
//                onResult {
//                    result -> dataFlow.emit(result)
//                }
//            }
//        }.launchIn(viewModelScope)
//    }
//
//    fun onRefresh(requestCode: RequestCode) {
//        viewModelScope.launch {
//            onRefresh.emit(requestCode)
//        }
//    }
//
//    fun productActivated() = viewModelScope.launch {
//        productActivatedSubject.emit(Unit)
//    }
//
//}

//class MainViewModel : ViewModel() {
//    private val onRefresh = MutableSharedFlow<RequestCode>()
//    private val onInit get() = flow { emit(RequestCode.RC_INIT) }
//
//    val dataFlow = MutableSharedFlow<Result<String>>(replay = 1)
//
//    val productActivatedSubject = MutableSharedFlow<Unit>()
//
//    init {
//        merge(
//            onInit,
//            onRefresh,
//            productActivatedSubject
//        ).flatMapLatest {
//            flow {
//                delay(3000L)
//                emit("Some Value")
//            }.handleLoading(dataFlow)
//                .handleError(dataFlow)
//                .handleResult(dataFlow)
//        }.launchIn(viewModelScope)
//    }
//
//    fun onRefresh(requestCode: RequestCode) {
//        viewModelScope.launch {
//            onRefresh.emit(requestCode)
//        }
//    }
//
//    fun productActivated() = viewModelScope.launch {
//        productActivatedSubject.emit(Unit)
//    }
//
//}


//class MainViewModel : ViewModel() {
//    private val onRefresh = MutableSharedFlow<RequestCode>()
//    val dataFlow = MutableSharedFlow<Result<String>>()
//
//    init {
//        onRefresh.flatMapLatest {
//            flow {
//                delay(3000L)
//                emit("Some Value")
//            }.handleLoading(dataFlow)
//                .handleError(dataFlow)
//                .handleResult(dataFlow)
//        }.launchIn(viewModelScope)
//    }
//
//    fun onRefresh(requestCode: RequestCode) {
//        viewModelScope.launch {
//            onRefresh.emit(requestCode)
//        }
//    }
//
//}

//class MainViewModel : ViewModel() {
//    private val onRefresh = MutableSharedFlow<RequestCode>()
//    val dataLiveData = MutableLiveData<Result<String>>()
//
//    init {
//        onRefresh.flatMapLatest {
//            flow {
//                delay(3000L)
//                emit("Some Value")
//            }.handleLoading(dataLiveData)
//                .handleError(dataLiveData)
//                .handleResult(dataLiveData)
//        }.launchIn(viewModelScope)
//    }
//
//    fun onRefresh(requestCode: RequestCode) {
//        viewModelScope.launch {
//            onRefresh.emit(requestCode)
//        }
//    }
//
//}


//class MainViewModel : ViewModel() {
//    private val onRefresh = MutableSharedFlow<RequestCode>()
//    private val onInit get() = flow { emit(RequestCode.RC_INIT) }
//
//    private val dataSource = DataSource()
//
//    lateinit var dataFlow: Flow<Result<String>>
//        private set
//
//    val dataLiveData = MutableLiveData<Result<String>>()
//    val requestCodeDataLiveData = MutableLiveData<RequestedResult<String>>()
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
//
//    init {
//        onStopRequest()
//        callWithFlow()
//        callWithoutFlow()
//        callWithFlowRequested()
//        callWithoutFlowRequested()
//
//        flow {
//            val data1 = viewModelScope.async { dataSource.getData() }.await()
//            val data2 = viewModelScope.async { dataSource.getData() }.await()
//
//            emit(Pair(data1, data2))
//        }
//
//        dataFlow = merge(onInit, onRefresh).flatMapLatest {
//            flow {
//                emit(Result.Loading)
//
//                try {
//                    emit(Result.Success(dataSource.getData()))
//                } catch (e: Exception) {
//                    emit(Result.Error(e))
//                }
//            }
//        }
//    }
//
//    private fun onStopRequest() {
//        loopFlow.onEach {
//            dataLiveData.postValue(Result.Success(it.toString()))
//        }.takeWhile { !stopFlag }.launchIn(viewModelScope)
//    }
//
//    private fun callWithFlow() { // requested res
//        // oninit onrefresh
//        merge(
//            onInit,
//            onRefresh
//        ).flatMapLatest {
//            dataSource.getDataFlow()
//                .handleLoading(dataLiveData)
//                .handleError(dataLiveData)
//                .handleResult(dataLiveData)
//        }.launchIn(viewModelScope)
//    }
//
//    private fun callWithoutFlow() {
//        merge(onInit, onRefresh).mapLatest {
//            networkExecutor {
//                execute {
//                    dataSource.getData()
//                }
//
//                onResult { result ->
//                    dataLiveData.value = result
//                }
//            }
//        }.launchIn(viewModelScope)
//    }
//
//    private fun call(): Flow<Result<String>> {
//        return merge(onInit, onRefresh).flatMapLatest {
//            flow {
//                emit(Result.Loading)
//
//                try {
//                    emit(Result.Success(dataSource.getData()))
//                } catch (e: Exception) {
//                    emit(Result.Error(e))
//                }
//            }
//        }
//    }
//
//    private fun callWithFlowRequested() {
//        merge(
//            onInit,
//            onRefresh
//        ).flatMapLatest { rc ->
//            dataSource.getDataFlow()
//                .handleRequestedLoading(requestCodeDataLiveData, rc)
//                .handleRequestedError(requestCodeDataLiveData, rc)
//                .handleRequestedResult(requestCodeDataLiveData, rc)
//        }.launchIn(viewModelScope)
//    }
//
//    private fun callWithoutFlowRequested() {
//        merge(onInit, onRefresh).onEach { rc ->
//            networkExecutor {
//                execute { dataSource.getData() }
//
//                onRequestedResult(rc) { result ->
//                    requestCodeDataLiveData.postValue(result)
//                }
//            }
//        }.launchIn(viewModelScope)
//    }
//
//    fun onRefresh(requestCode: RequestCode) {
//        viewModelScope.launch {
//            onRefresh.emit(requestCode)
//        }
//    }
//
//    fun onStop() {
//        viewModelScope.launch {
//            stopFlag = !stopFlag
//        }
//    }
//}

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
