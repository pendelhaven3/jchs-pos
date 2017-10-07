package com.pj.magic.gui.tables.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pj.magic.Constants;
import com.pj.magic.gui.tables.PaymentSalesInvoicesTable;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.service.PaymentService;
import com.pj.magic.util.FormatterUtil;

@Component
public class PaymentSalesInvoicesTableModel extends AbstractTableModel {

	private static final String[] columnNames = 
		{"Transaction Date", "SI No.", "Net Amount", "Adj. Amount", "Amount Due", "Due Date"};
	
	@Autowired private PaymentService paymentService;
	
	private List<PaymentSalesInvoice> paymentSalesInvoices = new ArrayList<>();
	
	@Override
	public int getRowCount() {
		return paymentSalesInvoices.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PaymentSalesInvoice paymentSalesInvoice = paymentSalesInvoices.get(rowIndex);
		switch (columnIndex) {
		case PaymentSalesInvoicesTable.TRANSACTION_DATE_COLUMN_INDEX:
			return FormatterUtil.formatDate(paymentSalesInvoice.getSalesInvoice().getTransactionDate());
		case PaymentSalesInvoicesTable.SALES_INVOICE_NUMBER_COLUMN_INDEX:
			return paymentSalesInvoice.getSalesInvoice().getSalesInvoiceNumber();
		case PaymentSalesInvoicesTable.NET_AMOUNT_COLUMN_INDEX:
			return FormatterUtil.formatAmount(paymentSalesInvoice.getSalesInvoice().getTotalNetAmount());
		case PaymentSalesInvoicesTable.ADJUSTMENT_AMOUNT_COLUMN_INDEX:
			BigDecimal amount = paymentSalesInvoice.getAdjustedAmount();
			if (!amount.equals(Constants.ZERO)) {
				return FormatterUtil.formatAmount(amount);
			} else {
				return null;
			}
		case PaymentSalesInvoicesTable.AMOUNT_DUE_COLUMN_INDEX:
			return FormatterUtil.formatAmount(paymentSalesInvoice.getAmountDue());
		case PaymentSalesInvoicesTable.DUE_DATE_COLUMN_INDEX:
			return FormatterUtil.formatDate(paymentSalesInvoice.getSalesInvoice().getDueDate());
		}
		return null;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case PaymentSalesInvoicesTable.NET_AMOUNT_COLUMN_INDEX:
		case PaymentSalesInvoicesTable.ADJUSTMENT_AMOUNT_COLUMN_INDEX:
		case PaymentSalesInvoicesTable.AMOUNT_DUE_COLUMN_INDEX:
			return Number.class;
		default:
			return Object.class;
		}
	}

	public void setPayment(Payment payment) {
		if (payment != null) {
			paymentSalesInvoices = payment.getSalesInvoices();
		} else {
			paymentSalesInvoices.clear();
		}
		fireTableDataChanged();
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	public PaymentSalesInvoice getPaymentSalesInvoice(int rowIndex) {
		return paymentSalesInvoices.get(rowIndex);
	}

	public List<PaymentSalesInvoice> getPaymentSalesInvoices() {
		return paymentSalesInvoices;
	}

	public void removeItem(PaymentSalesInvoice paymentSalesInvoice) {
		paymentSalesInvoices.remove(paymentSalesInvoice);
		paymentService.delete(paymentSalesInvoice);
		fireTableDataChanged();
	}

	public boolean hasItems() {
		return !paymentSalesInvoices.isEmpty();
	}
	
}
