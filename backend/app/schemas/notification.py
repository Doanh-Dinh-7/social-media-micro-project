from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class NotificationBase(BaseModel):
    NoiDung: str
    MaNguoiDung: int

class NotificationCreate(NotificationBase):
    pass

class NotificationResponse(NotificationBase):
    MaThongBao: int
    ThoiGian: datetime
    DaDoc: int
    MaBaiViet: Optional[int] = None
    AnhDaiDien: Optional[str] = None

    class Config:
        from_attributes = True

class NotificationUpdate(BaseModel):
    DaDoc: int = 1  # Đánh dấu là đã đọc