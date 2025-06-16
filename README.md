# Competitive Programming Assistant (CP Assistant)

A Java-based console application that helps competitive programming enthusiasts track their progress and get personalized practice recommendations.

## Features

1. **Problem Tracking**

   - Log solved problems with details (name, topic, platform, date)
   - View problems by topic
   - Export progress reports to CSV

2. **Performance Analysis**

   - Track problems solved per topic
   - Identify weak areas
   - Sort topics by proficiency

3. **Smart Recommendations**
   - Topic dependency tracking
   - Personalized practice suggestions
   - Problem recommendations based on weak areas

## Project Structure

```
cp-assistant/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── model/
│   │   │   │   └── Problem.java
│   │   │   ├── logic/
│   │   │   │   ├── ProblemTracker.java
│   │   │   │   ├── TopicGraph.java
│   │   │   │   ├── UserStats.java
│   │   │   │   └── Recommender.java
│   │   │   ├── utils/
│   │   │   │   └── CSVExporter.java
│   │   │   └── Main.java
│   │   └── resources/
│   └── test/
├── data/
│   ├── problems.txt
│   └── topic_graph.txt
├── output/
│   └── progress.csv
├── pom.xml
└── README.md
```

## Requirements

- Java 11 or higher
- Maven

## Building and Running

1. Clone the repository
2. Build the project:
   ```bash
   mvn clean package
   ```
3. Run the application:
   ```bash
   java -jar target/cp-assistant-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

## Usage

1. **Add a Solved Problem**

   - Enter problem details (name, topic, platform, date)
   - System automatically updates statistics

2. **View Problems by Topic**

   - Select a topic to see all solved problems
   - View problem details and solve dates

3. **View Weak Topics**

   - See topics sorted by proficiency
   - Identify areas needing more practice

4. **Get Recommendations**

   - Receive personalized topic suggestions
   - Get recommended problems to solve

5. **Export Progress**
   - Generate CSV reports of your progress
   - Track improvement over time

## Data Storage

- Problems are stored in `data/problems.txt`
- Topic dependencies are stored in `data/topic_graph.txt`
- Progress reports are exported to `output/` directory

## Contributing

Feel free to submit issues and enhancement requests!
