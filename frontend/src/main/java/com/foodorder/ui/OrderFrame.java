package com.foodorder.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class OrderFrame extends JFrame {
    private JTable orderTable;
    private JLabel totalLabel;
    private JButton orderButton;
    private JButton removeButton;
    private DefaultTableModel model;
    public static final String CART_FILE = System.getProperty("user.home") + File.separator + "foodorder_cart.txt";
    private final List<String[]> cartItems = new ArrayList<>();

    public OrderFrame() {
        setTitle("Đặt Đồ Ăn Online - Giỏ Hàng");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        loadCart();
        initComponents();
    }

    private void initComponents() {
        // Table
        String[] columns = {"Món ăn", "Đơn giá", "Số lượng", "Thành tiền"};
        model = new DefaultTableModel(columns, 0);
        for (String[] item : cartItems) {
            model.addRow(item);
        }

        orderTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(orderTable);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Tổng tiền: 0đ");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(255, 87, 34));
        updateTotal();

        JPanel buttonPanel = new JPanel();
        removeButton = new JButton("Xóa Item");
        removeButton.setBackground(Color.RED);
        removeButton.setForeground(Color.WHITE);
        removeButton.addActionListener(e -> removeSelectedItem());

        orderButton = new JButton("Đặt Hàng");
        orderButton.setBackground(new Color(255, 87, 34));
        orderButton.setForeground(Color.WHITE);
        orderButton.setFont(new Font("Arial", Font.BOLD, 14));

        orderButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Đặt hàng thành công! 🎉\nĐơn hàng của bạn đang được xử lý.");
            clearCart();
            dispose();
        });

        buttonPanel.add(removeButton);
        buttonPanel.add(orderButton);

        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void removeSelectedItem() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow != -1) {
            model.removeRow(selectedRow);
            cartItems.remove(selectedRow);
            saveCart();
            updateTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một item để xóa!");
        }
    }

    private void updateTotal() {
        // Tính tổng tiền (đơn giản, giả sử tất cả là số)
        double total = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            String priceStr = (String) model.getValueAt(i, 3);
            priceStr = priceStr.replace("đ", "").replace(",", "");
            try {
                total += Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        totalLabel.setText("Tổng tiền: " + String.format("%,.0f", total) + "đ");
    }

    private void loadCart() {
        cartItems.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(CART_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    cartItems.add(parts);
                } else {
                    String[] fallback = line.split(",");
                    if (fallback.length >= 4) {
                        String name = fallback[0];
                        String quantity = fallback[fallback.length - 2];
                        String total = fallback[fallback.length - 1];
                        String price = String.join(",", java.util.Arrays.copyOfRange(fallback, 1, fallback.length - 2));
                        cartItems.add(new String[]{name, price, quantity, total});
                    }
                }
            }
        } catch (IOException e) {
            // File not found or error, use default
        }
    }

    private void saveCart() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CART_FILE))) {
            for (String[] item : cartItems) {
                writer.println(String.join("|", item));
            }
        } catch (IOException e) {
            // Ignore save errors
        }
    }

    private void clearCart() {
        cartItems.clear();
        model.setRowCount(0);
        saveCart();
        updateTotal();
    }
}