package com.foodorder.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
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
import javax.swing.JTabbedPane;
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

    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JTable historyTable;
    private DefaultTableModel historyTableModel;
    private JLabel totalLabel;
    private final List<CartItem> cartItems = new ArrayList<>();
    private final List<OrderModel> orders = new ArrayList<>();

    public OrderFrame() {
        setTitle("Đặt Đồ Ăn Online - Giỏ Hàng và Lịch Sử");
        setSize(900, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        refreshCartItems();
        refreshTable();
        refreshHistory();
    }

    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Giỏ hàng", createCartPanel());
        tabbedPane.addTab("Lịch sử đặt hàng", createHistoryPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Món ăn", "Đơn giá", "Số lượng", "Thành tiền"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartTable = new JTable(cartTableModel);
        JScrollPane scrollPane = new JScrollPane(cartTable);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Tổng tiền: 0đ");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(255, 87, 34));

        JPanel buttonPanel = new JPanel();
        JButton removeButton = new JButton("Xóa item");
        removeButton.setBackground(Color.RED);
        removeButton.setForeground(Color.WHITE);
        removeButton.addActionListener(e -> removeSelectedItem());

        JButton orderButton = new JButton("Đặt hàng");
        orderButton.setBackground(new Color(255, 87, 34));
        orderButton.setForeground(Color.WHITE);
        orderButton.setFont(new Font("Arial", Font.BOLD, 14));
        orderButton.addActionListener(e -> placeOrder());

        buttonPanel.add(removeButton);
        buttonPanel.add(orderButton);

        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Mã đơn", "Trạng thái", "Tổng tiền", "Ngày tạo"};
        historyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(historyTableModel);
        historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Làm mới");
        JButton viewButton = new JButton("Xem chi tiết");
        JButton cancelButton = new JButton("Hủy đơn");

        refreshButton.addActionListener(e -> refreshHistory());
        viewButton.addActionListener(e -> viewSelectedOrderDetails());
        cancelButton.addActionListener(e -> cancelSelectedOrder());

        buttonPanel.add(refreshButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(cancelButton);

        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshCartItems() {
        cartItems.clear();
        cartItems.addAll(cartStore.getItems());
    }

    private void refreshTable() {
        cartTableModel.setRowCount(0);
        for (CartItem item : cartItems) {
            cartTableModel.addRow(new Object[]{
                    item.getFoodName(),
                    formatMoney(item.getUnitPrice()),
                    item.getQuantity(),
                    formatMoney(item.getSubtotal())
            });
        }
        updateTotal();
    }

    private void refreshHistory() {
        UserSession session = UserSession.getInstance();
        if (!session.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Bạn cần đăng nhập để xem lịch sử đơn hàng.");
            return;
        }

        try {
            OrderModel[] response = apiClient.get("/orders/user/" + session.getUserId(), OrderModel[].class);
            orders.clear();
            if (response != null) {
                orders.addAll(List.of(response));
            }
            historyTableModel.setRowCount(0);
            for (OrderModel order : orders) {
                historyTableModel.addRow(new Object[]{
                        order.getId(),
                        order.getStatus(),
                        formatMoney(order.getTotalPrice()),
                        order.getCreatedAt()
                });
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Không thể tải lịch sử đơn hàng: " + e.getMessage());
        }
    }

    private void removeSelectedItem() {
        int selectedRow = cartTable.getSelectedRow();
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

        javax.swing.JTextField nameField = new javax.swing.JTextField(session.getFullName() == null ? "" : session.getFullName());
        javax.swing.JTextField phoneField = new javax.swing.JTextField(session.getPhone() == null ? "" : session.getPhone());
        javax.swing.JTextField addressField = new javax.swing.JTextField(session.getAddress() == null ? "" : session.getAddress());

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Người nhận:"));
        panel.add(nameField);
        panel.add(new JLabel("SĐT:"));
        panel.add(phoneField);
        panel.add(new JLabel("Địa chỉ giao hàng:"));
        panel.add(addressField);

        int choice = JOptionPane.showConfirmDialog(this, panel, "Thông tin giao hàng", JOptionPane.OK_CANCEL_OPTION);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String receiverName = nameField.getText().trim();
        String receiverPhone = phoneField.getText().trim();
        String deliveryAddress = addressField.getText().trim();

        if (receiverName.isEmpty() || receiverPhone.isEmpty() || deliveryAddress.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Người nhận, SĐT và địa chỉ giao hàng không được để trống!");
            return;
        }

        List<CreateOrderItemRequestModel> items = cartStore.getItems().stream()
                .map(item -> new CreateOrderItemRequestModel(item.getFoodId(), item.getQuantity()))
                .toList();

        CreateOrderRequestModel request = new CreateOrderRequestModel(
                session.getUserId(),
                receiverName,
                receiverPhone,
                deliveryAddress,
                items
        );

        try {
            OrderModel createdOrder = apiClient.post("/orders", request, OrderModel.class);
            if (createdOrder == null) {
                JOptionPane.showMessageDialog(this, "Đặt hàng thành công!");
            } else {
                String orderCode = createdOrder.getId() != null ? "#" + createdOrder.getId() : "";
                JOptionPane.showMessageDialog(this,
                        "Đặt hàng thành công!\nMã đơn: " + orderCode + "\nTrạng thái: " + createdOrder.getStatus());
            }
            cartStore.clear();
            refreshCartItems();
            refreshTable();
            refreshHistory();
            dispose();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void viewSelectedOrderDetails() {
        OrderModel selectedOrder = getSelectedOrder();
        if (selectedOrder == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng trước.");
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Mã đơn: ").append(selectedOrder.getId()).append("\n");
        builder.append("Người nhận: ").append(selectedOrder.getReceiverName() == null ? "" : selectedOrder.getReceiverName()).append("\n");
        builder.append("SĐT: ").append(selectedOrder.getReceiverPhone() == null ? "" : selectedOrder.getReceiverPhone()).append("\n");
        builder.append("Địa chỉ: ").append(selectedOrder.getDeliveryAddress() == null ? "" : selectedOrder.getDeliveryAddress()).append("\n");
        builder.append("Trạng thái: ").append(selectedOrder.getStatus()).append("\n");
        builder.append("Tổng tiền: ").append(formatMoney(selectedOrder.getTotalPrice())).append("\n");
        builder.append("Ngày tạo: ").append(selectedOrder.getCreatedAt()).append("\n\n");
        builder.append("Chi tiết món:\n");

        if (selectedOrder.getItems() != null) {
            for (var item : selectedOrder.getItems()) {
                builder.append("- ")
                        .append(item.getFoodName())
                        .append(" | SL: ").append(item.getQuantity())
                        .append(" | Giá: ").append(formatMoney(item.getPrice()))
                        .append(" | Thành tiền: ").append(formatMoney(item.getSubtotal()))
                        .append("\n");
            }
        }

        javax.swing.JTextArea textArea = new javax.swing.JTextArea(builder.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 380));

        JOptionPane.showMessageDialog(this, scrollPane, "Chi tiết đơn hàng", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelSelectedOrder() {
        OrderModel selectedOrder = getSelectedOrder();
        if (selectedOrder == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng trước.");
            return;
        }

        if (!"PENDING".equalsIgnoreCase(selectedOrder.getStatus()) && !"CONFIRMED".equalsIgnoreCase(selectedOrder.getStatus())) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể hủy đơn khi trạng thái đang pending hoặc confirmed.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn hủy đơn " + selectedOrder.getId() + " không?",
                "Xác nhận hủy đơn",
                JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            apiClient.put("/orders/" + selectedOrder.getId() + "/cancel", null, OrderModel.class);
            JOptionPane.showMessageDialog(this, "Hủy đơn thành công!");
            refreshHistory();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Không thể hủy đơn: " + e.getMessage());
        }
    }

    private OrderModel getSelectedOrder() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }

        Long id = parseId(historyTableModel.getValueAt(selectedRow, 0));
        for (OrderModel order : orders) {
            if (order.getId() != null && order.getId().equals(id)) {
                return order;
            }
        }
        return null;
    }

    private Long parseId(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value == null) {
            return null;
        }
        return Long.parseLong(value.toString());
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
