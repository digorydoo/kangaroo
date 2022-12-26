#include "base/source/fstreamer.h"
#include "base/source/fstring.h"
#include "pluginterfaces/base/futils.h"
#include "pluginterfaces/base/ustring.h"
#include "pluginterfaces/vst/ivstevents.h"
#include "vstgui/plugin-bindings/vst3groupcontroller.h"
#include "CanvasView.h"
#include "UIController.h"
#include "KotlinSym.h"

using namespace Steinberg::Vst;

tresult PLUGIN_API UIController::initialize(FUnknown *context) {
    tresult result = EditController::initialize(context);

    if (result == kResultTrue) {
        KOTLIN.createUIController((long) this);
    }

    return kResultTrue;
}

tresult PLUGIN_API UIController::terminate() {
    KOTLIN.destroyUIController((long) this);
    return EditController::terminate();
}

IPlugView* PLUGIN_API UIController::createView(FIDString name) {
    ConstString strName(name);
    return strName == ViewType::kEditor ? new VSTGUI::VST3Editor(this, "Editor", "kangaroo.uidesc") : nullptr;
}

VSTGUI::CView *UIController::verifyView(
    VSTGUI::CView *view,
    const VSTGUI::UIAttributes &attrs,
    const VSTGUI::IUIDescription *desc,
    VSTGUI::VST3Editor *editor
) {
    auto canvas = dynamic_cast<CanvasView *>(view);

    if (canvas != nullptr) {
        KOTLIN.linkViewToController((long) canvas, (long) this);
    }

    return VST3EditorDelegate::verifyView(view, attrs, desc, editor);
}

tresult PLUGIN_API UIController::setComponentState(IBStream *state) {
    fprintf(stdout, "Controller::setComponentState\n");
    // The host first calls getState on the AudioProcessor instance to get the processor's default values, and then
    // calls setComponentState, assumingly with these values. Why would we need this?
    return kResultTrue;
}

tresult PLUGIN_API UIController::setState(IBStream *state) {
    IBStreamer stream(state, kLittleEndian);
    bool ok = KOTLIN.uiReadState((long) this, (long) &stream);
    return ok ? kResultTrue : kInternalError;
}

tresult PLUGIN_API UIController::getState(IBStream *state) {
    IBStreamer stream(state, kLittleEndian);
    bool ok = KOTLIN.uiWriteState((long) this, (long) &stream);
    return ok ? kResultTrue : kInternalError;
}

tresult PLUGIN_API UIController::setParamNormalized(ParamID tag, ParamValue value) {
    // We come here when the param is changed by UI as well as by AudioProcessor!
    KOTLIN.uiParamChanged((long) this, tag, value);
    return EditController::setParamNormalized(tag, value);
}

tresult UIController::beginEdit(ParamID tag) {
    fprintf(stdout, "UIController::beginEdit(%u)\n", tag);
    return EditController::beginEdit(tag);
}

tresult UIController::performEdit(ParamID tag, ParamValue valueNormalized) {
    fprintf(stdout, "UIController::performEdit(%u, %f)\n", tag, valueNormalized);
    return EditController::performEdit(tag, valueNormalized);
}

tresult UIController::endEdit(ParamID tag) {
    fprintf(stdout, "UIController::endEdit(%u)\n", tag);
    return EditController::endEdit(tag);
}

bool UIController::isPrivateParameter(const ParamID paramID) {
    bool result = false; // TODO
    fprintf(stdout, "UIController::isPrivateParameter(%u) -> %s", paramID, result ? "true" : "false");
    return result;
}

tresult PLUGIN_API UIController::getMidiControllerAssignment(
    int32 busIndex,
    int16 channel,
    CtrlNumber ctrlNumber,
    ParamID &outId
) {
    int id = KOTLIN.getMidiControllerAssignment((long) this, busIndex, channel, ctrlNumber);

    if (id >= 0) {
        outId = id;
        return kResultTrue;
    }

    return kResultFalse;
}

void UIController::sendNoteOnEvent(int pitch, float velo, int channel, float detune) {
    //fprintf(stdout, "UIController::sendNoteOnEvent(pitch=%d velo=%f channel=%d detune=%f)\n",
    //    pitch, velo, channel, detune);

    Event evt {};
    evt.type = Event::EventTypes::kNoteOnEvent;
    evt.flags = Event::EventFlags::kIsLive;
    evt.noteOn.channel = channel;
    evt.noteOn.pitch = pitch;
    evt.noteOn.tuning = detune;
    evt.noteOn.velocity = velo;
    evt.noteOn.length = 0;
    evt.noteOn.noteId = -1; // if we need this, it must be between kNoteIDUserRangeLowerBound..UpperBound
    sendEvent(evt);
}

void UIController::sendNoteOffEvent(int pitch, float velo, int channel) {
    //fprintf(stdout, "UIController::sendNoteOffEvent(pitch=%d velo=%f channel=%d)\n",
    //    pitch, velo, channel);

    Event evt {};
    evt.type = Event::EventTypes::kNoteOffEvent;
    evt.flags = Event::EventFlags::kIsLive;
    evt.noteOff.channel = channel;
    evt.noteOff.pitch = pitch;
    evt.noteOff.tuning = 0.f;
    evt.noteOff.velocity = velo;
    evt.noteOff.noteId = -1; // see sendNoteOnEvent
    sendEvent(evt);
}

void UIController::sendMIDICCOutEvent(int ccNumber, int value, int value2, int channel) {
    fprintf(stdout, "UIController::sendMIDICCOutEvent(cc=%d, value=%d, value2=%d, channel=%d)\n",
        ccNumber, value, value2, channel);

    Event evt {};
    evt.type = Event::EventTypes::kLegacyMIDICCOutEvent;
    evt.flags = Event::EventFlags::kIsLive;
    evt.midiCCOut.controlNumber = ccNumber;
    evt.midiCCOut.value = value;
    evt.midiCCOut.value2 = value2;
    evt.midiCCOut.channel = channel;
    sendEvent(evt);
}

void UIController::sendEvent(const Event &evt) {
    fprintf(stdout, "UIController::sendEvent\n");
    auto msg = owned(allocateMessage());

    if (msg) {
        msg->setMessageID(MsgIDEvent);

        if (auto attr = msg->getAttributes()) {
            attr->setBinary(MsgIDEvent, &evt, sizeof(Event));
        }

        peerConnection->notify(msg); // peerConnection is the AudioProcessor
    }
}

void UIController::addGenericParam(
    int tag,
    const char *title,
    const char *shortTitle,
    const char *units,
    int unitId,
    int flags,
    double defaultValue,
    int stepCount,
    int precision
) {
    auto *p = parameters.addParameter(
        USTRING(title && title[0] ? title : "Unknown"),
        USTRING(units),
        stepCount,
        defaultValue,
        flags,
        tag,
        unitId,
        USTRING(shortTitle)
    );
    p->setPrecision(precision);
}

void UIController::addRangeParam(
    int tag,
    const char *title,
    const char *shortTitle,
    const char *units,
    int unitId,
    int flags,
    double minValue,
    double maxValue,
    double defaultValue,
    int stepCount,
    int precision
) {
    auto *p = new RangeParameter(
        USTRING(title && title[0] ? title : "Unknown"),
        tag,
        USTRING(units),
        minValue,
        maxValue,
        defaultValue,
        stepCount,
        flags,
        unitId,
        USTRING(shortTitle)
    );
    p->setPrecision(precision);
    parameters.addParameter(p);
}

void UIController::addStringListParam(
    int tag,
    const char *title,
    const char *shortTitle,
    const char *units,
    int unitId,
    int flags,
    const char *items
) {
    auto *p = new StringListParameter(
        USTRING(title && title[0] ? title : "Unknown"),
        tag,
        USTRING(units),
        flags,
        unitId,
        USTRING(shortTitle)
    );

    // Items are passed newline-delimited from Kotlin

    std::string s = items ? items : "";
    std::string token;
    std::string::size_type start = 0;
    std::string::size_type found = -1;

    do {
        found = s.find("\n", start);

        if (found == std::string::npos) {
            token = s.substr(start);
        } else {
            token = s.substr(start, found - start);
        }

        if (token.length() > 0) {
            p->appendString(USTRING(token.c_str()));
        }

        start = found + 1;
    } while (found != std::string::npos);

    parameters.addParameter(p);
}
