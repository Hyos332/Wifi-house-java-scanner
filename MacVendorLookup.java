import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MacVendorLookup {

    private static final String API_URL = "https://api.macvendors.com/";

    public static String getVendor(String macAddress) {
        try {
            // La API espera la MAC en la URL
            URL url = new URL(API_URL + macAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            // Timeout para no bloquear si la API va lenta
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString(); // La API devuelve solo el nombre en texto plano
            } else {
                return "Desconocido";
            }
        } catch (Exception e) {
            // Si falla (sin internet, timeout, etc), devolvemos Desconocido
            return "Desconocido";
        }
    }
}
