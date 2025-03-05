from pydantic import BaseModel
from typing import Optional, List
from datetime import datetime
from app.schemas.user import UserResponse

class FriendRequestBase(BaseModel):
    NguoiNhan: int

class FriendRequestCreate(FriendRequestBase):
    pass

class FriendRequestResponse(BaseModel):
    MaLoiMoi: int
    NguoiGui: int
    NguoiNhan: int
    TrangThai: int
    ThoiGian: datetime
    nguoi_gui: Optional[UserResponse]
    nguoi_nhan: Optional[UserResponse]
    
    class Config:
        from_attributes = True

class FriendRequestUpdate(BaseModel):
    TrangThai: int  # 1: Chấp nhận, 2: Từ chối

class FriendResponse(BaseModel):
    MaNguoiDung: int
    MaBanBe: int
    NgayTao: datetime
    ban: Optional[UserResponse]
    
    class Config:
        from_attributes = True

class FriendSuggestionResponse(BaseModel):
    MaGoiY: int
    NguoiGoiY: int
    NguoiDuocGoiY: int
    TrangThai: int
    nguoi_duoc_goi_y: Optional[UserResponse]
    
    class Config:
        from_attributes = True