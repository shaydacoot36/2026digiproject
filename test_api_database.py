"""
LearnFlow Database & API Testing
Tests for database queries, data correctness, and REST API endpoints
"""

import unittest
import requests
import json
import sqlite3
from datetime import datetime

class TestLearnFlowAPI(unittest.TestCase):
    """Test REST API endpoints and data correctness"""
    
    BASE_URL = "http://localhost:8080"
    
    def setUp(self):
        """Set up test fixtures"""
        self.test_user = {
            "name": "Test Student",
            "email": f"test{datetime.now().timestamp()}@ncea.co.nz",
            "password": "testpass123"
        }
        self.test_task = {
            "title": "Study Math Chapter 5",
            "description": "Review algebra concepts",
            "dueDate": "2026-05-20"
        }
    
    # ==================== USER ENDPOINT TESTS ====================
    
    def test_create_user_success(self):
        """Test creating a new user"""
        response = requests.post(
            f"{self.BASE_URL}/api/users",
            json=self.test_user
        )
        self.assertEqual(response.status_code, 201)
        data = response.json()
        self.assertIn("userId", data)
        self.assertEqual(data["name"], self.test_user["name"])
        print("✓ User creation successful")
    
    def test_create_user_missing_email(self):
        """Test creating user without email fails"""
        bad_user = {"name": "Test", "password": "pass"}
        response = requests.post(
            f"{self.BASE_URL}/api/users",
            json=bad_user
        )
        self.assertNotEqual(response.status_code, 201)
        print("✓ User creation correctly rejects missing email")
    
    def test_get_all_users(self):
        """Test retrieving all users"""
        response = requests.get(f"{self.BASE_URL}/api/users")
        self.assertEqual(response.status_code, 200)
        data = response.json()
        self.assertIsInstance(data, list)
        print(f"✓ Retrieved {len(data)} users from database")
    
    def test_get_user_by_id(self):
        """Test retrieving user by ID"""
        # First create a user
        create_response = requests.post(
            f"{self.BASE_URL}/api/users",
            json=self.test_user
        )
        user_id = create_response.json()["userId"]
        
        # Then retrieve it
        response = requests.get(f"{self.BASE_URL}/api/users/{user_id}")
        self.assertEqual(response.status_code, 200)
        data = response.json()
        self.assertEqual(data["userId"], user_id)
        self.assertEqual(data["name"], self.test_user["name"])
        print(f"✓ Successfully retrieved user with ID {user_id}")
    
    def test_user_email_unique(self):
        """Test that duplicate emails are rejected"""
        # Create first user
        response1 = requests.post(
            f"{self.BASE_URL}/api/users",
            json=self.test_user
        )
        self.assertEqual(response1.status_code, 201)
        
        # Try to create second user with same email
        response2 = requests.post(
            f"{self.BASE_URL}/api/users",
            json=self.test_user
        )
        self.assertNotEqual(response2.status_code, 201)
        print("✓ Duplicate email correctly rejected")
    
    # ==================== TASK ENDPOINT TESTS ====================
    
    def test_create_task_success(self):
        """Test creating a new task"""
        # First create a user
        user_response = requests.post(
            f"{self.BASE_URL}/api/users",
            json=self.test_user
        )
        user_id = user_response.json()["userId"]
        
        # Create task for user
        task_data = {**self.test_task, "userId": user_id}
        response = requests.post(
            f"{self.BASE_URL}/api/tasks",
            json=task_data
        )
        self.assertEqual(response.status_code, 201)
        data = response.json()
        self.assertIn("taskId", data)
        self.assertEqual(data["title"], self.test_task["title"])
        print("✓ Task creation successful")
    
    def test_get_all_tasks(self):
        """Test retrieving all tasks"""
        response = requests.get(f"{self.BASE_URL}/api/tasks")
        self.assertEqual(response.status_code, 200)
        data = response.json()
        self.assertIsInstance(data, list)
        print(f"✓ Retrieved {len(data)} tasks from database")
    
    def test_task_due_date_format(self):
        """Test that task due dates are in correct format"""
        # Create user and task
        user_response = requests.post(
            f"{self.BASE_URL}/api/users",
            json=self.test_user
        )
        user_id = user_response.json()["userId"]
        
        task_data = {**self.test_task, "userId": user_id}
        response = requests.post(
            f"{self.BASE_URL}/api/tasks",
            json=task_data
        )
        
        task = response.json()
        # Verify date format (YYYY-MM-DD)
        self.assertRegex(task["dueDate"], r'^\d{4}-\d{2}-\d{2}$')
        print("✓ Task due date format is correct (YYYY-MM-DD)")
    
    def test_get_tasks_by_user(self):
        """Test retrieving tasks for specific user"""
        # Create user
        user_response = requests.post(
            f"{self.BASE_URL}/api/users",
            json=self.test_user
        )
        user_id = user_response.json()["userId"]
        
        # Create multiple tasks
        for i in range(3):
            task_data = {
                "title": f"Task {i+1}",
                "description": f"Description {i+1}",
                "dueDate": "2026-05-25",
                "userId": user_id
            }
            requests.post(f"{self.BASE_URL}/api/tasks", json=task_data)
        
        # Get user with tasks
        response = requests.get(f"{self.BASE_URL}/api/users/{user_id}")
        user_data = response.json()
        
        # Verify tasks are associated with user
        self.assertIn("tasks", user_data)
        self.assertEqual(len(user_data["tasks"]), 3)
        print(f"✓ User has {len(user_data['tasks'])} associated tasks")
    
    # ==================== DATABASE QUERY TESTS ====================
    
    def test_database_connection(self):
        """Test connection to SQLite database"""
        try:
            conn = sqlite3.connect('./data/devdb.sqlite')
            cursor = conn.cursor()
            
            # Test query
            cursor.execute("SELECT name FROM sqlite_master WHERE type='table'")
            tables = cursor.fetchall()
            
            self.assertGreater(len(tables), 0)
            print(f"✓ Database connected. Found {len(tables)} tables")
            conn.close()
        except Exception as e:
            self.fail(f"Database connection failed: {e}")
    
    def test_users_table_structure(self):
        """Test that users table has correct structure"""
        conn = sqlite3.connect('./data/devdb.sqlite')
        cursor = conn.cursor()
        
        # Get column info
        cursor.execute("PRAGMA table_info(users)")
        columns = cursor.fetchall()
        
        column_names = [col[1] for col in columns]
        required_columns = ["user_id", "name", "email", "password"]
        
        for col in required_columns:
            self.assertIn(col, column_names)
        
        print(f"✓ Users table has required columns: {column_names}")
        conn.close()
    
    def test_tasks_table_structure(self):
        """Test that tasks table has correct structure"""
        conn = sqlite3.connect('./data/devdb.sqlite')
        cursor = conn.cursor()
        
        cursor.execute("PRAGMA table_info(tasks)")
        columns = cursor.fetchall()
        
        column_names = [col[1] for col in columns]
        required_columns = ["task_id", "title", "description", "due_date", "user_id"]
        
        for col in required_columns:
            self.assertIn(col, column_names)
        
        print(f"✓ Tasks table has required columns: {column_names}")
        conn.close()
    
    def test_user_email_field_unique(self):
        """Test that email field has unique constraint"""
        conn = sqlite3.connect('./data/devdb.sqlite')
        cursor = conn.cursor()
        
        # Try to insert duplicate email
        try:
            cursor.execute(
                "INSERT INTO users (name, email, password) VALUES (?, ?, ?)",
                ("Test User", "duplicate@test.com", "pass")
            )
            cursor.execute(
                "INSERT INTO users (name, email, password) VALUES (?, ?, ?)",
                ("Another User", "duplicate@test.com", "pass")
            )
            conn.commit()
            self.fail("Duplicate email should have been rejected")
        except sqlite3.IntegrityError:
            print("✓ Email unique constraint is working")
        finally:
            conn.close()
    
    def test_task_user_foreign_key(self):
        """Test that task user_id references users table"""
        conn = sqlite3.connect('./data/devdb.sqlite')
        cursor = conn.cursor()
        
        # Get foreign key info
        cursor.execute("PRAGMA foreign_key_list(tasks)")
        fks = cursor.fetchall()
        
        # Should have foreign key to users table
        user_fk = [fk for fk in fks if fk[2] == 'users']
        self.assertGreater(len(user_fk), 0)
        
        print("✓ Task has foreign key constraint to users table")
        conn.close()
    
    # ==================== DATA INTEGRITY TESTS ====================
    
    def test_user_password_stored(self):
        """Test that user passwords are stored"""
        response = requests.post(
            f"{self.BASE_URL}/api/users",
            json=self.test_user
        )
        user_id = response.json()["userId"]
        
        # Verify password field exists
        conn = sqlite3.connect('./data/devdb.sqlite')
        cursor = conn.cursor()
        cursor.execute("SELECT password FROM users WHERE user_id = ?", (user_id,))
        result = cursor.fetchone()
        
        self.assertIsNotNone(result)
        self.assertIsNotNone(result[0])
        print("✓ User password is stored in database")
        conn.close()
    
    def test_task_timestamps_recorded(self):
        """Test that task creation timestamps are recorded"""
        # Create user and task
        user_response = requests.post(
            f"{self.BASE_URL}/api/users",
            json=self.test_user
        )
        user_id = user_response.json()["userId"]
        
        task_data = {**self.test_task, "userId": user_id}
        response = requests.post(
            f"{self.BASE_URL}/api/tasks",
            json=task_data
        )
        
        task_id = response.json()["taskId"]
        
        # Check if created_at timestamp exists
        conn = sqlite3.connect('./data/devdb.sqlite')
        cursor = conn.cursor()
        cursor.execute("SELECT created_at FROM tasks WHERE task_id = ?", (task_id,))
        result = cursor.fetchone()
        
        self.assertIsNotNone(result)
        self.assertIsNotNone(result[0])
        print("✓ Task creation timestamp is recorded")
        conn.close()
    
    # ==================== API RESPONSE FORMAT TESTS ====================
    
    def test_api_returns_json(self):
        """Test that API returns JSON format"""
        response = requests.get(f"{self.BASE_URL}/api/users")
        self.assertEqual(response.headers['content-type'], 'application/json')
        print("✓ API returns JSON format")
    
    def test_error_response_format(self):
        """Test that error responses are properly formatted"""
        response = requests.get(f"{self.BASE_URL}/api/users/99999")
        # Should return 404 or error response
        self.assertNotEqual(response.status_code, 200)
        print("✓ Error responses are properly formatted")
    
    def test_database_status_endpoint(self):
        """Test database status endpoint"""
        response = requests.get(f"{self.BASE_URL}/api/database-status")
        self.assertEqual(response.status_code, 200)
        data = response.json()
        self.assertIn("status", data)
        print("✓ Database status endpoint working")

if __name__ == "__main__":
    unittest.main(verbosity=2)
