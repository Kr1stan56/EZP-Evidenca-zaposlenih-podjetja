package ui;

import controller.AppController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MainWindow extends JFrame {

    private final AppController controller;
    private final JTable employeeTable;

    public MainWindow(AppController controller) {
        this.controller = controller;

        setTitle("EZP – Evidenca zaposlenih podjetja");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        employeeTable = createEmployeeTable();

        setContentPane(buildUi());
        refreshTable();

        setVisible(true);
    }

    private JPanel buildUi() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UiConfig.BG_APP);
        main.setBorder(BorderFactory.createEmptyBorder(UiConfig.PAD, UiConfig.PAD, UiConfig.PAD, UiConfig.PAD));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UiConfig.BG_BAR);
        top.setBorder(BorderFactory.createLineBorder(UiConfig.BORDER));

        JLabel title = new JLabel("Seznam zaposlenih");
        title.setFont(UiConfig.FONT_H1);
        title.setForeground(UiConfig.TEXT_MUTED);

        top.add(title, BorderLayout.WEST);
        top.add(buildButtonBar(), BorderLayout.EAST);

        JScrollPane sp = new JScrollPane(employeeTable);
        sp.setBorder(BorderFactory.createLineBorder(UiConfig.BORDER));

        main.add(top, BorderLayout.NORTH);
        main.add(sp, BorderLayout.CENTER);

        return main;
    }

    private JPanel buildButtonBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        p.setOpaque(false);

        JButton add = button("➕ Dodaj zaposlenega", UiConfig.PRIMARY, Color.WHITE, UiConfig.BTN_W, UiConfig.BTN_H, this::onAdd);
        JButton edit = button("✓ Uredi", UiConfig.SUCCESS, Color.WHITE, UiConfig.BTN_W, UiConfig.BTN_H, this::onEdit);
        JButton del = button("✖ Izbriši", UiConfig.DANGER, Color.WHITE, UiConfig.BTN_W, UiConfig.BTN_H, this::onDelete);
        JButton ref = button("⟳", UiConfig.BG_BAR, UiConfig.TEXT_MUTED, UiConfig.BTN_H, UiConfig.BTN_H, this::refreshTable);

        p.add(add);
        p.add(edit);
        p.add(del);
        p.add(ref);

        return p;
    }

    private JButton button(String text, Color bg, Color fg, int w, int h, Runnable action) {
        JButton b = new JButton(text);
        b.setFont(UiConfig.FONT_BASE);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(w, h));
        b.addActionListener(e -> action.run());
        return b;
    }

    private JTable createEmployeeTable() {
        String[] columns = {"ID", "Ime", "Priimek", "Delovno mesto", "Oddelek", "Plača", "Datum zaposlitve"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable t = new JTable(model);
        t.setRowHeight(UiConfig.TABLE_ROW_H);
        t.setFont(UiConfig.FONT_BASE);
        t.setBackground(Color.WHITE);
        t.setGridColor(UiConfig.BORDER);

        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setBackground(UiConfig.PRIMARY);
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setFont(UiConfig.FONT_BASE);

        // skrij ID
        t.getColumnModel().getColumn(0).setMinWidth(0);
        t.getColumnModel().getColumn(0).setMaxWidth(0);
        t.getColumnModel().getColumn(0).setPreferredWidth(0);

        return t;
    }

    private void refreshTable() {
        try {
            controller.loadEmployees((DefaultTableModel) employeeTable.getModel());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Napaka", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() {
        AddEmployee dlg = new AddEmployee(this, controller);
        dlg.setVisible(true);   // naj bo samo tukaj (ne še v AddEmployee konstruktorju)
        refreshTable();
    }

    private void onEdit() {
        int row = employeeTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Izberi zaposlenega!");
            return;
        }

        int employeeId = Integer.parseInt(employeeTable.getValueAt(row, 0).toString());

        AddEmployee dlg = new AddEmployee(this, controller, employeeId);
        dlg.setVisible(true);
        refreshTable();
    }

    private void onDelete() {
        int row = employeeTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Izberi zaposlenega!");
            return;
        }

        int employeeId = Integer.parseInt(employeeTable.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(this, "Izbrišem?", "Potrdi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            controller.deleteEmployee(employeeId);
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Napaka", JOptionPane.ERROR_MESSAGE);
        }
    }
}
