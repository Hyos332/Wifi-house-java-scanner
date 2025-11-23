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
✅ **¡NUEVO! Panel Web Gráfico**: Visualiza todos los dispositivos conectados en una interfaz moderna construida con React.

---

## 🖥️ Panel Web (Dashboard)

El proyecto incluye una interfaz web moderna para ver el estado de tu red.

- **URL**: `http://localhost` (Puerto 80)
- **Características**:
  - Lista de dispositivos en tiempo real.
  - Indicadores de estado (Online/Offline).
  - Identificación visual de fabricantes.
  - Modo oscuro por defecto.

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Propósito |
|------------|-----------|
| **Java 17** | Backend: Lógica de escaneo y API |
| **React + Vite** | Frontend: Interfaz gráfica moderna |
| **TailwindCSS** | Estilos del frontend |
| **Nginx** | Servidor web para el frontend |
| **Docker Compose** | Orquestación de microservicios (Backend + Frontend) |
| **Discord Webhooks** | Notificaciones |

---

## ⚙️ Arquitectura

El sistema se divide en dos microservicios:

### 1. Backend (Java)
- Escanea la red usando `ping` y `arp`.
- Identifica fabricantes con la API de macvendors.com.
- Envía alertas a Discord.
- Expone una API REST en el puerto `8080` (`/api/devices`).

### 2. Frontend (React)
- Consume la API del backend.
- Muestra los dispositivos en una interfaz amigable.
- Servido por Nginx en el puerto `80`.

---

## 🚀 Cómo Ejecutar el Proyecto

### Requisitos Previos
- Docker y Docker Compose instalados
- Acceso a la red local (los contenedores usan `network_mode: host`)

### Pasos

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

3. **Levantar los servicios:**
   ```bash
   sudo docker-compose up -d --build
   ```

4. **Acceder al Dashboard:**
   - Abre tu navegador y ve a: `http://localhost:3000`

5. **Ver los logs:**
   ```bash
   sudo docker-compose logs -f
   ```

6. **Detener todo:**
   ```bash
   sudo docker-compose down
   ```

