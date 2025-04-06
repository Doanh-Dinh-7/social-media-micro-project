from fastapi import Request, HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from app.utils.security import get_current_user
from app.config.database import get_db
from sqlalchemy.orm import Session

security = HTTPBearer()

# Danh sách các endpoint công khai không cần xác thực
PUBLIC_ENDPOINTS = [
    "/",
    "/docs",
    "/redoc",
    "/openapi.json",
    "/favicon.ico",
    "/api/auth/login",
    "/api/auth/register"
]

async def auth_middleware(request: Request, call_next):

    # Kiểm tra nếu là endpoint công khai
    if request.url.path in PUBLIC_ENDPOINTS:
        return await call_next(request)
    
    try:
        # Lấy token từ header
        auth_header = request.headers.get("Authorization")
        if not auth_header:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Missing Authorization header",
                headers={"WWW-Authenticate": "Bearer"}
            )
        
        # # Kiểm tra định dạng token
        # if not auth_header.startswith("Bearer "):
        #     raise HTTPException(
        #         status_code=status.HTTP_401_UNAUTHORIZED,
        #         detail="Invalid token format",
        #         headers={"WWW-Authenticate": "Bearer"}
        #     )
        
        # Lấy token
        token = auth_header.split(" ")[1]
        
        # Kiểm tra token
        db = next(get_db())
        await get_current_user(token, db)
        
        # Tiếp tục xử lý request
        response = await call_next(request)
        return response
        
    except HTTPException as e:
        raise e
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid token",
            headers={"WWW-Authenticate": "Bearer"}
        )

