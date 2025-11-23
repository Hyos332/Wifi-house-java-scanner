import React, { useEffect, useState } from 'react';
import { Wifi, Clock, Shield, Activity, Smartphone, Laptop, Monitor, HelpCircle, RefreshCw } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

function cn(...inputs) {
  return twMerge(clsx(inputs));
}

// Utility to pick an icon based on vendor
const getDeviceIcon = (vendor) => {
  const v = (vendor || "").toLowerCase();
  if (v.includes("apple")) return <Smartphone className="w-6 h-6" />;
  if (v.includes("samsung") || v.includes("xiaomi")) return <Smartphone className="w-6 h-6" />;
  if (v.includes("intel") || v.includes("msi") || v.includes("dell")) return <Laptop className="w-6 h-6" />;
  if (v.includes("sony") || v.includes("lg")) return <Monitor className="w-6 h-6" />;
  return <HelpCircle className="w-6 h-6" />;
};

function App() {
  const [devices, setDevices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [lastUpdated, setLastUpdated] = useState(new Date());

  const fetchDevices = async () => {
    try {
      // In development we might need a proxy, but for now we assume relative path works if served by Java
      // Or we can mock it for dev
      const res = await fetch('/api/devices');
      if (!res.ok) throw new Error("Failed to fetch");
      const data = await res.json();

      // Sort: Online first, then IP
      const sorted = data.sort((a, b) => {
        if (a.isOnline !== b.isOnline) return b.isOnline - a.isOnline;
        return ipToNum(a.ip) - ipToNum(b.ip);
      });

      setDevices(sorted);
      setLastUpdated(new Date());
    } catch (e) {
      console.error(e);
      // Mock data for dev if fetch fails (e.g. running standalone vite)
      if (import.meta.env.DEV && devices.length === 0) {
        setDevices([
          { ip: "192.168.1.1", mac: "00:11:22:33:44:55", vendor: "Cisco", lastSeen: "2025/11/23 12:00:00", isOnline: true },
          { ip: "192.168.1.105", mac: "AA:BB:CC:DD:EE:FF", vendor: "Apple, Inc.", lastSeen: "2025/11/23 12:05:00", isOnline: true },
          { ip: "192.168.1.200", mac: "11:22:33:44:55:66", vendor: "Unknown", lastSeen: "2025/11/23 11:00:00", isOnline: false },
        ]);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDevices();
    const interval = setInterval(fetchDevices, 5000);
    return () => clearInterval(interval);
  }, []);

  const onlineCount = devices.filter(d => d.isOnline).length;

  return (
    <div className="min-h-screen bg-background text-slate-100 p-6 md:p-12 font-sans selection:bg-primary/30">
      <div className="max-w-7xl mx-auto space-y-12">

        {/* Header */}
        <header className="flex flex-col md:flex-row justify-between items-center gap-6 border-b border-slate-800 pb-8">
          <div className="flex items-center gap-4">
            <div className="p-3 bg-primary/10 rounded-2xl ring-1 ring-primary/20">
              <Wifi className="w-8 h-8 text-primary" />
            </div>
            <div>
              <h1 className="text-3xl font-bold bg-gradient-to-r from-blue-400 to-indigo-400 bg-clip-text text-transparent">
                WiFi Sentinel
              </h1>
              <p className="text-slate-400 text-sm mt-1">Monitor de Red en Tiempo Real</p>
            </div>
          </div>

          <div className="flex items-center gap-6 bg-surface/50 p-2 rounded-2xl border border-slate-800/50 backdrop-blur-sm">
            <StatBadge icon={<Activity />} label="Online" value={onlineCount} color="text-emerald-400" />
            <div className="w-px h-8 bg-slate-800" />
            <StatBadge icon={<Shield />} label="Total" value={devices.length} color="text-blue-400" />
            <div className="w-px h-8 bg-slate-800" />
            <div className="px-4 flex flex-col items-end">
              <span className="text-xs text-slate-500 font-medium uppercase tracking-wider">Última act.</span>
              <span className="text-xs text-slate-300 font-mono">{lastUpdated.toLocaleTimeString()}</span>
            </div>
          </div>
        </header>

        {/* Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          <AnimatePresence>
            {devices.map((device) => (
              <DeviceCard key={device.ip} device={device} />
            ))}
          </AnimatePresence>
        </div>

        {devices.length === 0 && !loading && (
          <div className="text-center py-20 text-slate-500">
            <RefreshCw className="w-12 h-12 mx-auto mb-4 opacity-20 animate-spin" />
            <p>Esperando datos del escáner...</p>
          </div>
        )}
      </div>
    </div>
  );
}

function StatBadge({ icon, label, value, color }) {
  return (
    <div className="flex items-center gap-3 px-4">
      <div className={cn("p-2 rounded-lg bg-slate-800/50", color)}>
        {React.cloneElement(icon, { size: 18 })}
      </div>
      <div>
        <div className="text-2xl font-bold leading-none">{value}</div>
        <div className="text-xs text-slate-500 font-medium uppercase tracking-wider mt-1">{label}</div>
      </div>
    </div>
  );
}

function DeviceCard({ device }) {
  const isOnline = device.isOnline;

  return (
    <motion.div
      layout
      initial={{ opacity: 0, scale: 0.95 }}
      animate={{ opacity: 1, scale: 1 }}
      exit={{ opacity: 0, scale: 0.95 }}
      className={cn(
        "relative group overflow-hidden rounded-2xl border p-5 transition-all duration-300",
        isOnline
          ? "bg-surface border-slate-700/50 hover:border-primary/50 hover:shadow-lg hover:shadow-primary/5"
          : "bg-surface/30 border-slate-800/50 opacity-60 hover:opacity-100"
      )}
    >
      {/* Status Indicator */}
      <div className="absolute top-4 right-4 flex items-center gap-2">
        <span className={cn(
          "flex h-2.5 w-2.5 rounded-full",
          isOnline ? "bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.5)]" : "bg-rose-500"
        )} />
      </div>

      <div className="flex items-start gap-4 mb-4">
        <div className={cn(
          "p-3 rounded-xl",
          isOnline ? "bg-blue-500/10 text-blue-400" : "bg-slate-700/30 text-slate-500"
        )}>
          {getDeviceIcon(device.vendor)}
        </div>
        <div>
          <h3 className="font-mono text-lg font-semibold tracking-tight text-slate-200">
            {device.ip}
          </h3>
          <p className="text-sm text-slate-400 font-medium truncate max-w-[140px]" title={device.vendor}>
            {device.vendor || "Desconocido"}
          </p>
        </div>
      </div>

      <div className="space-y-2 pt-4 border-t border-slate-800/50">
        <div className="flex justify-between text-xs">
          <span className="text-slate-500">MAC Address</span>
          <span className="font-mono text-slate-400">{device.mac}</span>
        </div>
        <div className="flex justify-between text-xs">
          <span className="text-slate-500">Visto por última vez</span>
          <span className="font-mono text-slate-400">
            {device.lastSeen.split(' ')[1]}
          </span>
        </div>
      </div>

      {/* Hover Glow Effect */}
      <div className="absolute inset-0 rounded-2xl bg-gradient-to-tr from-white/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none" />
    </motion.div>
  );
}

function ipToNum(ip) {
  return ip.split('.').reduce((acc, octet) => (acc << 8) + parseInt(octet, 10), 0) >>> 0;
}

export default App;
