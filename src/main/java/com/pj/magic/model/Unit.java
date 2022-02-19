package com.pj.magic.model;

import java.util.HashMap;
import java.util.Map;

public class Unit {

	public static final String CASE = "CASE";
	public static final String TIES = "TIES";
	public static final String PACK = "PACK";
	public static final String HDZN = "HDZN";
	public static final String PIECES = "PCS";

	private static final Map<String, Integer> compareMap = new HashMap<>();
	
	static {
		compareMap.put(CASE, 5);
		compareMap.put(TIES, 4);
		compareMap.put(PACK, 3);
		compareMap.put(HDZN, 2);
		compareMap.put(PIECES, 1);
	}
	
	private Unit() {
		// Can never be instantiated. This class is only for the constants.
	}
	
    /** 
     * Compare two units.
     * 
     * @return <code>-1</code> if unit1 is less than unit2<br/>
     *          <code>1</code> if unit1 is greater than unit2<br/>
     *          <code>0</code> if unit1 is the same as unit2
    */
	public static int compare(String unit1, String unit2) {
		int value1 = compareMap.get(unit1);
		int value2 = compareMap.get(unit2);
		
		if (value1 < value2) {
			return -1;
		} else if (value1 > value2) {
			return 1;
		} else {
			return 0;
		}
	}

	public static String[] values() {
		return new String[] {null, Unit.PIECES, Unit.HDZN, Unit.PACK, Unit.TIES, Unit.CASE};
	}
	
}
