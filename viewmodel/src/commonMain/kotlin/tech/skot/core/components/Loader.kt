package tech.skot.core.components

import tech.skot.core.di.coreViewInjector

class Loader : Component<LoaderVC>() {

    override val view = coreViewInjector.loader()

    fun workStarted() {
        loadingCounter++
    }

    fun workEnded() {
        loadingCounter--
    }

    private var loadingCounter = 0
        set(value) {
            field = value
            view.visible = value > 0
        }
}