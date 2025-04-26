from fastapi import FastAPI, Depends
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.openapi.docs import get_swagger_ui_html
from fastapi.openapi.utils import get_openapi
from app.config.settings import settings
from app.config.database import engine, Base
from app.routers import auth, user, post, friendship, message
from app.middleware.auth import auth_middleware
import os

# Tạo các bảng trong database
Base.metadata.create_all(bind=engine)

# Tạo thư mục uploads nếu chưa tồn tại
os.makedirs(settings.UPLOAD_FOLDER, exist_ok=True)

app = FastAPI(
    title=settings.APP_NAME,
    description="API cho mạng xã hội mini",
    version="1.0.0",
    docs_url=None,  # Tắt docs mặc định
    redoc_url=None,  # Tắt redoc mặc định
)

# Cấu hình CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Trong môi trường production, hãy chỉ định cụ thể các domain được phép
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Thêm middleware xác thực
app.middleware("http")(auth_middleware)

# Mount thư mục uploads để phục vụ file tĩnh
app.mount("/uploads", StaticFiles(directory=settings.UPLOAD_FOLDER), name="uploads")

# Đăng ký các router
app.include_router(auth.router, prefix=settings.API_PREFIX)
app.include_router(user.router, prefix=settings.API_PREFIX)
app.include_router(post.router, prefix=settings.API_PREFIX) 
app.include_router(friendship.router, prefix=settings.API_PREFIX)
app.include_router(message.router, prefix=settings.API_PREFIX)

@app.get("/")
async def root():
    return {"message": "Chào mừng đến với API mạng xã hội mini"}

# Custom Swagger UI
@app.get("/docs", include_in_schema=False)
async def custom_swagger_ui_html():
    return get_swagger_ui_html(
        openapi_url="/openapi.json",
        title="API Documentation",
        swagger_js_url="https://cdn.jsdelivr.net/npm/swagger-ui-dist@5/swagger-ui-bundle.js",
        swagger_css_url="https://cdn.jsdelivr.net/npm/swagger-ui-dist@5/swagger-ui.css",
        swagger_ui_parameters={
            "persistAuthorization": True,
            "displayRequestDuration": True,
            "docExpansion": "none",
            "defaultModelsExpandDepth": -1,
            "defaultModelExpandDepth": 3,
            "defaultModelRendering": "example",
            "displayOperationId": False,
            "filter": True,
            "showExtensions": True,
            "showCommonExtensions": True,
            "supportedSubmitMethods": ["get", "post", "put", "delete", "patch"],
            "validatorUrl": None,
            "withCredentials": True,
            "oauth2RedirectUrl": None,
            "showMutatedRequest": True,
            "showRequestHeaders": True,
            "showResponseHeaders": True,
            "showResponseDescription": True,
            "showExtensions": True,
            "showCommonExtensions": True,
            "deepLinking": True,
            "syntaxHighlight.theme": "monokai",
            "syntaxHighlight.activate": True,
            "syntaxHighlight": True,
            "tryItOutEnabled": True,
            "requestSnippetsEnabled": True,
            "requestSnippets": {
                "generators": {
                    "curl_bash": {
                        "title": "cURL (bash)",
                        "syntax": "bash"
                    },
                    "curl_powershell": {
                        "title": "cURL (PowerShell)",
                        "syntax": "powershell"
                    },
                    "curl_cmd": {
                        "title": "cURL (CMD)",
                        "syntax": "bash"
                    }
                },
                "defaultExpanded": True,
                "languages": None
            }
        }
    )

# Custom OpenAPI schema
@app.get("/openapi.json", include_in_schema=False)
async def get_open_api_endpoint():
    openapi_schema = get_openapi(
        title=settings.APP_NAME,
        version="1.0.0",
        description="API cho mạng xã hội mini",
        routes=app.routes,
    )
    
    # Cấu hình security scheme
    openapi_schema["components"]["securitySchemes"] = {
        "BearerAuth": {
            "type": "http",
            "scheme": "bearer",
            "bearerFormat": "JWT",
            "description": "Nhập access token của bạn. Ví dụ: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        }
    }

    # Thêm security requirement cho tất cả các endpoint trừ các endpoint công khai
    public_paths = ["/", "/docs", "/redoc", "/openapi.json", "/favicon.ico", "/api/auth/login", "/api/auth/register"]
    for path, methods in openapi_schema["paths"].items():
        if path not in public_paths:
            for method in methods.values():
                method.setdefault("security", []).append({"BearerAuth": []})

    return openapi_schema

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)

