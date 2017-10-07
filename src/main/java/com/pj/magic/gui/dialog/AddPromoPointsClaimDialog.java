package com.pj.magic.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.exception.NotEnoughPromoPointsException;
import com.pj.magic.gui.component.MagicTextField;
import com.pj.magic.model.PromoPointsClaim;
import com.pj.magic.service.PromoRedemptionService;
import com.pj.magic.util.ComponentUtil;

@Component
public class AddPromoPointsClaimDialog extends MagicDialog {

	private static final Logger logger = LoggerFactory.getLogger(AddPromoPointsClaimDialog.class);
	
	@Autowired private PromoRedemptionService promoRedemptionService;
	
	private MagicTextField pointsField;
	private MagicTextField remarksField;
	private JButton saveButton;
	private JButton cancelButton;
	private PromoPointsClaim claim;
	
	public AddPromoPointsClaimDialog() {
		setSize(500, 180);
		setLocationRelativeTo(null);
		setTitle("Add Promo Points Claim");
		getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
	}

	@PostConstruct
	public void initialize() {
		initializeComponents();
		registerKeyBindings();
		layoutComponents();
	}
	
	private void initializeComponents() {
		pointsField = new MagicTextField();
		pointsField.setNumbersOnly(true);
		
		remarksField = new MagicTextField();
		remarksField.setMaximumLength(300);
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveClaim();
			}
		});
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
	}

	private void saveClaim() {
		if (!validateClaim()) {
			return;
		}
		
		claim.setPoints(pointsField.getTextAsInteger());
		claim.setRemarks(remarksField.getText());
		
		try {
			promoRedemptionService.save(claim);
		} catch (NotEnoughPromoPointsException e) {
			showErrorMessage(
					MessageFormat.format("Not enough promo points\nPoints claimed: {0}\nPoints available: {1}",
							e.getClaimPoints(), e.getAvailablePoints()));
			return;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			showErrorMessage("Unexpected error occurred during saving");
			return;
		}
		
		showMessage("Promo claim saved");
		setVisible(false);
	}

	private boolean validateClaim() {
		if (pointsField.getText().isEmpty()) {
			showErrorMessage("Points must be specified");
			return false;
		}
		
		if (pointsField.getTextAsInteger() == 0) {
			showErrorMessage("Points must be greater than 0");
			return false;
		}
		
		if (remarksField.getText().isEmpty()) {
			showErrorMessage("Remarks must be specified");
			return false;
		}
		
		return true;
	}

	private void layoutComponents() {
		setLayout(new GridBagLayout());
		int currentRow = 0;

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Points:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		pointsField.setPreferredSize(new Dimension(100, 25));
		add(pointsField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		add(ComponentUtil.createLabel(100, "Remarks:"), c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = currentRow;
		c.anchor = GridBagConstraints.WEST;
		remarksField.setPreferredSize(new Dimension(300, 25));
		add(remarksField, c);
		
		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		add(Box.createVerticalStrut(10), c);

		currentRow++;
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = currentRow;
		c.gridwidth = 2;
		add(ComponentUtil.createGenericPanel(saveButton, cancelButton), c);
	}

	private void registerKeyBindings() {
	}

	@Override
	protected void doWhenEscapeKeyPressed() {
		// do nothing
	}
	
	public void updateDisplay(PromoPointsClaim claim) {
		this.claim = claim;
		
		if (claim.isNew()) {
			clearDisplay();
			return;
		}
		
		this.claim = promoRedemptionService.getPromoPointsClaim(claim.getId());
		this.claim.setPromo(claim.getPromo());
		claim = this.claim;
		
		pointsField.setText(String.valueOf(claim.getPoints()));
		remarksField.setText(String.valueOf(claim.getRemarks()));
	}

	private void clearDisplay() {
		pointsField.setText(null);
		remarksField.setText(null);
	}
	
}