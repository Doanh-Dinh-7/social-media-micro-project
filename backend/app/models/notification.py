from sqlalchemy import Column, Integer, String, DateTime, ForeignKey, Text
from sqlalchemy.orm import relationship
from datetime import datetime
from app.config.database import Base

class ThongBao(Base):
    __tablename__ = "ThongBao"
    
    MaThongBao = Column(Integer, primary_key=True, index=True, autoincrement=True)
    MaNguoiDung = Column(Integer, ForeignKey("NguoiDung.MaNguoiDung"))
    NoiDung = Column(Text, nullable=False)
    ThoiGian = Column(DateTime, nullable=False, default=datetime.now)
    DaDoc = Column(Integer, default=0)  # 0: Chưa đọc, 1: Đã đọc
    
    # Relationship
    nguoi_dung = relationship("NguoiDung", back_populates="thong_bao")