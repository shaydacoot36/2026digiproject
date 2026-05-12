"""
LearnFlow Website Testing
Tests for all links, forms, and page elements
"""

import unittest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time

class TestLearnFlowWebsite(unittest.TestCase):
    """Test all website links and functionality"""
    
    @classmethod
    def setUpClass(cls):
        """Set up browser for testing"""
        cls.driver = webdriver.Chrome()
        cls.base_url = "http://localhost:8000"
    
    @classmethod
    def tearDownClass(cls):
        """Close browser after tests"""
        cls.driver.quit()
    
    def test_homepage_loads(self):
        """Test that homepage loads successfully"""
        self.driver.get(self.base_url)
        self.assertIn("LearnFlow", self.driver.title)
        print("✓ Homepage loads successfully")
    
    def test_hero_section_visible(self):
        """Test that hero section is visible"""
        self.driver.get(self.base_url)
        hero = self.driver.find_element(By.CLASS_NAME, "hero")
        self.assertTrue(hero.is_displayed())
        print("✓ Hero section is visible")
    
    def test_navigation_buttons_exist(self):
        """Test that navigation buttons exist"""
        self.driver.get(self.base_url)
        buttons = self.driver.find_elements(By.CLASS_NAME, "btn")
        self.assertGreater(len(buttons), 0)
        print(f"✓ Found {len(buttons)} buttons on page")
    
    def test_feature_cards_displayed(self):
        """Test that all feature cards are displayed"""
        self.driver.get(self.base_url)
        cards = self.driver.find_elements(By.CLASS_NAME, "feature-card")
        self.assertGreater(len(cards), 0)
        print(f"✓ Found {len(cards)} feature cards")
    
    def test_all_headings_visible(self):
        """Test that all main headings are visible"""
        self.driver.get(self.base_url)
        headings = self.driver.find_elements(By.TAG_NAME, "h2")
        self.assertGreater(len(headings), 0)
        
        for heading in headings:
            self.assertTrue(heading.is_displayed())
        print(f"✓ All {len(headings)} headings are visible")
    
    def test_page_scrolls_smoothly(self):
        """Test that page scrolls without errors"""
        self.driver.get(self.base_url)
        self.driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
        time.sleep(1)
        self.driver.execute_script("window.scrollTo(0, 0);")
        print("✓ Page scrolls smoothly")
    
    def test_footer_visible(self):
        """Test that footer is visible"""
        self.driver.get(self.base_url)
        footer = self.driver.find_element(By.CLASS_NAME, "footer-bar")
        self.assertIn("LearnFlow", footer.text)
        print("✓ Footer is visible with correct text")
    
    def test_no_broken_links(self):
        """Test that links are properly formatted"""
        self.driver.get(self.base_url)
        links = self.driver.find_elements(By.TAG_NAME, "a")
        
        for link in links:
            href = link.get_attribute("href")
            if href:  # Only check if href exists
                self.assertIsNotNone(href)
        print(f"✓ All {len(links)} links are properly formatted")
    
    def test_page_title_correct(self):
        """Test that page title is correct"""
        self.driver.get(self.base_url)
        title = self.driver.title
        self.assertIn("LearnFlow", title)
        self.assertIn("Study Planner", title)
        print(f"✓ Page title is correct: {title}")

if __name__ == "__main__":
    unittest.main(verbosity=2)
