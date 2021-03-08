package tech.skot.core.components.presented

import tech.skot.core.components.ComponentVC
import tech.skot.core.components.SKLayoutNo
import tech.skot.core.components.ScreenVC

@SKLayoutNo
interface BottomSheetVC:ComponentVC {

    data class Shown(val screen:ScreenVC)

    var state :Shown?
}