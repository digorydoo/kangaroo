#pragma once

#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
    int tag;
    float value;
    int sampleOffset;
    int valid;
} SParamChange;

enum {
    UNSUPPORTED_EVENT = 0,
    NOTE_ON_EVENT,
    NOTE_OFF_EVENT,
    CC_OUT_EVENT
};

typedef struct {
    unsigned int type;
    int busIndex;
    int sampleOffset;
    double ppqPosition;
    int isLive;
    int user1Bit;
    int user2Bit;
    int channel;
    int pitch;
    float tuning;
    float velocity;
    int noteLength;
    int noteId;
    int ccNumber;
    int ccValue;
    int ccValue2;
    // int dataType;
    // int dataSize;
    // const unsigned char *dataPtr;
    // float pressure;
} SMidiEvent;

SParamChange cppGetInputParamChange(long ptr, int idx);
void cppSetOutputParamChange(long ptr, int tag, float value);

#ifdef __cplusplus
}
#endif
