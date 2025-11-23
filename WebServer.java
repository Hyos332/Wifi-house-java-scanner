import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.nio.file.Files;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.stream.Collectors;

public class WebServer {

    private static final int PORT = 8080;

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Endpoint for the UI
        server.createContext("/", new StaticHandler());

        // Endpoint for data
        server.createContext("/api/devices", new ApiHandler());

        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("🌍 Web Server iniciado en http://localhost:" + PORT);
    }

    static class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String root = "web";
            String path = t.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }

            File file = new File(root + path).getCanonicalFile();

            if (!file.isFile()) {
                String response = "404 (Not Found)\n";
                t.sendResponseHeaders(404, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                t.sendResponseHeaders(200, file.length());
                OutputStream os = t.getResponseBody();
                Files.copy(file.toPath(), os);
                os.close();
            }
        }
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
