from fastapi import APIRouter, Depends, status, Body
from sqlalchemy.orm import Session
from typing import List
from app.config.database import get_db
from app.schemas.friendship import FriendRequestCreate, FriendRequestResponse, FriendRequestUpdate, FriendResponse
from app.controllers.friendship import FriendshipController
from app.utils.security import get_current_active_user

router = APIRouter(
    prefix="/friends",
    tags=["Friends"],
    responses={404: {"description": "Not found"}},
)

@router.post("/requests/", response_model=FriendRequestResponse, status_code=status.HTTP_201_CREATED)
async def send_friend_request(
    request: FriendRequestCreate,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    '''
    Gửi lời mời kết bạn
    '''
    return await FriendshipController.send_friend_request(current_user["nguoi_dung"].MaNguoiDung, request, db)

@router.put("/requests/{ma_loi_moi}", response_model=FriendRequestResponse)
async def respond_friend_request(
    ma_loi_moi: int,
    request: FriendRequestUpdate,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    '''
    Xử lý lời mời kết bạn (đồng ý/ từ chối)
    '''
    return await FriendshipController.respond_friend_request(current_user["nguoi_dung"].MaNguoiDung, ma_loi_moi, request, db)

@router.get("/", response_model=List[FriendResponse])
async def get_friends(
    skip: int = 0,
    limit: int = 20,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    '''
    Lấy danh sách bạn bè
    '''
    return await FriendshipController.get_friends(current_user["nguoi_dung"].MaNguoiDung, skip, limit, db)

@router.get("/requests/", response_model=List[FriendRequestResponse])
async def get_friend_requests(
    skip: int = 0,
    limit: int = 20,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    '''
    Lấy danh sách lời mời kết bạn nhận được
    '''
    return await FriendshipController.get_friend_requests(current_user["nguoi_dung"].MaNguoiDung, skip, limit, db) 