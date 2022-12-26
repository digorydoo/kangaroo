#pragma once

#ifdef __cplusplus
extern "C" {
#endif

void cppSetLineWidth(long ctx, float width);
// void setLineStyle(const CLineStyle& style);
void cppSetGlobalAlpha(long ctx, float alpha);
void cppSetAntiAliasing(long ctx, int enabled);
void cppSetFrameColor(long ctx, float red, float green, float blue);
void cppSetFillColor(long ctx, float red, float green, float blue);
void cppSetFontColor(long ctx, float red, float green, float blue);
void cppSetFontSize(long ctx, float size);

void cppDrawArc(long ctx, int left, int top, int right, int bottom, float startAngle, float endAngle);
void cppDrawLine(long ctx, int x1, int y1, int x2, int y2);
void cppDrawPixel(long ctx, int x, int y, float opacity);
void cppDrawRect(long ctx, int left, int top, int right, int bottom);
void cppDrawRoundRect(long ctx, int left, int top, int right, int bottom, int cornerX, int cornerY,
    int hasTopLeftCorner, int hasTopRightCorner, int hasBottomLeftCorner, int hasBottomRightCorner);
void cppDrawEllipse(long ctx, int left, int top, int right, int bottom);

void cppFillRect(long ctx, int left, int top, int right, int bottom);
void cppFillRoundRect(long ctx, int left, int top, int right, int bottom, int cornerX, int cornerY,
    int hasTopLeftCorner, int hasTopRightCorner, int hasBottomLeftCorner, int hasBottomRightCorner);
void cppFillEllipse(long ctx, int left, int top, int right, int bottom);
void cppFillRing(long ctx, int left, int top, int right, int bottom, int thickness);

void cppFillRingWithLinearGradient(
    long ctx,
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
);

// void fillLinearGradient (CGraphicsPath* path, const CGradient& gradient, const CPoint& startPoint, const CPoint& endPoint, bool
//   evenOdd = false, CGraphicsTransform* transformation = nullptr)
// void fillRadialGradient (CGraphicsPath* path, const CGradient& gradient, const CPoint& center, CCoord radius, const CPoint&
//   originOffset = CPoint (0,0), bool evenOdd = false, CGraphicsTransform* transformation = nullptr)

void cppDrawText(long ctx, const char *s, int x, int y);
int cppMeasureText(long ctx, const char *s);

void cppSetClipRect(long ctx, int left, int top, int right, int bottom);
void cppResetClipRect(long ctx);

void cppDrawBitmap(long ctx, long bmpPtr, int left, int top, int right, int bottom);
// void drawBitmapNinePartTiled(CBitmap* bitmap, const CRect& dest, const CNinePartTiledDescription& desc, float alpha = 1.f);
// void fillRectWithBitmap(CBitmap* bitmap, const CRect& srcRect, const CRect& dstRect, float alpha);

// void saveGlobalState();
// void restoreGlobalState();

#ifdef __cplusplus
}
#endif
