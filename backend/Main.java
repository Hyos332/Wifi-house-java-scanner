import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    private static final HashSet<String> IGNORED_MACS = new HashSet<>(Arrays.asList(
            "30:bd:13:5f:98:20",
            "f4:8c:50:1c:f8:4f"));

    // Thread-safe map to store devices: IP -> Device
    private static final ConcurrentHashMap<String, Device> knownDevices = new ConcurrentHashMap<>();

    public static Collection<Device> getDevices() {
        return knownDevices.values();
    }

    public static void main(String[] args) {
        System.out.println("Iniciando Monitor de Red WiFi...");

        // Start Web Server in a separate thread
        new Thread(() -> {
            try {
                new WebServer().start();
            } catch (Exception e) {
                System.err.println("Error iniciando WebServer: " + e.getMessage());
            }
        }).start();

        System.out.println("Presiona Ctrl+C para detener.");

        boolean firstRun = true;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        while (true) {
            try {
                System.out.println("\nEscaneando red...");
                HashMap<String, String> currentScan = WifiScanner.getConnectedDevices();
                Set<String> currentIPs = currentScan.keySet();
                String now = dtf.format(LocalDateTime.now());

                // Mark all known devices as offline initially for this scan cycle
                // We will mark them online if found in current scan
                // Actually, better to just update status based on presence

                // 1. Process currently found devices
                for (String ip : currentIPs) {
                    String mac = currentScan.get(ip);

                    Device device = knownDevices.get(ip);

                    if (device == null) {
                        // New Device Found
                        if (!firstRun && !IGNORED_MACS.contains(mac)) {
                            // Check for spam (simple check)
                            // For simplicity, we'll keep the spam check logic simple or omit if not
                            // strictly needed
                            // But let's keep the core logic: lookup vendor
                            String vendor = MacVendorLookup.getVendor(mac);

                            String msg = "⚠️ Nuevo dispositivo conectado:\n" +
                                    "🌐 IP: " + ip + "\n" +
                                    "🆔 MAC: " + mac + "\n" +
                                    "🏭 Fabricante: " + vendor + "\n" +
                                    "🕒 Hora: " + now;

                            System.out.println(msg);
                            Notifier.sendDiscord(msg);

                            device = new Device(ip, mac, vendor, now, true);
                        } else {
                            // First run or ignored, just add without alert
                            // We still need vendor for the UI
                            String vendor = MacVendorLookup.getVendor(mac);
                            device = new Device(ip, mac, vendor, now, true);
                            if (IGNORED_MACS.contains(mac)) {
                                System.out.println("🔇 Dispositivo conocido (ignorado/inicio): " + ip);
                            }
                        }
                        knownDevices.put(ip, device);
                    } else {
                        // Device already known, update status
                        if (!device.isOnline()) {
                            System.out.println("♻️ Dispositivo reconectado: " + ip);
                            // Optional: Send reconnection alert
                        }
                        device.setOnline(true);
                        device.setLastSeen(now);
                        // Update MAC if changed (unlikely for same IP but possible with DHCP)
                        if (!device.getMac().equals(mac)) {
                            // Handle MAC change if needed
                        }
                    }
                }

                // 2. Process disconnected devices
                for (String ip : knownDevices.keySet()) {
                    if (!currentIPs.contains(ip)) {
                        Device device = knownDevices.get(ip);
                        if (device.isOnline()) {
                            System.out.println("🔻 Dispositivo desconectado: " + ip);
                            device.setOnline(false);
                        }
                    }
                }

                if (firstRun) {
                    System.out.println("Escaneo inicial completado. Dispositivos: " + knownDevices.size());
                    firstRun = false;
                }

                Thread.sleep(5000);

            } catch (InterruptedException e) {
                System.out.println("Programa detenido.");
                break;
            } catch (Exception e) {
                System.err.println("Error inesperado: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
