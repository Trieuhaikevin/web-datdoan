# 🍕 Ứng Dụng Đặt Đồ Ăn Online

Ứng dụng đặt đồ ăn desktop (Java Swing) kết hợp với backend RESTful (Spring Boot + JPA). File này mô tả chi tiết các chức năng hiện có, hành vi nghiệp vụ quan trọng, API, cách chạy và kiểm thử.

**Mục tiêu**: cho phép người dùng duyệt menu theo danh mục, đặt hàng với thông tin người nhận (tên, số điện thoại, địa chỉ), quản lý đơn hàng, nhận thông báo khi trạng thái đơn thay đổi; admin quản lý món/danh mục/đơn/người dùng.

---

## 📑 Nội dung

1. [✨ Tính Năng Chính](#-tính-năng-chính)
2. [📁 Cấu Trúc Dự Án](#-cấu-trúc-dự-án)
3. [🗄️ Cơ Sở Dữ Liệu](#️-cơ-sở-dữ-liệu)
4. [🔌 API Endpoints](#-api-endpoints)
5. [📝 Chi Tiết Chức Năng](#-chi-tiết-chức-năng)
6. [⚙️ Hướng Dẫn Cài Đặt và Chạy](#️-hướng-dẫn-cài-đặt-và-chạy)
7. [💻 Frontend Components](#-frontend-components--behavior)
8. [📊 Data Model](#-data-model-entities)
9. [✅ Kiểm thử](#-kiểm-thử-manual)
10. [🚀 Vận hành & Mở rộng](#-vận-hành--mở-rộng)
11. [🔧 Troubleshooting](#-troubleshooting)
12. [🏗️ Áp Dụng Nguyên Tắc OOP](#️-áp-dụng-nguyên-tắc-oop)
13. [👨‍💻 Tác Giả & Liên hệ](#-tác-giả--liên-hệ)

---

## ✨ Tính Năng Chính

### Cho Người Dùng
- 🔐 **Đăng ký / Đăng nhập**: Tạo tài khoản và quản lý profile cá nhân.
- 🍽️ **Duyệt menu**: Xem danh sách món ăn **nhóm theo danh mục**, chia sẻ hình ảnh, giá, số lượng tồn kho.
- 🛒 **Giỏ hàng thông minh**: Chọn số lượng bằng `QuantitySelector`, thêm/xóa món.
- 📋 **Đặt hàng**: Nhập thông tin người nhận (tên, điện thoại, địa chỉ giao hàng).
- 📜 **Lịch sử đơn hàng**: Xem toàn bộ đơn, trạng thái, chi tiết items.
- ❌ **Hủy đơn**: Cho phép hủy đơn ở trạng thái PENDING hoặc CONFIRMED.
- 🔔 **Thông báo thời gian thực**: Nhận thông báo khi admin thay đổi trạng thái đơn hàng (không lặp lại).

### Cho Admin
- 👥 **Quản lý người dùng**: Xem danh sách, xóa tài khoản.
- 🍜 **Quản lý món ăn**: Thêm/sửa/xóa, cập nhật tồn kho, gán danh mục.
- 📂 **Quản lý danh mục**: CRUD danh mục, tách liên kết khi xóa.
- 📦 **Quản lý đơn hàng**: Xem tất cả đơn, cập nhật trạng thái (PENDING → CONFIRMED → DELIVERING → DELIVERED).
- **Luật tồn kho**: Tự động trừ tồn kho khi đơn chuyển sang DELIVERING/DELIVERED.
- 🗑️ **Safe Delete**: Nếu xóa món có trong đơn PENDING/CONFIRMED, tự động chuyển đơn sang CANCELLED.

---

## 📁 Cấu Trúc Dự Án

```
Food Order Web/
├── backend/                          # Spring Boot REST API
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/foodorder/
│   │   │   │   ├── FoodOrderApplication.java
│   │   │   │   ├── config/
│   │   │   │   │   ├── DefaultAdminConfig.java
│   │   │   │   │   ├── DefaultAdminInitializer.java
│   │   │   │   │   └── SecurityConfig.java
│   │   │   │   ├── controller/
│   │   │   │   │   ├── AuthController.java        # Đăng ký/Đăng nhập
│   │   │   │   │   ├── FoodController.java        # CRUD Món
│   │   │   │   │   ├── CategoryController.java    # CRUD Danh mục
│   │   │   │   │   ├── OrderController.java       # CRUD Đơn hàng
│   │   │   │   │   └── UserController.java        # Quản lý User
│   │   │   │   ├── dto/                           # Data Transfer Objects
│   │   │   │   ├── model/                         # JPA Entities
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Category.java
│   │   │   │   │   ├── Food.java
│   │   │   │   │   ├── Order.java
│   │   │   │   │   └── OrderItem.java
│   │   │   │   ├── repository/                    # Spring Data JPA repositories
│   │   │   │   ├── service/                       # Business logic
│   │   │   │   │   ├── AuthService.java
│   │   │   │   │   ├── FoodService.java
│   │   │   │   │   ├── CategoryService.java
│   │   │   │   │   ├── OrderService.java
│   │   │   │   │   └── UserService.java
│   │   │   │   └── security/                      # JWT / Spring Security
│   │   │   └── resources/
│   │   │       └── application.properties         # DB config, server port
│   │   └── test/
│   ├── pom.xml
│   ├── mvnw & mvnw.cmd               # Maven Wrapper
│   └── HELP.md
│
├── frontend/                         # Java Swing Desktop App
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/foodorder/
│   │   │   │   ├── client/
│   │   │   │   │   └── ApiClient.java             # HTTP client (Gson)
│   │   │   │   ├── model/
│   │   │   │   │   ├── UserSession.java           # Singleton user info
│   │   │   │   │   ├── CartStore.java             # Singleton cart
│   │   │   │   │   ├── NotificationStore.java     # Persisted notifications
│   │   │   │   │   ├── FoodModel.java
│   │   │   │   │   ├── OrderModel.java
│   │   │   │   │   └── CategoryModel.java
│   │   │   │   └── ui/
│   │   │   │       ├── LoginFrame.java            # Login/Registration
│   │   │   │       ├── MainFrame.java             # Menu + Polling
│   │   │   │       ├── OrderFrame.java            # Cart + Order History
│   │   │   │       ├── AdminFrame.java            # Admin management
│   │   │   │       └── QuantitySelector.java      # Quantity control
│   │   │   └── resources/
│   │   │       └── icons/                         # UI icons
│   │   └── test/
│   ├── pom.xml
│   ├── run.bat
│   └── target/ (build artifacts)
│
├── pom.xml                           # Parent pom (multi-module)
├── README.md                         # File này
├── run_frontend.bat
└── start.bat
```

---

## 🗄️ Cơ Sở Dữ Liệu

### Schema

#### Bảng `user`
```sql
CREATE TABLE user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  full_name VARCHAR(255),
  phone VARCHAR(20),
  address VARCHAR(500),
  role VARCHAR(50) DEFAULT 'USER',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Bảng `category`
```sql
CREATE TABLE category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Bảng `food`
```sql
CREATE TABLE food (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  price DECIMAL(10, 2) NOT NULL,
  image_url VARCHAR(500),
  available BOOLEAN DEFAULT TRUE,
  stock_quantity INT DEFAULT 0,
  category_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);
```

#### Bảng `order`
```sql
CREATE TABLE `order` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  receiver_name VARCHAR(255),
  receiver_phone VARCHAR(20),
  delivery_address VARCHAR(500),
  total_price DECIMAL(10, 2) NOT NULL,
  status VARCHAR(50) DEFAULT 'PENDING',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);
```

#### Bảng `order_item`
```sql
CREATE TABLE order_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  food_id BIGINT,
  quantity INT NOT NULL DEFAULT 1,
  price DECIMAL(10, 2) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (order_id) REFERENCES `order`(id) ON DELETE CASCADE,
  FOREIGN KEY (food_id) REFERENCES food(id) ON DELETE SET NULL
);
```

### Quan hệ
- `User` 1-N `Order`
- `Category` 1-N `Food`
- `Order` 1-N `OrderItem`
- `Food` 1-N `OrderItem` (nullable — để lịch sử khi food bị xóa)

---

## 📝 Chi Tiết Chức Năng

### 1.1. Xác thực & Người dùng

- **Đăng ký**: `POST /api/auth/register` — tạo user mới, lưu email/password.
- **Đăng nhập**: `POST /api/auth/login` — xác thực, trả thông tin session (userId, fullName, email, phone, address, role).
- **Cập nhật profile**: `PUT /api/users/{id}` — update fullName, phone, address.
- **Quản lý người dùng** (admin): `GET /api/users`, xem/xóa tài khoản.

### 1.2. Menu & Danh mục

- **Lấy danh sách món**: `GET /api/foods` — tất cả món ăn.
- **Lấy món có sẵn**: `GET /api/foods/available` — chỉ hiển thị có stock > 0.
- **Lấy theo danh mục**: `GET /api/foods/category/{id}` — lọc món theo danh mục.
- **Frontend UI**: 
  - Hiển thị menu **nhóm theo danh mục**: mỗi danh mục là một section với header (tên category) và grid món.
  - Những món **không có danh mục** hiển thị dưới section "**Những món khác**".
- **CRUD Danh mục** (admin): 
  - `GET /api/categories` — danh sách tất cả.
  - `POST /api/categories` — thêm mới.
  - `PUT /api/categories/{id}` — cập nhật.
  - `DELETE /api/categories/{id}` — xóa (backend tháo liên kết từ các món rồi mới xóa để giữ lịch sử).

### 1.3. Quản lý Món Ăn

- **CRUD Món** (admin):
  - `POST /api/foods` — thêm mới (name, description, imageUrl, price, stockQuantity, category).
  - `PUT /api/foods/{id}` — cập nhật.
  - `PUT /api/foods/{id}/stock` — cập nhật số lượng tồn kho.
  - `DELETE /api/foods/{id}` — xóa.

- **Hành vi khi xóa một món**:
  - Nếu món đã nằm trong các đơn **PENDING** hoặc **CONFIRMED**, các đơn đó sẽ tự động chuyển trạng thái thành `CANCELLED`.
  - OrderItem vẫn được giữ trong lịch sử nhưng `food` sẽ set về `null` (để hiển thị "Món đã xóa" trong UI).
  - Điều này đảm bảo không mất lịch sử và tự động hủy những đơn liên quan.

### 1.4. Giỏ hàng & Đặt hàng

- **Giỏ hàng** (frontend):
  - Lưu trữ local: `CartStore` (singleton) giữ danh sách items chưa thanh toán.
  - Thêm/xóa/cập nhật số lượng.
  - **Bộ chọn số lượng**: `QuantitySelector` với nút `-`, field hiển thị số lượng, nút `+`.
    - Min = 1, Max = stock của món đó.
    - Khi tồn kho thay đổi, UI update giới hạn max.

- **Tạo đơn hàng**: `POST /api/orders`
  - Payload:
    ```json
    {
      "userId": 1,
      "receiverName": "Nguyễn Văn A",
      "receiverPhone": "0123456789",
      "deliveryAddress": "123 Đường ABC, TP HCM",
      "items": [
        {"foodId": 1, "quantity": 2},
        {"foodId": 3, "quantity": 1}
      ]
    }
    ```
  - Backend kiểm tra tồn kho (stock >= quantity), tạo order với trạng thái `PENDING`.
  - **Không trừ kho** lúc này (chỉ kiểm tra có đủ hay không).

### 1.5. Đơn hàng & Trạng thái

- **Xem lịch sử**: `GET /api/orders/user/{id}` — danh sách tất cả đơn của user.
- **Xem đơn mới nhất** (dùng cho polling): `GET /api/orders/user/{id}/latest` — trả đơn được tạo/update gần nhất.
- **Chi tiết đơn**: `GET /api/orders/{id}`.
- **Cập nhật trạng thái** (admin): `PUT /api/orders/{id}/status`
  - Payload: `{"status": "CONFIRMED"}` hoặc `DELIVERING`, `DELIVERED`, `CANCELLED`.
  - Các chuyển đổi hợp lệ:
    - `PENDING` → `CONFIRMED`, `CANCELLED`.
    - `CONFIRMED` → `DELIVERING`, `CANCELLED`.
    - `DELIVERING` → `DELIVERED`.
    - `CANCELLED` / `DELIVERED` không chuyển tiếp được.

- **Luật Tồn kho khi chuyển trạng thái**:
  - `PENDING` / `CONFIRMED` / `CANCELLED`: **không trừ kho** (chỉ giữ nguyên).
  - `DELIVERING` / `DELIVERED`: **trừ tồn kho** theo số lượng từng item trong order.
    - Nếu không đủ tồn → backend trả lỗi `400 Bad Request` và từ chối chuyển trạng thái.
    - Nếu đủ → trừ tồn kho ngay lập tức và persist vào DB.

- **Hủy đơn** (user): `PUT /api/orders/{id}/cancel`
  - User chỉ có thể hủy khi đơn có trạng thái `PENDING` hoặc `CONFIRMED`.
  - Admin có thể hủy bất kỳ lúc nào (trừ đã `DELIVERED`).
  - Khi hủy: trạng thái → `CANCELLED`, tồn kho không bị trừ.

### 1.6. Thông báo Trạng thái Đơn hàng

- **Frontend Polling**:
  - Mỗi 10 giây, frontend gọi `GET /api/orders/user/{id}/latest` để lấy đơn mới nhất.
  - So sánh với lần lấy trước:
    - Nếu `orderId` mới hoặc `status` thay đổi → hiển thị notification.
    - Nếu không có thay đổi → im lặng.

- **Phi Lặp Thông báo**:
  - Frontend lưu các cặp `(orderId, status)` đã thông báo vào file JSON:
    - **Windows**: `%USERPROFILE%\.foodorder\notifications.json`
    - **Unix/Mac**: `~/.foodorder/notifications.json`
  - Mỗi cặp chỉ thông báo **1 lần duy nhất** (ngay cả khi logout/login lại, nó vẫn ghi nhớ).
  - Để reset, user xóa file.

- **Kiểu thông báo**: JOptionPane popup (hiện tại), có thể upgrade sang non-blocking toast hoặc SSE/WebSocket.

### 1.7. Luật Toàn vẹn Dữ liệu

- **Khi xóa Danh mục**:
  - Backend detach các món (category = null) trước khi xóa category.
  - Đảm bảo Food không còn FK lệ thuộc.

- **Khi xóa Monk**:
  - Nếu có trong order PENDING/CONFIRMED → order chuyển `CANCELLED`.
  - OrderItem giữ lại nhưng `food` = null.

---

## 🔌 API Endpoints

### Authentication
```
POST   /api/auth/register       (email, fullName, password)
POST   /api/auth/login          (email, password)
```

### Foods (CRUD)
```
GET    /api/foods
GET    /api/foods/available
GET    /api/foods/{id}
GET    /api/foods/category/{id}
POST   /api/foods               (admin)
PUT    /api/foods/{id}          (admin)
PUT    /api/foods/{id}/stock    (admin, số lượng)
DELETE /api/foods/{id}          (admin)
```

### Categories (CRUD)
```
GET    /api/categories
GET    /api/categories/{id}
POST   /api/categories          (admin)
PUT    /api/categories/{id}     (admin)
DELETE /api/categories/{id}     (admin)
```

### Orders
```
GET    /api/orders                 (admin, tất cả đơn)
GET    /api/orders/user/{id}       (user, lịch sử của anh)
GET    /api/orders/user/{id}/latest (user, đơn mới nhất — dùng polling)
GET    /api/orders/{id}            (chi tiết)
POST   /api/orders                 (tạo đơn mới)
PUT    /api/orders/{id}/status     (admin, cập nhật trạng thái)
PUT    /api/orders/{id}/cancel     (user/admin, hủy đơn)
DELETE /api/orders/{id}            (admin)
```

### Users
```
GET    /api/users              (admin)
GET    /api/users/{id}
PUT    /api/users/{id}         (update profile)
DELETE /api/users/{id}         (admin)
```

---

## 💻 Frontend Components & Behavior

- **LoginFrame**: Đăng nhập / Đăng ký tài khoản.
  - Lưu session vào `UserSession` singleton.

- **MainFrame**: Menu chính — hiển thị món ăn.
  - **Nhóm theo danh mục**: mỗi category là một section view.
  - **Food Card**:
    - Image (scaled, fit inside card).
    - Name (HTML wrapped để tránh truncate).
    - Price, Stock.
    - **QuantitySelector** (`- [value] +`).
    - "Add to Cart" button.
  - Khi nhấn "Add to Cart" → thêm vào `CartStore`.

- **OrderFrame**: Giỏ hàng & Lịch sử đơn (tabbed view).
  - **Tab Giỏ hàng** (Cart):
    - Hiển thị items, số lượng, giá.
    - Cho phép xóa item, thay đổi số lượng.
    - Field nhập: Tên người nhận, SĐT, Địa chỉ giao hàng.
    - Nút "Đặt hàng" → `POST /api/orders` với receiver info.
  - **Tab Lịch sử** (Order History):
    - Danh sách đơn của user.
    - Chi tiết đơn (items, trạng thái, receiver info).
    - Nút hủy (nếu trạng thái cho phép).

- **AdminFrame**: Quản lý (tabbed).
  - Users: danh sách, xóa.
  - Foods: CRUD, cập nhật tồn kho.
  - Categories: CRUD.
  - Orders: danh sách, xem chi tiết (bao gồm receiver name/phone/address), cập nhật trạng thái.

- **Notification behavior**:
  - Polling thread chạy mỗi 10s.
  - Phát hiện thay đổi → hiển thị popup.
  - Lưu vào `NotificationStore` (file JSON).

---

## 📊 Data Model (Entities)

### User
```java
id: Long (PK)
email: String (unique)
password: String (hashed)
fullName: String
phone: String
address: String
role: String (USER, ADMIN)
```

### Category
```java
id: Long (PK)
name: String
description: String
```

### Food
```java
id: Long (PK)
name: String
description: String
price: BigDecimal
imageUrl: String
available: Boolean
stockQuantity: Integer
category: Category (FK, nullable)
```

### Order
```java
id: Long (PK)
user: User (FK)
receiverName: String
receiverPhone: String
deliveryAddress: String
totalPrice: BigDecimal
status: String (PENDING, CONFIRMED, DELIVERING, DELIVERED, CANCELLED)
createdAt: LocalDateTime
updatedAt: LocalDateTime
```

### OrderItem
```java
id: Long (PK)
order: Order (FK)
food: Food (FK, nullable — có thể null nếu food được xóa)
quantity: Integer
price: BigDecimal
```

---

## ⚙️ Hướng Dẫn Cài Đặt và Chạy

### Yêu cầu
- Java 21+
- Maven
- MySQL / PostgreSQL / hoặc H2 (dev)

### Cấu hình
1. Chỉnh sửa `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/foodorder
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.ddl-auto=update
   ```

### Start Backend
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```
Backend sẽ lắng nghe trên `http://localhost:8080`.

### Build & Run Frontend
**Option 1: Maven build**
```powershell
cd frontend
mvn clean package
java -cp target/classes;target/lib/* com.foodorder.client.LoginFrame
```

**Option 2: IDE (intellij/Eclipse)**
- Mở project frontend.
- Run `com.foodorder.client.LoginFrame` as Java Application.

---

## ✅ Kiểm thử (Manual)

### Test Case 1: Đăng ký & Đăng nhập
1. Chạy frontend → Login window.
2. Click "Đăng ký" → nhập email, fullName, password.
3. Click "Đăng nhập" → sử dụng credentials vừa tạo.
4. ✅ Expect: MainFrame hiển thị menu.

### Test Case 2: Đặt hàng
1. MainFrame: chọn một số món, dùng `QuantitySelector` để chỉnh số lượng.
2. Click "Add to Cart" cho mỗi món.
3. Click tab "Giỏ hàng" (OrderFrame).
4. Nhập receiver info (tên, SĐT, địa chỉ).
5. Click "Đặt hàng".
6. ✅ Expect: Order được tạo, OrderHistory hiển thị đơn ở trạng thái `PENDING`.

### Test Case 3: Admin cập nhật trạng thái & Stock
1. Login as admin.
2. AdminFrame → Orders tab.
3. Tìm đơn vừa tạo, click "Update Status" → chọn `CONFIRMED`.
4. ✅ Stock vẫn giữ nguyên (chưa trừ).
5. Update Status → `DELIVERING`.
6. ✅ Expect: Backend trừ tồn kho; nếu không đủ, lỗi 400.

### Test Case 4: User nhận thông báo
1. User trong MainFrame thấy đơn.
2. Admin AdminFrame → Update Status → `DELIVERED`.
3. ✅ Expect: User nhận notification popup sau ~10s polling.
4. Nếu đóng popup, lần tới polling không show lại (vì đã lưu vào `NotificationStore`).

### Test Case 5: Hủy đơn
1. User OrderHistory → chọn đơn `PENDING`.
2. Click "Cancel".
3. ✅ Expect: Trạng thái → `CANCELLED`, stock không thay đổi.

### Test Case 6: Xóa là Món có trong Đơn
1. Admin AdminFrame → Foods tab → tìm một món trong đơn `PENDING/CONFIRMED`.
2. Click Delete.
3. ✅ Expect:
   - Đơn liên quan chuyển `CANCELLED`.
   - OrderItem vẫn lưu nhưng `food` = null (hiển thị "Món đã xóa").

### Test Case 7: Xóa Danh mục
1. Admin AdminFrame → Categories tab.
2. Chọn category có món → Delete.
3. ✅ Expect: Foods trong category đó có `category` = null, hiển thị ở "Những món khác".

---

## 🚀 Vận hành & Mở rộng

### Hiện tại
- **Notifications**: polling mỗi 10s via `GET /api/orders/user/{id}/latest`.
- **Stock rules**: trừ khi chuyển sang `DELIVERING`/`DELIVERED`; không trừ cho `PENDING`/`CONFIRMED`/`CANCELLED`.
- **History**: lưu giữ order item ngay cả khi food bị xóa.

### Tối ưu hóa tiềm năng
- **Real-time thông báo**: chuyển sang Server-Sent Events (SSE) hoặc WebSocket thay vì polling.
- **Caching**: cache danh sách category/foods ở frontend để giảm request.
- **Batch operations**: admin mass-update trạng thái nhiều đơn cùng lúc.
- **Payment integration**: thêm gateway thanh toán (Stripe, Momo, v.v.).
- **Search & filter**: tìm kiếm món, lọc đơn theo trạng thái, ngày.
- **Database migrations**: dùng Flyway/Liquibase cho production (thay `ddl-auto=update`).

### Rollback Stock Policy
- Hiện: khi chuyển `DELIVERING`, trừ kho; nếu sau đó cancel order, stock không được khôi phục.
- **Policy**: cần định nghĩa: có nên khôi phục stock khi order cancel từ `DELIVERING` hay không?

---

## 🔧 Troubleshooting

### Frontend không build
- **Giải pháp**: Kiểm tra Maven ở PATH (`mvn --version`).
- Nếu không: tải Maven từ [maven.apache.org](https://maven.apache.org).
- Hoặc dùng IDE (IntelliJ / Eclipse) để build.

### Notifications lặp lại sau khi close app
- **Giải pháp**: Xóa file `%USERPROFILE%\.foodorder\notifications.json` (Windows) hoặc `~/.foodorder/notifications.json` (Unix).
- Hoặc: implement nút "Reset Notifications" trong UI.

### Ảnh không hiển thị trên Food Card
- **Giải pháp**: Kiểm tra `imageUrl` có hợp lệ HTTP/local path.
- Hoặc thêm fallback icon mặc định nếu URL invalid.

### Database connection error
- **Giải pháp**: Kiểm tra `application.properties` DB credentials.
- Chắc chắn MySQL / DB server đang chạy.
- Thử tạo DB: `CREATE DATABASE foodorder;`.

### Stock không được trừ khi update Status → DELIVERING
- **Giải pháp**: Backend kiểm tra stock; nếu không đủ, trả lỗi 400.
- Xem log backend để debug.

---

## 🏗️ Áp Dụng Nguyên Tắc OOP

Dự án này tuân theo các quy tắc thiết kế hướng đối tượng (OOP) cốt lõi:

### 1. **Encapsulation (Đóng gói)**
- **Backend**: Các Service class đóng gói logic nghiệp vụ, Controller chỉ xử lý HTTP request/response.
- Ví dụ: `OrderService` quản lý create order, update status, deduct stock; `OrderController` chỉ gọi service.
- **Frontend**: `CartStore`, `UserSession`, `NotificationStore` là singletons, đóng gói trạng thái toàn cục.

### 2. **Inheritance (Kế thừa)**
- **JPA Entities**: Có thể sử dụng `BaseEntity` chung (id, createdAt, updatedAt).
- **Controllers**: Có thể sử dụng `BaseController` để xử lý error chung.
- **Swing Components**: Tái sử dụng `JFrame`, `JPanel` để xây dựng UI.

### 3. **Polymorphism (Đa hình)**
- **Service interfaces**: `FoodService`, `OrderService`, `CategoryService` cùng implement logic business.
- **Repository pattern**: `JpaRepository<T, ID>` cung cấp CRUD generics cho tất cả entities.
- **Exception handling**: `ResponseStatusException` được sử dụng để tạo HTTP responses động.

### 4. **Abstraction (Trừu tượng)**
- **DTOs**: `CreateOrderRequest`, `OrderResponse`, `FoodResponse` trừu tượng hóa dữ liệu gửi/nhận.
- **Repository**: `OrderRepository.findFirstByUserIdOrderByCreatedAtDesc()` trừu tượng query DB phức tạp.
- **ApiClient**: `client.doPost()`, `client.doGet()` trừu tượng HTTP calls từ UI layers.

### 5. **Single Responsibility Principle (SRP)**
- `FoodService` chỉ quản lý Food; `OrderService` chỉ quản lý Order.
- `LoginFrame` xử lý login UI; `MainFrame` xử lý menu display.
- Mỗi class có 1 trách nhiệm chính, không trộn logic.

### 6. **Open/Closed Principle (OCP)**
- Backend mở rộng bằng cách thêm endpoint trong Controller mà không sửa code cũ.
- Frontend mở rộng UI bằng cách thêm Frame mới mà không thay đổi Frame cũ.

### 7. **Dependency Injection**
- **Spring Boot**: `@Autowired` inject `Service` vào `Controller`.
- **Frontend**: Singletons (`UserSession`, `CartStore`) được inject vào frames.

### 8. **Model-View-Controller (MVC)**
- **Backend**: Models (entities) → Service (business logic) → Controller (HTTP layer).
- **Frontend**: Models (data classes) → UI Frames (views) → ApiClient (controller).

---

## 👨‍💻 Tác Giả & Liên hệ

**Dự án**: Ứng Dụng Đặt Đồ Ăn Online | Food Order Application

**Phiên bản**: 1.0

**Ngôn ngữ**: Tiếng Việt (Documentation) / Java (Code)

**Công nghệ chính**:
- Backend: Java 21, Spring Boot 3.x, Spring Data JPA, MySQL
- Frontend: Java Swing, Gson (HTTP client)
- Build: Maven, Java 21+

**Tác giả & Nhóm**:
- **Nhóm Phát triển**: Nhóm 7
- **Họ và tên**: Hoàng Hải Triều
- **Email**: haitrieuhoang0312@gmail.com
- **Mã sinh viên**: B22DCVT557

**Đóng góp**:
Nếu bạn muốn đóng góp hoặc báo cáo lỗi:
1. Fork repository.
2. Tạo branch feature: `git checkout -b feature/YourFeature`.
3. Commit changes: `git commit -m 'Add YourFeature'`.
4. Push: `git push origin feature/YourFeature`.
5. Tạo Pull Request.


**Lời cảm ơn**:
- Spring Boot team cho framework mạnh mẽ.
- Java Swing community cho UI components.
- Maven team cho build tool.