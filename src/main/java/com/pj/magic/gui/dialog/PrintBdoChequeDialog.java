package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.util.ComponentUtil;
import com.pj.magic.util.ExcelUtil;
import com.pj.magic.util.FormatterUtil;
import com.pj.magic.util.NumberUtil;

import net.sourceforge.jdatepicker.impl.UtilCalendarModel;

public class PrintBdoChequeDialog extends MagicDialog {

	private static final Logger LOGGER = LoggerFactory.getLogger(PrintBdoChequeDialog.class);
	
	private MagicTextField nameField;
	private MagicTextField amountField;
	private UtilCalendarModel dateModel;
	private JButton printButton;
	
	public PrintBdoChequeDialog() {
		setSize(800, 200);
		setLocationRelativeTo(null);
		setTitle("Print BDO Cheque");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
		initialize();
	}

	public void initialize() {
		initializeComponents();
		layoutComponents();
	}

	private void initializeComponents() {
		nameField = new MagicTextField();
		amountField = new MagicTextField();
		dateModel = new UtilCalendarModel();
		
		printButton = new JButton("Print");
		printButton.addActionListener(e -> printCheque());
		
		focusOnComponentWhenThisPanelIsDisplayed(nameField);
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		// nothing
	}
	
	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(140, "Date:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		
		add(ComponentUtil.createDatePicker(dateModel), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(140, "Name:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		nameField.setPreferredSize(new Dimension(500, 25));
		add(nameField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(140, "Amount:"), c);

		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		amountField.setPreferredSize(new Dimension(150, 25));
		add(amountField, c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.CENTER;
		add(Box.createVerticalStrut(5), c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		printButton.setPreferredSize(new Dimension(100, 25));
		add(printButton, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createGlue(), c);
	}
	
	public void updateDisplay(PurchasePayment purchasePayment) {
		nameField.setText(purchasePayment.getSupplier().getName());
		amountField.setText(FormatterUtil.formatAmount(purchasePayment.getTotalAmountDue()));
		
		dateModel.setValue(null);
		dateModel.setValue(Calendar.getInstance());
	}
	
	private void printCheque() {
		try (
			InputStream templateStream = getTemplateStream();
			Workbook workbook = new XSSFWorkbook(templateStream);
		) {
			try {
				Sheet sheet = workbook.getSheetAt(0);

				Row row = sheet.getRow(1);
				String formattedChequeDate = FormatterUtil.formatChequeDate(dateModel.getValue().getTime());
				row.getCell(7).setCellValue(formattedChequeDate.substring(0, 1));
				row.getCell(8).setCellValue(formattedChequeDate.substring(1, 2));
				row.getCell(10).setCellValue(formattedChequeDate.substring(3, 4));
				row.getCell(11).setCellValue(formattedChequeDate.substring(4, 5));
				row.getCell(13).setCellValue(formattedChequeDate.substring(6, 7));
				row.getCell(14).setCellValue(formattedChequeDate.substring(7, 8));
				row.getCell(15).setCellValue(formattedChequeDate.substring(8, 9));
				row.getCell(16).setCellValue(formattedChequeDate.substring(9, 10));
				
				row = sheet.getRow(2);
				row.getCell(1).setCellValue(nameField.getText());
				row.getCell(7).setCellValue(FormatterUtil.formatAmount(NumberUtil.toBigDecimal(amountField.getText())));
				
				row = sheet.getRow(3);
				row.getCell(1).setCellValue(convertToText(NumberUtil.toBigDecimal(amountField.getText())));
				
				if (localTemplateExists()) {
					templateStream.close();
				}
				
				File tempFile = new File(Paths.get(System.getProperty("user.home")).toAbsolutePath().toString() + "/magic-print-bdo-cheque.xlsx");
				try (
					FileOutputStream out = new FileOutputStream(tempFile);
				) {
					workbook.write(out);
					ExcelUtil.openExcelFile(tempFile);
				} catch (IOException e) {
					showUnexpectedErrorMessage();
				}
			} finally {
				workbook.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error("Unexpected error", e);
			showUnexpectedErrorMessage();
		}
	}

	// TODO: Refactor this to not use BigDecimal operations
	private static String convertToText(BigDecimal amount) {
		StringBuilder sb = new StringBuilder();
		
		int millions = amount.divideToIntegralValue(new BigDecimal("1000000")).intValue();
		if (millions > 0) {
			sb.append(convertToTensText(millions));
			sb.append(" MILLION ");
		}
		
		amount = amount.subtract(new BigDecimal("1000000").multiply(new BigDecimal(millions)));
		int hundredThousand = amount.divideToIntegralValue(new BigDecimal("100000")).intValue();
		if (hundredThousand > 0) {
			sb.append(convertToDigitText(hundredThousand));
			sb.append(" HUNDRED ");
		}
		
		amount = amount.subtract(new BigDecimal("100000").multiply(new BigDecimal(hundredThousand)));
		int thousand = amount.divideToIntegralValue(new BigDecimal("1000")).intValue();
		if (thousand > 0) {
			sb.append(convertToTensText(thousand));
			sb.append(" THOUSAND ");
		} else if (hundredThousand > 0) {
			sb.append("THOUSAND ");
		}
		
		amount = amount.subtract(new BigDecimal("1000").multiply(new BigDecimal(thousand)));
		
		int hundred = amount.divideToIntegralValue(new BigDecimal("100")).intValue();
		if (hundred > 0) {
			sb.append(convertToDigitText(hundred));
			sb.append(" HUNDRED ");
		}
		
		amount = amount.subtract(new BigDecimal("100").multiply(new BigDecimal(hundred)));
		int ones = amount.intValue();
		sb.append(convertToTensText(amount.intValue()));
		if (ones > 0) {
			sb.append(" ");
		}
		
		amount = amount.subtract(new BigDecimal(amount.intValue()));
		int cents = amount.multiply(new BigDecimal("100")).intValue();
		if (cents > 0) {
			sb.append("& ");
			sb.append(String.valueOf(cents)).append("/100");
		} else {
			sb.append("ONLY");
		}
		
		return sb.toString();
	}

	private static String convertToDigitText(int digit) {
		switch (digit) {
		case 1: return "ONE";
		case 2: return "TWO";
		case 3: return "THREE";
		case 4: return "FOUR";
		case 5: return "FIVE";
		case 6: return "SIX";
		case 7: return "SEVEN";
		case 8: return "EIGHT";
		case 9: return "NINE";
		default: return "";
		}
	}
	
	private static String convertToTensText(int number) {
		int tens = number / 10;
		int digit = number - tens * 10;
		
		switch (tens) {
		case 0:
			return convertToDigitText(digit);
		case 1:
			switch (digit) {
			case 0: return "TEN";
			case 1: return "ELEVEN";
			case 2: return "TWELVE";
			case 3: return "THIRTEEN";
			case 4: return "FOURTEEN";
			case 5: return "FIFTEEN";
			case 6: return "SIXTEEN";
			case 7: return "SEVENTEEN";
			case 8: return "EIGHTEEN";
			case 9: return "NINETEEN";
			}
		case 2:
			switch (digit) {
			case 0: return "TWENTY";
			default: return "TWENTY-" + convertToDigitText(digit);
			}
		case 3:
			switch (digit) {
			case 0: return "THIRTY";
			default: return "THIRTY-" + convertToDigitText(digit);
			}
		case 4:
			switch (digit) {
			case 0: return "FORTY";
			default: return "FORTY-" + convertToDigitText(digit);
			}
		case 5:
			switch (digit) {
			case 0: return "FIFTY";
			default: return "FIFTY-" + convertToDigitText(digit);
			}
		case 6:
			switch (digit) {
			case 0: return "SIXTY";
			default: return "SIXTY-" + convertToDigitText(digit);
			}
		case 7:
			switch (digit) {
			case 0: return "SEVENTY";
			default: return "SEVENTY-" + convertToDigitText(digit);
			}
		case 8:
			switch (digit) {
			case 0: return "EIGHTY";
			default: return "EIGHTY-" + convertToDigitText(digit);
			}
		case 9:
			switch (digit) {
			case 0: return "NINETY";
			default: return "NINETY-" + convertToDigitText(digit);
			}
		}
		return "";
	}
	
	private InputStream getTemplateStream() throws FileNotFoundException {
		return localTemplateExists() ? localTemplateStream() : applicationTemplateStream();
	}
	
	private boolean localTemplateExists() {
		return Files.exists(localTemplatePath());
	}
	
	private Path localTemplatePath() {
		return Paths.get(System.getProperty("user.home"), "magic-print-bdo-cheque.xlsx");
	}
	
	private InputStream localTemplateStream() throws FileNotFoundException {
		return new FileInputStream(localTemplatePath().toFile());
	}

	private InputStream applicationTemplateStream() {
		return getClass().getResourceAsStream("/excel/printBdoCheque.xlsx");
	}
	
}