package com.pj.magic.model.util;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

import com.pj.magic.Constants;

public class CellStyleBuilder {
	
	private Workbook workbook;
	private Short align;
    private Short verticalAlign;
    private Short borderTop;
	private Short borderBottom;
	private Short borderRight;
	private Boolean amountFormat;
	private Font font;
	private Boolean fullBorderThin;
	private Boolean underline;
    private Boolean text;
    private Boolean dateFormat;
    private Boolean bold;
	
	public static CellStyleBuilder createStyle(Workbook workbook) {
		return new CellStyleBuilder(workbook);
	}
	
	private CellStyleBuilder(Workbook workbook) {
		this.workbook = workbook;
	}
	
	public CellStyleBuilder setAlignment(short align) {
		this.align = align;
		return this;
	}
	
	public CellStyleBuilder setVerticalAlignment(short verticalAlign) {
		this.verticalAlign = verticalAlign;
		return this;
	}
	
	public CellStyle build() {
		CellStyle style = workbook.createCellStyle();
		if (align != null) {
			style.setAlignment(align);
		}
		if (verticalAlign != null) {
			style.setVerticalAlignment(verticalAlign);
		}
        if (borderTop != null) {
            style.setBorderTop(borderTop);
        }
		if (borderBottom != null) {
			style.setBorderBottom(borderBottom);
		}
		if (borderRight != null) {
			style.setBorderRight(borderRight);
		}
		if (amountFormat != null && amountFormat) {
			style.setDataFormat(workbook.createDataFormat().getFormat(Constants.AMOUNT_FORMAT));
		}
        if (text != null && text) {
            style.setDataFormat(workbook.createDataFormat().getFormat("@"));
        }
        if (dateFormat != null && dateFormat) {
            style.setDataFormat(workbook.createDataFormat().getFormat("m/d/yy"));
        }
		if (fullBorderThin != null) {
			style.setBorderLeft(CellStyle.BORDER_THIN);
			style.setBorderTop(CellStyle.BORDER_THIN);
			style.setBorderRight(CellStyle.BORDER_THIN);
			style.setBorderBottom(CellStyle.BORDER_THIN);
		}
		
        if (bold != null && bold) {
            if (font == null) {
                font = workbook.createFont();
            }
            font.setBold(true);
        }
		if (underline != null && underline) {
		    if (font == null) {
		        font = workbook.createFont();
		    }
		    font.setUnderline(Font.U_SINGLE);
		}
        if (font != null) {
            style.setFont(font);
        }
        
		return style;
	}

	public CellStyleBuilder setBorderBottom(short borderBottom) {
		this.borderBottom = borderBottom;
		return this;
	}

	public CellStyleBuilder setBorderRight(short borderRight) {
		this.borderRight = borderRight;
		return this;
	}

	public CellStyleBuilder setAmountFormat(boolean amountFormat) {
		this.amountFormat = amountFormat;
		return this;
	}
	
	public CellStyleBuilder setFont(Font font) {
		this.font = font;
		return this;
	}
	
	public CellStyleBuilder setFullBorderThin(Boolean fullBorderThin) {
		this.fullBorderThin = fullBorderThin;
		return this;
	}
	
	public CellStyleBuilder setUnderline(boolean underline) {
	    this.underline = underline;
	    return this;
	}
	
    public CellStyleBuilder setText(boolean text) {
        this.text = text;
        return this;
    }
	
    public CellStyleBuilder setDateFormat(boolean dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }
    
    public CellStyleBuilder setBold(boolean bold) {
        this.bold = bold;
        return this;
    }
    
    public CellStyleBuilder setBorderTop(short borderTop) {
        this.borderTop = borderTop;
        return this;
    }
    
}