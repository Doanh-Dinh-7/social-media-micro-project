from sqlalchemy.orm import Session
from app.models.notification import ThongBao
from typing import List

class NotificationController:
    @staticmethod
    async def get_notifications(current_user_id: int, skip: int, limit: int, db: Session):
        notifications = db.query(ThongBao).filter(ThongBao.MaNguoiDung == current_user_id)
        notifications = notifications.order_by(ThongBao.ThoiGian.desc()).offset(skip).limit(limit).all()
        return notifications 