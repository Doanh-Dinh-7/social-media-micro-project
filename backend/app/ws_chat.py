from fastapi import APIRouter, WebSocket, WebSocketDisconnect, Depends, HTTPException
from typing import Dict
from sqlalchemy.orm import Session
from app.config.database import get_db
from app.controllers.message import MessageController
from app.schemas.message import MessageCreate

router = APIRouter()

# Lưu kết nối WebSocket của từng user
active_connections: Dict[int, WebSocket] = {}

@router.websocket("/ws/chat/{user_id}")
async def chat_websocket(websocket: WebSocket, user_id: int, db: Session = Depends(get_db)):
    await websocket.accept()
    active_connections[user_id] = websocket
    try:
        while True:
            data = await websocket.receive_json()
            receiver_id = data["receiver_id"]
            message_text = data["message"]
            # Tạo schema MessageCreate
            message_data = MessageCreate(NguoiNhan=receiver_id, NoiDung=message_text)
            # Lưu tin nhắn vào DB qua controller
            try:
                await MessageController.send_message(user_id, message_data, db)
            except HTTPException as e:
                # Gửi lỗi về client qua WebSocket
                await websocket.send_json({"error": e.detail})
                continue  # hoặc break nếu muốn đóng kết nối
            # Gửi tin nhắn cho người nhận nếu họ đang online
            if receiver_id in active_connections:
                await active_connections[receiver_id].send_json({
                    "sender_id": user_id,
                    "message": message_text
                })
    except WebSocketDisconnect:
        del active_connections[user_id] 