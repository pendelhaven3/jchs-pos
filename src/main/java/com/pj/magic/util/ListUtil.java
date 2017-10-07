package com.pj.magic.util;

import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

public class ListUtil {

	public static <T extends Comparable<T>> List<T> asSortedList(List<T> list) {
		Collections.sort(list);
		return list;
	}

	public static <T> DefaultComboBoxModel<T> toDefaultComboBoxModel(List<T> list) {
		return toDefaultComboBoxModel(list, false);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> DefaultComboBoxModel<T> toDefaultComboBoxModel(List<T> list, boolean insertDefaultNull) {
		DefaultComboBoxModel<T> model = new DefaultComboBoxModel<>((T[])list.toArray());
		if (insertDefaultNull) {
			model.insertElementAt(null, 0);
		}
		return model;
	}
	
}