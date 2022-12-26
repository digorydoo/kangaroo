#include "base/source/fstreamer.h"
#include "pluginterfaces/base/ustring.h"
#include "pluginterfaces/vst/ivstparameterchanges.h"
#include "KotlinSym.h"
#include "UIController.h"
#include "AudioProcessorGlue.h"
#include "AudioProcessor.h"
#include <algorithm>

AudioProcessor::AudioProcessor() {
    fprintf(stdout, "AudioProcessor ctor\n");
    KOTLIN.createAudioProcessor((long) this);
}

AudioProcessor::~AudioProcessor() {
    fprintf(stdout, "AudioProcessor dtor\n");
    KOTLIN.destroyAudioProcessor((long) this);
}

tresult PLUGIN_API AudioProcessor::initialize(FUnknown *context) {
    fprintf(stdout, "AudioProcessor::initialize\n");
    tresult result = AudioEffect::initialize(context);

    if (result == kResultTrue) {
        addAudioOutput(STR16("Audio Output"), SpeakerArr::kStereo);
        addEventInput(STR16("Event Input"), 1);
    }

    return result;
}

tresult PLUGIN_API AudioProcessor::notify(IMessage* message) {
    auto msgID = message->getMessageID();
    fprintf(stdout, "AudioProcessor::notify(%s)\n", msgID);

    if (strcmp(msgID, MsgIDEvent) != 0) {
        return kResultFalse;
    }

    if (auto attr = message->getAttributes ()) {
        const void *msgData;
        uint32 msgSize;

        if (attr->getBinary(MsgIDEvent, msgData, msgSize) == kResultTrue && msgSize == sizeof(Event)) {
            auto &evt = *((const Event *) msgData);
            handleEvent(evt);
        }
    }

    return kResultTrue;
}

void AudioProcessor::handleEvent(const Event &evt) {
    SMidiEvent sme;
    memset(&sme, 0, sizeof(sme));
    sme.busIndex = evt.busIndex;
    sme.sampleOffset = evt.sampleOffset;
    sme.ppqPosition = evt.ppqPosition;
    sme.isLive = (evt.flags & Event::kIsLive) != 0;
    sme.user1Bit = (evt.flags & Event::kUserReserved1) != 0;
    sme.user2Bit = (evt.flags & Event::kUserReserved2) != 0;

    switch(evt.type) {
        case Event::kNoteOnEvent:
            sme.type = NOTE_ON_EVENT;
            sme.channel = evt.noteOn.channel;
            sme.pitch = evt.noteOn.pitch;
            sme.tuning = evt.noteOn.tuning;
            sme.velocity = evt.noteOn.velocity;
            sme.noteLength = evt.noteOn.length;
            sme.noteId = evt.noteOn.noteId;
            break;
        case Event::kNoteOffEvent:
            sme.type = NOTE_OFF_EVENT;
            sme.channel = evt.noteOff.channel;
            sme.pitch = evt.noteOff.pitch;
            sme.velocity = evt.noteOff.velocity;
            sme.noteId = evt.noteOff.noteId;
            sme.tuning = evt.noteOff.tuning;
            break;
        case Event::kLegacyMIDICCOutEvent:
            sme.type = CC_OUT_EVENT;
            sme.channel = evt.midiCCOut.channel;
            sme.ccNumber = evt.midiCCOut.controlNumber;
            sme.ccValue = evt.midiCCOut.value;
            sme.ccValue2 = evt.midiCCOut.value2;
            break;
        default:
            sme.type = UNSUPPORTED_EVENT;
    }

    KOTLIN.audioProcessEvent((long) this, &sme);
}

tresult PLUGIN_API AudioProcessor::setState(IBStream *state) {
    IBStreamer stream(state, kLittleEndian);
    bool ok = KOTLIN.audioReadState((long) this, (long) &stream);
    return ok ? kResultTrue : kInternalError;
}

tresult PLUGIN_API AudioProcessor::getState(IBStream *state) {
    IBStreamer stream(state, kLittleEndian);
    bool ok = KOTLIN.audioWriteState((long) this, (long) &stream);
    return ok ? kResultTrue : kInternalError;
}

tresult PLUGIN_API AudioProcessor::setBusArrangements(
    SpeakerArrangement *inputs,
    int32 numIns,
    SpeakerArrangement *outputs,
    int32 numOuts
) {
    // We only support one stereo output bus. FIXME support mono as well
    if (numIns == 0 && numOuts == 1 && outputs[0] == SpeakerArr::kStereo) {
        return AudioEffect::setBusArrangements(inputs, numIns, outputs, numOuts);
    }
    return kResultFalse;
}

tresult PLUGIN_API AudioProcessor::canProcessSampleSize(int32 symbolicSampleSize) {
    // The sampleSize can only be either 32bit float or 64bit double. We support both.
    return (symbolicSampleSize == kSample32 || symbolicSampleSize == kSample64) ? kResultTrue : kResultFalse;
}

tresult PLUGIN_API AudioProcessor::setActive(TBool state) {
    if (state) {
        KOTLIN.audioSetActive((long) this, (double) processSetup.sampleRate);
    } else {
        KOTLIN.audioSetInactive((long) this);
    }

    return AudioEffect::setActive(state);
}

tresult PLUGIN_API AudioProcessor::process(ProcessData& data) {
    if (data.numOutputs < 1) {
        KOTLIN.audioFlush((long) this); // not sure when this happens, and what we should do here
    } else {
        auto &output = data.outputs[0]; // we always have one output only (we ensured this in setBusArrangements)

        if (data.inputEvents != nullptr) {
            int count = data.inputEvents->getEventCount();
            Event evt;

            for (int i = 0; i < count; i++) {
                if (data.inputEvents->getEvent(i, evt) == kResultTrue) {
                    handleEvent(evt);
                }
            }
        }

        cachedInputParamChanges = data.inputParameterChanges;
        cachedOutputParamChanges = data.outputParameterChanges;

        bool playing = KOTLIN.audioProcessBlock(
            (long) this,
            !data.inputParameterChanges ? 0 : data.inputParameterChanges->getParameterCount(),
            data.numSamples,
            (void *) output.channelBuffers32[0],
            (void *) output.channelBuffers32[1], // at the moment always stereo, see setBusArrangements
            processSetup.symbolicSampleSize == kSample32
        );

        if (!playing) {
            output.silenceFlags = 0x11; // left and right channel are silent
        }

        cachedInputParamChanges = nullptr;
        cachedOutputParamChanges = nullptr;
    }

    return kResultTrue;
}

/**
 * Called by Kotlin AudioProcessor to retrieve the UI parameter changes.
 */
SParamChange AudioProcessor::getInputParamChange(int idx) {
    SParamChange result { 0, 0, 0, 0 };

    if (cachedInputParamChanges != nullptr) {
        auto &chg = *cachedInputParamChanges;

        if (idx >= 0 && idx < chg.getParameterCount()) {
            IParamValueQueue *queue = chg.getParameterData(idx);

            if (queue) {
                int32 sampleOffset = 0;
                ParamValue value = 0;
                ParamID tag = queue->getParameterId();

                if (queue->getPoint(queue->getPointCount() - 1, sampleOffset, value) == kResultTrue) {
                    result.tag = tag;
                    result.value = value;
                    result.sampleOffset = sampleOffset;
                    result.valid = true;
                }
            }
        }
    }

    return result;
}

/**
 * Called by Kotlin AudioProcessor to tell the UI about parameter changes.
 */
void AudioProcessor::setOutputParamChange(int tag, float value) {
    if (cachedOutputParamChanges) {
        int32 index = 0;
        IParamValueQueue *queue = cachedOutputParamChanges->addParameterData(tag, index);

        if (queue) {
            queue->addPoint(0, value, index);
        }
    }
}
