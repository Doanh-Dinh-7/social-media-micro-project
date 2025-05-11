from fastapi import HTTPException, status
from sqlalchemy.orm import Session
from sqlalchemy import or_, and_
from app.models.friendship import LoiMoiKetBan, BanBe
from app.models.user import NguoiDung
from app.schemas.friendship import FriendRequestCreate, FriendRequestUpdate
from app.schemas.user import UserResponse
from typing import List
from app.controllers.notification import NotificationController

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
        # Đã có lời mời chờ xử lý hoặc bị từ chối
        existing_request = db.query(LoiMoiKetBan).filter(
            or_(
                and_(LoiMoiKetBan.NguoiGui == current_user_id, LoiMoiKetBan.NguoiNhan == request.NguoiNhan),
                and_(LoiMoiKetBan.NguoiGui == request.NguoiNhan, LoiMoiKetBan.NguoiNhan == current_user_id)
            )
        ).order_by(LoiMoiKetBan.ThoiGian.desc()).first()
        if existing_request:
            if existing_request.TrangThai == 0:
                raise HTTPException(status_code=400, detail="Đã có lời mời kết bạn đang chờ xử lý.")
            elif existing_request.TrangThai == 2:
                # Nếu bị từ chối thì cập nhật lại trạng thái thành 0 (chờ xử lý)
                existing_request.TrangThai = 0
                db.commit()
                db.refresh(existing_request)
                return existing_request
        # Kiểm tra người nhận tồn tại
        user_nhan = db.query(NguoiDung).filter(NguoiDung.MaNguoiDung == request.NguoiNhan).first()
        if not user_nhan:
            raise HTTPException(status_code=404, detail="Người nhận không tồn tại.")
        # Tạo lời mời
        friend_request = LoiMoiKetBan(NguoiGui=current_user_id, NguoiNhan=request.NguoiNhan)
        db.add(friend_request)
        db.commit()
        db.refresh(friend_request)
        
        # Tạo thông báo
        if request.NguoiNhan != current_user_id:
            user_gui = db.query(NguoiDung).filter(NguoiDung.MaNguoiDung == current_user_id).first()
            noi_dung = f"{user_gui.TenNguoiDung} đã gửi lời mời kết bạn tới bạn."
            import asyncio
            asyncio.create_task(NotificationController.create_notification(request.NguoiNhan, noi_dung, None, current_user_id, db))
        
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
        result = []
        for f in friends:
            ban_obj = db.query(NguoiDung).filter(NguoiDung.MaNguoiDung == f.MaBanBe).first()
            friend_schema = {
                "MaNguoiDung": f.MaNguoiDung,
                "MaBanBe": f.MaBanBe,
                "NgayTao": f.NgayTao,
                "ban": UserResponse.from_orm(ban_obj) if ban_obj else None
            }
            result.append(friend_schema)
        return result

    @staticmethod
    async def get_friend_requests(current_user_id: int, skip: int, limit: int, db: Session):
        requests = db.query(LoiMoiKetBan).filter(
            LoiMoiKetBan.NguoiNhan == current_user_id,
            LoiMoiKetBan.TrangThai == 0
        ).order_by(LoiMoiKetBan.ThoiGian.desc()).offset(skip).limit(limit).all()
        return requests

    @staticmethod
    async def unfriend(current_user_id: int, friend_id: int, db: Session):
        # Xóa cả 2 chiều quan hệ bạn bè
        deleted1 = db.query(BanBe).filter(BanBe.MaNguoiDung == current_user_id, BanBe.MaBanBe == friend_id).delete()
        deleted2 = db.query(BanBe).filter(BanBe.MaNguoiDung == friend_id, BanBe.MaBanBe == current_user_id).delete()
        db.commit()
        if deleted1 == 0 and deleted2 == 0:
            raise HTTPException(status_code=404, detail="Không tìm thấy quan hệ bạn bè để hủy.")
        return {"message": "Đã hủy kết bạn thành công."}

    @staticmethod
    async def cancel_friend_request(current_user_id: int, ma_loi_moi: int, db: Session):
        friend_request = db.query(LoiMoiKetBan).filter(LoiMoiKetBan.MaLoiMoi == ma_loi_moi).first()
        if not friend_request:
            raise HTTPException(status_code=404, detail="Lời mời kết bạn không tồn tại.")
        if friend_request.NguoiGui != current_user_id:
            raise HTTPException(status_code=403, detail="Bạn không có quyền huỷ lời mời này.")
        if friend_request.TrangThai != 0:
            raise HTTPException(status_code=400, detail="Chỉ có thể huỷ lời mời đang chờ xử lý.")
        db.delete(friend_request)
        db.commit()
        return {"message": "Đã huỷ lời mời kết bạn thành công."} 
    
# - Trạng thái lời mời kết bạn:
#  0: Đã gửi lời mời 
#  1. Là bạn bè
#  2: Bị từ chối 