package ch.digorydoo.kangaroo.param

import interop.cppMakeParamFlags

@Suppress("unused")
abstract class Param {
    abstract val title: String
    abstract val tag: ParamTag
    abstract val units: String?
    abstract val defaultValue: Float
    abstract val stepCount: Int
    abstract val unitId: Int // 0 is kRootUnitId
    abstract val precision: Int
    abstract val balanced: Boolean // value can be negative
    abstract val canAutomate: Boolean // ParameterInfo::kCanAutomate
    abstract val hidden: Boolean // parameter should be NOT displayed (implies !canAutomate && isReadOnly)
    abstract val readOnly: Boolean // param cannot be changed from outside plugin (implies !canAutomate)
    abstract val wrapAround: Boolean // attempts to set the value out of limits will result in wrap around
    abstract val isList: Boolean // param should be displayed as list
    abstract val isProgramChange: Boolean // param is a pgchg (unitId gives info about associated unit)
    abstract val isBypass: Boolean // special bypass parameter (only one allowed): plug-in can handle bypass

    open val normDefaultValue: Float
        get() = defaultValue

    val shortTitle: String
        get() = title.replace(" ", "")

    val flags: Int
        get() = cppMakeParamFlags(
            if (canAutomate) 1 else 0,
            if (readOnly) 1 else 0,
            if (hidden) 1 else 0,
            if (wrapAround) 1 else 0,
            if (isList) 1 else 0,
            if (isProgramChange) 1 else 0,
            if (isBypass) 1 else 0
        )

    class GenericParam(
        override val title: String,
        override val tag: ParamTag,
        override val units: String? = null,
        override val defaultValue: Float = 0.0f,
        override val stepCount: Int = 0,
        override val unitId: Int = 0,
        override val precision: Int = 0,
        override val balanced: Boolean = false,
        override val canAutomate: Boolean = true,
        override val hidden: Boolean = false,
        override val readOnly: Boolean = hidden,
        override val wrapAround: Boolean = false,
        override val isProgramChange: Boolean = false,
        override val isBypass: Boolean = false,
    ): Param() {
        override val isList = false
    }

    class RangeParam(
        override val title: String,
        override val tag: ParamTag,
        override val units: String? = null,
        val minValue: Float = 0.0f,
        val maxValue: Float = 1.0f,
        override val defaultValue: Float = 0.0f,
        override val unitId: Int = 0,
        override val precision: Int = 0,
        override val hidden: Boolean = false,
        override val readOnly: Boolean = hidden,
    ): Param() {
        override val balanced = minValue < 0.0f
        override val stepCount = if (precision > 0) 0 else (maxValue - minValue).toInt()
        override val canAutomate = !readOnly
        override val wrapAround = false
        override val isProgramChange = false
        override val isBypass = false
        override val isList = false
        override val normDefaultValue = if (balanced) 0.5f else (defaultValue - minValue) / (maxValue - minValue)
    }

    class StringListParam(
        override val title: String,
        override val tag: ParamTag,
        override val units: String? = null,
        override val unitId: Int = 0,
        val list: List<String>,
    ): Param() {
        override val defaultValue = 0.0f
        override val stepCount = -1
        override val precision = 0
        override val balanced = false
        override val canAutomate = true
        override val hidden = false
        override val readOnly = false
        override val wrapAround = false
        override val isProgramChange = false
        override val isBypass = false
        override val isList = true
    }
}
