#include "vstgui/lib/ccolor.h"
#include "vstgui/lib/coffscreencontext.h"
#include "vstgui/lib/cgradient.h"
#include "vstgui/lib/cgraphicspath.h"
#include "CDrawContextGlue.h"

using namespace VSTGUI;

void cppSetLineWidth(long context, float width) {
    auto ctx = (CDrawContext *) context;

    if (width <= 1.0f) {
        ctx->setLineWidth(ctx->getHairlineSize());
    } else {
        ctx->setLineWidth(width);
    }
}

void cppSetGlobalAlpha(long context, float alpha) {
    auto ctx = (CDrawContext *) context;
    ctx->setGlobalAlpha(alpha);
}

void cppSetAntiAliasing(long context, int enabled) {
    auto ctx = (CDrawContext *) context;
    auto mode = ctx->getDrawMode()(); // I hate C++

    if (enabled) {
        mode = mode | kAntiAliasing;
    } else {
        mode = mode & ~kAntiAliasing;
    }

    ctx->setDrawMode(mode);
}

void cppSetFrameColor(long context, float red, float green, float blue) {
    auto ctx = (CDrawContext *) context;
    CColor c((int)(red * 255.0f), (int)(green * 255.0f), (int)(blue * 255.0f), 255);
    ctx->setFrameColor(c);
}

void cppSetFillColor(long context, float red, float green, float blue) {
    auto ctx = (CDrawContext *) context;
    CColor c((int)(red * 255.0f), (int)(green * 255.0f), (int)(blue * 255.0f), 255);
    ctx->setFillColor(c);
}

void cppSetFontColor(long context, float red, float green, float blue) {
    auto ctx = (CDrawContext *) context;
    CColor c((int)(red * 255.0f), (int)(green * 255.0f), (int)(blue * 255.0f), 255);
    ctx->setFontColor(c);
}

void cppSetFontSize(long context, float size) {
    auto ctx = (CDrawContext *) context;
    ctx->setFont(ctx->getFont(), size);
}

void cppDrawArc(
    long context,
    int left,
    int top,
    int right,
    int bottom,
    float startAngle,
    float endAngle
) {
    auto ctx = (CDrawContext *) context;
    auto r = CRect(left, top, right, bottom);
    float phi = (float)(startAngle * 180.0 / M_PI);
    float rho = (float)(endAngle * 180.0 / M_PI);
    ctx->drawArc(r, phi, rho, kDrawStroked);
}

void cppDrawPixel(long context, int x, int y, float opacity) {
    if (opacity <= 0) {
        return;
    }

    auto ctx = (CDrawContext *) context;
    auto mode = ctx->getDrawMode()();
    ctx->setDrawMode(mode & ~kAntiAliasing); // turn off anti-aliasing
    auto prevColour = ctx->getFillColor();
    auto colour = ctx->getFrameColor();
    colour.alpha = (int)(255.0f * opacity);
    ctx->setFillColor(colour);

    // ctx->drawPoint is buggy (draws more than one pixel, colour is sometimes too dark).
    // Unfortunately, drawLine has the exact same bug when anti-aliasing is turned off.
    // So we're using drawRect instead, although this is bound to be inefficient.
    auto r = CRect(x, y, x + 1, y + 1);
    ctx->setLineWidth(ctx->getHairlineSize());
    ctx->drawRect(r, kDrawFilled);

    ctx->setDrawMode(mode); // restore previous mode
    ctx->setFillColor(prevColour); // restore previous fill colour
}

void cppDrawLine(long context, int x1, int y1, int x2, int y2) {
    auto ctx = (CDrawContext *) context;
    auto pt1 = CPoint(x1, y1);
    auto pt2 = CPoint(x2, y2);
    ctx->drawLine(pt1, pt2); // Note that drawLine is buggy when anti-aliasing is turned off...
}

void cppDrawRect(long context, int left, int top, int right, int bottom) {
    auto ctx = (CDrawContext *) context;
    auto r = CRect(left, top, right, bottom);
    ctx->drawRect(r, kDrawStroked);
}

static CGraphicsPath *createRingPath(CDrawContext *ctx, const CRect &r, int thickness) {
    int rx1 = r.left;
    int rx2 = r.right;
    int ry1 = r.top;
    int ry2 = r.bottom;
    int ohx = (int)((rx2 - rx1) / 2);
    int ohy = (int)((ry2 - ry1) / 2);
    auto x1 = rx1 + 2.0 * ohx;
    auto x2 = rx2 - 2.0 * ohx;
    auto y1 = ry1 + 2.0 * ohy;
    auto y2 = ry2 - 2.0 * ohy;

    auto path = ctx->createGraphicsPath();
    path->beginSubpath(CPoint(rx2 - ohx, ry1));
    path->addArc(CRect(x2, ry1, rx2, y1), 270.0, 360.0, true);
    path->addArc(CRect(x2, y2, rx2, ry2), 0.0, 90.0, true);
    path->addArc(CRect(rx1, y2, x1, ry2), 90.0, 180.0, true);
    path->addArc(CRect(rx1, ry1, x1, y1), 180.0, 270.0, true);
    path->closeSubpath();

    rx1 += thickness;
    rx2 -= thickness;
    ry1 += thickness;
    ry2 -= thickness;
    ohx = (int)((rx2 - rx1) / 2);
    ohy = (int)((ry2 - ry1) / 2);
    x1 = rx1 + 2.0 * ohx;
    x2 = rx2 - 2.0 * ohx;
    y1 = ry1 + 2.0 * ohy;
    y2 = ry2 - 2.0 * ohy;

    path->beginSubpath(CPoint(rx2 - ohx, ry1));
    path->addArc(CRect(x2, ry1, rx2, y1), 270.0, 360.0, true);
    path->addArc(CRect(x2, y2, rx2, ry2), 0.0, 90.0, true);
    path->addArc(CRect(rx1, y2, x1, ry2), 90.0, 180.0, true);
    path->addArc(CRect(rx1, ry1, x1, y1), 180.0, 270.0, true);
    path->closeSubpath();

    return path;
}

void cppFillRing(long context, int left, int top, int right, int bottom, int thickness) {
    auto ctx = (CDrawContext *) context;
    auto path = createRingPath(ctx, CRect(left, top, right, bottom), thickness);
    ctx->drawGraphicsPath(path, CDrawContext::kPathFilledEvenOdd);
    path->forget();
}

void cppFillRingWithLinearGradient(
    long context,
    int left,
    int top,
    int right,
    int bottom,
    int thickness,
    long gradientPtr,
    int gradStartX,
    int gradStartY,
    int gradEndX,
    int gradEndY
) {
    auto ctx = (CDrawContext *) context;
    auto g = (CGradient *) gradientPtr;
    auto path = createRingPath(ctx, CRect(left, top, right, bottom), thickness);
    CPoint startPt(gradStartX, gradStartY);
    CPoint endPt(gradEndX, gradEndY);
    ctx->fillLinearGradient(path, *g, startPt, endPt, true /* evenOdd */);
    path->forget();
}

static CGraphicsPath *createRoundRectPath(
    CDrawContext *ctx,
    const CRect &r,
    const CPoint &corner,
    bool hasTopLeftCorner,
    bool hasTopRightCorner,
    bool hasBottomLeftCorner,
    bool hasBottomRightCorner
) {
    auto path = ctx->createGraphicsPath();
    auto x1 = r.left + 2.0 * corner.x;
    auto x2 = r.right - 2.0 * corner.x;
    auto y1 = r.top + 2.0 * corner.y;
    auto y2 = r.bottom - 2.0 * corner.y;

    path->beginSubpath(CPoint(r.right - (hasTopRightCorner ? corner.x : 0), r.top));

    if (hasTopRightCorner) {
        path->addArc(CRect(x2, r.top, r.right, y1), 270.0, 360.0, true);
    }

    if (hasBottomRightCorner) {
        path->addArc(CRect(x2, y2, r.right, r.bottom), 0.0, 90.0, true);
    } else {
        path->addLine(CPoint(r.right, r.bottom));
    }

    if (hasBottomLeftCorner) {
        path->addArc(CRect(r.left, y2, x1, r.bottom), 90.0, 180.0, true);
    } else {
        path->addLine(CPoint(r.left, r.bottom));
    }

    if (hasTopLeftCorner) {
        path->addArc(CRect(r.left, r.top, x1, y1), 180.0, 270.0, true);
    } else {
        path->addLine(CPoint(r.left, r.top));
    }

    path->closeSubpath();
    return path;
}

void cppDrawRoundRect(
    long context,
    int left,
    int top,
    int right,
    int bottom,
    int cornerX,
    int cornerY,
    int hasTopLeftCorner,
    int hasTopRightCorner,
    int hasBottomLeftCorner,
    int hasBottomRightCorner
) {
    auto ctx = (CDrawContext *) context;
    auto path = createRoundRectPath(
        ctx,
        CRect(left, top, right, bottom),
        CPoint(cornerX, cornerY),
        hasTopLeftCorner,
        hasTopRightCorner,
        hasBottomLeftCorner,
        hasBottomRightCorner
    );
    ctx->drawGraphicsPath(path, CDrawContext::kPathStroked);
    path->forget();
}

void cppFillRoundRect(
    long context,
    int left,
    int top,
    int right,
    int bottom,
    int cornerX,
    int cornerY,
    int hasTopLeftCorner,
    int hasTopRightCorner,
    int hasBottomLeftCorner,
    int hasBottomRightCorner
) {
    auto ctx = (CDrawContext *) context;
    auto path = createRoundRectPath(
        ctx,
        CRect(left, top, right, bottom),
        CPoint(cornerX, cornerY),
        hasTopLeftCorner,
        hasTopRightCorner,
        hasBottomLeftCorner,
        hasBottomRightCorner
    );
    ctx->drawGraphicsPath(path, CDrawContext::kPathFilled);
    path->forget();
}

void cppDrawEllipse(long context, int left, int top, int right, int bottom) {
    auto ctx = (CDrawContext *) context;
    ctx->drawEllipse(CRect(left, top, right, bottom), kDrawStroked);
}

void cppDrawText(long context, const char *s, int x, int y) {
    auto ctx = (CDrawContext *) context;
    ctx->drawString(s, CPoint(x, y), true);
}

int cppMeasureText(long context, const char *s) {
    auto ctx = (CDrawContext *) context;
    return ctx->getStringWidth(s);
}

void cppFillRect(long context, int left, int top, int right, int bottom) {
    auto ctx = (CDrawContext *) context;
    ctx->drawRect(CRect(left, top, right, bottom), kDrawFilled);
}

void cppFillEllipse(long context, int left, int top, int right, int bottom) {
    auto ctx = (CDrawContext *) context;
    ctx->drawEllipse(CRect(left, top, right, bottom), kDrawFilled);
}

void cppSetClipRect(long context, int left, int top, int right, int bottom) {
    auto ctx = (CDrawContext *) context;
    ctx->setClipRect(CRect(left, top, right, bottom));
}

void cppResetClipRect(long context) {
    auto ctx = (CDrawContext *) context;
    ctx->resetClipRect();
}

void cppDrawBitmap(long context, long bmpPtr, int left, int top, int right, int bottom) {
    auto ctx = (CDrawContext *) context;
    auto bmp = (CBitmap *) bmpPtr;
    CRect r(left, top, right, bottom);
    ctx->drawBitmap(bmp, r, CPoint(0, 0), 1.0f);
}
