import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.stream.Collectors;

public class WebServer {

    private static final int PORT = 8080;

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Endpoint for data
        server.createContext("/api/devices", new ApiHandler());

        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("🌍 API Server iniciado en http://localhost:" + PORT);
    }

    static class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // Get devices from Main
            Collection<Device> devices = Main.getDevices();

            // Build JSON array
            String jsonResponse = "[" +
                    devices.stream()
                            .map(Device::toJson)
                            .collect(Collectors.joining(","))
                    +
                    "]";

            t.getResponseHeaders().set("Content-Type", "application/json");
            t.getResponseHeaders().set("Access-Control-Allow-Origin", "*"); // Allow CORS for dev
            t.sendResponseHeaders(200, jsonResponse.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(jsonResponse.getBytes());
            os.close();
        }
    }
}
