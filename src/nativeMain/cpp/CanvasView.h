#pragma once

#include <vector>
#include <map>
#include "vstgui/lib/cview.h"

namespace VSTGUI { class CGradient; }

class CanvasView : public VSTGUI::CView {
public:
    CanvasView();
    ~CanvasView();

    bool attached(VSTGUI::CView *parent) override;
    bool removed(VSTGUI::CView *parent) override;

    void drawRect(VSTGUI::CDrawContext* context, const VSTGUI::CRect &dirtyRect) override;

    void setViewSize(const VSTGUI::CRect &rect, bool invalid = true) override;
    // void parentSizeChanged() override;

    void onMouseDownEvent(VSTGUI::MouseDownEvent &event) override;
    void onMouseUpEvent(VSTGUI::MouseUpEvent &event) override;
    void onMouseMoveEvent(VSTGUI::MouseMoveEvent &event) override;
    void onMouseCancelEvent(VSTGUI::MouseCancelEvent &event) override;
    // void onMouseWheelEvent(VSTGUI::MouseWheelEvent &event) override;
    // void onZoomGestureEvent(VSTGUI::ZoomGestureEvent &event) override;

	void onKeyboardEvent(VSTGUI::KeyboardEvent &event) override;

    // maybe better: cvstguitimer.h Call::later(lambda, time);
    // void onIdle() override;
    // call setWantsIdle(true); idleRate = 10;

    // Called from Kotlin
    VSTGUI::CBitmap *getBitmapCached(const char *name);
    void rememberGradient(VSTGUI::CGradient &g);

private:
    std::map<std::string, VSTGUI::CBitmap *> bitmaps;
    std::vector<VSTGUI::CGradient *> gradients;
};
