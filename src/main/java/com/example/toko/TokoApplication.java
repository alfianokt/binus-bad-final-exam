package com.example.toko;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.example.toko.model.Product;
import com.example.toko.model.Transaction;
import com.example.toko.model.TransactionReport;
import com.example.toko.service.Database;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TokoApplication extends JFrame {
    private Connection connection;
    private Database db;

    public TokoApplication() {
        try {
            String url = "jdbc:mysql://localhost:3306/binus-bad-fe";
            String username = "root";
            String password = "mysecretpassword";

            connection = DriverManager.getConnection(url, username, password);

            System.out.println("==> Database connected!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        db = new Database(connection);

        setTitle("Sistem Manajemen Inventari");
        setSize(600, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JTabbedPane tabPane = new JTabbedPane();

        JPanel productManagementPanel = createProductManagementPanel();
        JPanel inventoryTrackingPanel = createInventoryTrackingPanel();
        JPanel reportingPanel = createReportingPanel();

        tabPane.addTab("Manajemen Produk", productManagementPanel);
        tabPane.addTab("Pelacakan Inventaris", inventoryTrackingPanel);
        tabPane.addTab("Pelaporan", reportingPanel);

        tabPane.setSelectedIndex(0);
        mainPanel.add(tabPane, BorderLayout.CENTER);

        add(mainPanel);

        setVisible(true);
    }

    private void initTableProductsData(DefaultTableModel tableModel) {
        // reset table
        tableModel.setRowCount(0);

        db.getAllProducts().forEach(o -> {
            tableModel.addRow(new Object[] { o.getId(), o.getName(), o.getCategory(), o.getDescription(), o.getPrice(),
                    o.getStock() });
        });
    }

    private JPanel createProductManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // add form
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        JLabel labelName = new JLabel("Nama Produk:");
        JTextField inputName = new JTextField(40);
        JLabel labelCategory = new JLabel("Kategory Produk:");
        JTextField inputCategory = new JTextField(40);
        JLabel labelDesc = new JLabel("Deskripsi:");
        JTextField inputDesc = new JTextField(40);
        JLabel labelPrice = new JLabel("Harga:");
        JTextField inputPrice = new JTextField(20);
        JLabel labelStock = new JLabel("Jumlah Stok:");
        JTextField inputStock = new JTextField(20);
        JButton btnAdd = new JButton("Tambah Produk");
        JButton btnUpdate = new JButton("Update");
        JButton btnClose = new JButton("Keluar");

        // 1
        inputPanel.add(labelName);
        inputPanel.add(inputName);

        // 2
        inputPanel.add(labelCategory);
        inputPanel.add(inputCategory);

        // 3
        inputPanel.add(labelDesc);
        inputPanel.add(inputDesc);

        // 4
        inputPanel.add(labelPrice);
        inputPanel.add(inputPrice);

        // 5
        inputPanel.add(labelStock);
        inputPanel.add(inputStock);

        // 6
        inputPanel.add(new JPanel());
        inputPanel.add(btnAdd);

        // 7
        inputPanel.add(btnUpdate);
        inputPanel.add(btnClose);

        btnAdd.setVisible(true);
        btnUpdate.setVisible(false);
        btnClose.setVisible(false);

        // table
        JTable tableProducts = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableModel.setColumnIdentifiers(new Object[] { "ID", "Nama", "Kategori", "Deskripsi", "Harga", "Stok" });

        tableProducts.setModel(tableModel);
        tableProducts.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(tableProducts);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // event handling
        // add data
        btnAdd.addActionListener(e -> {
            String name = inputName.getText().trim();
            String category = inputCategory.getText().trim();
            String desc = inputDesc.getText().trim();
            String priceStr = inputPrice.getText().trim();
            String stockStr = inputStock.getText().trim();

            // Validasi input
            if (name.isEmpty() || category.isEmpty() || desc.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Semua field harus diisi.");
                return;
            }

            try {
                int price = Integer.parseInt(priceStr);
                int stock = Integer.parseInt(stockStr);

                db.createProduct(new Product(stock, name, category, desc, stock, price));

                inputName.setText("");
                inputCategory.setText("");
                inputDesc.setText("");
                inputPrice.setText("");
                inputStock.setText("");

                initTableProductsData(tableModel);
                JOptionPane.showMessageDialog(panel, "Data berhasil disimpan!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Harga dan Jumlah Stok harus berupa angka.");
            }
        });

        // close edit mode
        btnClose.addActionListener(e -> {
            btnAdd.setVisible(true);
            btnUpdate.setVisible(false);
            btnClose.setVisible(false);

            inputName.setText("");
            inputCategory.setText("");
            inputDesc.setText("");
            inputPrice.setText("");
            inputStock.setText("");
        });

        // enter edit mode
        tableProducts.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableProducts.getSelectedRow();
                if (selectedRow != -1) {
                    // edit mode
                    btnAdd.setVisible(false);
                    btnUpdate.setVisible(true);
                    btnClose.setVisible(true);

                    inputName.setText(tableProducts.getValueAt(selectedRow, 1).toString());
                    inputCategory.setText(tableProducts.getValueAt(selectedRow, 2).toString());
                    inputDesc.setText(tableProducts.getValueAt(selectedRow, 3).toString());
                    inputPrice.setText(tableProducts.getValueAt(selectedRow, 4).toString());
                    inputStock.setText(tableProducts.getValueAt(selectedRow, 5).toString());
                }
            }
        });

        // save updated data
        btnUpdate.addActionListener(e -> {
            int row = tableProducts.getSelectedRow();

            if (row != -1) {
                String name = inputName.getText().trim();
                String category = inputCategory.getText().trim();
                String desc = inputDesc.getText().trim();
                String priceStr = inputPrice.getText().trim();
                String stockStr = inputStock.getText().trim();

                try {
                    int price = Integer.parseInt(priceStr);
                    int stock = Integer.parseInt(stockStr);
                    int id = Integer.parseInt(tableProducts.getValueAt(row, 0).toString());

                    db.updateProduct(new Product(id, name, category, desc, stock, price));

                    JOptionPane.showMessageDialog(panel, "Data berhasil diupdate!");

                    initTableProductsData(tableModel);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Harga dan Jumlah Stok harus berupa angka.");
                }
            }
        });

        initTableProductsData(tableModel);

        return panel;
    }

    private JPanel createInventoryTrackingPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // add form
        JPanel gridPanel = new JPanel(new GridLayout(11, 2, 10, 10));
        JLabel labelName = new JLabel("Nama Produk:");
        JTextField inputName = new JTextField(40);
        JLabel labelCategory = new JLabel("Kategory Produk:");
        JTextField inputCategory = new JTextField(40);

        JButton btnSearch = new JButton("Cari Produk");
        JButton btnSell = new JButton("Jual");
        JButton btnClose = new JButton("Keluar");

        // result
        JLabel resultId = new JLabel("");
        JLabel resultName = new JLabel("");
        JLabel resultCategori = new JLabel("");
        JLabel resultDesc = new JLabel("");
        JLabel resultStock = new JLabel("");
        JLabel resultPrice = new JLabel("");

        // 1
        gridPanel.add(new JLabel("Cari Produk"));
        gridPanel.add(resultId);

        // 2
        gridPanel.add(labelName);
        gridPanel.add(inputName);

        // 3
        gridPanel.add(labelCategory);
        gridPanel.add(inputCategory);

        // 4
        gridPanel.add(new JPanel());
        gridPanel.add(btnSearch);

        // 5
        gridPanel.add(new JLabel("Hasil Pencarian"));
        gridPanel.add(new JLabel());

        // 6
        gridPanel.add(new JLabel("Nama :"));
        gridPanel.add(resultName);

        // 7
        gridPanel.add(new JLabel("Kategori :"));
        gridPanel.add(resultCategori);

        // 8
        gridPanel.add(new JLabel("Deskripsi :"));
        gridPanel.add(resultDesc);

        // 9
        gridPanel.add(new JLabel("Stok :"));
        gridPanel.add(resultStock);

        // 10
        gridPanel.add(new JLabel("Harga :"));
        gridPanel.add(resultPrice);

        // 11
        gridPanel.add(btnSell);
        gridPanel.add(btnClose);

        btnSearch.setVisible(true);

        panel.add(gridPanel, BorderLayout.NORTH);

        btnClose.setVisible(false);
        btnSell.setVisible(false);

        // search
        btnSearch.addActionListener(e -> {
            Product product = db.findProductByNameOrCategory(inputName.getText(), inputCategory.getText());

            if (product == null) {
                JOptionPane.showMessageDialog(panel, "Data tidak ditemukan!");
            } else {
                resultId.setText(String.valueOf(product.getId()));
                resultName.setText(product.getName());
                resultCategori.setText(product.getCategory());
                resultDesc.setText(product.getDescription());
                resultPrice.setText(String.valueOf(product.getPrice()));
                resultStock.setText(String.valueOf(product.getStock()));

                btnClose.setVisible(true);
                btnSell.setVisible(true);
            }
        });

        // close edit mode
        btnClose.addActionListener(e -> {
            resultId.setText("");
            resultName.setText("");
            resultCategori.setText("");
            resultDesc.setText("");
            resultPrice.setText("");
            resultStock.setText("");

            btnClose.setVisible(false);
            btnSell.setVisible(false);
        });

        // sell
        btnSell.addActionListener(e -> {
            try {
                int id = Integer.parseInt(resultId.getText());
                int price = Integer.parseInt(resultPrice.getText());
                int stock = Integer.parseInt(resultStock.getText());

                if (stock < 1) {
                    JOptionPane.showMessageDialog(panel, "Stock tidak cukup!");
                    return;
                }

                Product product = new Product(id, resultName.getText(), resultCategori.getText(), resultDesc.getText(),
                        stock, price);
                Transaction transaction = new Transaction(0, price, 1, id, null);

                // decrement stock
                product.setStock(stock - 1);

                db.updateProduct(product);
                db.createTransaction(transaction);

                resultStock.setText(String.valueOf(product.getStock()));
                JOptionPane.showMessageDialog(panel, "Barang berhasil dijual!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Harga dan Jumlah Stok harus berupa angka.");
            }
        });

        return panel;
    }

    private JPanel createReportingPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // add form
        JPanel gridPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JLabel labelDateStart = new JLabel("Tanggal Mulai (YYYY-MM-DD):");
        JTextField inputDateStart = new JTextField(40);
        JLabel labelDateEnd = new JLabel("Tanggal Akhir (YYYY-MM-DD):");
        JTextField inputDateEnd = new JTextField(40);
        JButton btnSearch = new JButton("Cari");

        inputDateStart.setText("2023-01-01");
        inputDateEnd.setText("2024-12-31");


        // 1
        gridPanel.add(new JLabel("Laporan Inventaris Bulanan"));
        gridPanel.add(new JLabel());

        // 2
        gridPanel.add(labelDateStart);
        gridPanel.add(inputDateStart);

        // 3
        gridPanel.add(labelDateEnd);
        gridPanel.add(inputDateEnd);

        // 4
        gridPanel.add(new JLabel());
        gridPanel.add(btnSearch);

        // table
        JTable tableProducts = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableModel.setColumnIdentifiers(new Object[] { "Nama", "Stock", "Jumlah", "Tanggal" });

        tableProducts.setModel(tableModel);
        tableProducts.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(tableProducts);

        panel.add(gridPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // search
        btnSearch.addActionListener(e -> {
            tableModel.setNumRows(0);

            try {
                LocalDate dateStart = parseDate(inputDateStart.getText());
                LocalDate dateEnd = parseDate(inputDateEnd.getText());

                java.util.List<TransactionReport> reports = db.getTransactionReports(dateStart, dateEnd);

                if (reports.size() == 0) {
                    JOptionPane.showMessageDialog(panel, "Data tidak ditemukan!");
                }

                reports.forEach(o -> {
                    tableModel.addRow(new Object[] { o.getProductName(), o.getProductStock(), o.getAmount(), o.getDate() });
                });

            } catch (DateTimeParseException err) {
                JOptionPane.showMessageDialog(panel, "Tanggal start tidak valid!");
            }
        });

        return panel;
    }

    public static LocalDate parseDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return LocalDate.parse(dateString, formatter);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TokoApplication::new);
    }
}