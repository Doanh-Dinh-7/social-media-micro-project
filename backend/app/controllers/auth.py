from fastapi import HTTPException, status, Depends
from sqlalchemy.orm import Session
from app.models.user import NguoiDung, TaiKhoan
from app.schemas.user import UserCreate, UserLogin, TokenResponse, UserResponse
from app.utils.security import verify_password, get_password_hash, create_access_token, create_refresh_token
from datetime import timedelta
from app.config.settings import settings

class AuthController:
    @staticmethod
    async def register(user_data: UserCreate, db: Session):
        try:
            # Kiểm tra email đã tồn tại chưa
            existing_account = db.query(TaiKhoan).filter(TaiKhoan.Email == user_data.Email).first()
            if existing_account:
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Email đã được sử dụng"
                )
            
            # Tạo người dùng mới
            new_user = NguoiDung(
                TenNguoiDung=user_data.TenNguoiDung
            )
            db.add(new_user)
            db.flush()
            print(f"Người dùng mới: {new_user.MaNguoiDung}")  # Debug
            
            # Tạo tài khoản cho người dùng
            hashed_password = get_password_hash(user_data.MatKhau)
            new_account = TaiKhoan(
                MaNguoiDung=new_user.MaNguoiDung,
                Email=user_data.Email,
                MatKhau=hashed_password,
                TrangThai=1  # Hoạt động
            )
            db.add(new_account)
            db.commit()
            db.refresh(new_user)
            
            return new_user
        except Exception as e:
            db.rollback()  # Hủy thay đổi nếu có lỗi
            print(f"Lỗi khi đăng ký: {e}")  # Debug
            raise HTTPException(status_code=500, detail=str(e))
    
    @staticmethod
    async def login(user_data: UserLogin, db: Session):
        # Tìm tài khoản theo email
        account = db.query(TaiKhoan).filter(TaiKhoan.Email == user_data.Email).first()
        if not account:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Email hoặc mật khẩu không chính xác",
                headers={"WWW-Authenticate": "Bearer"},
            )
        
        # Kiểm tra mật khẩu
        if not verify_password(user_data.MatKhau, account.MatKhau):
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Email hoặc mật khẩu không chính xác",
                headers={"WWW-Authenticate": "Bearer"},
            )
        
        # Kiểm tra trạng thái tài khoản
        if account.TrangThai == 0:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Tài khoản đã bị khóa"
            )
        
        # Lấy thông tin người dùng
        user = db.query(NguoiDung).filter(NguoiDung.MaNguoiDung == account.MaNguoiDung).first()
        
        # Tạo token
        access_token_expires = timedelta(minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES)
        access_token = create_access_token(
            data={"sub": account.Email}, expires_delta=access_token_expires
        )
        refresh_token = create_refresh_token(data={"sub": account.Email})
        
        return TokenResponse(
            access_token=access_token,
            refresh_token=refresh_token,
            user=UserResponse.from_orm(user)
        )
    
    @staticmethod
    async def refresh_token(refresh_token: str, db: Session):
        from app.utils.security import get_current_user
        
        try:
            # Xác thực refresh token
            current_user = await get_current_user(token=refresh_token, db=db)
            
            # Tạo access token mới
            access_token_expires = timedelta(minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES)
            access_token = create_access_token(
                data={"sub": current_user["tai_khoan"].Email}, 
                expires_delta=access_token_expires
            )
            
            return {"access_token": access_token, "token_type": "bearer"}
        except Exception as e:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Invalid refresh token",
                headers={"WWW-Authenticate": "Bearer"},
            )

    @staticmethod
    async def update_name(user_id: int, ten_nguoi_dung: str, db: Session):
        try:
            # Tìm người dùng theo ID
            user = db.query(NguoiDung).filter(NguoiDung.MaNguoiDung == user_id).first()
            if not user:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Người dùng không tồn tại"
                )
            
            # Cập nhật tên người dùng
            user.TenNguoiDung = ten_nguoi_dung
            db.commit()
            db.refresh(user)
            
            return UserResponse.from_orm(user)
        except Exception as e:
            db.rollback()
            raise HTTPException(status_code=500, detail=str(e))