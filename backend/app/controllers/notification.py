from sqlalchemy.orm import Session
from app.models.notification import ThongBao
from typing import List

class NotificationController:
    @staticmethod
    async def get_notifications(current_user_id: int, skip: int, limit: int, db: Session):
        notifications = db.query(ThongBao).filter(ThongBao.MaNguoiDung == current_user_id)
        notifications = notifications.order_by(ThongBao.ThoiGian.desc()).offset(skip).limit(limit).all()
        return notifications

    @staticmethod
    async def create_notification(ma_nguoi_nhan: int, noi_dung: str, db: Session):
        thong_bao = ThongBao(MaNguoiDung=ma_nguoi_nhan, NoiDung=noi_dung)
        db.add(thong_bao)
        db.commit()
        db.refresh(thong_bao)
        return thong_bao 