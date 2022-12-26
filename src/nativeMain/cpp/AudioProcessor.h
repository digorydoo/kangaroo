#pragma once

#include "pluginterfaces/vst/ivstevents.h"
#include "public.sdk/source/vst/vstaudioeffect.h"
#include "public.sdk/source/vst/utility/ringbuffer.h"
#include "AudioProcessorGlue.h"

using namespace Steinberg;
using namespace Steinberg::Vst;

class AudioProcessor : public AudioEffect {
public:
	AudioProcessor();
	~AudioProcessor();
	
	tresult PLUGIN_API initialize(FUnknown* context) override;
	tresult PLUGIN_API setBusArrangements(SpeakerArrangement* inputs, int32 numIns, SpeakerArrangement* outputs,
	    int32 numOuts) override;

	tresult PLUGIN_API setState(IBStream *state) override;
	tresult PLUGIN_API getState(IBStream *state) override;

	tresult PLUGIN_API canProcessSampleSize(int32 symbolicSampleSize) override;
	tresult PLUGIN_API setActive(TBool state) override;
	tresult PLUGIN_API process(ProcessData &data) override;

	tresult PLUGIN_API notify(IMessage *message) override;

	SParamChange getInputParamChange(int idx);
	void setOutputParamChange(int tag, float value);

private:
    void handleEvent(const Event &evt);

    IParameterChanges *cachedInputParamChanges = nullptr;
    IParameterChanges *cachedOutputParamChanges = nullptr;
};
