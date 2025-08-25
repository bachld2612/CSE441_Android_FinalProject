# ==================== STAGE 1: BUILD PHASE ====================
# Sử dụng một image chứa JDK và Maven để build dự án
FROM maven:3.8.5-openjdk-17 AS build

# Đặt thư mục làm việc bên trong container
WORKDIR /app

# Copy file pom.xml và tải các dependencies trước.
# Điều này giúp tận dụng Docker cache: nếu pom.xml không thay đổi, bước này sẽ không chạy lại.
COPY pom.xml .
RUN mvn dependency:go-offline -B # -B là chế độ batch mode, không hỏi tương tác

# Copy toàn bộ mã nguồn của bạn vào container
COPY src ./src

# Build dự án Spring Boot thành một file JAR thực thi được
# '-DskipTests' để bỏ qua chạy unit tests trong quá trình build Docker (để nhanh hơn)
# Bạn có thể bỏ '-DskipTests' nếu muốn chạy tests trong quá trình build image
RUN mvn package -DskipTests

# ==================== STAGE 2: RUN PHASE ====================
# Sử dụng một image JRE (Java Runtime Environment) nhẹ hơn để chạy ứng dụng
# 'eclipse-temurin:17-jre-jammy' là một lựa chọn tốt, nhỏ gọn và an toàn
FROM eclipse-temurin:17-jre-jammy

# Đặt thư mục làm việc bên trong container chạy ứng dụng
WORKDIR /app

# Copy file JAR đã được build từ giai đoạn 'build' trước đó sang giai đoạn 'run' này
# Cần thay thế 'your-artifact-name-0.0.1-SNAPSHOT.jar' bằng tên file JAR thực tế của bạn
# Tên này thường nằm trong thư mục 'target' sau khi chạy 'mvn package'
# Ví dụ: nếu dự án của bạn tên là 'my-spring-project', file có thể là 'my-spring-project-0.0.1-SNAPSHOT.jar'
COPY --from=build /app/target/Backend-0.0.1-SNAPSHOT.jar app.jar

# Thông báo cho Docker rằng container sẽ lắng nghe trên cổng 8080.
# Điều này chỉ để thông tin, Railway sẽ điều khiển cổng thực tế thông qua biến môi trường PORT.
EXPOSE 8080

# Thiết lập biến môi trường TZ cho múi giờ để đảm bảo đúng thời gian trong log và ứng dụng
ENV TZ=Asia/Ho_Chi_Minh

# Lệnh mặc định để khởi chạy ứng dụng Spring Boot
# Ứng dụng sẽ đọc các biến môi trường (DB_URL, CLOUDINARY_API_KEY, v.v.)
# mà bạn sẽ cung cấp cho container khi nó được khởi chạy trên Railway.
ENTRYPOINT ["java", "-jar", "app.jar"]