# social-media-micro-project

## Giới thiệu dự án

Đây là dự án mạng xã hội mini, nơi người dùng có thể:
- Đăng ký, đăng nhập, cập nhật thông tin cá nhân
- Kết bạn, gửi/nhận lời mời kết bạn, hủy kết bạn
- Đăng bài viết, bình luận, thích bài viết
- Nhắn tin, trò chuyện với bạn bè
- Đề xuất kết bạn, tìm kiếm người dùng, tìm kiếm cuộc trò chuyện
- Cập nhật ảnh đại diện, ảnh bìa (lưu trữ trên Cloudinary)
- Nhận thông báo khi có tương tác mới

Dự án được xây dựng với mục tiêu thực hành teamwork, phát triển ứng dụng thực tế, áp dụng các công nghệ hiện đại.

## Thành viên nhóm
- **Đinh Sỹ Quốc Doanh** (Leader project, Backend)
- **Nguyễn Anh Tú** (Database)
- **Lê Hoàng Anh Thư** (Frontend Mobile)
- **Phạm Thị Hoài Ngọc** (Frontend Mobile)

## Công nghệ sử dụng
- **Backend:** Python, FastAPI, SQLAlchemy, Cloudinary
- **Database:** SQL Server
- **Frontend Mobile:** Android (Java/Kotlin)
- **Khác:** Cloudinary (lưu trữ ảnh), JWT (xác thực), CORS, v.v.

## Cấu trúc thư mục dự án

```
├── backend/
│   ├── app/
│   │   ├── controllers/   # Xử lý logic nghiệp vụ
│   │   ├── routers/       # Định nghĩa các API endpoint
│   │   ├── models/        # Định nghĩa các model ORM
│   │   ├── schemas/       # Định nghĩa các schema (Pydantic)
│   │   ├── utils/         # Tiện ích, hàm dùng chung (cloudinary, bảo mật...)
│   │   ├── config/        # Cấu hình database, settings
│   │   ├── middleware/    # Middleware (JWT, xác thực...)
│   │   └── main.py        # Điểm khởi động FastAPI
│   ├── requirements.txt   # Thư viện backend
│   └── uploads/           # Thư mục lưu file upload tạm thời (nếu có)
├── frontend/              # Source code mobile app (Android)
├── database/              # Script tạo database, migration
└── README.md              # Tài liệu dự án
```

## Hướng dẫn chạy backend

Cài đặt thư viện:
```
pip install -r requirements.txt --no-cache-dir
```

Chạy server phát triển:
```
uvicorn app.main:app --reload
```

Kết nối API local:
```
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

## Liên hệ
Nếu có thắc mắc hoặc đóng góp, vui lòng liên hệ thành viên nhóm qua email hoặc github.
