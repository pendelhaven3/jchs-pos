package com.pj.magic.util;

import java.io.File;
import java.io.IOException;

public class ExcelUtil {

	public static void openExcelFile(File file) throws IOException {
		String[] cmdarray = new String[]{"cmd.exe", "/c", file.getAbsolutePath()}; 
		Runtime.getRuntime().exec(cmdarray);
	}
	
}
