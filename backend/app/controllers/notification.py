from sqlalchemy.orm import Session
from app.models.notification import ThongBao
from typing import List
from app.models.user import NguoiDung
import re

class NotificationController:
    @staticmethod
    async def get_notifications(current_user_id: int,  db: Session, skip: int = 0, limit: int = 20 ):
        notifications = db.query(ThongBao).filter(ThongBao.MaNguoiDung == current_user_id).order_by(ThongBao.ThoiGian.desc()).offset(skip).limit(limit).all()
        result = []
        for tb in notifications:
            # Lấy thông tin người gửi nếu có (giả sử có trường NguoiGui hoặc MaNguoiGui trong ThongBao)
            avatar = None
            if hasattr(tb, 'MaNguoiGui') and tb.MaNguoiGui:
                user = db.query(NguoiDung).filter(NguoiDung.MaNguoiDung == tb.MaNguoiGui).first()
                avatar = user.AnhDaiDien if user else None
            
            # Lấy mã bài viết nếu có
            ma_bai_viet = getattr(tb, 'MaBaiViet', None)
            result.append({
                "MaThongBao": tb.MaThongBao,
                "NoiDung": tb.NoiDung,
                "ThoiGian": tb.ThoiGian,
                "DaDoc": tb.DaDoc,
                "AnhDaiDien": avatar,
                "MaBaiViet": ma_bai_viet,
                "MaNguoiDung": tb.MaNguoiDung
            })
        return result

    @staticmethod
    async def create_notification( ma_nguoi_nhan: int, noi_dung: str, ma_bai_viet: int, ma_nguoi_gui: int, db: Session):
        thong_bao = ThongBao(MaNguoiDung=ma_nguoi_nhan, NoiDung=noi_dung, MaBaiViet=ma_bai_viet, MaNguoiGui=ma_nguoi_gui)
        db.add(thong_bao)
        db.commit()
        db.refresh(thong_bao)
        return thong_bao 