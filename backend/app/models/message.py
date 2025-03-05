from sqlalchemy import Column, Integer, String, DateTime, ForeignKey, Text
from sqlalchemy.orm import relationship
from datetime import datetime
from app.config.database import Base

class CuocTroChuyen(Base):
    __tablename__ = "CuocTroChuyen"
    
    MaCuocTroChuyen = Column(Integer, primary_key=True, index=True, autoincrement=True)
    NguoiDung_1 = Column(Integer, ForeignKey("NguoiDung.MaNguoiDung"))
    NguoiDung_2 = Column(Integer, ForeignKey("NguoiDung.MaNguoiDung"))
    TinNhanCuoi = Column(Integer, ForeignKey("TinNhan.MaTinNhan"), nullable=True)
    NgayTao = Column(DateTime, nullable=False, default=datetime.now)
    
    # Relationships
    nguoi_dung_1 = relationship("NguoiDung", foreign_keys=[NguoiDung_1])
    nguoi_dung_2 = relationship("NguoiDung", foreign_keys=[NguoiDung_2])
    tin_nhan = relationship("TinNhan", back_populates="cuoc_tro_chuyen", foreign_keys="TinNhan.MaCuocTroChuyen")
    tin_nhan_cuoi = relationship("TinNhan", foreign_keys=[TinNhanCuoi])

class TinNhan(Base):
    __tablename__ = "TinNhan"
    
    MaTinNhan = Column(Integer, primary_key=True, index=True, autoincrement=True)
    MaNguoiGui = Column(Integer, ForeignKey("NguoiDung.MaNguoiDung"))
    MaCuocTroChuyen = Column(Integer, ForeignKey("CuocTroChuyen.MaCuocTroChuyen"))
    NoiDung = Column(Text, nullable=False)
    NgayGui = Column(DateTime, nullable=False, default=datetime.now)
    
    # Relationships
    nguoi_gui = relationship("NguoiDung", back_populates="tin_nhan_gui", foreign_keys=[MaNguoiGui])
    cuoc_tro_chuyen = relationship("CuocTroChuyen", back_populates="tin_nhan", foreign_keys=[MaCuocTroChuyen])