from pydantic import BaseModel, EmailStr, Field, validator
from typing import Optional, List
from datetime import datetime

# Base User Schema
class UserBase(BaseModel):
    TenNguoiDung: str = Field(..., min_length=2, max_length=255)

class UserCreate(UserBase):
    Email: EmailStr
    MatKhau: str = Field(..., min_length=6)
    
    @validator('MatKhau')
    def password_complexity(cls, v):
        # if not any(char.isdigit() for char in v):
        #     raise ValueError('Mật khẩu phải chứa ít nhất 1 chữ số')
        # if not any(char.isupper() for char in v):
        #     raise ValueError('Mật khẩu phải chứa ít nhất 1 chữ hoa')
        return v

class UserLogin(BaseModel):
    Email: EmailStr
    MatKhau: str

class UserResponse(UserBase):
    MaNguoiDung: int
    NgayTao: datetime
    AnhDaiDien: str = None
    AnhBia: str = None
    
    class Config:
        from_attributes  = True

class UserProfileResponse(UserResponse):
    Email: EmailStr
    TrangThai: int
    TheoDoi: int = 0  # Số lượng người dùng đã gửi lời mời kết bạn chưa được chấp nhận
    
    class Config:
        from_attributes  = True

class TokenResponse(BaseModel):
    access_token: str
    refresh_token: str
    token_type: str = "bearer"
    user: UserResponse