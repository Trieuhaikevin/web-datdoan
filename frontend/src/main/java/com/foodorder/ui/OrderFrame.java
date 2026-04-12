package com.foodorder.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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

import com.foodorder.client.ApiClient;
import com.foodorder.model.CartItem;
import com.foodorder.model.CartStore;
import com.foodorder.model.CreateOrderItemRequestModel;
import com.foodorder.model.CreateOrderRequestModel;
import com.foodorder.model.OrderModel;
import com.foodorder.model.UserSession;

public class OrderFrame extends JFrame {
    private final ApiClient apiClient = new ApiClient();
    private final CartStore cartStore = CartStore.getInstance();
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");

    private JTable orderTable;
    private JLabel totalLabel;
    private JButton orderButton;
    private JButton removeButton;
    private DefaultTableModel model;
    private final List<CartItem> cartItems = new ArrayList<>();

    public OrderFrame() {
        setTitle("Đặt Đồ Ăn Online - Giỏ Hàng");
        setSize(700, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        refreshCartItems();
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        String[] columns = {"Món ăn", "Đơn giá", "Số lượng", "Thành tiền"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(orderTable);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Tổng tiền: 0đ");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(255, 87, 34));

        JPanel buttonPanel = new JPanel();
        removeButton = new JButton("Xóa Item");
        removeButton.setBackground(Color.RED);
        removeButton.setForeground(Color.WHITE);
        removeButton.addActionListener(e -> removeSelectedItem());

        orderButton = new JButton("Đặt Hàng");
        orderButton.setBackground(new Color(255, 87, 34));
        orderButton.setForeground(Color.WHITE);
        orderButton.setFont(new Font("Arial", Font.BOLD, 14));
        orderButton.addActionListener(e -> placeOrder());

        buttonPanel.add(removeButton);
        buttonPanel.add(orderButton);

        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void refreshCartItems() {
        cartItems.clear();
        cartItems.addAll(cartStore.getItems());
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (CartItem item : cartItems) {
            model.addRow(new Object[]{
                    item.getFoodName(),
                    formatMoney(item.getUnitPrice()),
                    item.getQuantity(),
                    formatMoney(item.getSubtotal())
            });
        }
        updateTotal();
    }

    private void removeSelectedItem() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một item để xóa!");
            return;
        }

        CartItem item = cartItems.get(selectedRow);
        cartStore.removeByFoodId(item.getFoodId());
        refreshCartItems();
        refreshTable();
    }

    private void placeOrder() {
        if (cartStore.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống!");
            return;
        }

        UserSession session = UserSession.getInstance();
        if (!session.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Bạn cần đăng nhập lại để đặt hàng.");
            return;
        }

        String defaultAddress = session.getAddress() == null ? "" : session.getAddress();
        String deliveryAddress = JOptionPane.showInputDialog(this, "Nhập địa chỉ giao hàng:", defaultAddress);
        if (deliveryAddress == null) {
            return;
        }
        if (deliveryAddress.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Địa chỉ giao hàng không được để trống!");
            return;
        }

        List<CreateOrderItemRequestModel> items = cartStore.getItems().stream()
                .map(item -> new CreateOrderItemRequestModel(item.getFoodId(), item.getQuantity()))
                .toList();

        CreateOrderRequestModel request = new CreateOrderRequestModel(
                session.getUserId(),
                deliveryAddress.trim(),
                items
        );

        try {
            OrderModel createdOrder = apiClient.post("/orders", request, OrderModel.class);
            String orderCode = createdOrder != null && createdOrder.getId() != null
                    ? "#" + createdOrder.getId()
                    : "";
            JOptionPane.showMessageDialog(this,
                    "Đặt hàng thành công!\nMã đơn: " + orderCode);
            cartStore.clear();
            refreshCartItems();
            refreshTable();
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void updateTotal() {
        BigDecimal total = cartStore.getTotal();
        totalLabel.setText("Tổng tiền: " + formatMoney(total));
    }

    private String formatMoney(BigDecimal value) {
        BigDecimal normalized = value == null ? BigDecimal.ZERO : value;
        return moneyFormat.format(normalized) + "đ";
    }
}
