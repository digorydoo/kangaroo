#include "public.sdk/source/main/pluginfactory.h"
#include "AudioProcessor.h"
#include "UIController.h"
#include "version.h"

// I created the FUIDs with uuidgen (shell). Note that there are two PNGs for fuidAudioProcessor.

static Steinberg::FUID fuidAudioProcessor(0x184A483B, 0xD9924E20, 0x810205E1, 0x273A6EB1);
static Steinberg::FUID fuidUIController(0x728074C8, 0xEEC8435C, 0x96FB3023, 0xA3920A66);

static Steinberg::FUnknown *createAudioProcessor(void *) {
    auto c = new AudioProcessor();
    c->setControllerClass(fuidUIController);
    return (Steinberg::Vst::IAudioProcessor *) c;
}

static Steinberg::FUnknown *createUIController(void *) {
    return (Steinberg::Vst::IEditController *) new UIController();
}

BEGIN_FACTORY_DEF(stringCompanyName, stringCompanyWeb, stringCompanyEmail)
	DEF_CLASS2(
        INLINE_UID_FROM_FUID(fuidAudioProcessor),
        PClassInfo::kManyInstances,
        kVstAudioEffectClass,
        stringPluginName,
        Vst::kDistributable,
        Vst::PlugType::kInstrumentSynth,
        FULL_VERSION_STR,
        kVstVersionString,
        createAudioProcessor
    )
	DEF_CLASS2(
        INLINE_UID_FROM_FUID(fuidUIController),
        PClassInfo::kManyInstances,
        kVstComponentControllerClass,
        stringPluginName,
        0,	// not used here
        "", // not used here
        FULL_VERSION_STR,
        kVstVersionString,
        createUIController
    )
END_FACTORY
