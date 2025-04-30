from sqlalchemy import Column, Integer, String, DateTime, ForeignKey, Boolean, SmallInteger, UnicodeText
from sqlalchemy.orm import relationship
from datetime import datetime
from app.config.database import Base
from app.models.notification import ThongBao

class NguoiDung(Base):
    __tablename__ = "NguoiDung"
    
    MaNguoiDung = Column(Integer, primary_key=True, index=True, autoincrement=True)
    TenNguoiDung = Column(UnicodeText, nullable=False)
    NgayTao = Column(DateTime, nullable=False, default=datetime.now)
    AnhDaiDien = Column(String(255), nullable=True)  # Đường dẫn avatar
    AnhBia = Column(String(255), nullable=True)      # Đường dẫn ảnh bìa
    
    # Relationships
    tai_khoan = relationship("TaiKhoan", back_populates="nguoi_dung", uselist=False)
    bai_viet = relationship("BaiViet", back_populates="nguoi_dung")
    binh_luan = relationship("BinhLuan", back_populates="nguoi_dung")
    luot_thich = relationship("LuotThich", back_populates="nguoi_dung")
    thong_bao = relationship("ThongBao", back_populates="nguoi_dung")
    
    # Relationships cho tin nhắn
    tin_nhan_gui = relationship("TinNhan", foreign_keys="TinNhan.MaNguoiGui", back_populates="nguoi_gui")
    
    # Relationships cho kết bạn
    loi_moi_gui = relationship("LoiMoiKetBan", foreign_keys="LoiMoiKetBan.NguoiGui", back_populates="nguoi_gui")
    loi_moi_nhan = relationship("LoiMoiKetBan", foreign_keys="LoiMoiKetBan.NguoiNhan", back_populates="nguoi_nhan")
    
    # Relationships cho gợi ý kết bạn
    goi_y_gui = relationship("GoiYKetBan", foreign_keys="GoiYKetBan.NguoiGoiY", back_populates="nguoi_goi_y")
    goi_y_nhan = relationship("GoiYKetBan", foreign_keys="GoiYKetBan.NguoiDuocGoiY", back_populates="nguoi_duoc_goi_y")

class TaiKhoan(Base):
    __tablename__ = "TaiKhoan"
    
    MaTaiKhoan = Column(Integer, primary_key=True, index=True, autoincrement=True)
    MaNguoiDung = Column(Integer, ForeignKey("NguoiDung.MaNguoiDung"), unique=True)
    Email = Column(String(255), unique=True, nullable=False, index=True)
    MatKhau = Column(String(255), nullable=False)
    TrangThai = Column(SmallInteger, default=1)  # 1: Hoạt động, 0: Bị khóa
    
    # Relationship
    nguoi_dung = relationship("NguoiDung", back_populates="tai_khoan")