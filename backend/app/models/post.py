from sqlalchemy import Column, Integer, String, DateTime, ForeignKey, UnicodeText
from sqlalchemy.orm import relationship
from datetime import datetime
from app.config.database import Base

class QuyenRiengTu(Base):
    __tablename__ = "QuyenRiengTu"
    
    MaQuyenRiengTu = Column(Integer, primary_key=True, index=True, autoincrement=True)
    Loai = Column(String(50), nullable=False)  # Công khai, Bạn bè, Riêng tư
    
    # Relationship
    bai_viet = relationship("BaiViet", back_populates="quyen_rieng_tu")

class ChuDe(Base):
    __tablename__ = "ChuDe"
    
    MaChuDe = Column(Integer, primary_key=True, index=True, autoincrement=True)
    Loai = Column(String(100), nullable=False)  # Kinh nghiệm, ...
    
    # Relationship
    bai_viet = relationship("BaiViet", back_populates="chu_de")

class BaiViet(Base):
    __tablename__ = "BaiViet"
    
    MaBaiViet = Column(Integer, primary_key=True, index=True, autoincrement=True)
    MaNguoiDung = Column(Integer, ForeignKey("NguoiDung.MaNguoiDung"))
    MaQuyenRiengTu = Column(Integer, ForeignKey("QuyenRiengTu.MaQuyenRiengTu"))
    MaChuDe = Column(Integer, ForeignKey("ChuDe.MaChuDe"))
    NgayTao = Column(DateTime, nullable=False, default=datetime.now)
    NgayCapNhat = Column(DateTime, nullable=True)
    NoiDung = Column(UnicodeText, nullable=False)
    
    # Relationships
    nguoi_dung = relationship("NguoiDung", back_populates="bai_viet")
    quyen_rieng_tu = relationship("QuyenRiengTu", back_populates="bai_viet")
    chu_de = relationship("ChuDe", back_populates="bai_viet")
    binh_luan = relationship("BinhLuan", back_populates="bai_viet")
    luot_thich = relationship("LuotThich", back_populates="bai_viet")
    hinh_anh = relationship("HinhAnh", back_populates="bai_viet")

class BinhLuan(Base):
    __tablename__ = "BinhLuan"
    
    MaBinhLuan = Column(Integer, primary_key=True, index=True, autoincrement=True)
    MaBaiViet = Column(Integer, ForeignKey("BaiViet.MaBaiViet"))
    MaNguoiDung = Column(Integer, ForeignKey("NguoiDung.MaNguoiDung"))
    NgayTao = Column(DateTime, nullable=False, default=datetime.now)
    NoiDung = Column(UnicodeText, nullable=False)
    
    # Relationships
    bai_viet = relationship("BaiViet", back_populates="binh_luan")
    nguoi_dung = relationship("NguoiDung", back_populates="binh_luan")

class LuotThich(Base):
    __tablename__ = "LuotThich"
    
    MaLuotThich = Column(Integer, primary_key=True, index=True, autoincrement=True)
    MaBaiViet = Column(Integer, ForeignKey("BaiViet.MaBaiViet"))
    MaNguoiDung = Column(Integer, ForeignKey("NguoiDung.MaNguoiDung"))
    NgayTao = Column(DateTime, nullable=False, default=datetime.now)
    
    # Relationships
    bai_viet = relationship("BaiViet", back_populates="luot_thich")
    nguoi_dung = relationship("NguoiDung", back_populates="luot_thich")

class HinhAnh(Base):
    __tablename__ = "HinhAnh"
    
    MaHinhAnh = Column(Integer, primary_key=True, index=True, autoincrement=True)
    MaBaiDang = Column(Integer, ForeignKey("BaiViet.MaBaiViet"))
    Url = Column(String(500), nullable=False)
    
    # Relationship
    bai_viet = relationship("BaiViet", back_populates="hinh_anh")