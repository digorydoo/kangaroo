#include "AudioProcessor.h"
#include "AudioProcessorGlue.h"

SParamChange cppGetInputParamChange(long ptr, int idx) {
    auto processor = (AudioProcessor *) ptr;
    return processor->getInputParamChange(idx);
}

void cppSetOutputParamChange(long ptr, int tag, float value) {
    auto processor = (AudioProcessor *) ptr;
    processor->setOutputParamChange(tag, value);
}
