package com.pj.magic.gui.component;

import com.pj.magic.model.Unit;

public class UnitComboBox extends MagicComboBox<String> {

	public UnitComboBox() {
		super(Unit.values());
	}
	
}
