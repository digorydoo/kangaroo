#pragma once

#include "public.sdk/source/vst/vsteditcontroller.h"
#include "public.sdk/source/vst/vstnoteexpressiontypes.h"
#include "pluginterfaces/vst/ivstmidicontrollers.h"
#include "pluginterfaces/vst/ivstmidilearn.h"
#include "pluginterfaces/vst/ivstevents.h"
#include "vstgui/plugin-bindings/vst3editor.h"
#include <array>

using namespace Steinberg;
using namespace Steinberg::Vst;

class UIController :
    public EditController,
    public IMidiMapping,
    public VSTGUI::VST3EditorDelegate
{
public:
	// EditController

	tresult PLUGIN_API initialize(FUnknown* context) override;
	tresult PLUGIN_API terminate() override;
	IPlugView* PLUGIN_API createView(FIDString name) override;
	tresult PLUGIN_API setComponentState(IBStream* state) override;
	tresult PLUGIN_API setState(IBStream* state) override;
	tresult PLUGIN_API getState(IBStream* state) override;
	tresult beginEdit(ParamID tag) override;
	tresult performEdit(ParamID tag, ParamValue valueNormalized) override;
	tresult endEdit(ParamID tag) override;
	tresult PLUGIN_API setParamNormalized(ParamID tag, ParamValue value) override;

	// IMidiMapping

	tresult PLUGIN_API getMidiControllerAssignment(int32 busIndex, int16 channel, CtrlNumber ctrlNumber,
	    ParamID &outId) override;

	// VST3EditorDelegate

	bool isPrivateParameter(const ParamID paramID) override;

    VSTGUI::CView *verifyView(VSTGUI::CView *view, const VSTGUI::UIAttributes &attrs,
        const VSTGUI::IUIDescription *desc, VSTGUI::VST3Editor *editor) override;

    // Called from Kotlin

    void sendNoteOnEvent(int pitch, float velo, int channel, float detune);
    void sendNoteOffEvent(int pitch, float velo, int channel);
    void sendMIDICCOutEvent(int ccNumber, int value, int value2, int channel);

    void addGenericParam(int tag, const char *title, const char *shortTitle, const char *units, int unitId, int flags,
        double defaultValue, int stepCount, int precision);
    void addRangeParam(int tag, const char *title, const char *shortTitle, const char *units, int unitId, int flags,
        double minValue, double maxValue, double defaultValue, int stepCount, int precision);
    void addStringListParam(int tag, const char *title, const char *shortTitle, const char *units, int unitId,
        int flags, const char *items);

	OBJ_METHODS(UIController, EditController)
	DEFINE_INTERFACES
		DEF_INTERFACE(IMidiMapping)
	END_DEFINE_INTERFACES(EditController)
	REFCOUNT_METHODS(EditController)

private:
    void sendEvent(const Steinberg::Vst::Event &evt);
};

static constexpr auto MsgIDEvent = "Event";
