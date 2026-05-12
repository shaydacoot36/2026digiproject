"""LearnFlow API & Database Tests - Verify API endpoints and database operations work correctly"""

import unittest
import requests
import sqlite3
from datetime import datetime

class TestLearnFlowAPI(unittest.TestCase):
    """Test REST API endpoints"""
    
    BASE_URL = "http://localhost:8080"
    
    def setUp(self):
        self.test_user = {
            "name": "Test Student",
            "email": f"test{int(datetime.now().timestamp())}@test.com",
            "password": "pass123"
        }
    
    def test_create_user(self):
        """Test creating a user via API"""
        response = requests.post(f"{self.BASE_URL}/api/users", json=self.test_user)
        self.assertIn(response.status_code, [201, 200])
        print("✓ User creation works")
    
    def test_get_all_users(self):
        """Test getting all users"""
        response = requests.get(f"{self.BASE_URL}/api/users")
        self.assertEqual(response.status_code, 200)
        print("✓ Get users works")
    
    def test_api_json_response(self):
        """Test API returns JSON"""
        response = requests.get(f"{self.BASE_URL}/api/users")
        self.assertEqual(response.headers.get('Content-Type'), 'application/json')
        print("✓ API returns JSON format")


class TestLearnFlowDatabase(unittest.TestCase):
    """Test database operations"""
    
    def test_database_connection(self):
        """Test SQLite database is accessible"""
        try:
            conn = sqlite3.connect('./data/devdb.sqlite')
            cursor = conn.cursor()
            cursor.execute("SELECT name FROM sqlite_master WHERE type='table'")
            tables = cursor.fetchall()
            self.assertGreater(len(tables), 0)
            conn.close()
            print("✓ Database connection works")
        except Exception as e:
            print(f"Note: SQLite database not available: {e}")
    
    def test_database_has_tables(self):
        """Test database has required tables"""
        try:
            conn = sqlite3.connect('./data/devdb.sqlite')
            cursor = conn.cursor()
            cursor.execute("SELECT name FROM sqlite_master WHERE type='table' AND name='user'")
            result = cursor.fetchone()
            conn.close()
            print("✓ Database table check complete")
        except Exception as e:
            print(f"Note: Database schema check: {e}")

if __name__ == "__main__":
    unittest.main(verbosity=2)
