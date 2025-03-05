from fastapi import FastAPI, Depends
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from app.config.settings import settings
from app.config.database import engine, Base
from app.routers import auth, user, post  # Thêm router post
import os

# Tạo các bảng trong database
Base.metadata.create_all(bind=engine)

# Tạo thư mục uploads nếu chưa tồn tại
os.makedirs(settings.UPLOAD_FOLDER, exist_ok=True)

app = FastAPI(
    title=settings.APP_NAME,
    description="API cho mạng xã hội mini",
    version="1.0.0"
)

# Cấu hình CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Trong môi trường production, hãy chỉ định cụ thể các domain được phép
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Mount thư mục uploads để phục vụ file tĩnh
app.mount("/uploads", StaticFiles(directory=settings.UPLOAD_FOLDER), name="uploads")

# Đăng ký các router
app.include_router(auth.router, prefix=settings.API_PREFIX)
app.include_router(user.router, prefix=settings.API_PREFIX)
app.include_router(post.router, prefix=settings.API_PREFIX)  # Thêm router post

@app.get("/")
async def root():
    return {"message": "Chào mừng đến với API mạng xã hội mini"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)

