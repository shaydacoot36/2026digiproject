import os
import sqlite3
from flask import Flask, render_template, g, request, redirect, url_for, jsonify

app = Flask(__name__)       

# Support both databases
DATABASE_DEV = 'spring boot project/ai-wrapper-java/data/devdb.sqlite'
DATABASE_DEV2 = 'dev2db.sqlite'

def get_db(db_name='dev2db'):
    """Get database connection. Use 'devdb' for Spring Boot SQLite, 'dev2db' for local."""
    db_path = DATABASE_DEV if db_name == 'devdb' else DATABASE_DEV2
    if 'db' not in g:
        g.db = sqlite3.connect(db_path)
        g.db.row_factory = sqlite3.Row
    return g.db

@app.teardown_appcontext
def close_db(error):
    db = g.pop('db', None)
    if db is not None:
        db.close()

@app.route("/")
def index():
    return render_template('home.html')

# API endpoints for Users (from devdb.sqlite - Spring Boot database)
@app.route("/api/users", methods=['GET'])
def get_users():
    """Get all users from Spring Boot database"""
    db = get_db('devdb')
    users = db.execute('SELECT user_id, name, email FROM users').fetchall()
    return jsonify([dict(u) for u in users])

@app.route("/api/users/<int:user_id>", methods=['GET'])
def get_user(user_id):
    """Get specific user with their tasks"""
    db = get_db('devdb')
    user = db.execute('SELECT user_id, name, email FROM users WHERE user_id = ?', (user_id,)).fetchone()
    if not user:
        return jsonify({"error": "User not found"}), 404
    
    tasks = db.execute('SELECT task_id, title, description, due_date FROM tasks WHERE user_id = ?', (user_id,)).fetchall()
    return jsonify({"user": dict(user), "tasks": [dict(t) for t in tasks]})

# API endpoints for Tasks (from devdb.sqlite - Spring Boot database)
@app.route("/api/tasks", methods=['GET'])
def get_tasks():
    """Get all tasks from Spring Boot database"""
    db = get_db('devdb')
    tasks = db.execute('SELECT task_id, title, description, due_date, user_id FROM tasks').fetchall()
    return jsonify([dict(t) for t in tasks])

@app.route("/api/database-info", methods=['GET'])
def database_info():
    """Get info about connected databases"""
    devdb_exists = os.path.exists(DATABASE_DEV)
    dev2db_exists = os.path.exists(DATABASE_DEV2)
    
    info = {
        "devdb_sqlite": {
            "path": DATABASE_DEV,
            "exists": devdb_exists,
            "description": "Spring Boot H2 SQLite database with Users and Tasks"
        },
        "dev2db_sqlite": {
            "path": DATABASE_DEV2,
            "exists": dev2db_exists,
            "description": "Legacy Flask SQLite database"
        }
    }
    
    # Try to get counts if databases exist
    try:
        if devdb_exists:
            db = get_db('devdb')
            user_count = db.execute('SELECT COUNT(*) as count FROM users').fetchone()[0]
            task_count = db.execute('SELECT COUNT(*) as count FROM tasks').fetchone()[0]
            info["devdb_sqlite"]["user_count"] = user_count
            info["devdb_sqlite"]["task_count"] = task_count
    except Exception as e:
        info["devdb_sqlite"]["error"] = str(e)
    
    return jsonify(info)

if __name__ == "__main__":
    app.run(debug=True)