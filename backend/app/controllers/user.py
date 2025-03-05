from fastapi import HTTPException, status, Depends
from sqlalchemy.orm import Session
from app.models.user import NguoiDung, TaiKhoan
from app.schemas.user import UserProfileResponse, UserResponse
from typing import List

class UserController:
    @staticmethod
    async def get_user_profile(user_id: int, db: Session):
        # Tìm người dùng theo ID
        user = db.query(NguoiDung).filter(NguoiDung.MaNguoiDung == user_id).first()
        if not user:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Người dùng không tồn tại"
            )
        
        # Lấy thông tin tài khoản
        account = db.query(TaiKhoan).filter(TaiKhoan.MaNguoiDung == user.MaNguoiDung).first()
        
        # Tạo response
        response = UserProfileResponse(
            MaNguoiDung=user.MaNguoiDung,
            TenNguoiDung=user.TenNguoiDung,
            NgayTao=user.NgayTao,
            Email=account.Email,
            TrangThai=account.TrangThai
        )
        
        return response
    
    @staticmethod
    async def search_users(keyword: str, current_user_id: int, db: Session):
        # Tìm kiếm người dùng theo tên
        users = db.query(NguoiDung).filter(
            NguoiDung.TenNguoiDung.ilike(f"%{keyword}%"),
            NguoiDung.MaNguoiDung != current_user_id
        ).all()
        
        return [UserResponse.from_orm(user) for user in users]
    
    @staticmethod
    async def update_profile(user_id: int, ten_nguoi_dung: str, db: Session):
        # Tìm người dùng theo ID
        user = db.query(NguoiDung).filter(NguoiDung.MaNguoiDung == user_id).first()
        if not user:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Người dùng không tồn tại"
            )
        
        # Cập nhật thông tin
        user.TenNguoiDung = ten_nguoi_dung
        db.commit()
        db.refresh(user)
        
        return UserResponse.from_orm(user)