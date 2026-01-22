package neo.rpgsystemlol.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import neo.rpgsystem.abilities.NeoSkills;
import neo.rpgsystem.abilities.beam.AbilitiesBeamManager;
import neo.rpgsystem.commands.RPGCommand;
import neo.rpgsystem.commands.SkillCommand;
import neo.rpgsystem.integration.mmocore.MMOCoreIntegration;
import neo.rpgsystem.listeners.SkillListener;
import neo.rpgsystem.listeners.DamageListener;

public class Main extends JavaPlugin {
	
	public static final String ABILITIES_PREFIX = "§e§l[ §c§lKỹ Năng §e§l]§r ";
	public static final String PREFIX = "§8[§6RPGSystem§8] §r";
	
	public static Main pl;
	
	@Override
	public void onEnable() {
		pl = this;
		
		// Lưu config mặc định
		saveDefaultConfig();
		
		// Khởi tạo NeoSkills
		NeoSkills.setup();
		
		// Khởi tạo AbilitiesBeamManager
		AbilitiesBeamManager.setup();
		
		// Đăng ký commands
		registerCommands();
		
		// Đăng ký listeners
		registerListeners();
		
		// Khởi động MMOCore integration sau 1 tick
		new BukkitRunnable() {
			@Override
			public void run() {
				MMOCoreIntegration.setup();
			}
		}.runTaskLater(this, 1);
		
		getLogger().info("§aRPGSystemLOL has been enabled!");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("§cRPGSystemLOL has been disabled!");
	}
	
	private void registerCommands() {
		getCommand("rpg").setExecutor(new RPGCommand());
		getCommand("skill").setExecutor(new SkillCommand());
	}
	
	private void registerListeners() {
		Bukkit.getPluginManager().registerEvents(new SkillListener(), this);
		Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
	}
}
