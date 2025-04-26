from fastapi import HTTPException, status
from sqlalchemy.orm import Session
from sqlalchemy import or_, and_
from app.models.friendship import LoiMoiKetBan, BanBe
from app.models.user import NguoiDung
from app.schemas.friendship import FriendRequestCreate, FriendRequestUpdate
from typing import List

class FriendshipController:
    @staticmethod
    async def send_friend_request(current_user_id: int, request: FriendRequestCreate, db: Session):
        # Không cho gửi lời mời tới chính mình
        if current_user_id == request.NguoiNhan:
            raise HTTPException(status_code=400, detail="Không thể gửi lời mời kết bạn tới chính mình.")
        # Đã là bạn bè
        is_friend = db.query(BanBe).filter(
            or_(and_(BanBe.MaNguoiDung == current_user_id, BanBe.MaBanBe == request.NguoiNhan),
                and_(BanBe.MaNguoiDung == request.NguoiNhan, BanBe.MaBanBe == current_user_id))
        ).first()
        if is_friend:
            raise HTTPException(status_code=400, detail="Đã là bạn bè.")
        # Đã có lời mời chờ xử lý
        existing_request = db.query(LoiMoiKetBan).filter(
            or_(
                and_(LoiMoiKetBan.NguoiGui == current_user_id, LoiMoiKetBan.NguoiNhan == request.NguoiNhan, LoiMoiKetBan.TrangThai == 0),
                and_(LoiMoiKetBan.NguoiGui == request.NguoiNhan, LoiMoiKetBan.NguoiNhan == current_user_id, LoiMoiKetBan.TrangThai == 0)
            )
        ).first()
        if existing_request:
            raise HTTPException(status_code=400, detail="Đã có lời mời kết bạn đang chờ xử lý.")
        # Kiểm tra người nhận tồn tại
        user = db.query(NguoiDung).filter(NguoiDung.MaNguoiDung == request.NguoiNhan).first()
        if not user:
            raise HTTPException(status_code=404, detail="Người nhận không tồn tại.")
        # Tạo lời mời
        friend_request = LoiMoiKetBan(NguoiGui=current_user_id, NguoiNhan=request.NguoiNhan)
        db.add(friend_request)
        db.commit()
        db.refresh(friend_request)
        return friend_request

    @staticmethod
    async def respond_friend_request(current_user_id: int, ma_loi_moi: int, request: FriendRequestUpdate, db: Session):
        friend_request = db.query(LoiMoiKetBan).filter(LoiMoiKetBan.MaLoiMoi == ma_loi_moi).first()
        if not friend_request:
            raise HTTPException(status_code=404, detail="Lời mời kết bạn không tồn tại.")
        if friend_request.NguoiNhan != current_user_id:
            raise HTTPException(status_code=403, detail="Bạn không có quyền xử lý lời mời này.")
        if friend_request.TrangThai != 0:
            raise HTTPException(status_code=400, detail="Lời mời đã được xử lý.")
        friend_request.TrangThai = request.TrangThai
        db.commit()
        # Nếu chấp nhận thì tạo quan hệ bạn bè 2 chiều
        if request.TrangThai == 1:
            db.add(BanBe(MaNguoiDung=friend_request.NguoiGui, MaBanBe=friend_request.NguoiNhan))
            db.add(BanBe(MaNguoiDung=friend_request.NguoiNhan, MaBanBe=friend_request.NguoiGui))
            db.commit()
        db.refresh(friend_request)
        return friend_request

    @staticmethod
    async def get_friends(current_user_id: int, skip: int, limit: int, db: Session):
        friends = db.query(BanBe).filter(BanBe.MaNguoiDung == current_user_id).order_by(BanBe.NgayTao.desc()).offset(skip).limit(limit).all()
        return friends 