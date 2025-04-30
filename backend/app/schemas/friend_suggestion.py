from pydantic import BaseModel
from typing import List
from datetime import datetime

class UserBasicInfo(BaseModel):
    MaNguoiDung: int
    TenNguoiDung: str
    AnhDaiDien: str | None = None
    
    class Config:
        from_attributes = True

class FriendSuggestionResponse(BaseModel):
    suggestions: List[UserBasicInfo]
    total: int 