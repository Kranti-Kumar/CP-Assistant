# Competitive Programming Assistant

A Java-based tool to help competitive programmers track their progress, analyze performance, and get personalized recommendations.

## Features

### Problem Tracking

- Track solved problems from multiple platforms (Codeforces, LeetCode)
- Organize problems by topics and difficulty levels
- Store problems in separate files for different users
- Prevent duplicate problem entries
- Manual problem entry support

### User Data Integration

- Fetch user profiles from Codeforces
- Fetch user profiles from LeetCode
- Import solved problems automatically
- Store user data in separate files
- Track user statistics and progress

### Performance Analysis

- Topic-wise problem distribution
- Weak topic identification
- Progress tracking over time
- Platform-specific statistics
- User ranking and rating tracking

### Recommendation System

- Topic-based recommendations
- Problem difficulty suggestions
- Learning path recommendations
- Weak topic focus
- Platform-specific problem suggestions

### Data Management

- Separate files for different users
- Local problem tracking
- CSV export functionality
- Automatic data organization
- Duplicate prevention

## Project Structure

```
src/main/java/com/cpassistant/
├── api/
│   ├── CodeforcesAPI.java    # Codeforces API integration
│   └── LeetCodeAPI.java      # LeetCode API integration
├── logic/
│   ├── ProblemTracker.java   # Problem management
│   ├── TopicGraph.java       # Topic dependencies
│   ├── UserStats.java        # User statistics
│   └── Recommender.java      # Recommendation system
├── model/
│   ├── Problem.java          # Problem data model
│   └── User.java            # User data model
├── utils/
│   └── CSVExporter.java     # Data export utility
└── Main.java                # Application entry point

data/
├── local_problems.txt       # Manually added problems
├── [username]_problems.txt  # Codeforces user problems
└── LC_[username]_problems.txt # LeetCode user problems
```

## Usage

1. **Add Solved Problems**

   - Manually add problems you've solved
   - Specify problem name, topic, and platform
   - Problems are stored in local_problems.txt

2. **View Problem History**

   - View all solved problems
   - See topic-wise distribution
   - Check weak and strong topics
   - View problems by platform

3. **Get Recommendations**

   - Receive topic recommendations
   - Get problem suggestions
   - Focus on weak topics
   - Platform-specific recommendations

4. **Import User Data**

   - Fetch Codeforces user data (Option 6)
   - Fetch LeetCode user data (Option 7)
   - Automatic problem import
   - User statistics tracking

5. **Export Data**
   - Export progress to CSV
   - Analyze performance
   - Track progress over time

## Setup

1. Clone the repository
2. Ensure you have Java 11 or higher installed
3. Build the project using Maven:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   java -jar target/cpassistant-1.0-SNAPSHOT.jar
   ```

## Dependencies

- Java 11+
- Maven
- JSON Library (for API responses)
- HTTP Client (for API requests)

## API Integration

### Codeforces API

- User profile information
- Solved problems
- Problem tags and difficulty
- Submission history

### LeetCode API

- User profile information
- Solved problems
- Problem tags
- Submission history

## Data Storage

- Problems are stored in separate files for:
  - Local problems (local_problems.txt)
  - Codeforces users ([username]\_problems.txt)
  - LeetCode users (LC\_[username]\_problems.txt)
- Each file contains:
  - Problem name
  - Topic
  - Platform
  - Date solved

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
