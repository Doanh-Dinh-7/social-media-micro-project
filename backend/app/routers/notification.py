from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from typing import List
from app.config.database import get_db
from app.schemas.notification import NotificationResponse
from app.controllers.notification import NotificationController
from app.utils.security import get_current_active_user

router = APIRouter(
    prefix="/notifications",
    tags=["Notifications"],
    responses={404: {"description": "Not found"}},
)

@router.get("/", response_model=List[NotificationResponse])
async def get_notifications(
    skip: int = 0,
    limit: int = 20,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    '''
    Lấy danh sách thông báo của người dùng hiện tại
    '''
    return await NotificationController.get_notifications(current_user["nguoi_dung"].MaNguoiDung,db, skip, limit) 