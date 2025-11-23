# Usamos una imagen base ligera de Java
FROM openjdk:17-jdk-slim

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
RUN javac Main.java WifiScanner.java Notifier.java MacVendorLookup.java

# Comando para ejecutar la aplicación
CMD ["java", "Main"]
