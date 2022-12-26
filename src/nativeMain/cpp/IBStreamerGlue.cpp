#include "base/source/fstreamer.h"
#include "IBStreamerGlue.h"

int cppWriteInt16(long ptr, int value) {
    auto streamer = (Steinberg::IBStreamer *) ptr;
    return streamer->writeInt16((Steinberg::int16) value);
}

int cppWriteDouble(long ptr, double value) {
    auto streamer = (Steinberg::IBStreamer *) ptr;
    return streamer->writeDouble(value);
}

ReadInt16Result cppReadInt16(long ptr) {
    auto streamer = (Steinberg::IBStreamer *) ptr;
    ReadInt16Result result;
    Steinberg::int16 tmp = 0;
    result.ok = streamer->readInt16(tmp);
    result.value = (int) tmp;
    return result;
}

ReadDoubleResult cppReadDouble(long ptr) {
    auto streamer = (Steinberg::IBStreamer *) ptr;
    ReadDoubleResult result;
    result.ok = streamer->readDouble(result.value);
    return result;
}
