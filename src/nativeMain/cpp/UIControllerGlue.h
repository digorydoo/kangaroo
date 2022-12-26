#pragma once

#ifdef __cplusplus
extern "C" {
#endif

void cppSendNoteOnEvent(long ptr, int pitch, float velo, int channel, float detune);
void cppSendNoteOffEvent(long ptr, int pitch, float velo, int channel);
void cppSendMIDICCOutEvent(long ptr, int ccNumber, int value, int value2, int channel);

void cppBeginParamEdit(long ptr, int tag);
void cppEndParamEdit(long ptr, int tag);
void cppSetParamNormalized(long ptr, int tag, float value, int performEdit);

int cppMakeParamFlags(
    int canAutomate,
    int isReadOnly,
    int isHidden,
    int isWrapAround,
    int isList,
    int isProgramChange,
    int isBypass
);

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
);

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
);

void cppAddStringListParam(
    long ptr,
    int tag,
    const char *title,
    const char *shortTitle,
    const char *units,
    int unitId,
    int flags,
    const char *items
);

#ifdef __cplusplus
}
#endif
