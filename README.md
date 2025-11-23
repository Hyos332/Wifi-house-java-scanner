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

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Propósito |
|------------|-----------|
| **Java 17** | Lenguaje principal del proyecto |
| **Docker** | Contenedorización para ejecución persistente |
| **Docker Compose** | Orquestación del contenedor |
| **Eclipse Temurin JDK** | Imagen base de Java para Docker |
| **Discord Webhooks API** | Sistema de notificaciones en tiempo real |
| **macvendors.com API** | Identificación de fabricantes por MAC |
| **Linux net-tools** | Comandos `arp` y `ping` para escaneo de red |

---

## ⚙️ ¿Cómo funciona por debajo?

### Arquitectura del Sistema

El programa está dividido en **4 componentes principales**:

#### 1. **WifiScanner.java** - El Explorador 🔍
- Realiza un **Ping Sweep** activo: envía paquetes ICMP a todas las IPs de la subred (192.168.1.1 - 192.168.1.254).
- Lee la **tabla ARP** del sistema operativo para obtener las direcciones MAC de los dispositivos que respondieron.
- **Filtra entradas fantasma**: ignora entradas ARP incompletas o sin dirección MAC válida.
- Devuelve un `HashMap<IP, MAC>` con los dispositivos realmente conectados.

#### 2. **MacVendorLookup.java** - El Detective 🕵️
- Consulta la API pública de **macvendors.com** para identificar el fabricante del dispositivo.
- Usa la dirección MAC (los primeros 6 caracteres, llamados OUI) para determinar si es Apple, Samsung, Intel, etc.
- Tiene un timeout de 2 segundos para no bloquear el programa si la API falla.

#### 3. **Notifier.java** - El Mensajero �
- Envía mensajes a Discord mediante **Webhooks**.
- Lee la URL del Webhook desde una variable de entorno (`DISCORD_WEBHOOK_URL`) para mantener la seguridad.
- Formatea el mensaje en JSON y lo envía mediante una petición HTTP POST.

#### 4. **Main.java** - El Orquestador 🎼
- **Bucle infinito** que escanea la red cada 5 segundos.
- Compara el escaneo actual con el anterior para detectar:
  - **Nuevas conexiones**: IPs que no estaban antes → Envía alerta.
  - **Desconexiones**: IPs que desaparecieron → Las borra de la memoria para poder alertar si vuelven.
- **Protección anti-spam**: Si detecta más de 5 dispositivos nuevos de golpe, asume que es un error de escaneo y no envía alertas.

### Flujo de Ejecución

```
┌─────────────────────────────────────────────────────────────┐
│  1. Ping Sweep (192.168.1.1 → 192.168.1.254)               │
│     └─> Genera tráfico ARP en la red                       │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  2. Leer tabla ARP del sistema                              │
│     └─> Filtrar entradas con MAC válida                    │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  3. Comparar con escaneo anterior                           │
│     ├─> ¿IP nueva? → Consultar fabricante → Enviar alerta  │
│     └─> ¿IP desaparecida? → Borrar de memoria              │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  4. Esperar 5 segundos y repetir                            │
└─────────────────────────────────────────────────────────────┘
```

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

4. **Ver los logs en tiempo real:**
   ```bash
   sudo docker-compose logs -f
   ```

5. **Detener el vigilante:**
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
   javac Main.java WifiScanner.java Notifier.java MacVendorLookup.java
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

---

## � Ejemplo de Alerta

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

## 🤝 Contribuciones

¡Las contribuciones son bienvenidas! Si tienes ideas para mejorar el escáner (base de datos, dashboard web, múltiples notificadores), abre un **Pull Request** o un **Issue**.

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Úsalo, modifícalo y compártelo libremente.

---

## 🙏 Agradecimientos

- **macvendors.com** por su API pública de identificación de fabricantes.
- **Discord** por su sistema de Webhooks tan sencillo y potente.
- **Eclipse Temurin** por proporcionar imágenes Docker de Java mantenidas y seguras.
