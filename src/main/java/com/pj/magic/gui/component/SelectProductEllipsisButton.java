package com.pj.magic.gui.component;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pj.magic.gui.dialog.SelectProductDialog;
import com.pj.magic.model.Product;
import com.pj.magic.model.search.ProductSearchCriteria;
import com.pj.magic.util.ComponentUtil;

public class SelectProductEllipsisButton extends EllipsisButton {

	private static final String TOOLTIP_TEXT = "Select Product";
	
	private SelectProductDialog selectProductDialog;
	private MagicTextField productCodeField;
	private JLabel productDescriptionLabel;
	private OnSelectProductAction onSelectProductAction;
	
	public SelectProductEllipsisButton(SelectProductDialog selectProductDialog, 
			MagicTextField productCodeField, JLabel productDescriptionLabel) {
		this.selectProductDialog = selectProductDialog;
		this.productCodeField = productCodeField;
		this.productDescriptionLabel = productDescriptionLabel;
		
		setText(TOOLTIP_TEXT);
		addActionListener(e -> openSelectProductDialog());
		productCodeField.onF5Key(() -> openSelectProductDialog());
	}
	
	private void openSelectProductDialog() {
		ProductSearchCriteria criteria = new ProductSearchCriteria();
		criteria.setCodeOrDescriptionLike(productCodeField.getText());
		criteria.setActive(true);
		
		selectProductDialog.searchProducts(criteria);
		selectProductDialog.setVisible(true);
		
		Product product = selectProductDialog.getSelectedProduct();
		if (product != null) {
			productCodeField.setText(product.getCode());
			productDescriptionLabel.setText(product.getDescription());
			if (onSelectProductAction != null) {
				onSelectProductAction.onSelectProduct(product);
			}
		}
	}

	public JPanel getFieldsPanel() {
		if (!productCodeField.isPreferredSizeSet()) {
			productCodeField.setPreferredSize(new Dimension(100, 25));
		}
		if (!productDescriptionLabel.isPreferredSizeSet()) {
			productDescriptionLabel.setPreferredSize(new Dimension(300, 25));
		}
		return ComponentUtil.createGenericPanel(productCodeField, this, 
				Box.createHorizontalStrut(10), productDescriptionLabel);
	}
	
	public void addOnSelectProductAction(OnSelectProductAction onSelectProductAction) {
		this.onSelectProductAction = onSelectProductAction;
	}
	
	public interface OnSelectProductAction {
		
		void onSelectProduct(Product product);
		
	}
	
}
