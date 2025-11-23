import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WifiScanner {

    public static HashMap<String, String> getConnectedDevices() {
        HashMap<String, String> devices = new HashMap<>();
        String subnet = "192.168.1."; 

        System.out.print("Escaneando red (Ping Sweep)... ");
        
        try {
            for (int i = 1; i < 255; i++) {
                String host = subnet + i;
                String pingCmd = "ping -c 1 -W 1 " + host;
                Runtime.getRuntime().exec(pingCmd);
            }
            Thread.sleep(3000); 
            System.out.println("Hecho.");
        } catch (Exception e) {
            System.err.println("Error en Ping Sweep: " + e.getMessage());
        }

        String command = "arp -a";
        
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            
            Pattern ipPattern = Pattern.compile("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b");
            Pattern macPattern = Pattern.compile("([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})");

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (line.contains("incomplete") || line.contains("no entry")) continue;

                Matcher macMatcher = macPattern.matcher(line);
                if (!macMatcher.find()) continue;
                String mac = macMatcher.group();

                Matcher ipMatcher = ipPattern.matcher(line);
                if (ipMatcher.find()) {
                    String ip = ipMatcher.group();
                    if (ip.startsWith(subnet)) {
                        devices.put(ip, mac);
                    }
                }
            }
            process.waitFor();
            
        } catch (Exception e) {
            System.err.println("Error al leer ARP: " + e.getMessage());
            e.printStackTrace();
        }
        
        return devices;
    }
}
