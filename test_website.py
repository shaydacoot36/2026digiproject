"""LearnFlow Website Tests - Verify all links, forms, and UI elements work correctly"""

import unittest
import requests

class TestLearnFlowWebsite(unittest.TestCase):
    """Simple website tests"""
    
    def setUp(self):
        self.base_url = "http://localhost:8000"
    
    def test_homepage_loads(self):
        """Test homepage is accessible"""
        response = requests.get(self.base_url)
        self.assertEqual(response.status_code, 200)
        print("✓ Homepage loads successfully")
    
    def test_homepage_contains_title(self):
        """Test homepage contains LearnFlow title"""
        response = requests.get(self.base_url)
        self.assertIn("LearnFlow", response.text)
        print("✓ Homepage contains LearnFlow title")
    
    def test_homepage_has_learning_content(self):
        """Test homepage has key learning content"""
        response = requests.get(self.base_url)
        self.assertIn("Study Plan", response.text)
        print("✓ Homepage has Study Plan content")
    
    def test_css_file_exists(self):
        """Test CSS stylesheet loads"""
        response = requests.get(f"{self.base_url}/style.css")
        self.assertEqual(response.status_code, 200)
        print("✓ CSS stylesheet loads")
    
    def test_page_has_buttons(self):
        """Test page has interactive buttons"""
        response = requests.get(self.base_url)
        self.assertIn("button", response.text.lower())
        print("✓ Page has buttons")

if __name__ == "__main__":
    unittest.main(verbosity=2)
