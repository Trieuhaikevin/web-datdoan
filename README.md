# 🍕 Web Đặt Đồ Ăn Online

Ứng dụng đặt đồ ăn online xây dựng bằng Java, áp dụng các nguyên tắc OOP.

---

## 📋 Mô Tả

Web Đặt Đồ Ăn Online là một ứng dụng cho phép người dùng xem menu, đặt món và theo dõi đơn hàng. Admin có thể quản lý kho hàng và xử lý đơn hàng.

---

## 🛠️ Công Nghệ Sử Dụng

### Backend
- **Java 21**
- **Spring Boot 3.5**
- **Spring Data JPA**
- **Spring Security**
- **MySQL 8.0**
- **Lombok**
- **Maven**

### Frontend
- **Java Swing**

---

## 📁 Cấu Trúc Dự Án

```
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
```

---

## 🎯 Chức Năng

### 👤 Người Dùng
- Đăng ký / Đăng nhập tài khoản
- Xem danh sách món ăn theo danh mục
- Thêm món vào giỏ hàng
- Đặt hàng và xem trạng thái đơn hàng

### 👨‍💼 Admin
- Quản lý kho hàng (thêm, sửa, xóa món ăn)
- Quản lý đơn hàng (cập nhật trạng thái)

---

## 🗄️ Sơ Đồ Database

```
users ──────── orders ──────── order_items ──────── foods
                                                      │
categories ───────────────────────────────────────────┘
```

### Các bảng chính
| Bảng | Mô tả |
|------|-------|
| `users` | Thông tin người dùng |
| `foods` | Danh sách món ăn |
| `categories` | Danh mục món ăn |
| `orders` | Đơn hàng |
| `order_items` | Chi tiết từng món trong đơn |

---

## 🔌 API Endpoints

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/foods` | Lấy tất cả món ăn |
| GET | `/api/foods/available` | Lấy món đang có sẵn |
| GET | `/api/foods/category/{id}` | Lấy món theo danh mục |
| POST | `/api/foods` | Thêm món mới |
| DELETE | `/api/foods/{id}` | Xóa món |
| GET | `/api/orders` | Lấy tất cả đơn hàng |
| POST | `/api/orders` | Tạo đơn hàng mới |
| PUT | `/api/orders/{id}/status` | Cập nhật trạng thái |
| GET | `/api/categories` | Lấy danh mục |
| GET | `/api/users` | Lấy danh sách user |

---

## ⚙️ Hướng Dẫn Cài Đặt

### Yêu cầu
- Java 21+
- MySQL 8.0+
- Maven

### Bước 1: Clone project
```bash
git clone https://github.com/your-username/web-datdoan.git
cd web-datdoan
```

### Bước 2: Tạo database
```sql
CREATE DATABASE foodorder_db;
```

### Bước 3: Cấu hình database
Mở file `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/foodorder_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### Bước 4: Chạy Backend
```bash
cd backend
./mvnw spring-boot:run
```
Backend sẽ chạy tại: `http://localhost:8080`

### Bước 5: Chạy Frontend
Mở file `frontend/src/com/foodorder/ui/LoginFrame.java` trong VS Code → nhấn **Run**

---

## 🏗️ OOP Áp Dụng

| Tính chất | Áp dụng ở đâu |
|-----------|---------------|
| **Encapsulation** | Các lớp Model có getter/setter qua Lombok |
| **Inheritance** | Các Frame kế thừa `JFrame` |
| **Polymorphism** | Service implement Interface |
| **Abstraction** | Repository là Interface |

---

## 👨‍💻 Tác Giả

- **Họ tên:** [Tên của bạn]
- **MSSV:** [Mã số sinh viên]
- **Môn:** Lập Trình Hướng Đối Tượng (OOP)

---

## 📄 License

MIT License
