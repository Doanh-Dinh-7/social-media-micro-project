from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from sqlalchemy.engine import URL
from .settings import settings

# Cấu hình kết nối SQL Server
DATABASE_URL = URL.create(
    "mssql+pyodbc",
    username=settings.DB_USER,
    password=settings.DB_PASSWORD,
    host=settings.DB_SERVER,
    database=settings.DB_NAME,
    query={"driver": "ODBC Driver 17 for SQL Server"},
)

engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

# Dependency để lấy database session
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
        
if __name__ == "__main__":
    try:
        with engine.connect() as connection:
            print("Kết nối đến SQL Server thành công!")
    except Exception as e:
        print(f"Lỗi kết nối đến SQL Server: {e}")
