from fastapi import Request, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from app.utils.security import get_current_user
from app.config.database import get_db
from sqlalchemy.orm import Session
from app.models.user import TaiKhoan, NguoiDung
from app.config.settings import settings
from jose import jwt


security = HTTPBearer()

# Danh sách các endpoint công khai không cần xác thực
PUBLIC_ENDPOINTS = [
    "/",
    "/docs",
    "/redoc",
    "/openapi.json",
    "/favicon.ico",
    "/api/auth/login",
    "/api/auth/register"
]

async def auth_middleware(request: Request, call_next):

    # Kiểm tra nếu là endpoint công khai
    if request.url.path in PUBLIC_ENDPOINTS:
        return await call_next(request)
    
    try:
        # Lấy token từ header
        auth_header = request.headers.get("Authorization")
        if not auth_header:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Missing Authorization header",
                headers={"WWW-Authenticate": "Bearer"}
            )
        
        # Kiểm tra định dạng token
        if not auth_header.startswith("Bearer "):
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Invalid token format",
                headers={"WWW-Authenticate": "Bearer"}
            )
        
        # Lấy token
        token = auth_header.split(" ")[1]
        
        payload = jwt.decode(token, settings.SECRET_KEY, algorithms=[settings.ALGORITHM])
        email = payload.get("sub")
        if not email:
            raise HTTPException(status_code=401, detail="Invalid token payload")

        db: Session = next(get_db())
        tai_khoan = db.query(TaiKhoan).filter(TaiKhoan.Email == email).first()
        if not tai_khoan:
            raise HTTPException(status_code=401, detail="User not found")

        if tai_khoan.TrangThai == 0:
            raise HTTPException(status_code=403, detail="Account is disabled")

        nguoi_dung = db.query(NguoiDung).filter(NguoiDung.MaNguoiDung == tai_khoan.MaNguoiDung).first()
        # Gán user vào request để các hàm khác dùng
        request.state.current_user = {
            "tai_khoan": tai_khoan,
            "nguoi_dung": nguoi_dung
        }

        return await call_next(request)
        
    except HTTPException as e:
        raise e
    except Exception as e:
        print(e)
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid token",
            headers={"WWW-Authenticate": "Bearer"}
        )

