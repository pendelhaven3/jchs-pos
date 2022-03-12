package com.pj.magic.gui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.gui.component.MagicToolBar;
import com.pj.magic.model.Product;
import com.pj.magic.model.Product2;
import com.pj.magic.service.ProductService;
import com.pj.magic.util.ComponentUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MaintainTrisysProductPanel extends StandardMagicPanel {

	@Autowired
	private ProductService productService;
	
	private Product product;
	
	private JLabel productCodeLabel = new JLabel();
	private JLabel descriptionLabel = new JLabel();
	private JLabel product2IdLabel = new JLabel();
	private JButton unlinkButton;
	private JButton linkButton;
	private JButton goToProduct2Button;
	
	@Override
	public String getTitle() {
		return "Maintain Trisys Product";
	}
	
	@Override
	protected void initializeComponents() {
		unlinkButton = new JButton("Unlink Code from Product");
		unlinkButton.addActionListener(e -> unlinkCodeFromProduct());

		linkButton = new JButton("Link Code to Product");
		linkButton.addActionListener(e -> linkCodeToProduct());
		
		goToProduct2Button = new JButton("Go to Product");
		goToProduct2Button.addActionListener(e -> goToProduct());
	}

	@Override
	protected void layoutMainPanel(JPanel mainPanel) {
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		
		int currentRow = 0;
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets.left = 50;
		c.gridx = 0;
		c.gridy = currentRow;
		c.weightx = c.weighty = 0.0;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(125, "Product Code:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		productCodeLabel.setPreferredSize(new Dimension(100, 25));
		mainPanel.add(productCodeLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.left = 50;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(125, "Description:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		descriptionLabel.setPreferredSize(new Dimension(400, 25));
		mainPanel.add(descriptionLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.left = 50;
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		mainPanel.add(ComponentUtil.createLabel(125, "Product ID:"), c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		product2IdLabel.setPreferredSize(new Dimension(200, 25));
		mainPanel.add(product2IdLabel, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.insets.top = 20;
		c.gridx = 1;
		c.gridy = currentRow;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		unlinkButton.setPreferredSize(new Dimension(200, 30));
		linkButton.setPreferredSize(new Dimension(200, 30));
		goToProduct2Button.setPreferredSize(new Dimension(150, 30));
		mainPanel.add(ComponentUtil.createGenericPanel(
				unlinkButton, Box.createHorizontalStrut(10), linkButton, Box.createHorizontalStrut(10), goToProduct2Button), c);
	}

	public void updateDisplay(Product product) {
		this.product = product = productService.getProduct(product.getId());
		
		productCodeLabel.setText(product.getCode());
		descriptionLabel.setText(product.getDescription());
		if (product.getProduct2Id() != null) {
			product2IdLabel.setText(String.valueOf(product.getProduct2Id()));
		} else {
			product2IdLabel.setText(null);
		}
		
		unlinkButton.setEnabled(product.isLinked());
		linkButton.setEnabled(!product.isLinked());
	}

	@Override
	protected void doOnBack() {
		getMagicFrame().switchBack();
	}

	@Override
	protected void addToolBarButtons(MagicToolBar toolBar) {
	}

	@Override
	protected void registerKeyBindings() {
	}
	
	private void unlinkCodeFromProduct() {
		if (confirm("Unlink Code from Product?")) {
			try {
				productService.unlinkCodeFromProduct(product);
				showMessage("Code unlinked");
				updateDisplay(product);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				showErrorMessage(e.getMessage());
			}
		}
	}

	private void linkCodeToProduct() {
		showMessage("Coming soon!");
	}
	
	private void goToProduct() {
		getMagicFrame().setBackPanel("TRISYS_PRODUCT_LIST_PANEL");
		getMagicFrame().switchToEditProductPanel(new Product2(product.getProduct2Id()));
	}
	
}
