from fastapi import APIRouter, Depends, status, WebSocket, WebSocketDisconnect
from sqlalchemy.orm import Session
from typing import List, Any
from app.config.database import get_db
from app.schemas.message import MessageCreate, MessageResponse
from app.controllers.message import MessageController
from app.utils.security import get_current_active_user
from app.routers.ws_chat import chat_websocket, active_connections

router = APIRouter(
    prefix="/messages",
    tags=["Messages"],
    responses={404: {"description": "Not found"}},
)

# @router.post("/", response_model=MessageResponse, status_code=status.HTTP_201_CREATED)
# async def send_message(
#     data: MessageCreate,
#     db: Session = Depends(get_db),
#     current_user = Depends(get_current_active_user)
# ):
#     '''
#     Gửi tin nhắn
#     '''
#     return await MessageController.send_message(current_user["nguoi_dung"].MaNguoiDung, data, db)

@router.websocket("/{user_id}")
async def websocket_endpoint(websocket: WebSocket, user_id: int, db: Session = Depends(get_db)):
    """
    WebSocket cho chat giữa hai người dùng
    """
    await websocket.accept()
    active_connections[user_id] = websocket
    try:
        while True:
            data = await websocket.receive_json()
            await chat_websocket(websocket, user_id, db, data)
    except WebSocketDisconnect:
        del active_connections[user_id]

@router.get("/conversations", response_model=List[Any])
async def get_conversations(
    skip: int = 0,
    limit: int = 20,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    '''
    Lấy danh sách hộp tin nhắn (conversation list)
    '''
    return await MessageController.get_conversations(current_user["nguoi_dung"].MaNguoiDung, skip, limit, db) 

@router.get("/conversations/search", response_model=List[Any])
async def search_conversations(
    keyword: str,
    skip: int = 0,
    limit: int = 20,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    '''
    Tìm kiếm cuộc trò chuyện theo tên người dùng
    '''
    return await MessageController.search_conversations(current_user["nguoi_dung"].MaNguoiDung, keyword, skip, limit, db)

@router.get("/{user_id}", response_model=List[MessageResponse])
async def get_messages(
    user_id: int,
    skip: int = 0,
    limit: int = 20,
    db: Session = Depends(get_db),
    current_user = Depends(get_current_active_user)
):
    '''
    Lấy tin nhắn giữa hai người dùng
    '''
    return await MessageController.get_messages(current_user["nguoi_dung"].MaNguoiDung, user_id, skip, limit, db)

