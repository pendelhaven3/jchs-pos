package com.pj.magic.gui.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.gui.component.MagicToolBarButton;
import com.pj.magic.service.PrintService;

@Component
public class PrintPreviewDialog extends MagicDialog {

	private static final int COLUMNS_PER_LINE = 80;
	
	@Autowired private PrintService printService;
	
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JButton previousButton;
	private JButton nextButton;
	private int currentPage;
	private int totalPages;
	private List<String> printPages;
	private MagicTextField pageField;
	private JLabel lastPageNumberLabel;
	private boolean useCondensedFontForPrinting;
	
	public PrintPreviewDialog() {
		setSize(800, 600);
		setLocationRelativeTo(null);
		setTitle("Print Preview");
		
		initializeComponents();
		layoutMainPanel();
		registerKeyBindings();
	}
	
	private void registerKeyBindings() {
		pageField.onEnterKey(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jumpToPage();
			}
		});
	}

	private void jumpToPage() {
		Integer targetPage = pageField.getTextAsInteger();
		if (targetPage == null) {
			return;
		}
		if (targetPage != null && !(targetPage >= 1 && targetPage <= totalPages)) {
			showErrorMessage("Invalid page");
			return;
		}
		
		currentPage = targetPage - 1;
		displayPage(currentPage);
		updatePageNumberLabelAndNavigation();
	}

	private void initializeComponents() {
		pageField = new MagicTextField();
		pageField.setNumbersOnly(true);
		lastPageNumberLabel = new JLabel();
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		// none
	}
	
	public void layoutMainPanel() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		add(createToolBar(), c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		scrollPane = new JScrollPane(createPrintPreviewPanel());
		add(scrollPane, c);
	}

	private MagicToolBar createToolBar() {
		MagicToolBar toolBar = new MagicToolBar();
		
		JPanel pageNumberPanel = createPageNumberPanel();
		Dimension dim = new Dimension(130, 36);
		pageNumberPanel.setPreferredSize(dim);
		pageNumberPanel.setMinimumSize(dim);
		pageNumberPanel.setMaximumSize(dim);
		toolBar.add(pageNumberPanel);
		
		previousButton = new MagicToolBarButton("left", "Previous Page");
		previousButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				previousPage();
			}
		});
		toolBar.add(previousButton);
		
		nextButton = new MagicToolBarButton("right", "Next Page");
		nextButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				nextPage();
			}
		});
		toolBar.add(nextButton);
		
		JButton printButton = new MagicToolBarButton("print", "Print");
		printButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				print();
			}
		});
		toolBar.add(printButton);
		
		JButton printCurrentPageButton = new MagicToolBarButton("print_current", "Print Current Page");
		printCurrentPageButton.addActionListener(e -> printCurrentPage());
		toolBar.add(printCurrentPageButton);
		
		return toolBar;
	}

	private JPanel createPageNumberPanel() {
		JPanel panel = new JPanel();
		
		panel.add(new JLabel("Page"));
		
		pageField.setPreferredSize(new Dimension(25, 25));
		panel.add(pageField);
		
		panel.add(new JLabel("of "));
		
		lastPageNumberLabel.setPreferredSize(new Dimension(25, 25));
		panel.add(lastPageNumberLabel);
		
		
		return panel;
	}

	private void print() {
		if (useCondensedFontForPrinting) {
			printService.printWithCondensedFont(printPages);
		} else {
			printService.print(printPages);
		}
	}

	private void printCurrentPage() {
		List<String> toPrint = Arrays.asList(printPages.get(currentPage));
		if (useCondensedFontForPrinting) {
			printService.printWithCondensedFont(toPrint);
		} else {
			printService.print(toPrint);
		}
	}

	private void previousPage() {
		displayPage(--currentPage);
		updatePageNumberLabelAndNavigation();
	}

	protected void nextPage() {
		displayPage(++currentPage);
		updatePageNumberLabelAndNavigation();
	}

	public JPanel createPrintPreviewPanel() {
		JPanel panel = new JPanel();
		
		panel.setLayout(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.insets = new Insets(30, 30, 30, 30);
		
		textArea = new JTextArea();
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
		textArea.setEditable(false);
		textArea.setColumns(COLUMNS_PER_LINE);
		textArea.setLineWrap(true);
		panel.add(textArea, c);
		
		c = new GridBagConstraints();
		c.weighty = 1.0;
		c.gridy = 1;
		panel.add(Box.createGlue(), c);
		
		return panel;
	}
	
	public void updateDisplay(List<String> printPages) {
		this.printPages = printPages;
		currentPage = 0;
		totalPages = printPages.size();
		displayPage(currentPage);
		updatePageNumberLabelAndNavigation();
		textArea.setColumns(COLUMNS_PER_LINE);
		useCondensedFontForPrinting = false;
	}
	
	public void setColumnsPerLine(int columnsPerLine) {
		textArea.setColumns(columnsPerLine);
	}

	private void displayPage(int page) {
		textArea.setText(printPages.get(page));
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				scrollPane.getVerticalScrollBar().setValue(0);
			}
		});
	}

	private void updatePageNumberLabelAndNavigation() {
		pageField.setText(String.valueOf(currentPage + 1));
		lastPageNumberLabel.setText(String.valueOf(totalPages));
		previousButton.setEnabled(currentPage > 0);
		nextButton.setEnabled(currentPage + 1 < totalPages);
	}
	
	public void setUseCondensedFontForPrinting(boolean useCondensedFontForPrinting) {
		this.useCondensedFontForPrinting = useCondensedFontForPrinting;
	}
	
}