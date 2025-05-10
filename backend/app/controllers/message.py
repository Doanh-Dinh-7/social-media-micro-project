from fastapi import HTTPException, status
from sqlalchemy.orm import Session
from sqlalchemy import or_, desc
from app.models.message import TinNhan, CuocTroChuyen
from app.models.friendship import BanBe
from app.models.user import NguoiDung
from app.schemas.message import MessageCreate
from typing import List

class MessageController:
    @staticmethod
    async def send_message(current_user_id: int, data: MessageCreate, db: Session):
        # Kiểm tra bạn bè
        print(f"[MESSAGE] Gửi tin nhắn từ {current_user_id} đến {data.NguoiNhan}")
        if data.NguoiNhan:
            is_friend = db.query(BanBe).filter(BanBe.MaNguoiDung == current_user_id, BanBe.MaBanBe == data.NguoiNhan).first()
            if not is_friend:
                raise HTTPException(status_code=403, detail="Chỉ gửi tin nhắn cho bạn bè.")
            # Tìm hoặc tạo cuộc trò chuyện
            conversation = db.query(CuocTroChuyen).filter(
                or_(
                    (CuocTroChuyen.NguoiDung_1 == current_user_id) & (CuocTroChuyen.NguoiDung_2 == data.NguoiNhan),
                    (CuocTroChuyen.NguoiDung_1 == data.NguoiNhan) & (CuocTroChuyen.NguoiDung_2 == current_user_id)
                )
            ).first()
            if not conversation:
                conversation = CuocTroChuyen(NguoiDung_1=current_user_id, NguoiDung_2=data.NguoiNhan)
                db.add(conversation)
                db.commit()
                db.refresh(conversation)
            ma_cuoc_tro_chuyen = conversation.MaCuocTroChuyen
        else:
            ma_cuoc_tro_chuyen = data.MaCuocTroChuyen
            conversation = db.query(CuocTroChuyen).filter(CuocTroChuyen.MaCuocTroChuyen == ma_cuoc_tro_chuyen).first()
            if not conversation or (current_user_id not in [conversation.NguoiDung_1, conversation.NguoiDung_2]):
                raise HTTPException(status_code=404, detail="Cuộc trò chuyện không tồn tại hoặc bạn không có quyền.")
        # Tạo tin nhắn
        message = TinNhan(MaNguoiGui=current_user_id, MaCuocTroChuyen=ma_cuoc_tro_chuyen, NoiDung=data.NoiDung)
        db.add(message)
        db.commit()
        db.refresh(message)
        # Cập nhật tin nhắn cuối cho cuộc trò chuyện
        conversation.TinNhanCuoi = message.MaTinNhan
        db.commit()
        db.refresh(conversation)
        return message

    @staticmethod
    async def get_messages(current_user_id: int, user_id: int, skip: int, limit: int, db: Session):
        # Tìm cuộc trò chuyện giữa hai người
        conversation = db.query(CuocTroChuyen).filter(
            or_(
                (CuocTroChuyen.NguoiDung_1 == current_user_id) & (CuocTroChuyen.NguoiDung_2 == user_id),
                (CuocTroChuyen.NguoiDung_1 == user_id) & (CuocTroChuyen.NguoiDung_2 == current_user_id)
            )
        ).first()
        if not conversation:
            return []
        # messages = db.query(TinNhan).filter(TinNhan.MaCuocTroChuyen == conversation.MaCuocTroChuyen).order_by(TinNhan.NgayGui.desc()).offset(skip).limit(limit).all()
        messages = db.query(TinNhan).filter(TinNhan.MaCuocTroChuyen == conversation.MaCuocTroChuyen).order_by(TinNhan.NgayGui.asc()).offset(skip).limit(limit).all()
        return messages

    @staticmethod
    async def get_conversations(current_user_id: int, skip: int, limit: int, db: Session):
        # Lấy danh sách các cuộc trò chuyện của user
        conversations = db.query(CuocTroChuyen).filter(
            or_(CuocTroChuyen.NguoiDung_1 == current_user_id, CuocTroChuyen.NguoiDung_2 == current_user_id)
        ).order_by(desc(CuocTroChuyen.TinNhanCuoi)).offset(skip).limit(limit).all()
        result = []
        for conv in conversations:
            # Xác định người còn lại
            other_id = conv.NguoiDung_2 if conv.NguoiDung_1 == current_user_id else conv.NguoiDung_1
            user = db.query(NguoiDung).filter(NguoiDung.MaNguoiDung == other_id).first()
            last_msg = db.query(TinNhan).filter(TinNhan.MaTinNhan == conv.TinNhanCuoi).first()
            result.append({
                "MaCuocTroChuyen": conv.MaCuocTroChuyen,
                "user_id": user.MaNguoiDung if user else None,
                "ten_nguoi_dung": user.TenNguoiDung if user else None,
                "anh_dai_dien": getattr(user, "AnhDaiDien", None),
                "noi_dung_cuoi": last_msg.NoiDung if last_msg else None,
                "thoi_gian_cuoi": last_msg.NgayGui if last_msg else None
            })
        return result

    @staticmethod
    async def search_conversations(current_user_id: int, keyword: str, skip: int, limit: int, db: Session):
        # Lấy tất cả cuộc trò chuyện của user hiện tại
        conversations = db.query(CuocTroChuyen).filter(
            or_(CuocTroChuyen.NguoiDung_1 == current_user_id, CuocTroChuyen.NguoiDung_2 == current_user_id)
        ).order_by(desc(CuocTroChuyen.TinNhanCuoi)).all()
        result = []
        for conv in conversations:
            # Xác định người còn lại
            other_id = conv.NguoiDung_2 if conv.NguoiDung_1 == current_user_id else conv.NguoiDung_1
            user = db.query(NguoiDung).filter(NguoiDung.MaNguoiDung == other_id).first()
            if not user or keyword.lower() not in user.TenNguoiDung.lower():
                continue
            last_msg = db.query(TinNhan).filter(TinNhan.MaTinNhan == conv.TinNhanCuoi).first()
            result.append({
                "MaCuocTroChuyen": conv.MaCuocTroChuyen,
                "user_id": user.MaNguoiDung if user else None,
                "ten_nguoi_dung": user.TenNguoiDung if user else None,
                "anh_dai_dien": getattr(user, "AnhDaiDien", None),
                "noi_dung_cuoi": last_msg.NoiDung if last_msg else None,
                "thoi_gian_cuoi": last_msg.NgayGui if last_msg else None
            })
        # Phân trang kết quả
        return result[skip:skip+limit] 