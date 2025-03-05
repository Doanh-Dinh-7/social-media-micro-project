from fastapi import HTTPException, status, UploadFile, File
from sqlalchemy.orm import Session
from app.models.post import BaiViet, HinhAnh, BinhLuan, LuotThich
from app.schemas.post import PostCreate, PostResponse, PostUpdate
from typing import List, Optional
import os
import uuid
from datetime import datetime
from app.config.settings import settings

class PostController:
    @staticmethod
    async def create_post(
        post_data: PostCreate, 
        user_id: int, 
        images: Optional[List[UploadFile]] = None,
        db: Session = None
    ):
        # Tạo bài viết mới
        new_post = BaiViet(
            MaNguoiDung=user_id,
            MaQuyenRiengTu=post_data.MaQuyenRiengTu,
            MaChuDe=post_data.MaChuDe,
            NoiDung=post_data.NoiDung
        )
        db.add(new_post)
        db.flush()
        
        # Xử lý hình ảnh nếu có
        if images:
            for image in images:
                # Tạo tên file duy nhất
                file_extension = os.path.splitext(image.filename)[1]
                unique_filename = f"{uuid.uuid4()}{file_extension}"
                file_path = os.path.join(settings.UPLOAD_FOLDER, unique_filename)
                
                # Lưu file
                with open(file_path, "wb") as buffer:
                    buffer.write(await image.read())
                
                # Lưu thông tin hình ảnh vào database
                new_image = HinhAnh(
                    MaBaiDang=new_post.MaBaiViet,
                    Url=f"/uploads/{unique_filename}"
                )
                db.add(new_image)
        
        db.commit()
        db.refresh(new_post)
        
        # Lấy bài viết với thông tin đầy đủ
        return await PostController.get_post_by_id(new_post.MaBaiViet, user_id, db)
    
    @staticmethod
    async def get_post_by_id(post_id: int, user_id: int, db: Session):
        # Lấy bài viết theo ID
        post = db.query(BaiViet).filter(BaiViet.MaBaiViet == post_id).first()
        if not post:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Bài viết không tồn tại"
            )
        
        # Lấy danh sách hình ảnh
        images = db.query(HinhAnh).filter(HinhAnh.MaBaiDang == post_id).all()
        
        # Đếm số lượt thích
        like_count = db.query(LuotThich).filter(LuotThich.MaBaiViet == post_id).count()
        
        # Đếm số bình luận
        comment_count = db.query(BinhLuan).filter(BinhLuan.MaBaiViet == post_id).count()
        
        # Kiểm tra người dùng hiện tại đã thích bài viết chưa
        user_liked = db.query(LuotThich).filter(
            LuotThich.MaBaiViet == post_id,
            LuotThich.MaNguoiDung == user_id
        ).first() is not None
        
        # Tạo response
        post_response = PostResponse.from_orm(post)
        post_response.hinh_anh = images
        post_response.so_luot_thich = like_count
        post_response.so_binh_luan = comment_count
        post_response.da_thich = user_liked
        
        return post_response
    
    @staticmethod
    async def get_news_feed(user_id: int, skip: int, limit: int, db: Session):
        # Lấy danh sách bạn bè
        from app.models.friendship import BanBe
        friend_ids = db.query(BanBe.MaBanBe).filter(BanBe.MaNguoiDung == user_id).all()
        friend_ids = [id[0] for id in friend_ids]
        friend_ids.append(user_id)  # Thêm ID của người dùng hiện tại
        
        # Lấy bài viết từ người dùng và bạn bè
        posts = db.query(BaiViet).filter(
            BaiViet.MaNguoiDung.in_(friend_ids)
        ).order_by(BaiViet.NgayTao.desc()).offset(skip).limit(limit).all()
        
        # Tạo response
        result = []
        for post in posts:
            post_response = await PostController.get_post_by_id(post.MaBaiViet, user_id, db)
            result.append(post_response)
        
        return result
    
    @staticmethod
    async def update_post(post_id: int, post_data: PostUpdate, user_id: int, db: Session):
        # Lấy bài viết theo ID
        post = db.query(BaiViet).filter(BaiViet.MaBaiViet == post_id).first()
        if not post:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Bài viết không tồn tại"
            )
        
        # Kiểm tra quyền sở hữu
        if post.MaNguoiDung != user_id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Không có quyền chỉnh sửa bài viết này"
            )
        
        # Cập nhật thông tin
        if post_data.NoiDung is not None:
            post.NoiDung = post_data.NoiDung
        if post_data.MaQuyenRiengTu is not None:
            post.MaQuyenRiengTu = post_data.MaQuyenRiengTu
        if post_data.MaChuDe is not None:
            post.MaChuDe = post_data.MaChuDe
        
        post.NgayCapNhat = datetime.now()
        db.commit()
        db.refresh(post)
        
        # Lấy bài viết với thông tin đầy đủ
        return await PostController.get_post_by_id(post.MaBaiViet, user_id, db)
    
    @staticmethod
    async def delete_post(post_id: int, user_id: int, db: Session):
        # Lấy bài viết theo ID
        post = db.query(BaiViet).filter(BaiViet.MaBaiViet == post_id).first()
        if not post:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Bài viết không tồn tại"
            )
        
        # Kiểm tra quyền sở hữu
        if post.MaNguoiDung != user_id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Không có quyền xóa bài viết này"
            )
        
        # Xóa hình ảnh liên quan
        images = db.query(HinhAnh).filter(HinhAnh.MaBaiDang == post_id).all()
        for image in images:
            # Xóa file hình ảnh
            file_path = os.path.join(os.getcwd(), image.Url.lstrip('/'))
            if os.path.exists(file_path):
                os.remove(file_path)
            db.delete(image)
        
        # Xóa lượt thích
        db.query(LuotThich).filter(LuotThich.MaBaiViet == post_id).delete()
        
        # Xóa bình luận
        db.query(BinhLuan).filter(BinhLuan.MaBaiViet == post_id).delete()
        
        # Xóa bài viết
        db.delete(post)
        db.commit()
        
        return {"message": "Bài viết đã được xóa thành công"}