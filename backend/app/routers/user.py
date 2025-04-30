from fastapi import APIRouter, Depends, HTTPException, status, Body, UploadFile, File
from sqlalchemy.orm import Session
from app.config.database import get_db
from app.schemas.user import UserProfileResponse, UserResponse
from app.controllers.user import UserController
from app.utils.security import get_current_active_user
from typing import List

router = APIRouter(
    prefix="/users",
    tags=["Users"],
    responses={404: {"description": "Not found"}},
)

@router.get("/profile/{user_id}", response_model=UserProfileResponse)
async def get_user_profile(
    user_id: int, 
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    """
    Lấy thông tin profile của người dùng
    """
    return await UserController.get_user_profile(user_id, current_user["nguoi_dung"].MaNguoiDung, db)

@router.get("/search", response_model=List[UserResponse])
async def search_users(
    keyword: str,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    """
    Tìm kiếm người dùng theo tên
    """
    return await UserController.search_users(keyword, current_user["nguoi_dung"].MaNguoiDung, db)

@router.put("/profile", response_model=UserResponse)
async def update_profile(
    ten_nguoi_dung: str = Body(..., embed=True),
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    """
    Cập nhật thông tin profile
    """
    return await UserController.update_profile(
        current_user["nguoi_dung"].MaNguoiDung, 
        ten_nguoi_dung, 
        db
    )

@router.put("/avatar", response_model=UserResponse)
async def update_avatar(
    file: UploadFile = File(...),
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    '''
    Cập nhật ảnh đại diện (avatar) lên Cloudinary
    '''
    file_bytes = await file.read()
    user = await UserController.update_avatar(current_user["nguoi_dung"].MaNguoiDung, file_bytes, file.filename, db)
    return UserResponse.from_orm(user)

@router.put("/cover", response_model=UserResponse)
async def update_cover(
    file: UploadFile = File(...),
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    '''
    Cập nhật ảnh bìa (cover) lên Cloudinary
    '''
    file_bytes = await file.read()
    user = await UserController.update_cover(current_user["nguoi_dung"].MaNguoiDung, file_bytes, file.filename, db)
    return UserResponse.from_orm(user)