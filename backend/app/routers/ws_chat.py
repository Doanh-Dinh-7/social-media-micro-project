from fastapi import WebSocket, HTTPException
from sqlalchemy.orm import Session
from app.controllers.message import MessageController
from app.schemas.message import MessageCreate

# Lưu kết nối WebSocket của từng user
active_connections = {}

async def chat_websocket(websocket: WebSocket, user_id: int, db: Session, data: dict):
    receiver_id = data["NguoiNhan"]
    message_text = data["NoiDung"]
    # Tạo schema MessageCreate
    message_data = MessageCreate(NguoiNhan=receiver_id, NoiDung=message_text)
    # Lưu tin nhắn vào DB qua controller
    try:
        await MessageController.send_message(user_id, message_data, db)
    except HTTPException as e:
        # Gửi lỗi về client qua WebSocket
        await websocket.send_json({"error": e.detail})
        return
    # Gửi tin nhắn cho người nhận nếu họ đang online
    if receiver_id in active_connections:
        await active_connections[receiver_id].send_json({
            "NguoiGui": user_id,
            "NoiDung": message_text
        }) 