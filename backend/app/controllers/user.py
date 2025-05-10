from fastapi import HTTPException, status, Depends, UploadFile
from sqlalchemy.orm import Session
from sqlalchemy import and_, func
from app.models.user import NguoiDung, TaiKhoan
from app.models.friendship import LoiMoiKetBan, BanBe
from app.schemas.user import UserProfileResponse, UserResponse
from typing import List
from app.utils.cloudinary import upload_image

class UserController:
    @staticmethod
    async def get_user_profile(user_id: int, current_user_id: int, db: Session):
        # Tìm người dùng theo ID
        user = db.query(NguoiDung).filter(NguoiDung.MaNguoiDung == user_id).first()
        if not user:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Người dùng không tồn tại"
            )
        
        # Lấy thông tin tài khoản
        account = db.query(TaiKhoan).filter(TaiKhoan.MaNguoiDung == user.MaNguoiDung).first()
        
        # Đếm số lượng lời mời kết bạn chưa được chấp nhận gửi đến user này
        pending_requests_count = db.query(func.count(LoiMoiKetBan.MaLoiMoi)).filter(
            and_(
                LoiMoiKetBan.NguoiNhan == user_id,
                LoiMoiKetBan.TrangThai == 0
            )
        ).scalar()
        
        # Tạo response
        response = UserProfileResponse(
            MaNguoiDung=user.MaNguoiDung,
            TenNguoiDung=user.TenNguoiDung,
            NgayTao=user.NgayTao,
            Email=account.Email,
            TrangThai=account.TrangThai,
            TheoDoi=pending_requests_count,
            AnhDaiDien=user.AnhDaiDien,
            AnhBia=user.AnhBia
        )
        
        return response
    
    @staticmethod
    async def search_users(keyword: str, current_user_id: int, db: Session):
        # Tìm kiếm người dùng theo tên
        users = db.query(NguoiDung).filter(
            NguoiDung.TenNguoiDung.ilike(f"%{keyword}%"),
            NguoiDung.MaNguoiDung != current_user_id
        ).all()
        result = []
        for user in users:
            # Kiểm tra quan hệ bạn bè
            is_friend = db.query(BanBe).filter(
                ((BanBe.MaNguoiDung == current_user_id) & (BanBe.MaBanBe == user.MaNguoiDung)) |
                ((BanBe.MaNguoiDung == user.MaNguoiDung) & (BanBe.MaBanBe == current_user_id))
            ).first()
            if is_friend:
                quan_he = 1  # Đã là bạn bè
            else:
                # Kiểm tra đã gửi lời mời kết bạn chưa
                da_gui_loi_moi = db.query(LoiMoiKetBan).filter(
                    LoiMoiKetBan.NguoiGui == current_user_id,
                    LoiMoiKetBan.NguoiNhan == user.MaNguoiDung,
                    LoiMoiKetBan.TrangThai == 0
                ).first()
                if da_gui_loi_moi:
                    quan_he = 2  # Đã gửi lời mời kết bạn
                else:
                    duoc_gui_loi_moi = db.query(LoiMoiKetBan).filter(
                        LoiMoiKetBan.NguoiNhan == current_user_id,
                        LoiMoiKetBan.NguoiGui == user.MaNguoiDung,
                        LoiMoiKetBan.TrangThai == 0
                    ).first()
                    if duoc_gui_loi_moi:
                        quan_he = 3  # Đã nhận lời mời kết bạn
                    else:
                        quan_he = 4  # Chưa có quan hệ
            # Tạo dict đầy đủ các trường
            user_dict = {
                "MaNguoiDung": user.MaNguoiDung,
                "TenNguoiDung": user.TenNguoiDung,
                "NgayTao": user.NgayTao,
                "AnhDaiDien": user.AnhDaiDien,
                "AnhBia": user.AnhBia,
                "QuanHe": quan_he
            }
            result.append(UserResponse(**user_dict))
        return result
    
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

    @staticmethod
    async def update_avatar(current_user_id: int, file: bytes, filename: str, db: Session):
        # Upload lên Cloudinary
        result = await upload_image(file, folder="avatar")
        url = result['secure_url']
        user = db.query(NguoiDung).filter(NguoiDung.MaNguoiDung == current_user_id).first()
        if not user:
            raise HTTPException(status_code=404, detail="Không tìm thấy người dùng.")
        user.AnhDaiDien = url
        db.commit()
        db.refresh(user)
        return user

    @staticmethod
    async def update_cover(current_user_id: int, file: bytes, filename: str, db: Session):
        # Upload lên Cloudinary
        result = await upload_image(file, folder="cover")
        url = result['secure_url']
        user = db.query(NguoiDung).filter(NguoiDung.MaNguoiDung == current_user_id).first()
        if not user:
            raise HTTPException(status_code=404, detail="Không tìm thấy người dùng.")
        user.AnhBia = url
        db.commit()
        db.refresh(user)
        return user