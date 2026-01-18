package neo.rpgsystemlol.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import neo.rpgsystem.integration.mmocore.MMOCoreIntegration;

public class Main extends JavaPlugin {
	
	public static final String ABILITIES_PREFIX = "§e§l[ §c§lKỹ Năng §e§l]§r ";
	
	public static Main pl;
	
	public void onEnable() {
		pl = this;
		
		// Khởi động MMOCore integration sau 1 tick để đảm bảo các plugin khác đã load
		new BukkitRunnable() {
			@Override
			public void run() {
				MMOCoreIntegration.setup();
			}
		}.runTaskLater(this, 1);
	}

}
