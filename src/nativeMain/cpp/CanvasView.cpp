#include "vstgui/lib/cbitmap.h"
#include "vstgui/lib/cframe.h"
#include "vstgui/lib/cgradient.h"
#include "vstgui/lib/coffscreencontext.h"
#include "vstgui/lib/events.h"
#include "CanvasView.h"
#include "KotlinSym.h"

using namespace VSTGUI;

#define super CView

CanvasView::CanvasView(): CView(CRect(0, 0, 0, 0)) {
    fprintf(stdout, "CanvasView ctor\n");

    if (sizeof(long) < 8) {
        fprintf(stdout, "ERROR: sizeof(long) is only %lu\n", sizeof(long));
    }

    setWantsFocus(true);
    KOTLIN.createCanvasView((long) this);
}

CanvasView::~CanvasView() {
    KOTLIN.destroyCanvasView((long) this);

    fprintf(stdout, "CanvasView: The number of bitmaps was %lu.\n", bitmaps.size());

    for (const auto& [name, bmp] : bitmaps) {
        bmp->forget(); // decrement ref count
    }

    fprintf(stdout, "CanvasView: The number of gradients was %lu.\n", gradients.size());

    if (gradients.size() > 100) {
        fprintf(stdout, "Warning: CanvasView: The number of gradients is rather large. Are you leaking them?\n");
    }

    for (CGradient *g : gradients) {
        g->forget(); // decrement ref count
    }
}

bool CanvasView::attached(CView *parent) {
    bool ret = super::attached(parent);
    // fprintf(stdout, "CanvasView::attached\n");
    auto frame = getFrame();
    frame->setFocusView(this); // to get keyboard input
    return ret;
}

bool CanvasView::removed(CView *parent) {
    bool ret = super::removed(parent);
    // fprintf(stdout, "CanvasView::removed\n");
    return ret;
}

void CanvasView::setViewSize(const CRect &rect, bool invalid) {
    super::setViewSize(rect, invalid);
    // setMouseableArea(r); necessary?
    fprintf(stdout, "CanvasView::setViewSize\n");

    if (isAttached()) {
        KOTLIN.uiSetViewSize((long) this, rect.left, rect.top, rect.right, rect.bottom);
    }
}

void CanvasView::drawRect(CDrawContext *context, const CRect &dirtyRect) {
    CRect r = getViewSize();
    context->beginDraw();
    KOTLIN.uiOnPaint(
        (long) this,
        (long) context,
        r.left,
        r.top,
        r.right,
        r.bottom,
        dirtyRect.left,
        dirtyRect.top,
        dirtyRect.right,
        dirtyRect.bottom
    );
    context->endDraw();
}

void CanvasView::onMouseDownEvent(MouseDownEvent &event) {
    auto frame = getFrame();
    frame->setFocusView(this);

    int x = (int) event.mousePosition.x;
    int y = (int) event.mousePosition.y;
    bool shift = event.modifiers.has(ModifierKey::Shift);
    bool alt = event.modifiers.has(ModifierKey::Alt);
    bool ctrl = event.modifiers.has(ModifierKey::Control);
    bool meta = event.modifiers.has(ModifierKey::Super);
    // Ctrl and Super are swapped under macOS. Moreover, Ctrl+Click usually opens the context menu.
    // The plugin should not need to distinguish this, so we treat all three of them as ALT.
    KOTLIN.uiOnMouseDownEvent((long) this, x, y, event.clickCount, shift, alt || ctrl || meta);
    event.consumed = true; // otherwise we don't get the up event
}

void CanvasView::onMouseMoveEvent(MouseMoveEvent &event) {
    int x = (int) event.mousePosition.x;
    int y = (int) event.mousePosition.y;
    bool shift = event.modifiers.has(ModifierKey::Shift);
    bool alt = event.modifiers.has(ModifierKey::Alt);
    bool ctrl = event.modifiers.has(ModifierKey::Control);
    bool meta = event.modifiers.has(ModifierKey::Super);
    // Ctrl and Super are swapped under macOS. Moreover, Ctrl+Click usually opens the context menu.
    // The plugin should not need to distinguish this, so we treat all three of them as ALT.
    KOTLIN.uiOnMouseMoveEvent((long) this, x, y, shift, alt || ctrl || meta);
    event.consumed = true; // otherwise we don't get the up event
}

void CanvasView::onMouseUpEvent(MouseUpEvent &event) {
    int x = (int) event.mousePosition.x;
    int y = (int) event.mousePosition.y;
    bool shift = event.modifiers.has(ModifierKey::Shift);
    bool alt = event.modifiers.has(ModifierKey::Alt);
    bool ctrl = event.modifiers.has(ModifierKey::Control);
    bool meta = event.modifiers.has(ModifierKey::Super);
    // Ctrl and Super are swapped under macOS. Moreover, Ctrl+Click usually opens the context menu.
    // The plugin should not need to distinguish this, so we treat all three of them as ALT.
    KOTLIN.uiOnMouseUpEvent((long) this, x, y, shift, alt || ctrl || meta);
    event.consumed = true;
}

void CanvasView::onMouseCancelEvent(MouseCancelEvent &event) {
    KOTLIN.uiOnMouseCancelEvent((long) this);
    event.consumed = true;
}

void CanvasView::onKeyboardEvent(KeyboardEvent &event) {
    bool ctrl = event.modifiers.has(ModifierKey::Control); // swapped with Super on macOS
    bool meta = event.modifiers.has(ModifierKey::Super); // swapped with Control on macOS

    if (ctrl || meta) {
        return;
    }

    bool down = event.type == EventType::KeyDown;
    bool shift = event.modifiers.has(ModifierKey::Shift);
    bool alt = event.modifiers.has(ModifierKey::Alt);

    event.consumed = KOTLIN.uiOnKeyboardEvent(
        (long) this,
        (long) event.character,
        (int) event.virt,
        down,
        event.isRepeat,
        shift,
        alt
    );
}

VSTGUI::CBitmap *CanvasView::getBitmapCached(const char *name) {
    auto it = bitmaps.find(name);

    if (it != bitmaps.end()) {
        return it->second;
    }

    auto desc = VSTGUI::CResourceDescription(name);
    auto bmp = new VSTGUI::CBitmap(desc);
    bmp->remember();
    bitmaps[name] = bmp;
    return bmp;
}

/*
    We could also create off-screens, like this:
    auto offscreen = COffscreenContext::create(CPoint(width, geight));
    offscreen->beginDraw();
    whiteKeyBitmap->draw(offscreen, r);
    offscreen->endDraw();
*/

void CanvasView::rememberGradient(CGradient &g) {
    g.remember(); // increment ref count
    gradients.push_back(&g);
}
