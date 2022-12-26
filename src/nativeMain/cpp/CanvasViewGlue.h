#pragma once

#ifdef __cplusplus
extern "C" {
#endif

void cppInvalidRect(long ptr, int left, int top, int right, int bottom);

long cppGetBitmapCached(long ptr, const char *name);

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
);

void cppAddGradientStop(
    long gradientPtr, // sic!
    float stopValue,
    float red,
    float green,
    float blue
);

// bool doDrag(const DragDescription& dragDescription, const SharedPointer<IDragCallback>& callback = {})
// SharedPointer<IDropTarget> getDropTarget()
// void setDropTarget(const SharedPointer<IDropTarget> &dt)
// const CRect &getViewSize()

// Idle Methods
// Should be used when a view needs to do a task periodically.
// The onIdle() method will be called only if the view is attached.
// void onIdle()
// void setWantsIdle(bool state)
// bool wantsIdle()
// uint32_t idleRate = 30
// maybe better: cvstguitimer.h Call::later(lambda, time);

// via view->getFrame(): (CView *)
// int32_t getKnobMode()
// void scrollRect(const CRect& src, const CPoint& distance)
// bool performDrag(const DragDescription& desc, const SharedPointer<IDragCallback>& callback)
// Optional<ModalViewSessionID> beginModalViewSession(CView* view)
// bool endModalViewSession(ModalViewSessionID session)
// void setFocusView(CView* pView)

#ifdef __cplusplus
}
#endif
