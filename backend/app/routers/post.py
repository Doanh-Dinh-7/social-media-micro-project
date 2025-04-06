from fastapi import APIRouter, Depends, HTTPException, status, File, UploadFile, Form
from sqlalchemy.orm import Session
from app.config.database import get_db
from app.schemas.post import PostCreate, PostResponse, PostUpdate
from app.models.post import HinhAnh
from app.controllers.post import PostController
from app.utils.security import get_current_active_user
from typing import List, Optional
import cloudinary

router = APIRouter(
    prefix="/posts",
    tags=["Posts"],
    responses={404: {"description": "Not found"}},
)

@router.post("/", response_model=PostResponse, status_code=status.HTTP_201_CREATED)
async def create_post(
    noi_dung: str = Form(...),
    ma_quyen_rieng_tu: int = Form(...),
    ma_chu_de: Optional[int] = Form(None),
    images: Optional[List[UploadFile]] = File(None),
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    """
    Tạo bài viết mới
    """
    # Create post data
    post_data = PostCreate(
        NoiDung=noi_dung,
        MaQuyenRiengTu=ma_quyen_rieng_tu,
        MaChuDe=ma_chu_de
    )

    # Create post and handle image uploads
    return await PostController.create_post(
        post_data=post_data,
        user_id=current_user["nguoi_dung"].MaNguoiDung,
        images=images,
        db=db
    )

@router.get("/{post_id}", response_model=PostResponse)
async def get_post(
    post_id: int,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    """
    Lấy thông tin chi tiết bài viết
    """
    return await PostController.get_post_by_id(
        post_id, 
        current_user["nguoi_dung"].MaNguoiDung, 
        db
    )

@router.get("/", response_model=List[PostResponse])
async def get_news_feed(
    skip: int = 0,
    limit: int = 10,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    """
    Lấy bảng tin (news feed)
    """
    return await PostController.get_news_feed(
        current_user["nguoi_dung"].MaNguoiDung, 
        skip, 
        limit, 
        db
    )

@router.put("/{post_id}", response_model=PostResponse)
async def update_post(
    post_id: int,
    post_data: PostUpdate,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    """
    Cập nhật bài viết
    """
    return await PostController.update_post(
        post_id, 
        post_data, 
        current_user["nguoi_dung"].MaNguoiDung, 
        db
    )

@router.delete("/{post_id}")
async def delete_post(
    post_id: int,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    """
    Xóa bài viết
    """
    return await PostController.delete_post(
        post_id, 
        current_user["nguoi_dung"].MaNguoiDung, 
        db
    )