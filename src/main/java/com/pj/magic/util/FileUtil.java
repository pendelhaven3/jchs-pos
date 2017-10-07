package com.pj.magic.util;

import java.io.File;
import java.nio.file.Paths;

import com.pj.magic.gui.component.ExcelFileFilter;
import com.pj.magic.gui.component.MagicFileChooser;

public class FileUtil {

	public static String getDesktopFolderPath() {
		return Paths.get(System.getProperty("user.home"), "Desktop").toAbsolutePath().toString();
	}
	
	public static File getDesktopFolderPathAsFile() {
		return new File(getDesktopFolderPath());
	}
	
	public static MagicFileChooser createSaveFileChooser(String filename) {
		MagicFileChooser fileChooser = new MagicFileChooser();
		fileChooser.setCurrentDirectory(FileUtil.getDesktopFolderPathAsFile());
		fileChooser.setFileFilter(ExcelFileFilter.getInstance());
		fileChooser.setSelectedFile(new File(filename));
		return fileChooser;
	}
	
}