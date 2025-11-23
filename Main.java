import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        System.out.println("Iniciando Monitor de Red WiFi...");
        System.out.println("Presiona Ctrl+C para detener.");

        HashSet<String> previousDevices = new HashSet<>();
        boolean firstRun = true;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        while (true) {
            try {
                System.out.println("\nEscaneando red...");
                HashMap<String, String> currentDevicesMap = WifiScanner.getConnectedDevices();
                Set<String> currentIPs = currentDevicesMap.keySet();

                if (firstRun) {
                    System.out.println("Escaneo inicial completado. Dispositivos encontrados: " + currentIPs.size());
                    for (String ip : currentIPs) {
                        System.out.println(" - " + ip + " [" + currentDevicesMap.get(ip) + "]");
                    }
                    previousDevices.addAll(currentIPs);
                    firstRun = false;
                } else {
                    int newDevicesCount = 0;
                    for (String ip : currentIPs) {
                        if (!previousDevices.contains(ip)) {
                            newDevicesCount++;
                        }
                    }

                    if (newDevicesCount > 5) {
                        System.out.println("⚠️ Se han detectado " + newDevicesCount + " dispositivos nuevos de golpe.");
                        System.out.println("Probablemente sea un re-escaneo masivo. Se actualiza la lista sin enviar alertas.");
                        previousDevices.addAll(currentIPs);
                    } else {
                        for (String ip : currentIPs) {
                            if (!previousDevices.contains(ip)) {
                                String mac = currentDevicesMap.get(ip);
                                String vendor = MacVendorLookup.getVendor(mac);
                                String time = dtf.format(LocalDateTime.now());
                                
                                String msg = "⚠️ Nuevo dispositivo conectado:\n" +
                                             "🌐 IP: " + ip + "\n" +
                                             "🆔 MAC: " + mac + "\n" +
                                             "🏭 Fabricante: " + vendor + "\n" +
                                             "🕒 Hora: " + time;
                                
                                System.out.println(msg);
                                Notifier.sendDiscord(msg);
                                
                                previousDevices.add(ip);
                                Thread.sleep(1000);
                            }
                        }
                    }

                    if (currentIPs.size() > 0) {
                        HashSet<String> disconnectedDevices = new HashSet<>(previousDevices);
                        disconnectedDevices.removeAll(currentIPs);

                        for (String ip : disconnectedDevices) {
                            System.out.println("🔻 Dispositivo desconectado: " + ip);
                            previousDevices.remove(ip); 
                        }
                    }
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
