# 🍕 Ứng Dụng Đặt Đồ Ăn Online

Một hệ thống đặt đồ ăn trực tuyến được xây dựng bằng Java, sử dụng Spring Boot cho backend và Java Swing cho giao diện người dùng desktop. Dự án này áp dụng các nguyên tắc lập trình hướng đối tượng (OOP) để quản lý người dùng, món ăn, danh mục và đơn hàng.

## 📖 Mô Tả Dự Án

Ứng dụng này cho phép người dùng đăng ký tài khoản, duyệt menu đồ ăn theo danh mục, thêm món vào giỏ hàng, đặt hàng và theo dõi trạng thái đơn hàng. Phía admin có thể quản lý món ăn, danh mục và đơn hàng. Backend cung cấp API RESTful để frontend kết nối.

## ✨ Tính Năng Chính

### 👤 Người Dùng
- **Đăng ký và đăng nhập**: Tạo tài khoản mới hoặc đăng nhập với email và mật khẩu.
- **Duyệt menu**: Xem danh sách món ăn theo danh mục, tìm kiếm món có sẵn.
- **Giỏ hàng**: Thêm, xóa món ăn khỏi giỏ hàng.
- **Đặt hàng**: Tạo đơn hàng từ giỏ hàng, xem lịch sử đơn hàng và trạng thái.

### 🛡️ Admin
- **Quản lý món ăn**: Thêm, sửa, xóa món ăn, cập nhật tồn kho.
- **Quản lý danh mục**: Tạo và chỉnh sửa danh mục món ăn.
- **Quản lý đơn hàng**: Xem tất cả đơn hàng, cập nhật trạng thái (Đang xử lý, Đang giao, Hoàn thành, Hủy).
- **Quản lý người dùng**: Xem danh sách người dùng.

## 🛠️ Công Nghệ Sử Dụng

### Backend
- **Java 21**: Ngôn ngữ lập trình chính.
- **Spring Boot 3.5**: Framework cho việc phát triển ứng dụng web.
- **Spring Data JPA**: Quản lý dữ liệu và truy vấn cơ sở dữ liệu.
- **Spring Security**: Xác thực và phân quyền.
- **MySQL 8.0**: Cơ sở dữ liệu quan hệ.
- **Lombok**: Giảm boilerplate code với annotations.
- **Maven**: Quản lý dependencies và build.

### Frontend
- **Java Swing**: Giao diện người dùng desktop.
- **Gson**: Xử lý JSON cho giao tiếp với API.

### Công Cụ Khác
- **Hibernate**: ORM cho JPA.
- **JUnit**: Testing framework.

## 📁 Cấu Trúc Dự Án

```
Food Order Web/
├── backend/                          # Backend Spring Boot
│   ├── src/main/java/com/foodorder/
│   │   ├── config/                   # Cấu hình ứng dụng
│   │   │   ├── DefaultAdminConfig.java
│   │   │   ├── DefaultAdminInitializer.java
│   │   │   └── SecurityConfig.java
│   │   ├── controller/               # REST Controllers
│   │   │   ├── AuthController.java
│   │   │   ├── CategoryController.java
│   │   │   ├── FoodController.java
│   │   │   ├── OrderController.java
│   │   │   └── UserController.java
│   │   ├── dto/                      # Data Transfer Objects
│   │   ├── model/                    # Entity Models
│   │   │   ├── Category.java
│   │   │   ├── Food.java
│   │   │   ├── Order.java
│   │   │   ├── OrderItem.java
│   │   │   └── User.java
│   │   ├── repository/               # JPA Repositories
│   │   ├── service/                  # Business Logic Services
│   │   └── security/                 # Security Components
│   └── src/main/resources/
│       └── application.properties    # Cấu hình ứng dụng
├── frontend/                         # Frontend Java Swing
│   ├── src/main/java/com/foodorder/
│   │   ├── client/                   # API Client
│   │   ├── model/                    # Frontend Models
│   │   └── ui/                       # Swing UI Components
│   └── pom.xml
├── generate_icons.py                 # Script tạo icon
├── resize_icons.py                   # Script resize icon
├── run_frontend.bat                  # Script chạy frontend
├── start.bat                         # Script chạy toàn bộ ứng dụng
└── README.md                         # Tài liệu này
```

## 🗄️ Cơ Sở Dữ Liệu

### Sơ Đồ Quan Hệ
```
users ──────── orders ──────── order_items ──────── foods
                                                      │
categories ───────────────────────────────────────────┘
```

### Các Bảng Chính
- **users**: Lưu thông tin người dùng (id, email, password, fullName, phone, address, role).
- **categories**: Danh mục món ăn (id, name, description).
- **foods**: Món ăn (id, name, description, price, imageUrl, available, stock, category_id).
- **orders**: Đơn hàng (id, user_id, totalAmount, status, createdAt, updatedAt).
- **order_items**: Chi tiết đơn hàng (id, order_id, food_id, quantity, price).

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/register` - Đăng ký

### Foods
- `GET /api/foods` - Lấy tất cả món ăn
- `GET /api/foods/available` - Lấy món có sẵn
- `GET /api/foods/category/{id}` - Lấy món theo danh mục
- `GET /api/foods/{id}` - Lấy món theo ID
- `POST /api/foods` - Tạo món mới
- `PUT /api/foods/{id}` - Cập nhật món
- `PUT /api/foods/{id}/stock` - Cập nhật tồn kho
- `DELETE /api/foods/{id}` - Xóa món

### Categories
- `GET /api/categories` - Lấy tất cả danh mục
- `GET /api/categories/{id}` - Lấy danh mục theo ID
- `POST /api/categories` - Tạo danh mục mới
- `PUT /api/categories/{id}` - Cập nhật danh mục
- `DELETE /api/categories/{id}` - Xóa danh mục

### Orders
- `GET /api/orders` - Lấy tất cả đơn hàng
- `GET /api/orders/user/{id}` - Lấy đơn hàng của user
- `GET /api/orders/{id}` - Lấy đơn hàng theo ID
- `POST /api/orders` - Tạo đơn hàng mới
- `PUT /api/orders/{id}/status` - Cập nhật trạng thái đơn hàng
- `DELETE /api/orders/{id}` - Xóa đơn hàng

### Users
- `GET /api/users` - Lấy tất cả người dùng
- `GET /api/users/{id}` - Lấy người dùng theo ID
- `PUT /api/users/{id}` - Cập nhật người dùng
- `DELETE /api/users/{id}` - Xóa người dùng

## 📝 Chi Tiết Chức Năng (Full Features)

Phần này liệt kê toàn bộ chức năng backend và frontend theo nhóm, bao gồm các endpoint, luồng nghiệp vụ và hành vi quan trọng.

### Authentication & Authorization
- **Đăng ký (public)**: `POST /api/auth/register` — đăng ký tài khoản mới (validate `fullName`, `email`, `password`), trả về `LoginResponse`.
- **Đăng nhập**: `POST /api/auth/login` — xác thực email + mật khẩu, trả về thông tin người dùng (không dùng JWT; session phía client).
- **Mã hóa mật khẩu**: BCrypt (mã hóa khi tạo user hoặc khi nâng cấp mật khẩu không mã hóa).
- **Security config**: `SecurityConfig` tắt CSRF cho dev và đặt CORS permissive.

### Users (Người dùng)
- **GET /api/users** — Lấy danh sách tất cả người dùng (dùng cho admin UI).
- **GET /api/users/{id}** — Lấy chi tiết user.
- **POST /api/users** — Tạo user (Admin) từ object `User` (backend validate email/password, check duplicate, bcrypt password).
- **PUT /api/users/{id}** — Cập nhật thông tin (fullName, phone, address).
- **DELETE /api/users/{id}** — Xóa user.
- **Default admin**: `DefaultAdminInitializer` tạo admin mặc định cấu hình trong `application.properties`.

### Categories (Danh mục)
- **GET /api/categories** — Lấy danh sách danh mục.
- **GET /api/categories/{id}** — Lấy danh mục chi tiết.
- **POST /api/categories** — Tạo danh mục mới.
- **PUT /api/categories/{id}** — Cập nhật danh mục.
- **DELETE /api/categories/{id}** — Xóa danh mục.

### Foods (Món ăn)
- **GET /api/foods** — Lấy tất cả món.
- **GET /api/foods/available** — Lấy món có sẵn (available = true).
- **GET /api/foods/category/{id}** — Lấy món theo danh mục.
- **GET /api/foods/{id}** — Lấy món theo ID.
- **POST /api/foods** — Tạo món mới.
- **PUT /api/foods/{id}** — Cập nhật thông tin món.
- **PUT /api/foods/{id}/stock** — Cập nhật tồn kho.
- **DELETE /api/foods/{id}** — Xóa món.

### Orders (Đơn hàng)
- **GET /api/orders** — Lấy tất cả đơn hàng (admin).
- **GET /api/orders/user/{id}** — Lấy đơn của một user.
- **GET /api/orders/{id}** — Lấy chi tiết đơn.
- **POST /api/orders** — Tạo đơn mới từ frontend (từ `CartStore`).
- **PUT /api/orders/{id}/status** — Cập nhật trạng thái đơn (PENDING, CONFIRMED, DELIVERING, DELIVERED, CANCELLED).
- **DELETE /api/orders/{id}** — Xóa đơn.

### Frontend (Java Swing) — Người dùng
- **Đăng nhập**: `LoginFrame` gọi `/auth/login`, lưu `UserSession`, điều hướng tới `AdminFrame` (nếu admin) hoặc `MainFrame`.
- **Đăng ký**: `RegisterFrame` gọi `/auth/register`.
- **MainFrame**: Hiển thị menu theo danh mục, tìm kiếm, lọc, chọn món.
- **Giỏ hàng**: `CartStore`, `CartItem` giữ trạng thái giỏ; thêm/xóa/sửa số lượng.
- **Đặt hàng**: Gửi `POST /api/orders` với danh sách `CreateOrderItemRequest`.
- **Xem lịch sử đơn hàng**: Gọi `GET /api/orders/user/{id}`.

### Frontend (Java Swing) — Admin
- **AdminFrame**: Bảng điều khiển quản trị.
  - Quản lý người dùng: Hiển thị, thêm (`POST /api/users`), sửa (`PUT /api/users/{id}`), xóa (`DELETE /api/users/{id}`).
  - Quản lý món ăn: CRUD món (gọi `/api/foods`).
  - Quản lý danh mục: CRUD danh mục.
  - Quản lý đơn hàng: Xem tất cả đơn, cập nhật trạng thái (`PUT /api/orders/{id}/status`).

### Client HTTP & Models
- **`ApiClient`**: Wrapper HTTP sử dụng `HttpURLConnection`, base `http://localhost:8080/api`, JSON via Gson, xử lý lỗi (đọc `message` hoặc `error`).
- **Frontend models**: `UserModel`, `LoginRequestModel`, `LoginResponse`, `FoodModel`, `CategoryModel`, `OrderModel`, `RegisterRequestModel`, `CartItem`.

### Validation & Error Handling
- Backend validate các trường bắt buộc (`fullName`, `email`, `password`) trong `AuthController` và `UserService`.
- `UserService` kiểm tra duplicate email và ném `IllegalArgumentException` nếu trùng.
- `ApiClient` hiển thị thông điệp lỗi trả về từ server cho người dùng.

### Utilities, Scripts & Tooling
- `start.bat` — khởi động backend + frontend.
- `run_frontend.bat` — chạy frontend.
- `generate_icons.py`, `resize_icons.py` — trợ giúp xử lý icon.
- Maven multi-module (root/backend/frontend) — build và package.

### Database & Persistence
- Entities: `User`, `Food`, `Category`, `Order`, `OrderItem`.
- Repositories: Spring Data JPA repositories (ví dụ `UserRepository.findByEmail`, `findByRole`).
- `spring.jpa.hibernate.ddl-auto=update` — tự động cập nhật schema trong dev.

### Other Behaviors / Notable Limitations
- Không dùng JWT/OAuth; session lưu ở client (`UserSession`).
- Không có xác thực email, reset password, 2FA, hoặc phân trang/lọc nâng cao trong admin.
- CORS được bật rộng rãi cho phát triển.

## ⚙️ Hướng Dẫn Cài Đặt và Chạy

### Yêu Cầu Hệ Thống
- Java 21 hoặc cao hơn
- MySQL 8.0 hoặc cao hơn
- Maven 3.6+
- Git

### Bước 1: Clone Repository
```bash
git clone <repository-url>
cd "Food Order Web"
```

### Bước 2: Thiết Lập Cơ Sở Dữ Liệu
1. Cài đặt MySQL và tạo database:
```sql
CREATE DATABASE foodorder_db;
```
2. Cập nhật thông tin kết nối trong `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/foodorder_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Bước 3: Chạy Ứng Dụng
#### Chạy Toàn Bộ (Khuyến Nghị)
```bash
start.bat
```
Script này sẽ khởi động cả backend và frontend.

#### Chạy Riêng Lẻ
- **Backend**:
```bash
cd backend
./mvnw spring-boot:run
```
Backend sẽ chạy tại `http://localhost:8080`

- **Frontend**:
```bash
run_frontend.bat
```
Hoặc mở `frontend/src/main/java/com/foodorder/ui/LoginFrame.java` trong IDE và chạy.

### Bước 4: Truy Cập Ứng Dụng
- Mở giao diện desktop Java Swing.
- Đăng nhập với tài khoản admin mặc định:
  - Email: admin@foodorder.com
  - Password: admin123

## 🏗️ Áp Dụng Nguyên Tắc OOP

Dự án này minh họa các nguyên tắc lập trình hướng đối tượng:

| Nguyên Tắc | Ví Dụ Trong Code |
|------------|------------------|
| **Encapsulation** | Các trường private trong model classes, sử dụng getters/setters qua Lombok |
| **Inheritance** | `AdminFrame`, `LoginFrame` kế thừa từ `JFrame` |
| **Polymorphism** | Services implement interfaces, override methods |
| **Abstraction** | Repository interfaces ẩn chi tiết truy vấn database |

## 🧪 Chạy Tests
```bash
cd backend
./mvnw test
```

## 📦 Build Dự Án
```bash
cd backend
./mvnw clean package
```

## 🤝 Đóng Góp
1. Fork repository
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

## 📄 Giấy Phép
Dự án này được phân phối dưới giấy phép MIT. Xem file `LICENSE` để biết thêm chi tiết.

## 👨‍💻 Tác Giả
- **Họ tên**: Hoàng Hai Triều
- **MSSV**: B22DCVT557
- **Môn học**: Lập Trình Hướng Đối Tượng (OOP)

## 📞 Liên Hệ
Nếu có câu hỏi hoặc góp ý, vui lòng tạo issue trên GitHub hoặc liên hệ qua email.
