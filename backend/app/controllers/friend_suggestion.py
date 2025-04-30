from fastapi import HTTPException
from sqlalchemy.orm import Session
from sqlalchemy import and_, not_, or_
from app.models.friendship import BanBe, LoiMoiKetBan
from app.models.user import NguoiDung, TaiKhoan
from app.schemas.friend_suggestion import UserBasicInfo, FriendSuggestionResponse
from typing import List

class FriendSuggestionController:
    @staticmethod
    async def get_friend_suggestions(current_user_id: int, db: Session, limit: int = 10) -> FriendSuggestionResponse:
        # Lấy danh sách ID bạn bè hiện tại
        friend_ids = db.query(BanBe.MaBanBe).filter(BanBe.MaNguoiDung == current_user_id).all()
        friend_ids = [id[0] for id in friend_ids]
        
        # Lấy danh sách ID người dùng đã gửi hoặc nhận lời mời kết bạn
        pending_request_ids = db.query(LoiMoiKetBan.NguoiGui).filter(
            and_(
                LoiMoiKetBan.NguoiNhan == current_user_id,
                LoiMoiKetBan.TrangThai == 0
            )
        ).union(
            db.query(LoiMoiKetBan.NguoiNhan).filter(
                and_(
                    LoiMoiKetBan.NguoiGui == current_user_id,
                    LoiMoiKetBan.TrangThai == 0
                )
            )
        ).all()
        pending_request_ids = [id[0] for id in pending_request_ids]
        
        # Lấy danh sách người dùng được đề xuất
        excluded_ids = friend_ids + pending_request_ids + [current_user_id]
        
        # Join với bảng TaiKhoan để lấy trạng thái hoạt động
        suggestions = db.query(NguoiDung).join(
            TaiKhoan, TaiKhoan.MaNguoiDung == NguoiDung.MaNguoiDung
        ).filter(
            and_(
                not_(NguoiDung.MaNguoiDung.in_(excluded_ids)),
                TaiKhoan.TrangThai == 1  # Chỉ lấy những tài khoản đang hoạt động
            )
        ).limit(limit).all()
        
        result = [UserBasicInfo.from_orm(user) for user in suggestions]
        return FriendSuggestionResponse(suggestions=result, total=len(result)) 