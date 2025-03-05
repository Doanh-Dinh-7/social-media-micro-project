from sqlalchemy import Column, Integer, String, DateTime, ForeignKey, SmallInteger, Table, UniqueConstraint
from sqlalchemy.orm import relationship
from datetime import datetime
from app.config.database import Base

# Bảng trung gian cho mối quan hệ bạn bè
class BanBe(Base):
    __tablename__ = "BanBe"
    
    MaNguoiDung = Column(Integer, ForeignKey("NguoiDung.MaNguoiDung"), primary_key=True)
    MaBanBe = Column(Integer, ForeignKey("NguoiDung.MaNguoiDung"), primary_key=True)
    NgayTao = Column(DateTime, nullable=False, default=datetime.now)
    
    # Đảm bảo không có bản ghi trùng lặp
    __table_args__ = (
        UniqueConstraint('MaNguoiDung', 'MaBanBe', name='uq_ban_be'),
    )

class LoiMoiKetBan(Base):
    __tablename__ = "LoiMoiKetBan"
    
    MaLoiMoi = Column(Integer, primary_key=True, index=True, autoincrement=True)
    NguoiGui = Column(Integer, ForeignKey("NguoiDung.MaNguoiDung"))
    NguoiNhan = Column(Integer, ForeignKey("NguoiDung.MaNguoiDung"))
    TrangThai = Column(SmallInteger, default=0)  # 0: Chờ duyệt, 1: Chấp nhận, 2: Từ chối
    ThoiGian = Column(DateTime, nullable=False, default=datetime.now)
    
    # Relationships
    nguoi_gui = relationship("NguoiDung", foreign_keys=[NguoiGui], back_populates="loi_moi_gui")
    nguoi_nhan = relationship("NguoiDung", foreign_keys=[NguoiNhan], back_populates="loi_moi_nhan")

class GoiYKetBan(Base):
    __tablename__ = "GoiYKetBan"
    
    MaGoiY = Column(Integer, primary_key=True, index=True, autoincrement=True)
    NguoiGoiY = Column(Integer, ForeignKey("NguoiDung.MaNguoiDung"))
    NguoiDuocGoiY = Column(Integer, ForeignKey("NguoiDung.MaNguoiDung"))
    TrangThai = Column(SmallInteger, default=0)  # 0: Chưa phản hồi, 1: Chấp nhận, 2: Bỏ qua
    
    # Relationships
    nguoi_goi_y = relationship("NguoiDung", foreign_keys=[NguoiGoiY], back_populates="goi_y_gui")
    nguoi_duoc_goi_y = relationship("NguoiDung", foreign_keys=[NguoiDuocGoiY], back_populates="goi_y_nhan")