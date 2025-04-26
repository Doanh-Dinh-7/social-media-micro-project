from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session
from typing import List, Any
from app.config.database import get_db
from app.schemas.message import MessageCreate, MessageResponse
from app.controllers.message import MessageController
from app.utils.security import get_current_active_user

router = APIRouter(
    prefix="/messages",
    tags=["Messages"],
    responses={404: {"description": "Not found"}},
)

@router.post("/", response_model=MessageResponse, status_code=status.HTTP_201_CREATED)
async def send_message(
    data: MessageCreate,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    '''
    Gửi tin nhắn
    '''
    return await MessageController.send_message(current_user["nguoi_dung"].MaNguoiDung, data, db)

@router.get("/{user_id}", response_model=List[MessageResponse])
async def get_messages(
    user_id: int,
    skip: int = 0,
    limit: int = 20,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    '''
    Lấy tin nhắn giữa hai người dùng
    '''
    return await MessageController.get_messages(current_user["nguoi_dung"].MaNguoiDung, user_id, skip, limit, db)

@router.get("/conversations", response_model=List[Any])
async def get_conversations(
    skip: int = 0,
    limit: int = 20,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    '''
    Lấy danh sách hộp tin nhắn (conversation list)
    '''
    return await MessageController.get_conversations(current_user["nguoi_dung"].MaNguoiDung, skip, limit, db) 