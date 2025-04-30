from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from app.config.database import get_db
from app.utils.security import get_current_active_user
from app.controllers.friend_suggestion import FriendSuggestionController
from app.schemas.friend_suggestion import FriendSuggestionResponse

router = APIRouter(
    prefix="/friends/suggestions",
    tags=["Friend Suggestions"],
    responses={404: {"description": "Not found"}},
)

@router.get("/", response_model=FriendSuggestionResponse)
async def get_suggestions(
    limit: int = 10,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    """
    Lấy danh sách đề xuất kết bạn cho người dùng hiện tại.
    - Không bao gồm bạn bè hiện tại
    - Không bao gồm người dùng đã có lời mời kết bạn đang chờ
    - Không bao gồm chính người dùng hiện tại
    """
    return await FriendSuggestionController.get_friend_suggestions(current_user["nguoi_dung"].MaNguoiDung, db, limit) 