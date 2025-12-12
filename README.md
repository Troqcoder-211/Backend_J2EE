# Backend_J2EE - Ourteam (Backend)

Mô tả ngắn: backend của dự án Ourteam, xây dựng bằng Spring Boot (Java) và quản lý bằng Maven. Hỗ trợ REST API, WebSocket, tích hợp AWS S3, Cloudinary, Redis, JWT và nhiều module dịch vụ.

Yêu cầu:
- Java 17+ (hoặc phiên bản tương thích với cấu hình project)
- Maven (hoặc sử dụng `mvnw`/`mvnw.cmd` có sẵn)
- Docker & Docker Compose (nếu muốn chạy trong container)

Chạy nhanh (Docker Compose):
```powershell
docker-compose -f compose.yaml --build
docker-compose -f compose.yaml up -d
```

Chạy trực tiếp với Maven (dev, load `.env` tùy chọn):
```powershell
mvn spring-boot:run -Dspring.config.import=optional:file:.env
# Hoặc dùng wrapper trên Windows
.\mvnw.cmd spring-boot:run -Dspring.config.import=optional:file:.env
```

Build và chạy jar:
```powershell
.\mvnw.cmd clean package -DskipTests
java -jar target\<tên-jar-của-bạn>.jar
```

Biến môi trường quan trọng (ví dụ):
- Database: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- JWT: `JWT_SECRET`, `JWT_EXPIRATION_MS`
- Redis: `REDIS_HOST`, `REDIS_PORT`
- AWS S3: `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_REGION`, `AWS_S3_BUCKET`
- Cloudinary: `CLOUDINARY_URL` hoặc `CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`
- Mail (nếu dùng): `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`

Gợi ý: tạo file `.env` hoặc `.env.example` chứa các biến này và dùng `-Dspring.config.import=optional:file:.env` khi chạy.

Cấu trúc thư mục chính:
- `src/main/java/j2ee/ourteam/` — mã nguồn chính
	- `configurations/` — cấu hình (Redis, S3, WebSocket, ...)
	- `controllers/` — REST API controllers
	- `services/` — business logic
	- `entities/`, `repositories/` — JPA entities & repositories
	- `websocket/` — cấu hình + handler WebSocket
- `src/main/resources/` — cấu hình ứng dụng, `prompts/` (RAG prompt)
- `test/` — unit/integration tests

Logging:
- Cấu hình logging tại `src/main/resources/logback-spring.xml`.

Test:
```powershell
.\mvnw.cmd test
```

Docker image (nếu muốn build image độc lập):
```powershell
docker build -t backend_j2ee .
docker run -p 8080:8080 --env-file .env backend_j2ee
```

Contributing:
- Mở issue nếu có lỗi hoặc đề xuất tính năng.
- Tạo Pull Request kèm mô tả thay đổi và test (nếu có).

License:
- Xem file `LICENSE` trong repo.

Nếu bạn muốn, mình có thể:
- Thêm `.env.example` với các biến môi trường mẫu.
- Thêm hướng dẫn cài đặt DB (schema mẫu) hoặc Postman collection.


