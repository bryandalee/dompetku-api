# ---------- Tahap 1: build file .jar ----------
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Salin file Maven dulu supaya download dependency bisa di-cache
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

# Baru salin kode, lalu build tanpa menjalankan test
COPY src src
RUN ./mvnw -B package -DskipTests

# ---------- Tahap 2: image akhir yang kecil, hanya berisi JRE ----------
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-Duser.timezone=Asia/Jakarta", "-jar", "app.jar"]
