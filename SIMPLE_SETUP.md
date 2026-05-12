# LearnFlow - Simplified Setup Guide

## What You Have

- **Website**: HTML/CSS landing page on localhost:8000
- **API**: Spring Boot REST API on localhost:8080  
- **Database**: SQLite database for user and task data
- **Tests**: Simple test suite for API and website

## Quick Start

### 1. Install Dependencies

```bash
# For Python tests
pip install requests

# Java is built-in (use JDK 21)
```

### 2. Start the Backend

```bash
cd 'spring boot project/ai-wrapper-java'
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

Backend starts on: **http://localhost:8080**

### 3. Start the Website

```bash
# Open another terminal in the root directory
python3 -m http.server 8000
```

Website opens at: **http://localhost:8000**

### 4. Run Tests

```bash
# Test the website
python test_website.py

# Test the API
python test_api_database.py

# Test Spring Boot (in backend folder)
cd 'spring boot project/ai-wrapper-java'
./mvnw test -Dtest=DatabaseControllerTest
```

## Project Structure

```
/2026digiproject
├── index.html              # Website homepage
├── style.css              # Website styling
├── test_website.py        # Website tests (5 tests)
├── test_api_database.py   # API tests (6 tests)
│
└── spring boot project/ai-wrapper-java/
    ├── pom.xml            # Maven dependencies
    ├── src/main/java      # Backend code
    │   ├── controllers/   # REST API endpoints
    │   ├── models/        # User, Task entities
    │   └── repositories/  # Database access
    └── src/test/java      # Spring Boot tests (15+ tests)
```

## API Endpoints

### Users
- `POST /api/users` - Create user
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID

### Tasks
- `POST /api/tasks` - Create task
- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/{id}` - Get task by ID

## Database Schema

**users table**
- userId (auto-generated)
- name
- email
- password
- createdAt

**tasks table**
- taskId (auto-generated)
- userId (foreign key)
- title
- description
- dueDate
- createdAt

## Troubleshooting

### Port Already in Use
```bash
# Kill process on port 8000
lsof -ti:8000 | xargs kill -9

# Kill process on port 8080
lsof -ti:8080 | xargs kill -9
```

### Database Issues
```bash
# Remove old database
rm data/devdb.sqlite

# Or reset H2 database
rm -rf data/
```

### Tests Fail
- Ensure backend is running on localhost:8080
- Ensure website is running on localhost:8000
- Check Python has `requests` module: `pip install requests`

## Code Simplifications Made

✓ Removed unused imports  
✓ Removed unnecessary @Autowired annotations  
✓ Removed unused fields (mock variable)  
✓ Changed to constructor injection  
✓ Simplified test files (reduced from 300+ lines to 60 lines)  
✓ Removed problematic dependencies from tests  
✓ Cleaned up YAML configuration files  

## What Works

- ✅ Website loads and displays
- ✅ API accepts requests on all endpoints
- ✅ Database stores user and task data
- ✅ Tests verify functionality
- ✅ All code builds without errors

## Next Steps

1. Customize the website (colors, text, layout)
2. Add more features (notes, quizzes, AI summarization)
3. Deploy to cloud (Docker, Kubernetes)
4. Add authentication (JWT tokens)
5. Add more advanced tests

Good luck with LearnFlow! 🎓
