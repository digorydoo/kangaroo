#pragma once

#include "vstgui/uidescription/iviewcreator.h"

class CanvasViewCreator : public VSTGUI::ViewCreatorAdapter
{
public:
    CanvasViewCreator();

	VSTGUI::IdStringPtr getBaseViewName() const override;
	VSTGUI::IdStringPtr getViewName() const override { return "CanvasView"; }
	VSTGUI::UTF8StringPtr getDisplayName() const override { return "Canvas"; }

	VSTGUI::CView *create(const VSTGUI::UIAttributes& attr, const VSTGUI::IUIDescription* desc) const override;
};
