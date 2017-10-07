package com.pj.magic.gui.component;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

@Component
public class ExcelFileFilter extends FileFilter {

	private static final String DESCRIPTION = "Excel workbook (*.xlsx)";
	private static final String EXCEL_FILE_EXTENSION = "xlsx";
	
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
	
	@Override
	public boolean accept(File f) {
		return FilenameUtils.getExtension(f.getName()).equals(EXCEL_FILE_EXTENSION);
	}
	
	private ExcelFileFilter() {}
	
	private static class LazyInitializationHolder {
		private static final ExcelFileFilter INSTANCE = new ExcelFileFilter();
	}
	
	public static ExcelFileFilter getInstance() {
		return LazyInitializationHolder.INSTANCE;
	}

}