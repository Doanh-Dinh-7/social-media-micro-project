from fastapi import HTTPException, status, UploadFile, File
from sqlalchemy.orm import Session
from sqlalchemy import desc
from app.models.post import BaiViet, HinhAnh, BinhLuan, LuotThich
from app.schemas.post import PostCreate, PostResponse, PostUpdate, CommentCreate, CommentResponse, LikeResponse
from typing import List, Optional
import os
import uuid
from datetime import datetime
from app.config.settings import settings
from app.utils.cloudinary import upload_image
import cloudinary
from app.controllers.notification import NotificationController
from app.models.user import NguoiDung

class PostController:
    @staticmethod
    async def create_post(
        post_data: PostCreate, 
        user_id: int, 
        images: Optional[List[UploadFile]] = None,
        db: Session = None
    ):
        try:
            # Tạo bài viết mới
            new_post = BaiViet(
                MaNguoiDung=user_id,
                MaQuyenRiengTu=post_data.MaQuyenRiengTu,
                MaChuDe=post_data.MaChuDe,
                NoiDung=post_data.NoiDung
            )
            db.add(new_post)
            db.flush()  # Get the post ID without committing
            
            # Xử lý hình ảnh nếu có
            if images:
                for image in images:
                    try:
                        # Upload image to Cloudinary
                        result = await upload_image(await image.read())
                        
                        # Lưu thông tin hình ảnh vào database
                        new_image = HinhAnh(
                            MaBaiDang=new_post.MaBaiViet,
                            Url=result['secure_url']  # Sử dụng secure_url từ Cloudinary
                        )
                        db.add(new_image)
                    except Exception as e:
                        # Nếu có lỗi khi upload ảnh, rollback transaction
                        db.rollback()
                        raise HTTPException(
                            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                            detail=f"Lỗi khi upload ảnh: {str(e)}"
                        )
            
            db.commit()
            db.refresh(new_post)
            
            # Lấy bài viết với thông tin đầy đủ
            return await PostController.get_post_by_id(new_post.MaBaiViet, user_id, db)
            
        except Exception as e:
            db.rollback()
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail=f"Lỗi khi tạo bài viết: {str(e)}"
            )
    
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
        # posts = db.query(BaiViet).filter(
        #     BaiViet.MaNguoiDung.in_(friend_ids)
        # ).order_by(BaiViet.NgayTao.desc()).offset(skip).limit(limit).all()
        
        posts = db.query(BaiViet).order_by(BaiViet.NgayTao.desc()).offset(skip).limit(limit).all()
        
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
            try:
                # Xóa ảnh từ Cloudinary
                public_id = image.Url.split('/')[-1].split('.')[0]  # Lấy public_id từ URL
                cloudinary.uploader.destroy(public_id)
            except Exception as e:
                # Log lỗi nhưng vẫn tiếp tục xóa bài viết
                print(f"Error deleting image from Cloudinary: {str(e)}")
            
            # Xóa record trong database
            db.delete(image)
        
        # Xóa lượt thích
        db.query(LuotThich).filter(LuotThich.MaBaiViet == post_id).delete()
        
        # Xóa bình luận
        db.query(BinhLuan).filter(BinhLuan.MaBaiViet == post_id).delete()
        
        # Xóa bài viết
        db.delete(post)
        db.commit()
        
        return {"message": "Bài viết đã được xóa thành công"}

class CommentController:
    @staticmethod
    def get_binh_luan_by_bai_viet(db: Session, ma_bai_viet: int, skip: int = 0, limit: int = 10):
        binh_luans = db.query(BinhLuan).filter(BinhLuan.MaBaiViet == ma_bai_viet).order_by(BinhLuan.NgayTao.asc()).offset(skip).limit(limit).all()
        result = []
        for bl in binh_luans:
            user = db.query(NguoiDung).filter(NguoiDung.MaNguoiDung == bl.MaNguoiDung).first()
            result.append({
                "MaBinhLuan": bl.MaBinhLuan,
                "MaBaiViet": bl.MaBaiViet,
                "MaNguoiDung": bl.MaNguoiDung,
                "NoiDung": bl.NoiDung,
                "NgayTao": bl.NgayTao,
                "TenNguoiDung": user.TenNguoiDung if user else None,
                "AnhDaiDien": user.AnhDaiDien if user else None
            })
        return result

    @staticmethod
    async def create_binh_luan(db: Session, binh_luan: CommentCreate, ma_nguoi_dung: int):
        # Kiểm tra bài viết có tồn tại không
        bai_viet = db.query(BaiViet).filter(BaiViet.MaBaiViet == binh_luan.MaBaiViet).first()
        if not bai_viet:
            raise HTTPException(status_code=404, detail="Bài viết không tồn tại")

        db_binh_luan = BinhLuan(
            MaBaiViet=binh_luan.MaBaiViet,
            MaNguoiDung=ma_nguoi_dung,
            NoiDung=binh_luan.NoiDung,
            NgayTao=datetime.now()
        )
        db.add(db_binh_luan)
        db.commit()
        db.refresh(db_binh_luan)

        # Lấy thông tin người dùng
        bl_dict = CommentResponse.from_orm(db_binh_luan).dict()
        bl_dict["TenNguoiDung"] = db_binh_luan.nguoi_dung.TenNguoiDung

        # Chỉ tạo thông báo nếu người bình luận KHÁC chủ bài viết
        if bai_viet.MaNguoiDung != ma_nguoi_dung:
            user = db.query(NguoiDung).filter(NguoiDung.MaNguoiDung == ma_nguoi_dung).first()
            noi_dung_tb = f"{user.TenNguoiDung} đã bình luận về bài viết của bạn: '{binh_luan.NoiDung}'"
            import asyncio
            asyncio.create_task(NotificationController.create_notification(bai_viet.MaNguoiDung, noi_dung_tb, db))
            # await NotificationController.create_notification(bai_viet.MaNguoiDung, noi_dung_tb, db)

        return bl_dict
    
    @staticmethod
    def delete_binh_luan(db: Session, ma_bai_viet: int, ma_binh_luan: int, ma_nguoi_dung: int):
        binh_luan = db.query(BinhLuan).filter(
            BinhLuan.MaBaiViet == ma_bai_viet,
            BinhLuan.MaBinhLuan == ma_binh_luan,
            BinhLuan.MaNguoiDung == ma_nguoi_dung
        ).first()
        if not binh_luan:
            raise HTTPException(status_code=404, detail="Bình luận không tồn tại")
        
        db.delete(binh_luan)
        db.commit()
        return {"message": "Đã xóa bình luận"}
    

class LikeController:
    @staticmethod
    def get_luot_thich_by_bai_viet(db: Session, ma_bai_viet: int, skip: int = 0, limit: int = 10):
        luot_thich = db.query(LuotThich).filter(LuotThich.MaBaiViet == ma_bai_viet)\
            .order_by(desc(LuotThich.NgayTao))\
            .offset(skip).limit(limit).all()
        
        # Lấy thông tin người dùng cho mỗi lượt thích
        result = []
        for lt in luot_thich:
            lt_dict = LikeResponse.from_orm(lt).dict()
            lt_dict["TenNguoiDung"] = lt.nguoi_dung.TenNguoiDung
            result.append(lt_dict)
        
        return result

    @staticmethod
    async def create_luot_thich(db: Session, ma_bai_viet: int, ma_nguoi_dung: int):
        # Kiểm tra bài viết có tồn tại không
        bai_viet = db.query(BaiViet).filter(BaiViet.MaBaiViet == ma_bai_viet).first()
        if not bai_viet:
            raise HTTPException(status_code=404, detail="Bài viết không tồn tại")

        # Kiểm tra đã thích chưa
        existing_like = db.query(LuotThich).filter(
            LuotThich.MaBaiViet == ma_bai_viet,
            LuotThich.MaNguoiDung == ma_nguoi_dung
        ).first()
        if existing_like:
            raise HTTPException(status_code=400, detail="Bạn đã thích bài viết này")

        db_luot_thich = LuotThich(
            MaBaiViet=ma_bai_viet,
            MaNguoiDung=ma_nguoi_dung,
            NgayTao=datetime.now()
        )
        db.add(db_luot_thich)
        db.commit()
        db.refresh(db_luot_thich)

        # Lấy thông tin người dùng
        lt_dict = LikeResponse.from_orm(db_luot_thich).dict()
        lt_dict["TenNguoiDung"] = db_luot_thich.nguoi_dung.TenNguoiDung

        # Chỉ tạo thông báo nếu người thích KHÁC chủ bài viết
        if bai_viet.MaNguoiDung != ma_nguoi_dung:
            user = db.query(NguoiDung).filter(NguoiDung.MaNguoiDung == ma_nguoi_dung).first()
            noi_dung_tb = f"{user.TenNguoiDung} đã thích bài viết của bạn."
            # await NotificationController.create_notification(bai_viet.MaNguoiDung, noi_dung_tb, db)
            import asyncio
            asyncio.create_task(NotificationController.create_notification(bai_viet.MaNguoiDung, noi_dung_tb, db))

        return lt_dict

    @staticmethod
    def delete_luot_thich(db: Session, ma_bai_viet: int, ma_nguoi_dung: int):
        luot_thich = db.query(LuotThich).filter(
            LuotThich.MaBaiViet == ma_bai_viet,
            LuotThich.MaNguoiDung == ma_nguoi_dung
        ).first()
        
        if not luot_thich:
            raise HTTPException(status_code=404, detail="Bạn chưa thích bài viết này")
        
        db.delete(luot_thich)
        db.commit()
        return {"message": "Đã bỏ thích bài viết"}