from sqlalchemy import event
from app.config.database import engine

# Hàm log trạng thái pool chi tiết
def print_pool_detail():
    pool_size = engine.pool.size()
    max_overflow = engine.pool._max_overflow
    checked_out = engine.pool.checkedout()
    total_limit = pool_size + max_overflow
    remaining = total_limit - checked_out
    print(engine.pool.status())
    print(f"POOL LIMIT: {pool_size} (pool_size) + {max_overflow} (max_overflow) = {total_limit} (tổng tối đa)")
    print(f"Checked out: {checked_out} | Còn lại có thể tạo: {remaining}")
    print("="*20)
    print()

# Hàm log khi có connection mới
@event.listens_for(engine, "connect")
def connect_event(dbapi_connection, connection_record):
    print(f"[DB_MONITOR] New connection opened. MỞ KẾT NỐI MỚI. Connection id: {id(dbapi_connection)}")
    print_pool_detail()

# Hàm log khi connection được đóng
@event.listens_for(engine, "close")
def close_event(dbapi_connection, connection_record):
    print(f"[DB_MONITOR] Connection closed. ĐÓNG KẾT NỐI. Connection id: {id(dbapi_connection)}")
    print_pool_detail()

# Hàm log khi connection được check-out (lấy ra dùng)
@event.listens_for(engine, "checkout")
def checkout_event(dbapi_connection, connection_record, connection_proxy):
    print(f"[DB_MONITOR] Connection checked out. LẤY RA DÙNG. Connection id: {id(dbapi_connection)}")
    print_pool_detail()

# Hàm log khi connection được trả về pool
@event.listens_for(engine, "checkin")
def checkin_event(dbapi_connection, connection_record):
    print(f"[DB_MONITOR] Connection checked in. TRẢ VỀ POOL. Connection id: {id(dbapi_connection)}")
    print_pool_detail() 
