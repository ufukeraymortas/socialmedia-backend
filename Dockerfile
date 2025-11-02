# 1. Java 17 ile başlayıp projeyi derle
FROM maven:3-openjdk-17 AS builder
ENV JAVA_OPTS="-Xmx512m -Xms256m"
# Proje dosyalarını Docker içine kopyala
COPY . /app
WORKDIR /app

# Projeyi derle (TESTLERİ ÇALIŞTIRMAMAK için -DskipTests ekledik)
RUN mvn clean install -DskipTests

# 2. Daha küçük, sadece çalıştırma ortamını içeren yeni bir katman başlat
FROM eclipse-temurin:17-jre-alpine

# Derlenmiş JAR dosyasını ilk katmandan buraya taşı
COPY --from=builder /app/target/*.jar app.jar

# Portumuzu 8080 olarak belirle
EXPOSE 8080

# Uygulamayı başlat
ENTRYPOINT ["java", "-jar", "/app.jar"]