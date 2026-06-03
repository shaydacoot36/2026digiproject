# LearnFlow - Phase 1 Testing Guide

## ✅ System Status

Both servers are running and fully operational:

- **Frontend**: http://localhost:8000
- **Backend**: http://localhost:8080
- **API**: http://localhost:8080/api/plans

---

## 🧪 How to Test the Complete Flow

### Option 1: Browser Testing (Recommended)

1. **Open Frontend**
   ```
   http://localhost:8000/index.html
   ```

2. **Fill Out the Form**
   - Goal: "Master NCEA Chemistry" (or your choice)
   - Days: 90
   - Hours per Week: 15

3. **Submit**
   - Click "Create Study Plan"
   - Watch the loading spinner
   - See your personalized 90-day study plan

4. **View Results**
   - Daily sessions with dates and topics
   - Hours allocated per day (~2.14 hours for balanced distribution)
   - Full plan spanning your chosen timeframe

---

### Option 2: Command Line Testing

```bash
# Test API directly
curl -X POST http://localhost:8080/api/plans \
  -H "Content-Type: application/json" \
  -d '{
    "goal": "Master Chemistry",
    "horizonDays": 90,
    "hoursPerWeek": 15
  }'
```

**Expected Response:**
```json
{
  "id": "d90edb0d-7c36-4394-a11b-36a80d3d0f84",
  "goal": "Master Chemistry",
  "createdAt": "2026-06-03",
  "sessions": [
    {
      "day": "2026-06-03",
      "topic": "Master Chemistry — focus area 1",
      "durationHours": 2.14
    },
    ...
  ]
}
```

---

## 📊 API Endpoints Available

### Study Plans
- `POST /api/plans` - Create new study plan
- `GET /api/plans/{id}` - Retrieve specific plan

### Users (Database)
- `GET /api/users` - List all users
- `GET /api/users/{id}` - Get user details
- `POST /api/users` - Create new user

### Tasks (Database)
- `GET /api/tasks` - List all tasks
- `POST /api/tasks` - Create new task
- `PUT /api/tasks/{id}` - Update task
- `GET /api/tasks/user/{userId}` - Get tasks for user

---

## 🐛 Troubleshooting

### Frontend Not Loading?
```bash
# Check frontend server
curl -s http://localhost:8000/index.html | head -10
```

### Backend Not Responding?
```bash
# Check backend health
curl -s http://localhost:8080/actuator/health
```

### Database Locked?
```bash
# Clean up and restart
pkill -9 -f spring-boot:run
rm -rf 'spring boot project/ai-wrapper-java/data/devdb*'
cd 'spring boot project/ai-wrapper-java'
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev" &
```

---

## 📝 What's Working

✅ Form submission from frontend  
✅ API receives study plan requests  
✅ Backend generates 90-day study plans  
✅ Plans displayed with daily breakdowns  
✅ Responsive UI with loading states  
✅ Error handling for network issues  

---

## 🚀 Next Phase Options

1. **Database Persistence** - Save plans to H2 database
2. **User Authentication** - Login/signup system
3. **Real AI Integration** - Replace mock with OpenAI API
4. **Progress Tracking** - Track actual study vs. planned
5. **Plan Refinement** - Edit/adjust existing plans

---

## 📂 Project Files

- `index.html` - Frontend form + JavaScript
- `style.css` - LearnFlow branding & styling
- `spring boot project/ai-wrapper-java/` - Backend service
- `spring boot project/ai-wrapper-java/src/main/java/com/example/aiplanner/` - Plan generation logic

---

**Status**: ✅ Phase 1 Complete - Ready for Testing!
