package com.foodorder.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.foodorder.client.ApiClient;
import com.foodorder.model.CartStore;
import com.foodorder.model.CategoryModel;
import com.foodorder.model.FoodModel;
import com.foodorder.model.FoodRequestModel;
import com.foodorder.model.OrderItemModel;
import com.foodorder.model.OrderModel;
import com.foodorder.model.UserSession;
import com.foodorder.model.UserModel;

public class AdminFrame extends JFrame {
    private final ApiClient apiClient = new ApiClient();
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0");

    private JTable foodTable;
    private JTable orderTable;
    private JTable userTable;
    private JTable categoryTable;
    private DefaultTableModel foodTableModel;
    private DefaultTableModel orderTableModel;
    private DefaultTableModel userTableModel;
    private DefaultTableModel categoryTableModel;

    private final List<FoodModel> foods = new ArrayList<>();
    private final List<OrderModel> orders = new ArrayList<>();
    private final List<UserModel> users = new ArrayList<>();
    private final List<CategoryModel> categories = new ArrayList<>();

    public AdminFrame() {
        setTitle("Admin - Quản lý hệ thống");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        refreshCategories();
        refreshFoods();
        refreshOrders();
        refreshUsers();
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 87, 34));

        JLabel titleLabel = new JLabel("QUẢN TRỊ HỆ THỐNG", SwingConstants.LEFT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel actionPanel = new JPanel();
        actionPanel.setBackground(new Color(255, 87, 34));

        JButton refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(e -> {
            refreshFoods();
            refreshOrders();
            refreshUsers();
        });

        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.addActionListener(e -> {
            UserSession.getInstance().clear();
            CartStore.getInstance().clear();
            new LoginFrame().setVisible(true);
            dispose();
        });

        actionPanel.add(refreshButton);
        actionPanel.add(logoutButton);
        headerPanel.add(actionPanel, BorderLayout.EAST);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Quản lý danh mục", createCategoryPanel());
        tabbedPane.addTab("Quản lý kho hàng", createInventoryPanel());
        tabbedPane.addTab("Quản lý đơn hàng", createOrderPanel());
        tabbedPane.addTab("Quản lý user", createUserPanel());

        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Tên danh mục", "Mô tả"};
        categoryTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        categoryTable = new JTable(categoryTableModel);
        categoryTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Làm mới");
        JButton addButton = new JButton("Thêm danh mục");
        JButton deleteButton = new JButton("Xóa danh mục");

        refreshButton.addActionListener(e -> refreshCategories());
        addButton.addActionListener(e -> addCategory());
        deleteButton.addActionListener(e -> deleteCategory());

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        panel.add(new JScrollPane(categoryTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshCategories() {
        try {
            CategoryModel[] response = apiClient.get("/categories", CategoryModel[].class);
            categories.clear();
            if (response != null) {
                categories.addAll(Arrays.asList(response));
            }

            categoryTableModel.setRowCount(0);
            for (CategoryModel category : categories) {
                categoryTableModel.addRow(new Object[]{
                        category.getId(),
                        category.getName(),
                        category.getDescription() == null ? "" : category.getDescription()
                });
            }
        } catch (Exception e) {
            showError("Không thể tải danh sách danh mục", e);
        }
    }

    private void addCategory() {
        JTextField nameField = new JTextField();
        JTextArea descriptionField = new JTextArea(3, 20);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Tên danh mục:"));
        panel.add(nameField);
        panel.add(new JLabel("Mô tả:"));
        panel.add(new JScrollPane(descriptionField));

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm danh mục", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên danh mục không được để trống");
                return;
            }

            CategoryModel newCategory = new CategoryModel();
            newCategory.setName(nameField.getText().trim());
            newCategory.setDescription(trimToNull(descriptionField.getText()));

            apiClient.post("/categories", newCategory, CategoryModel.class);
            refreshCategories();
            JOptionPane.showMessageDialog(this, "Thêm danh mục thành công!");
        } catch (Exception e) {
            showError("Không thể thêm danh mục", e);
        }
    }

    private void deleteCategory() {
        CategoryModel selectedCategory = getSelectedCategory();
        if (selectedCategory == null) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa danh mục \"" + selectedCategory.getName() + "\"?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            apiClient.delete("/categories/" + selectedCategory.getId());
            refreshCategories();
            refreshFoods();
            JOptionPane.showMessageDialog(this, "Xóa danh mục thành công!");
        } catch (Exception e) {
            showError("Không thể xóa danh mục", e);
        }
    }

    private CategoryModel getSelectedCategory() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục trước.");
            return null;
        }

        Long id = parseId(categoryTableModel.getValueAt(selectedRow, 0));
        for (CategoryModel category : categories) {
            if (category.getId() != null && category.getId().equals(id)) {
                return category;
            }
        }
        return null;
    }

    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Tên món", "Giá", "Tồn kho", "Khả dụng", "Danh mục"};
        foodTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        foodTable = new JTable(foodTableModel);
        foodTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        foodTable.getTableHeader().setReorderingAllowed(false);

        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Làm mới");
        JButton addButton = new JButton("Thêm món");
        JButton editButton = new JButton("Sửa món");
        JButton deleteButton = new JButton("Xóa món");
        JButton stockButton = new JButton("Cập nhật kho");

        refreshButton.addActionListener(e -> refreshFoods());
        addButton.addActionListener(e -> addFood());
        editButton.addActionListener(e -> editFood());
        deleteButton.addActionListener(e -> deleteFood());
        stockButton.addActionListener(e -> updateStock());

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(stockButton);

        panel.add(new JScrollPane(foodTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Mã đơn", "Khách hàng", "Tổng tiền", "Trạng thái", "Ngày tạo"};
        orderTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(orderTableModel);

        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Làm mới");
        JButton detailButton = new JButton("Xem chi tiết");
        JButton updateStatusButton = new JButton("Cập nhật trạng thái");

        refreshButton.addActionListener(e -> refreshOrders());
        detailButton.addActionListener(e -> viewOrderDetails());
        updateStatusButton.addActionListener(e -> updateOrderStatus());

        buttonPanel.add(refreshButton);
        buttonPanel.add(detailButton);
        buttonPanel.add(updateStatusButton);

        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshFoods() {
        try {
            FoodModel[] response = apiClient.get("/foods", FoodModel[].class);
            foods.clear();
            if (response != null) {
                foods.addAll(Arrays.asList(response));
            }

            foodTableModel.setRowCount(0);
            for (FoodModel food : foods) {
                foodTableModel.addRow(new Object[]{
                        food.getId(),
                        food.getName(),
                        formatMoney(food.getPrice()),
                        food.getStockQuantity(),
                        food.isAvailable() ? "Có" : "Không",
                        food.getCategoryName() == null ? "" : food.getCategoryName()
                });
            }
        } catch (Exception e) {
            showError("Không thể tải danh sách món ăn", e);
        }
    }

    private void refreshOrders() {
        try {
            OrderModel[] response = apiClient.get("/orders", OrderModel[].class);
            orders.clear();
            if (response != null) {
                orders.addAll(Arrays.asList(response));
            }

            orderTableModel.setRowCount(0);
            for (OrderModel order : orders) {
                orderTableModel.addRow(new Object[]{
                        order.getId(),
                        order.getUserName(),
                        formatMoney(order.getTotalPrice()),
                        order.getStatus(),
                        formatDateTime(order.getCreatedAt())
                });
            }
        } catch (Exception e) {
            showError("Không thể tải danh sách đơn hàng", e);
        }
    }

    private void addFood() {
        FoodRequestModel request = buildFoodRequest(null);
        if (request == null) {
            return;
        }

        try {
            apiClient.post("/foods", request, FoodModel.class);
            refreshFoods();
            JOptionPane.showMessageDialog(this, "Thêm món ăn thành công!");
        } catch (Exception e) {
            showError("Không thể thêm món ăn", e);
        }
    }

    private void editFood() {
        FoodModel selectedFood = getSelectedFood();
        if (selectedFood == null) {
            return;
        }

        FoodRequestModel request = buildFoodRequest(selectedFood);
        if (request == null) {
            return;
        }

        try {
            apiClient.put("/foods/" + selectedFood.getId(), request, FoodModel.class);
            refreshFoods();
            JOptionPane.showMessageDialog(this, "Cập nhật món ăn thành công!");
        } catch (Exception e) {
            showError("Không thể cập nhật món ăn", e);
        }
    }

    private void deleteFood() {
        FoodModel selectedFood = getSelectedFood();
        if (selectedFood == null) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa món \"" + selectedFood.getName() + "\"?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            apiClient.delete("/foods/" + selectedFood.getId());
            refreshFoods();
            JOptionPane.showMessageDialog(this, "Xóa món ăn thành công!");
        } catch (Exception e) {
            showError("Không thể xóa món ăn", e);
        }
    }

    private void updateStock() {
        FoodModel selectedFood = getSelectedFood();
        if (selectedFood == null) {
            return;
        }

        String input = JOptionPane.showInputDialog(
                this,
                "Nhập số lượng tồn kho mới:",
                selectedFood.getStockQuantity()
        );

        if (input == null) {
            return;
        }

        try {
            int quantity = Integer.parseInt(input.trim());
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "Số lượng tồn kho phải >= 0");
                return;
            }

            apiClient.put("/foods/" + selectedFood.getId() + "/stock?quantity=" + quantity, null, FoodModel.class);
            refreshFoods();
            JOptionPane.showMessageDialog(this, "Cập nhật tồn kho thành công!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng tồn kho không hợp lệ");
        } catch (Exception e) {
            showError("Không thể cập nhật tồn kho", e);
        }
    }

    private FoodRequestModel buildFoodRequest(FoodModel existing) {
        CategoryModel[] categories = loadCategories();

        JTextField nameField = new JTextField(existing != null ? existing.getName() : "");
        JTextField priceField = new JTextField(existing != null && existing.getPrice() != null
                ? existing.getPrice().toPlainString()
                : "");
        JTextField stockField = new JTextField(existing != null && existing.getStockQuantity() != null
                ? String.valueOf(existing.getStockQuantity())
                : "0");
        JTextField imageField = new JTextField(existing != null && existing.getImageUrl() != null ? existing.getImageUrl() : "");
        JTextField descriptionField = new JTextField(existing != null && existing.getDescription() != null ? existing.getDescription() : "");
        JCheckBox availableCheck = new JCheckBox("Đang bán", existing == null || existing.isAvailable());

        JComboBox<CategoryOption> categoryBox = new JComboBox<>();
        categoryBox.addItem(new CategoryOption(null, "(Không có danh mục)"));
        if (categories != null) {
            for (CategoryModel category : categories) {
                categoryBox.addItem(new CategoryOption(category.getId(), category.getName()));
            }
        }

        if (existing != null && existing.getCategoryId() != null) {
            for (int i = 0; i < categoryBox.getItemCount(); i++) {
                CategoryOption option = categoryBox.getItemAt(i);
                if (existing.getCategoryId().equals(option.id())) {
                    categoryBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Form panel (left side)
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        formPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        formPanel.add(new JLabel("Tên món"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Giá"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Tồn kho"));
        formPanel.add(stockField);
        formPanel.add(new JLabel("Danh mục"));
        formPanel.add(categoryBox);
        formPanel.add(new JLabel("Ảnh URL (tuỳ chọn)"));
        formPanel.add(imageField);
        formPanel.add(new JLabel("Mô tả (tuỳ chọn)"));
        formPanel.add(descriptionField);
        formPanel.add(availableCheck);

        // Existing foods list (right side)
        JTextArea foodListArea = new JTextArea();
        foodListArea.setEditable(false);
        foodListArea.setLineWrap(true);
        foodListArea.setWrapStyleWord(true);

        StringBuilder foodList = new StringBuilder("DANH SÁCH MÓN ĂN SẲN CÓ:\n");
        foodList.append("==========================\n");
        if (foods.isEmpty()) {
            foodList.append("(Chưa có món ăn nào)\n");
        } else {
            for (FoodModel food : foods) {
                if (existing == null || !food.getId().equals(existing.getId())) {
                    foodList.append("• ")
                            .append(food.getName())
                            .append(" - ")
                            .append(formatMoney(food.getPrice()))
                            .append("\n");
                }
            }
        }
        foodListArea.setText(foodList.toString());
        foodListArea.setCaretPosition(0);
        JScrollPane listScrollPane = new JScrollPane(foodListArea);
        listScrollPane.setPreferredSize(new java.awt.Dimension(200, 250));

        // Main panel with form and list side by side
        JPanel mainPanel = new JPanel(new java.awt.BorderLayout(10, 10));
        mainPanel.add(formPanel, java.awt.BorderLayout.CENTER);
        mainPanel.add(listScrollPane, java.awt.BorderLayout.EAST);

        int result = JOptionPane.showConfirmDialog(
                this,
                mainPanel,
                existing == null ? "Thêm món ăn" : "Sửa món ăn",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên món không được để trống");
                return null;
            }

            BigDecimal price = new BigDecimal(priceField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());
            if (stock < 0) {
                JOptionPane.showMessageDialog(this, "Tồn kho phải >= 0");
                return null;
            }

            CategoryOption selectedCategory = (CategoryOption) categoryBox.getSelectedItem();

            FoodRequestModel request = new FoodRequestModel();
            request.setName(name);
            request.setPrice(price);
            request.setStockQuantity(stock);
            request.setAvailable(stock > 0 && availableCheck.isSelected());
            request.setCategoryId(selectedCategory == null ? null : selectedCategory.id());
            request.setImageUrl(trimToNull(imageField.getText()));
            request.setDescription(trimToNull(descriptionField.getText()));
            return request;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá hoặc tồn kho không hợp lệ");
            return null;
        }
    }

    private CategoryModel[] loadCategories() {
        try {
            CategoryModel[] categories = apiClient.get("/categories", CategoryModel[].class);
            return categories == null ? new CategoryModel[0] : categories;
        } catch (Exception e) {
            return new CategoryModel[0];
        }
    }

    private void viewOrderDetails() {
        OrderModel selectedOrder = getSelectedOrder();
        if (selectedOrder == null) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Mã đơn: ").append(selectedOrder.getId()).append("\n");
        builder.append("Khách hàng: ").append(selectedOrder.getUserName()).append("\n");
        builder.append("Email: ").append(selectedOrder.getUserEmail()).append("\n");
        builder.append("Địa chỉ: ").append(selectedOrder.getDeliveryAddress()).append("\n");
        builder.append("Trạng thái: ").append(selectedOrder.getStatus()).append("\n");
        builder.append("Ngày tạo: ").append(formatDateTime(selectedOrder.getCreatedAt())).append("\n");
        builder.append("Tổng tiền: ").append(formatMoney(selectedOrder.getTotalPrice())).append("\n\n");
        builder.append("Danh sách món:\n");

        if (selectedOrder.getItems() != null) {
            for (OrderItemModel item : selectedOrder.getItems()) {
                builder.append("- ")
                        .append(item.getFoodName())
                        .append(" | SL: ").append(item.getQuantity())
                        .append(" | Đơn giá: ").append(formatMoney(item.getPrice()))
                        .append(" | Thành tiền: ").append(formatMoney(item.getSubtotal()))
                        .append("\n");
            }
        }

        JTextArea textArea = new JTextArea(builder.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(650, 380));

        JOptionPane.showMessageDialog(this, scrollPane, "Chi tiết đơn hàng", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateOrderStatus() {
        OrderModel selectedOrder = getSelectedOrder();
        if (selectedOrder == null) {
            return;
        }

        String[] statuses = {"PENDING", "CONFIRMED", "DELIVERING", "DELIVERED", "CANCELLED"};
        String selectedStatus = (String) JOptionPane.showInputDialog(
                this,
                "Chọn trạng thái mới:",
                "Cập nhật trạng thái",
                JOptionPane.PLAIN_MESSAGE,
                null,
                statuses,
                selectedOrder.getStatus()
        );

        if (selectedStatus == null) {
            return;
        }

        try {
            apiClient.put("/orders/" + selectedOrder.getId() + "/status?status=" + selectedStatus, null, OrderModel.class);
            refreshOrders();
            JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thành công!");
        } catch (Exception e) {
            showError("Không thể cập nhật trạng thái đơn hàng", e);
        }
    }

    private FoodModel getSelectedFood() {
        int selectedRow = foodTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn trước.");
            return null;
        }

        Long id = parseId(foodTableModel.getValueAt(selectedRow, 0));
        for (FoodModel food : foods) {
            if (food.getId() != null && food.getId().equals(id)) {
                return food;
            }
        }
        return null;
    }

    private OrderModel getSelectedOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng trước.");
            return null;
        }

        Long id = parseId(orderTableModel.getValueAt(selectedRow, 0));
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
        return Long.parseLong(String.valueOf(value));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String formatMoney(BigDecimal value) {
        BigDecimal normalized = value == null ? BigDecimal.ZERO : value;
        return moneyFormat.format(normalized) + "đ";
    }

    private String formatDateTime(String value) {
        if (value == null) {
            return "";
        }
        return value.replace('T', ' ');
    }

    private void showError(String title, Exception e) {
        String message = e.getMessage() == null ? "Lỗi không xác định" : e.getMessage();
        JOptionPane.showMessageDialog(this, title + ": " + message);
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Email", "Tên", "Điện thoại", "Địa chỉ", "Vai trò"};
        userTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userTableModel);
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Làm mới");
        JButton addButton = new JButton("Thêm user");
        JButton editButton = new JButton("Sửa user");
        JButton deleteButton = new JButton("Xóa user");

        refreshButton.addActionListener(e -> refreshUsers());
        addButton.addActionListener(e -> addUser());
        editButton.addActionListener(e -> editUser());
        deleteButton.addActionListener(e -> deleteUser());

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshUsers() {
        try {
            UserModel[] response = apiClient.get("/users", UserModel[].class);
            users.clear();
            if (response != null) {
                users.addAll(Arrays.asList(response));
            }

            userTableModel.setRowCount(0);
            for (UserModel user : users) {
                userTableModel.addRow(new Object[]{
                        user.getId(),
                        user.getEmail(),
                        user.getFullName() == null ? "" : user.getFullName(),
                        user.getPhone() == null ? "" : user.getPhone(),
                        user.getAddress() == null ? "" : user.getAddress(),
                        user.getRole() == null ? "" : user.getRole()
                });
            }
        } catch (Exception e) {
            showError("Không thể tải danh sách user", e);
        }
    }

    private void addUser() {
        JTextField emailField = new JTextField();
        JTextField passwordField = new JTextField();
        JTextField fullNameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Mật khẩu:"));
        panel.add(passwordField);
        panel.add(new JLabel("Tên:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Điện thoại:"));
        panel.add(phoneField);
        panel.add(new JLabel("Địa chỉ:"));
        panel.add(addressField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm user", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            if (emailField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email và mật khẩu không được để trống");
                return;
            }

            UserModel newUser = new UserModel();
            newUser.setEmail(emailField.getText().trim());
            newUser.setFullName(trimToNull(fullNameField.getText()));
            newUser.setPhone(trimToNull(phoneField.getText()));
            newUser.setAddress(trimToNull(addressField.getText()));
            newUser.setPassword(passwordField.getText());

            apiClient.post("/users/register", newUser, UserModel.class);
            refreshUsers();
            JOptionPane.showMessageDialog(this, "Thêm user thành công!");
        } catch (Exception e) {
            showError("Không thể thêm user", e);
        }
    }

    private void editUser() {
        UserModel selectedUser = getSelectedUser();
        if (selectedUser == null) {
            return;
        }

        JTextField emailField = new JTextField(selectedUser.getEmail());
        JTextField fullNameField = new JTextField(selectedUser.getFullName() == null ? "" : selectedUser.getFullName());
        JTextField phoneField = new JTextField(selectedUser.getPhone() == null ? "" : selectedUser.getPhone());
        JTextField addressField = new JTextField(selectedUser.getAddress() == null ? "" : selectedUser.getAddress());
        emailField.setEditable(false);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Tên:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Điện thoại:"));
        panel.add(phoneField);
        panel.add(new JLabel("Địa chỉ:"));
        panel.add(addressField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Sửa user", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            selectedUser.setFullName(trimToNull(fullNameField.getText()));
            selectedUser.setPhone(trimToNull(phoneField.getText()));
            selectedUser.setAddress(trimToNull(addressField.getText()));

            apiClient.put("/users/" + selectedUser.getId(), selectedUser, UserModel.class);
            refreshUsers();
            JOptionPane.showMessageDialog(this, "Cập nhật user thành công!");
        } catch (Exception e) {
            showError("Không thể cập nhật user", e);
        }
    }

    private void deleteUser() {
        UserModel selectedUser = getSelectedUser();
        if (selectedUser == null) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa user \"" + selectedUser.getEmail() + "\"?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            apiClient.delete("/users/" + selectedUser.getId());
            refreshUsers();
            JOptionPane.showMessageDialog(this, "Xóa user thành công!");
        } catch (Exception e) {
            showError("Không thể xóa user", e);
        }
    }

    private UserModel getSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn user trước.");
            return null;
        }

        Long id = parseId(userTableModel.getValueAt(selectedRow, 0));
        for (UserModel user : users) {
            if (user.getId() != null && user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    private record CategoryOption(Long id, String label) {
        @Override
        public String toString() {
            return label;
        }
    }
}
