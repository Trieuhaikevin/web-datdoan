🍕 Web Đặt Đồ Ăn Online
Ứng dụng đặt đồ ăn online xây dựng bằng Java, áp dụng các nguyên tắc OOP.

📋 Mô Tả
Web Đặt Đồ Ăn Online là một ứng dụng cho phép người dùng xem menu, đặt món và theo dõi đơn hàng. Admin có thể quản lý kho hàng và xử lý đơn hàng.

🛠️ Công Nghệ Sử Dụng
Backend

Java 21
Spring Boot 3.5
Spring Data JPA
Spring Security
MySQL 8.0
Lombok
Maven

Frontend

Java Swing


📁 Cấu Trúc Dự Án
web-datdoan/
├── backend/                        # Spring Boot API
│   └── src/main/java/com/foodorder/
│       ├── model/                  # Các lớp thực thể
│       │   ├── User.java
│       │   ├── Food.java
│       │   ├── Category.java
│       │   ├── Order.java
│       │   └── OrderItem.java
│       ├── repository/             # Truy vấn database
│       │   ├── UserRepository.java
│       │   ├── FoodRepository.java
│       │   ├── CategoryRepository.java
│       │   ├── OrderRepository.java
│       │   └── OrderItemRepository.java
│       ├── service/                # Xử lý logic nghiệp vụ
│       │   ├── UserService.java
│       │   ├── FoodService.java
│       │   ├── CategoryService.java
│       │   └── OrderService.java
│       ├── controller/             # API endpoints
│       │   ├── UserController.java
│       │   ├── FoodController.java
│       │   ├── CategoryController.java
│       │   └── OrderController.java
│       └── SecurityConfig.java
│
└── frontend/                       # Java Swing UI
    └── src/com/foodorder/
        ├── ApiService.java         # Kết nối với Backend
        └── ui/
            ├── LoginFrame.java         # Màn hình đăng nhập
            ├── RegisterFrame.java      # Màn hình đăng ký
            ├── MainFrame.java          # Màn hình menu chính
            ├── OrderFrame.java         # Màn hình giỏ hàng
            ├── AdminFrame.java         # Dashboard admin
            ├── AdminOrderFrame.java    # Quản lý đơn hàng
            └── AdminFoodFrame.java     # Quản lý kho hàng

🎯 Chức Năng
👤 Người Dùng

Đăng ký / Đăng nhập tài khoản
Xem danh sách món ăn theo danh mục
Thêm món vào giỏ hàng
Đặt hàng và xem trạng thái đơn hàng

👨‍💼 Admin

Quản lý kho hàng (thêm, sửa, xóa món ăn)
Quản lý đơn hàng (cập nhật trạng thái)


🗄️ Sơ Đồ Database
users ──────── orders ──────── order_items ──────── foods
                                                      │
categories ───────────────────────────────────────────┘
Các bảng chính
BảngMô tảusersThông tin người dùngfoodsDanh sách món ăncategoriesDanh mục món ănordersĐơn hàngorder_itemsChi tiết từng món trong đơn
