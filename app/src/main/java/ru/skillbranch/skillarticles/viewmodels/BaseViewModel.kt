package ru.skillbranch.skillarticles.viewmodels

import androidx.annotation.UiThread
import androidx.lifecycle.*

abstract class BaseViewModel<T>(initState: T) : ViewModel() {
    protected val notifications = MutableLiveData<Event<Notify>>()

    protected val state: MediatorLiveData<T> = MediatorLiveData<T>().apply {
        value = initState
    }

    //not null current state
    protected val currentState
        get() = state.value!!

    /***
     * лямбда выражение принимает в качестве аргумента лямбду в которую передается текущее состояние
     * и она возвращает модифицированное состояние, которое присваивается текущему состоянию
     */
    @UiThread
    protected inline fun updateState(update: (currentState: T) -> T) {
        val updateState: T = update(currentState)
        state.value = updateState
    }

    /***
     * более компактная форма записи observe принимает последним аргументом лямбда выражение обрабатывающее
     * изменение текущего состояния
     */
    fun observeState(owner: LifecycleOwner, onChanged: (newState: T) -> Unit) {
        state.observe(owner, Observer { onChanged(it!!) })
    }

    /***
     * более компактная фома записи observe вызывает лямбда выражение обработчик только в том случае
     * если сообщение не было уже обработанно, реализует данное поведение благодоря EventObserver
     */
    fun observeNotifications(owner: LifecycleOwner, onNotify: (notification: Notify) -> Unit) {
        notifications.observe(owner, EventObserver {onNotify(it) })
    }

    /***
     * функция принимает источник даных и лямбда выажение обрабатывающее поступающие данные
     * лямбда принимает новые данные и текущее состояние, изменяет его и возвращает
     * модифицированное состояние устанавливается как текущее
     */
    protected fun<S> subscribeOnDataSource(
        source: LiveData<S>,
        onChanged: (newValue : S, currentState:T) -> T?
    ) {
        state.addSource(source){
            state.value = onChanged(it, currentState) ?: return@addSource
        }
    }
}

class ViewModelFactory(private val params: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
            return ArticleViewModel(params) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class Event<out E>(private val content:E){
    var hasBeenHandled = false

    //возвращает контент который еще не был обработан иначе null
    fun getContentIfNotHandled():E?{
        return if(hasBeenHandled) null
        else{
            hasBeenHandled = true
            content
        }
    }
}

class EventObserver<E>(private val onEventUnhandledContent: (E) -> Unit):Observer<Event<E>>{
    //в качестве аргумента принимает лямбда выражение обработчик в которую передается необработанное
    //ранее событие получаемое в реализации метода Observer'a onChanged
    override fun onChanged(event: Event<E>?) {
        //если есть необработанное событие (контент) передай в качестве аргумента в лямбду
        // onEventUnhandledContent
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}

sealed class Notify(val message: String){
    data class TextMessage(val msg:String) : Notify(msg)

    data class ActionMessage(
        val msg: String,
        val actionLabel: String,
        val actionHandler: (() -> Unit)?
    ) : Notify(msg)

    data class ErrorMessage(
        val msg: String,
        val errLabel: String,
        val errHandler: (() -> Unit)?
    ) : Notify(msg)
}