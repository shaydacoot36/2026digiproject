
"""SQLite Database Query Tool"""

import sqlite3
import os
import json

DATABASE_DEV = 'spring boot project/ai-wrapper-java/data/devdb.sqlite'
DATABASE_DEV2 = 'dev2db.sqlite'

def connect_db(db_path):
    """Connect to SQLite database"""
    if not os.path.exists(db_path):
        return None
    try:
        conn = sqlite3.connect(db_path)
        conn.row_factory = sqlite3.Row
        return conn
    except Exception as e:
        print(f"Error: {e}")
        return None

def get_users(conn):
    """Get all users"""
    try:
        cursor = conn.execute('SELECT user_id, name, email FROM users')
        return [dict(row) for row in cursor.fetchall()]
    except Exception:
        return []

def get_tasks(conn):
    """Get all tasks"""
    try:
        cursor = conn.execute('SELECT task_id, title, description, due_date, user_id FROM tasks')
        return [dict(row) for row in cursor.fetchall()]
    except Exception:
        return []

def database_info():
    """Get database information"""
    info = {
        "devdb_sqlite": {"path": DATABASE_DEV, "exists": os.path.exists(DATABASE_DEV)},
        "dev2db_sqlite": {"path": DATABASE_DEV2, "exists": os.path.exists(DATABASE_DEV2)}
    }
    
    if info["devdb_sqlite"]["exists"]:
        conn = connect_db(DATABASE_DEV)
        if conn:
            try:
                info["devdb_sqlite"]["users"] = conn.execute('SELECT COUNT(*) as c FROM users').fetchone()[0]
                info["devdb_sqlite"]["tasks"] = conn.execute('SELECT COUNT(*) as c FROM tasks').fetchone()[0]
                conn.close()
            except:
                pass
    
    return info

def main():
    """Main interface"""
    print("\n" + "="*60)
    print("  SQLite Database Query Tool")
    print("="*60)
    
    info = database_info()
    print("\n Databases:")
    for db, data in info.items():
        print(f"  {db}: {data}")
    
    conn = connect_db(DATABASE_DEV)
    if not conn:
        print("\n Cannot connect to devdb.sqlite")
        return
    
    print("\n Connected to devdb.sqlite\n")
    
    users = get_users(conn)
    print("Users Found:", len(users))
    print(json.dumps(users, indent=2))
    
    tasks = get_tasks(conn)
    print("\n Tasks Found:", len(tasks))
    print(json.dumps(tasks, indent=2))
    
    conn.close()

if __name__ == "__main__":
    main()
