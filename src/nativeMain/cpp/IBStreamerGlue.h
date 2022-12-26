#pragma once

#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
    int value;
    int ok;
} ReadInt16Result;

typedef struct {
    double value;
    int ok;
} ReadDoubleResult;

int cppWriteInt16(long ptr, int value);
int cppWriteDouble(long ptr, double value);

ReadInt16Result cppReadInt16(long ptr);
ReadDoubleResult cppReadDouble(long ptr);

#ifdef __cplusplus
}
#endif
