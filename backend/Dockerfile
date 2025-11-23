# Usamos una imagen base ligera de Java (Eclipse Temurin es la recomendada ahora)
FROM eclipse-temurin:17-jdk-jammy

# Instalamos herramientas de red necesarias (ping y arp)
RUN apt-get update && apt-get install -y \
    net-tools \
    iputils-ping \
    && rm -rf /var/lib/apt/lists/*

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos los archivos fuente al contenedor
COPY . /app

# Compilamos el código
RUN javac *.java

# Exponemos el puerto del servidor web
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "Main"]
