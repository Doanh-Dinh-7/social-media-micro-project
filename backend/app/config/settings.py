from pydantic_settings import BaseSettings
from datetime import timedelta
import os
from dotenv import load_dotenv

# Load biến môi trường từ file .env
load_dotenv()

class Settings(BaseSettings):
    APP_NAME: str = "Social Network API"
    API_PREFIX: str = "/api"
    SECRET_KEY: str = os.getenv("SECRET_KEY")
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 30
    REFRESH_TOKEN_EXPIRE_DAYS: int = 7
    
    # Cấu hình SQL Server
    DB_USER: str = os.getenv("DB_USER")
    DB_PASSWORD: str = os.getenv("DB_PASSWORD")
    DB_SERVER: str = os.getenv("DB_SERVER")
    DB_NAME: str = os.getenv("DB_NAME")
    
    # Cấu hình lưu trữ file
    UPLOAD_FOLDER: str = "uploads"
    MAX_CONTENT_LENGTH: int = 16 * 1024 * 1024  # 16MB
    
    class Config:
        env_file = ".env"

settings = Settings()