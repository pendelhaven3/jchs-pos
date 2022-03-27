package com.pj.magic.report.pdf;

import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.TextRenderer;

public class CustomTextRenderer extends TextRenderer {

	public CustomTextRenderer(Text textElement) {
		super(textElement);
	}
	
    @Override
    public IRenderer getNextRenderer() {
        return new CustomTextRenderer((Text) getModelElement());
    }
    
	@Override
	public void trimFirst() {}

}
