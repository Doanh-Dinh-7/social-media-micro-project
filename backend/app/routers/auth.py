from fastapi import APIRouter, Depends, HTTPException, status, Body
from sqlalchemy.orm import Session
from app.config.database import get_db
from app.schemas.user import UserCreate, UserLogin, TokenResponse
from app.controllers.auth import AuthController

router = APIRouter(
    prefix="/auth",
    tags=["Authentication"],
    responses={404: {"description": "Not found"}},
)

@router.post("/register", response_model=TokenResponse, status_code=status.HTTP_201_CREATED)
async def register(user_data: UserCreate, db: Session = Depends(get_db)):
    """
    Đăng ký tài khoản mới
    """
    try:
        user = await AuthController.register(user_data, db)
        # Đăng nhập tự động sau khi đăng ký
        login_data = UserLogin(Email=user_data.Email, MatKhau=user_data.MatKhau)
        return await AuthController.login(login_data, db)
    except Exception as e:
        print(f"Đăng ký lỗi: {e}")  # Log lỗi ra console
        raise HTTPException(status_code=500, detail=str(e))  # Trả về lỗi chi tiết

@router.post("/login", response_model=TokenResponse)
async def login(user_data: UserLogin, db: Session = Depends(get_db)):
    """
    Đăng nhập và lấy token
    """
    return await AuthController.login(user_data, db)

@router.post("/refresh-token")
async def refresh_token(refresh_token: str = Body(..., embed=True), db: Session = Depends(get_db)):
    """
    Làm mới access token bằng refresh token
    """
    return await AuthController.refresh_token(refresh_token, db)