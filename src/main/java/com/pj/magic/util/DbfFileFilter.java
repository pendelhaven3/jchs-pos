package com.pj.magic.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;

public class DbfFileFilter extends FileFilter {

	private static final String DESCRIPTION = "DBF (*.dbf)";
	private static final String DBF_FILE_EXTENSION = "dbf";
	
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

	@Override
	public boolean accept(File f) {
		return FilenameUtils.getExtension(f.getName()).equalsIgnoreCase(DBF_FILE_EXTENSION);
	}
	
}
