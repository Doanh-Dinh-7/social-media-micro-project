from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime
from app.schemas.user import UserResponse
from fastapi import UploadFile, File

class PostImageBase(BaseModel):
    Url: str

class PostImageCreate(PostImageBase):
    pass

class PostImageResponse(PostImageBase):
    MaHinhAnh: int
    MaBaiDang: int
    
    class Config:
        from_attributes = True

class PostBase(BaseModel):
    NoiDung: str = Field(..., min_length=1)
    MaQuyenRiengTu: int
    MaChuDe: Optional[int] = None

class PostCreate(PostBase):
    pass  # Remove images field from here as it will be handled in the router

class PostUpdate(BaseModel):
    NoiDung: Optional[str] = None
    MaQuyenRiengTu: Optional[int] = None
    MaChuDe: Optional[int] = None

class PostResponse(PostBase):
    MaBaiViet: int
    MaNguoiDung: int
    NgayTao: datetime
    NgayCapNhat: Optional[datetime] = None
    nguoi_dung: Optional[UserResponse] = None
    hinh_anh: Optional[List[PostImageResponse]] = []
    so_luot_thich: Optional[int] = 0
    so_binh_luan: Optional[int] = 0
    da_thich: Optional[bool] = False
    
    class Config:
        from_attributes = True

class CommentBase(BaseModel):
    NoiDung: str = Field(..., min_length=1)

class CommentCreate(CommentBase):
    MaBaiViet: int

class CommentResponse(CommentBase):
    MaBinhLuan: int
    MaBaiViet: int
    MaNguoiDung: int
    NgayTao: datetime
    nguoi_dung: Optional[UserResponse] = None
    
    class Config:
        from_attributes = True

class LikeCreate(BaseModel):
    MaBaiViet: int

class LikeResponse(BaseModel):
    MaLuotThich: int
    MaBaiViet: int
    MaNguoiDung: int
    NgayTao: datetime
    
    class Config:
        from_attributes = True

class PrivacyResponse(BaseModel):
    MaQuyenRiengTu: int
    Loai: str
    
    class Config:
        from_attributes = True

class TopicResponse(BaseModel):
    MaChuDe: int
    Loai: str
    
    class Config:
        from_attributes = True

class BinhLuanBase(BaseModel):
    NoiDung: str = Field(..., min_length=1, max_length=1000)

class CommentCreate(BinhLuanBase):
    MaBaiViet: int

class CommentResponse(BinhLuanBase):
    MaBinhLuan: int
    MaBaiViet: int
    MaNguoiDung: int
    NgayTao: datetime
    TenNguoiDung: str = None
    AnhDaiDien: Optional[str] = None

    class Config:
        from_attributes = True

class LuotThichBase(BaseModel):
    MaBaiViet: int

class LikeResponse(LuotThichBase):
    MaLuotThich: int
    MaNguoiDung: int
    NgayTao: datetime
    TenNguoiDung: str = None

    class Config:
        from_attributes = True