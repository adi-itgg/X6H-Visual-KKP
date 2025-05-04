package io.aitech.pv.form.content.invoice;

import io.aitech.pv.MainFrame;
import io.aitech.pv.form.BaseForm;
import io.aitech.pv.form.content.BaseMasterFormContent;
import io.aitech.pv.repository.InvoiceRepository;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.Locale;

public class MasterInvoiceForm extends BaseMasterFormContent<InvoiceRepository> {

    private JTextField cariText;
    private JButton tambahButton;
    private JTable mTable;
    private JPanel mp;

    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    public MasterInvoiceForm(MainFrame mainFrame) {
        super(mainFrame.vertx(), mainFrame.invoiceRepository());
        initialize();
    }

    @Override
    protected String[] getHeaderColumns() {
        return new String[]{"Id", "Nama", "Tagihan"};
    }

    @Override
    protected JTextField getCariText() {
        return cariText;
    }

    @Override
    protected JButton getTambahButton() {
        return tambahButton;
    }

    @Override
    protected JTable getTable() {
        return mTable;
    }

    @Override
    protected BaseForm showAddRowForm(JFrame frame) {
        return null;
    }

    @Override
    public JPanel getMainPanel() {
        return mp;
    }

    @Override
    protected void addRow(ActionEvent actionEvent) {
        String name = JOptionPane.showInputDialog(mp, "Masukkan Nama:", "");
        String amount = JOptionPane.showInputDialog(mp, "Masukkan Total Tagihan:", "");
        long amountLong;
        try {
            amountLong = Long.parseLong(amount);
        } catch (NumberFormatException e) {
            showErrorDialog("Total Tagihan harus berupa angka");
            return;
        }
        if (name != null && !name.trim().isEmpty() && !amount.trim().isEmpty()) {
            model.addRow(new Object[]{"-", name, amountLong});
        }
    }

    @Override
    protected Object mapDisplayColumnValue(int index, Object value) {
        if (index == 2 && value instanceof Number) {
            return currencyFormatter.format(value).split(",")[0].replace("Rp", "Rp ");
        }
        return super.mapDisplayColumnValue(index, value);
    }

    @Override
    protected Object mapColumnValue(int index, Object value) {
        if (index == 2 && value instanceof String) {
            String cleanValue = (value + "")
                    .toLowerCase()
                    .replace("rp", "")
                    .replace(".", "")
                    .replace(",", "")
                    .trim();
            return Long.parseLong(cleanValue);
        }
        return super.mapColumnValue(index, value);
    }
}
