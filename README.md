# 📡 Java Network Sentinel

Un sistema de vigilancia de red ligero y eficiente escrito en Java. Escanea tu red local en busca de nuevos dispositivos conectados y te envía alertas en tiempo real a Discord, incluyendo información sobre el fabricante del dispositivo.

## 🚀 Características

- **Escaneo Activo:** Realiza un "Ping Sweep" para detectar dispositivos conectados, incluso si están inactivos.
- **Identificación de Fabricante:** Consulta una API para identificar si el dispositivo es Apple, Samsung, Xiaomi, etc., basándose en su dirección MAC.
- **Alertas en Tiempo Real:** Notificaciones instantáneas a través de Webhooks de Discord.
- **Detección de Reconexiones:** Te avisa cuando un dispositivo conocido se desconecta y vuelve a conectarse.
- **Filtrado Inteligente:** Ignora entradas ARP fantasmas o incompletas para evitar falsos positivos.
- **Anti-Spam:** Protección contra avalanchas de alertas en caso de reinicio de red.

## 🛠️ Requisitos

- Java Development Kit (JDK) 8 o superior.
- Sistema Operativo: Linux, macOS o Windows (con `ping` y `arp` disponibles).
- Conexión a Internet (para alertas de Discord y consulta de fabricantes).

## ⚙️ Configuración

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/tu-usuario/java-network-sentinel.git
   cd java-network-sentinel
   ```

2. **Configurar Variables de Entorno:**
   - Crea un archivo llamado `.env` en la raíz del proyecto.
   - Añade tu Webhook de Discord:
     ```env
     DISCORD_WEBHOOK_URL=https://discord.com/api/webhooks/TU_WEBHOOK_AQUI
     ```

3. **Ajustar Subred (Opcional):**
   - Si tu red no es `192.168.1.x`, abre `WifiScanner.java` y cambia la variable `subnet`.

## � Despliegue con Docker (Recomendado)

Para mantener el vigilante activo 24/7, puedes usar Docker.

1. **Construir y Ejecutar:**
   ```bash
   docker-compose up -d --build
   ```

2. **Ver logs:**
   ```bash
   docker-compose logs -f
   ```

3. **Detener:**
   ```bash
   docker-compose down
   ```

**Nota:** El contenedor usa `network_mode: "host"` para poder escanear la red local de tu router.

## 📦 Compilación Manual

```bash
# Compilar
javac Main.java WifiScanner.java Notifier.java MacVendorLookup.java

# Ejecutar
java Main
```

## 📝 Ejemplo de Alerta

```text
⚠️ Nuevo dispositivo conectado:
🌐 IP: 192.168.1.178
🆔 MAC: 3e:3f:48:9a:08:e1
🏭 Fabricante: Apple, Inc.
🕒 Hora: 2025/11/23 00:58:00
```

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Si tienes ideas para mejorar el escáner o añadir nuevas integraciones (Telegram, Slack, Email), ¡abre un Pull Request!

## 📄 Licencia

Este proyecto está bajo la Licencia MIT.
