from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime
from app.schemas.user import UserResponse

class MessageBase(BaseModel):
    NoiDung: str = Field(..., min_length=1)

class MessageCreate(MessageBase):
    MaCuocTroChuyen: Optional[int] = None
    NguoiNhan: Optional[int] = None  # Sử dụng khi tạo cuộc trò chuyện mới

class MessageResponse(MessageBase):
    MaTinNhan: int
    MaNguoiGui: int
    MaCuocTroChuyen: int
    NgayGui: datetime
    nguoi_gui: Optional[UserResponse] = None
    
    class Config:
        from_attributes = True

class ConversationResponse(BaseModel):
    MaCuocTroChuyen: int
    NguoiDung_1: int
    NguoiDung_2: int
    NgayTao: datetime
    TinNhanCuoi: Optional[int] = None
    nguoi_dung_1: Optional[UserResponse] = None
    nguoi_dung_2: Optional[UserResponse] = None
    tin_nhan_cuoi_obj: Optional[MessageResponse] = None
    
    class Config:
        from_attributes = True

class ConversationCreate(BaseModel):
    NguoiNhan: int  # ID của người nhận tin nhắn
    NoiDung: str = Field(..., min_length=1)  # Nội dung tin nhắn đầu tiên