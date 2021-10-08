package tech.skot.core.components

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import tech.skot.core.SKLog
import tech.skot.core.view.Style
import tech.skot.view.live.SKMessage


abstract class SKComponentViewProxy<B : Any> : SKComponentVC {

    protected val displayErrorMessage = SKMessage<String>()

    override fun displayErrorMessage(message: String) {
        displayErrorMessage.post(message)
    }

    protected val closeKeyboardMessage = SKMessage<Unit>()

    override fun closeKeyboard() {
        closeKeyboardMessage.post(Unit)
    }

    override var style: Style? = null

    init {
        SKLog.d("---- SKComponentViewProxy init")
    }

    abstract fun bindTo(
        activity: SKActivity,
        fragment: Fragment?,
        binding: B,
        collectingObservers: Boolean
    ): SKComponentView<B>

    fun _bindTo(
        activity: SKActivity,
        fragment: Fragment?,
        binding: B,
        collectingObservers: Boolean = false
    ) =
        bindTo(activity, fragment, binding, collectingObservers).apply {
            displayErrorMessage.observe {
                displayError(it)
            }
            closeKeyboardMessage.observe {
                closeKeyboard()
            }
        }

    open fun saveState() {
        //surchargée quand le component a un état à sauver
    }

    @CallSuper
    override fun onRemove() {

    }

    open val layoutId: Int? = null

    open fun inflate(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): B {
        TODO("inflate method not implemented")
    }

    fun bindToView(
        activity: SKActivity,
        fragment: Fragment?,
        view: View,
        collectingObservers: Boolean = false
    ) =
        _bindTo(activity, fragment, bindingOf(view), collectingObservers)

    fun inflateInParentAndBind(activity: SKActivity, fragment: Fragment?, parent: ViewGroup) {

        SKLog.d("----- inflateInParentAndBind")
        val inflater = (fragment?.layoutInflater ?: activity.layoutInflater).let { layoutInflater ->
            parent.context?.let { parentContext ->
                layoutInflater.cloneInContext(
                    style?.let { theme ->
                        SKLog.d("----- wrapp avec style $theme")
                        ContextThemeWrapper(parentContext, theme.res)
                    } ?: parentContext
                )
            } ?: layoutInflater
        }
        _bindTo(activity, fragment, inflate(inflater, parent, true), false)
    }

    open fun bindingOf(view: View): B {
        return (view as? B)
            ?: throw IllegalStateException("You cant't bind this component to a view")
    }

    fun bindToItemView(activity: SKActivity, fragment: Fragment?, view: View): SKComponentView<B> {
        if (layoutId == null) {
            throw IllegalStateException("You cant't bind this component to an Item's view, it has no layout Id")
        } else {
            return _bindTo(activity, fragment, bindingOf(view), collectingObservers = true)
        }
    }
}

