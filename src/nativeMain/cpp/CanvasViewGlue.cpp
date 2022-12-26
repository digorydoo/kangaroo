#include "vstgui/lib/cgradient.h"
#include "CanvasViewGlue.h"
#include "CanvasView.h"

using namespace VSTGUI;

void cppInvalidRect(long ptr, int left, int top, int right, int bottom) {
    auto v = (CanvasView *) ptr;
    v->invalidRect(CRect(left, top, right, bottom));
}

long cppGetBitmapCached(long ptr, const char *name) {
    auto v = (CanvasView *) ptr;
    return (long) v->getBitmapCached(name);
}

long cppCreateGradient(
    long viewPtr,
    float stop0Value,
    float stop0Red,
    float stop0Green,
    float stop0Blue,
    float stop1Value,
    float stop1Red,
    float stop1Green,
    float stop1Blue
) {
    auto v = (CanvasView *) viewPtr;
    CColor c0((int)(stop0Red * 255.0f), (int)(stop0Green * 255.0f), (int)(stop0Blue * 255.0f), 255);
    CColor c1((int)(stop1Red * 255.0f), (int)(stop1Green * 255.0f), (int)(stop1Blue * 255.0f), 255);
    auto g = CGradient::create(stop0Value, stop1Value, c0, c1);
    v->rememberGradient(*g);
    g->forget(); // g is still valid, as v has incremented its ref count
    return (long) g;
}

void cppAddGradientStop(
    long gradientPtr, // sic!
    float stopValue,
    float red,
    float green,
    float blue
) {
    auto g = (CGradient *) gradientPtr;
    CColor c((int)(red * 255.0f), (int)(green * 255.0f), (int)(blue * 255.0f), 255);
    g->addColorStop(stopValue, c);
}
