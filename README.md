# 📡 WiFi House Scanner

> Un vigilante de red inteligente que detecta quién entra y sale de tu WiFi en tiempo real.

---

## 💡 ¿Por qué creé esto?

Siempre me pregunté: *"¿Quién está conectado a mi WiFi ahora mismo?"*. Podía revisar el panel del router, pero quería algo **automático** que me avisara al instante cuando alguien se conectara o desconectara, especialmente para saber cuándo llegan familiares a casa o detectar dispositivos desconocidos.

Así nació este proyecto: un **escáner de red activo** que monitorea constantemente mi WiFi doméstica y me envía alertas a Discord con información detallada de cada dispositivo.

---

## 🎯 ¿Qué hace este programa?

Este sistema escanea tu red local cada 5 segundos y:

✅ **Detecta nuevos dispositivos** que se conectan al WiFi  
✅ **Identifica el fabricante** del dispositivo (Apple, Samsung, Intel, etc.) mediante su dirección MAC  
✅ **Envía alertas a Discord** con IP, MAC, fabricante y hora exacta de conexión  
✅ **Detecta desconexiones** y te avisa si un dispositivo vuelve a conectarse (ideal para saber cuándo alguien llega a casa)  
✅ **Corre 24/7 en Docker** sin necesidad de tener una terminal abierta  
✅ **¡NUEVO! Panel Web Gráfico**: Visualiza todos los dispositivos conectados en una interfaz moderna.

---

## �️ Panel Web (Dashboard)

El proyecto ahora incluye una interfaz web para ver el estado de tu red de forma visual.

- **URL**: `http://localhost:8080`
- **Características**:
  - Lista de dispositivos en tiempo real.
  - Indicadores de estado (Online/Offline).
  - Identificación visual de fabricantes.
  - Modo oscuro por defecto.

---

## �🛠️ Tecnologías Utilizadas

| Tecnología | Propósito |
|------------|-----------|
| **Java 17** | Lenguaje principal del proyecto |
| **Docker** | Contenedorización para ejecución persistente |
| **Docker Compose** | Orquestación del contenedor |
| **Eclipse Temurin JDK** | Imagen base de Java para Docker |
| **Discord Webhooks API** | Sistema de notificaciones en tiempo real |
| **macvendors.com API** | Identificación de fabricantes por MAC |
| **Linux net-tools** | Comandos `arp` y `ping` para escaneo de red |
| **HTML/CSS/JS** | Interfaz gráfica web (Dashboard) |
| **Java HttpServer** | Servidor web ligero integrado |

---

## ⚙️ ¿Cómo funciona por debajo?

### Arquitectura del Sistema

El programa está dividido en **5 componentes principales**:

#### 1. **WifiScanner.java** - El Explorador 🔍
- Realiza un **Ping Sweep** activo: envía paquetes ICMP a todas las IPs de la subred (192.168.1.1 - 192.168.1.254).
- Lee la **tabla ARP** del sistema operativo para obtener las direcciones MAC de los dispositivos que respondieron.
- **Filtra entradas fantasma**: ignora entradas ARP incompletas o sin dirección MAC válida.
- Devuelve un `HashMap<IP, MAC>` con los dispositivos realmente conectados.

#### 2. **MacVendorLookup.java** - El Detective 🕵️
- Consulta la API pública de **macvendors.com** para identificar el fabricante del dispositivo.
- Usa la dirección MAC (los primeros 6 caracteres, llamados OUI) para determinar si es Apple, Samsung, Intel, etc.
- Tiene un timeout de 2 segundos para no bloquear el programa si la API falla.

#### 3. **Notifier.java** - El Mensajero 📨
- Envía mensajes a Discord mediante **Webhooks**.
- Lee la URL del Webhook desde una variable de entorno (`DISCORD_WEBHOOK_URL`) para mantener la seguridad.
- Formatea el mensaje en JSON y lo envía mediante una petición HTTP POST.

#### 4. **WebServer.java** - El Servidor Web �
- Levanta un servidor HTTP ligero en el puerto 8080.
- Sirve la interfaz gráfica (`index.html`) y una API JSON (`/api/devices`).
- Permite consultar el estado de la red desde cualquier navegador.

#### 5. **Main.java** - El Orquestador 🎼
- **Bucle infinito** que escanea la red cada 5 segundos.
- Mantiene el estado de los dispositivos (`knownDevices`) para el servidor web.
- Compara el escaneo actual con el anterior para detectar nuevas conexiones y desconexiones.

---

## 🚀 Cómo Ejecutar el Proyecto

### Opción 1: Con Docker (Recomendado para 24/7)

#### Requisitos Previos
- Docker y Docker Compose instalados
- Acceso a la red local (el contenedor usa `network_mode: host`)

#### Pasos

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/Hyos332/Wifi-house-java-scanner.git
   cd Wifi-house-java-scanner
   ```

2. **Configurar el Webhook de Discord:**
   - Crea un archivo `.env` en la raíz del proyecto:
     ```bash
     nano .env
     ```
   - Añade tu Webhook URL:
     ```env
     DISCORD_WEBHOOK_URL=https://discord.com/api/webhooks/TU_WEBHOOK_AQUI
     ```

3. **Levantar el contenedor:**
   ```bash
   sudo docker-compose up -d --build
   ```

4. **Acceder al Dashboard:**
   - Abre tu navegador y ve a: `http://localhost:8080` (o la IP de tu servidor si lo corres en remoto).

5. **Ver los logs en tiempo real:**
   ```bash
   sudo docker-compose logs -f
   ```

6. **Detener el vigilante:**
   ```bash
   sudo docker-compose down
   ```

---

### Opción 2: Ejecución Manual (Para pruebas)

#### Requisitos Previos
- JDK 17 o superior
- `net-tools` instalado (`arp` y `ping`)

#### Pasos

1. **Compilar:**
   ```bash
   javac *.java
   ```

2. **Configurar Webhook:**
   - Exporta la variable de entorno:
     ```bash
     export DISCORD_WEBHOOK_URL="https://discord.com/api/webhooks/TU_WEBHOOK_AQUI"
     ```

3. **Ejecutar:**
   ```bash
   java Main
   ```

4. **Acceder al Dashboard:**
   - Abre `http://localhost:8080` en tu navegador.

---

## 🔔 Ejemplo de Alerta

Cuando un dispositivo se conecta, recibirás esto en Discord:

```
⚠️ Nuevo dispositivo conectado:
🌐 IP: 192.168.1.178
🆔 MAC: 3e:3f:48:9a:08:e1
🏭 Fabricante: Apple, Inc.
🕒 Hora: 2025/11/23 01:24:31
```

---

## 🔐 Seguridad

- El archivo `.env` está en `.gitignore` para **no exponer tu Webhook** en GitHub.
- El programa **solo lee** la red, no modifica ni bloquea dispositivos.
- Usa `network_mode: host` en Docker para acceder a la red física del host.

---

