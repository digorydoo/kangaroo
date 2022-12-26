#include "UIController.h"
#include "UIControllerGlue.h"

using namespace VSTGUI;

void cppSendNoteOnEvent(long ptr, int pitch, float velo, int channel, float detune) {
    auto controller = (UIController *) ptr;
    controller->sendNoteOnEvent(pitch, velo, channel, detune);
}

void cppSendNoteOffEvent(long ptr, int pitch, float velo, int channel) {
    auto controller = (UIController *) ptr;
    controller->sendNoteOffEvent(pitch, velo, channel);
}

void cppSendMIDICCOutEvent(long ptr, int ccNumber, int value, int value2, int channel) {
    auto controller = (UIController *) ptr;
    controller->sendMIDICCOutEvent(ccNumber, value, value2, channel);
}

void cppBeginParamEdit(long ptr, int tag) {
    auto controller = (UIController *) ptr;
    controller->beginEdit(tag);
}

void cppEndParamEdit(long ptr, int tag) {
    auto controller = (UIController *) ptr;
    controller->endEdit(tag);
}

void cppSetParamNormalized(long ptr, int tag, float value, int performEdit) {
    auto controller = (UIController *) ptr;
    bool result = controller->setParamNormalized(tag, value);

    if (result == Steinberg::kResultTrue && performEdit) {
        controller->performEdit(tag, controller->getParamNormalized(tag));
    }
}

int cppMakeParamFlags(
    int canAutomate,
    int isReadOnly,
    int isHidden,
    int isWrapAround,
    int isList,
    int isProgramChange,
    int isBypass
) {
    int flags = 0;
    if (canAutomate) flags |= Steinberg::Vst::ParameterInfo::kCanAutomate;
    if (isReadOnly) flags |= Steinberg::Vst::ParameterInfo::kIsReadOnly;
    if (isHidden) flags |= Steinberg::Vst::ParameterInfo::kIsHidden;
    if (isWrapAround) flags |= Steinberg::Vst::ParameterInfo::kIsWrapAround;
    if (isList) flags |= Steinberg::Vst::ParameterInfo::kIsList;
    if (isProgramChange) flags |= Steinberg::Vst::ParameterInfo::kIsProgramChange;
    if (isBypass) flags |= Steinberg::Vst::ParameterInfo::kIsBypass;
    return flags;
}

void cppAddGenericParam(
    long ptr,
    int tag,
    const char *title,
    const char *shortTitle,
    const char *units,
    int unitId,
    int flags,
    float defaultValue,
    int stepCount,
    int precision
) {
    auto controller = (UIController *) ptr;
    controller->addGenericParam(
        tag,
        title,
        shortTitle,
        units,
        unitId,
        flags,
        (double) defaultValue,
        stepCount,
        precision
    );
}

void cppAddRangeParam(
    long ptr,
    int tag,
    const char *title,
    const char *shortTitle,
    const char *units,
    int unitId,
    int flags,
    float minValue,
    float maxValue,
    float defaultValue,
    int stepCount,
    int precision
) {
    auto controller = (UIController *) ptr;
    controller->addRangeParam(
        tag,
        title,
        shortTitle,
        units,
        unitId,
        flags,
        (double) minValue,
        (double) maxValue,
        (double) defaultValue,
        stepCount,
        precision
    );
}

void cppAddStringListParam(
    long ptr,
    int tag,
    const char *title,
    const char *shortTitle,
    const char *units,
    int unitId,
    int flags,
    const char *items
) {
    auto controller = (UIController *) ptr;
    controller->addStringListParam(
        tag,
        title,
        shortTitle,
        units,
        unitId,
        flags,
        items
    );
}
