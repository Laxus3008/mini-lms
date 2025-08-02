# Mini Learning Management System (LMS) API

A backend service built with Spring Boot that manages courses, modules, lessons, and tracks user progress in a learning management system.

## Key Features

- **Course Management**
  - Create and list courses
  - View course details with modules
  - Track course-level progress

- **Module Management**
  - Add modules to courses
  - View module details with lessons
  - Calculate module completion progress

- **Lesson Management**
  - Support multiple content types (TEXT, VIDEO, IMAGE, PDF)
  - File upload functionality for multimedia content
  - Secure file storage and retrieval

- **Progress Tracking**
  - Track user progress at lesson level
  - Calculate aggregated progress for modules
  - Calculate overall course completion

## Tech Stack

- Java 17+
- Spring Boot 3+
- PostgreSQL Database
- Spring Data JPA
- OpenAPI (Swagger) Documentation

## Setup Instructions

### Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher

### Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE minilms;
```

2. Configure database connection in `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/minilms
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Application Setup

1. Clone the repository:
```bash
git clone https://github.com/your-username/mini-lms.git
cd mini-lms
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### API Documentation

Access the Swagger UI documentation at:
```
http://localhost:8080/swagger-ui.html
```

## API Endpoints

### Courses
- `POST /courses` - Create a new course
- `GET /courses` - List all courses
- `GET /courses/{id}` - Get course details
- `GET /courses/{courseId}/progress?userId={userId}` - Get course progress

### Modules
- `GET /modules/{id}` - Get module details
- `GET /modules/{moduleId}/progress?userId={userId}` - Get module progress

### Lessons
- `POST /lessons/modules/{moduleId}` - Create a new lesson
- `GET /lessons/{lessonId}` - Get lesson details
- `GET /lessons/{lessonId}/content` - Get lesson content
- `POST /lessons/{lessonId}/progress?userId={userId}` - Mark lesson as complete

## File Upload Support

The system supports various content types:
- TEXT: Stored directly in the database
- VIDEO: Stored in file system (supports mp4)
- IMAGE: Stored in file system (supports jpg, jpeg, png, gif)
- PDF: Stored in file system (supports pdf)

## Assumptions and Design Decisions

1. User Authentication
   - Basic user identification using userId parameter
   - No authentication implemented (can be added as needed)

2. File Storage
   - Files are stored in local file system
   - Unique filenames are generated using timestamps
   - Upload directory is configurable

3. Progress Tracking
   - Progress is calculated based on completed lessons
   - Module progress = completed lessons / total lessons
   - Course progress = average of module progresses

## Additional Notes

- The system uses an 'uploads' directory for file storage
- Configure maximum file upload size in application.properties
- All dates and times are in UTC
- Proper error handling and validation implemented

## Future Enhancements

- User authentication and authorization
- Cloud storage integration for files
- Content validation and virus scanning
- Real-time progress updates
- Learning path customization

## Contributing

Feel free to fork the repository and submit pull requests.
