package tech.skot.core.components

import android.view.View
import android.view.WindowInsets
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import tech.skot.view.extensions.updatePadding


abstract class SKScreenView<B : ViewBinding>(
    override val proxy: SKScreenViewProxy<B>,
    activity: SKActivity,
    fragment: Fragment?,
    binding: B
) : SKComponentView<B>(proxy, activity, fragment, binding) {
    val view: View = binding.root


    private var onBackPressed: (() -> Unit)? = null
    fun setOnBackPressed(onBackPressed: (() -> Unit)?) {
        this.onBackPressed = onBackPressed
    }

    var thisScreenSystemUiVisibility: Int? = null

    @CallSuper
    open fun onResume() {
//        SKLog.d("######->${this::class.simpleName} ${this.hashCode()} onResume ")
        activity.window.decorView.systemUiVisibility = thisScreenSystemUiVisibility ?: (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        proxy.onResume?.invoke()
    }

    @CallSuper
    open fun onPause() {
        proxy.onPause?.invoke()
//        SKLog.d("######<-${this::class.simpleName} ${this.hashCode()} onPause")
    }


    protected fun fullScreen(
        withPaddingTop: Boolean = false,
        onWindowInset: ((windowInsets: WindowInsets) -> Unit)? = null
    ) {
        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR).let {
            thisScreenSystemUiVisibility = it
            activity.window.decorView.systemUiVisibility = it
        }
        if (withPaddingTop || onWindowInset != null) {
            setOnWindowInsetManagingPaddingTop(withPaddingTop, onWindowInset)
        }
    }


    protected fun setOnWindowInsetManagingPaddingTop(
        withPaddingTop: Boolean = false,
        onWindowInset: ((windowInsets: WindowInsets) -> Unit)?
    ) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val loadedInsets = activity.window?.decorView?.rootWindowInsets
            val initialPaddingTop = view.paddingTop
            if (loadedInsets != null) {
                if (withPaddingTop) {
                    view.updatePadding(top = view.paddingTop + loadedInsets.systemWindowInsetTop)
                }
                onWindowInset?.invoke(loadedInsets)
            } else {
                view.setOnApplyWindowInsetsListener { view, windowInsets ->
                    if (withPaddingTop) {
                        view.updatePadding(top = initialPaddingTop + windowInsets.systemWindowInsetTop)
                    }
                    onWindowInset?.invoke(windowInsets)
                    windowInsets
                }
            }
        } else {
//            TODO("VERSION.SDK_INT < M")
        }

    }

    init {
        ScreensManager.backPressed.observe(this) {
            onBackPressed?.invoke()
        }

    }

}