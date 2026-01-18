package neo.rpgsystem.integration.mmocore;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import neo.rpgsystemlol.main.Main;

/**
 * Class quản lý việc tích hợp với MMOCore/MythicLib
 * - Kiểm tra MythicLib và MMOCore có được cài đặt không
 * - Đăng ký tất cả custom SkillHandler khi plugin khởi động
 */
public class MMOCoreIntegration {
    
    private static boolean mythicLibAvailable = false;
    private static boolean mmoCoreAvailable = false;
    
    /**
     * Khởi tạo và đăng ký tất cả skill handlers
     * Được gọi trong Main.onEnable() với delay 1 tick
     */
    public static void setup() {
        // Kiểm tra MythicLib
        Plugin mythicLib = Bukkit.getPluginManager().getPlugin("MythicLib");
        if (mythicLib != null && mythicLib.isEnabled()) {
            mythicLibAvailable = true;
            Bukkit.getLogger().info("[RPGSystemLOL] MythicLib detected!");
        } else {
            Bukkit.getLogger().warning("[RPGSystemLOL] MythicLib not found. MMOCore integration disabled.");
            return;
        }
        
        // Kiểm tra MMOCore
        Plugin mmoCore = Bukkit.getPluginManager().getPlugin("MMOCore");
        if (mmoCore != null && mmoCore.isEnabled()) {
            mmoCoreAvailable = true;
            Bukkit.getLogger().info("[RPGSystemLOL] MMOCore detected!");
        } else {
            Bukkit.getLogger().warning("[RPGSystemLOL] MMOCore not found. Some features may be limited.");
        }
        
        // Đăng ký skill handlers
        try {
            HumanSkillsRegistry.registerAll();
            Bukkit.getLogger().info("[RPGSystemLOL] Successfully registered Human skill handlers with MythicLib!");
        } catch (Exception e) {
            Bukkit.getLogger().severe("[RPGSystemLOL] Failed to register skill handlers: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Kiểm tra MythicLib có available không
     */
    public static boolean isMythicLibAvailable() {
        return mythicLibAvailable;
    }
    
    /**
     * Kiểm tra MMOCore có available không
     */
    public static boolean isMMOCoreAvailable() {
        return mmoCoreAvailable;
    }
}
