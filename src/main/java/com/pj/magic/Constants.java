package com.pj.magic;

import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;

import javax.swing.plaf.FontUIResource;

public class Constants {

	public static final int PRODUCT_CODE_MAXIMUM_LENGTH = 12;
	public static final int UNIT_MAXIMUM_LENGTH = 3;
	public static final int QUANTITY_MAXIMUM_LENGTH = 5;
	public static final String DATE_FORMAT = "MM/dd/yyyy";
	public static final String DATE_FORMAT_FOR_URL_VALUE = "MM-dd-yyyy";
	public static final String DATETIME_FORMAT = "MM/dd/yyyy h:mm aa";
	public static final String TIME_FORMAT = "h:mm aa";
	public static final String AMOUNT_FORMAT = "#,##0.00";
	public static final String INTEGER_FORMAT = "#,##0";
	public static final Color PASTEL_BLUE = new Color(204, 229, 255);
	public static final String ENTER_KEY_ACTION_NAME = "onEnterKey";
	public static final String ESCAPE_KEY_ACTION_NAME = "onEscapeKey";
	public static final String DELETE_KEY_ACTION_NAME = "onDeleteKey";
	public static final String F4_KEY_ACTION_NAME = "onF4Key";
	public static final String F5_KEY_ACTION_NAME = "onF5Key";
	public static final String F9_KEY_ACTION_NAME = "onF9Key";
	public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2); // TODO: migrate references to this
	public static final BigDecimal ONE = BigDecimal.ONE.setScale(2);
	public static final BigDecimal ONE_HUNDRED = new BigDecimal("100").setScale(2);
	public static final BigDecimal FIVE_CENTS = new BigDecimal("0.05");
	public static final long CANVASSER_PRICING_SCHEME_ID = 1L;
	public static final int CUSTOMER_CODE_MAXIMUM_LENGTH = 12;
	public static final int SUPPLIER_CODE_MAXIMUM_LENGTH = 12;
	
	public static final String UNEXPECTED_ERROR_MESSAGE = "Unexpected error occurred";
	
	public static final Font SUBMENU_FONT = new FontUIResource("Arial", Font.BOLD, 24);
	
}