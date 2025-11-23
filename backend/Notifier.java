import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Notifier {

    // Leemos la URL desde una variable de entorno (más seguro para Docker)
    // Si no existe, usa un valor por defecto (o lanza error)
    private static final String DISCORD_WEBHOOK_URL = System.getenv("DISCORD_WEBHOOK_URL") != null 
            ? System.getenv("DISCORD_WEBHOOK_URL") 
            : "TU_WEBHOOK_URL_AQUI";

    public static void sendDiscord(String message) {
        try {
            URL url = new URL(DISCORD_WEBHOOK_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String escapedMessage = message.replace("\n", "\\n").replace("\"", "\\\"");
            String jsonPayload = "{\"content\": \"" + escapedMessage + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            
            if (responseCode == 204) {
                System.out.println("Mensaje enviado a Discord correctamente.");
            } else {
                System.err.println("Error al enviar a Discord. Código: " + responseCode);
            }
            
        } catch (Exception e) {
            System.err.println("Excepción al enviar notificación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
