public class Device {
    private String ip;
    private String mac;
    private String vendor;
    private String lastSeen;
    private boolean isOnline;

    public Device(String ip, String mac, String vendor, String lastSeen, boolean isOnline) {
        this.ip = ip;
        this.mac = mac;
        this.vendor = vendor;
        this.lastSeen = lastSeen;
        this.isOnline = isOnline;
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }

    public String getVendor() {
        return vendor;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    // Simple JSON serialization manually to avoid dependencies
    public String toJson() {
        return String.format(
                "{\"ip\": \"%s\", \"mac\": \"%s\", \"vendor\": \"%s\", \"lastSeen\": \"%s\", \"isOnline\": %b}",
                ip, mac, vendor != null ? vendor.replace("\"", "\\\"") : "Unknown", lastSeen, isOnline);
    }
}
