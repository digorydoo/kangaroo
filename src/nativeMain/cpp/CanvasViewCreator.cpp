#include "vstgui/uidescription/detail/uiviewcreatorattributes.h"
#include "vstgui/uidescription/uiviewfactory.h"
#include "CanvasView.h"
#include "CanvasViewCreator.h"
#include "utils.h"

using namespace VSTGUI;

static CanvasViewCreator gCanvasViewCreator;

CanvasViewCreator::CanvasViewCreator() {
    UIViewFactory::registerViewCreator(*this);
}

CView *CanvasViewCreator::create(const UIAttributes &attrs, const IUIDescription *desc) const {
    return new CanvasView();
}

IdStringPtr CanvasViewCreator::getBaseViewName() const {
    return UIViewCreator::kCView;
}
