package neo.rpgsystem.abilities;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import neo.rpgsystemlol.main.Main;
import neo.rpgsystemlol.main.U;

public class CooldownSkills implements NeoSkill {
	
	public static double getValue(int point, double base, double addPerLevel) {
		if(point<=0) return 0;
		return base + addPerLevel*point;
	}
	
	private String name;
	private String displayName;
	private boolean msg;
	private HashMap<String, Long> cooldownPlayers = new HashMap<String, Long>();

	public CooldownSkills(String name, String displayName, boolean msg) {
		this.name = name;
		this.displayName = displayName;
		this.msg = msg;
	}
	
	public int tick = 0;
	
	public void onTick() {
		tick++;
		if(tick > 999991) tick =0;
		for(String s : new ArrayList<>(cooldownPlayers.keySet())) {
			if(cooldownPlayers.get(s) < System.currentTimeMillis()) {
				if(msg) {
					Player p = Bukkit.getPlayer(s);
					if(p != null) {
						p.sendMessage(Main.ABILITIES_PREFIX + " §6§lĐã hồi kỹ năng " + displayName + "!");
						U.playSound(p, Sound.BLOCK_STONE_BUTTON_CLICK_ON);
					}
				}
				cooldownPlayers.remove(s);
			}
		}
	}
	
	public void sendCDMessage(Player p) {
		p.sendMessage(Main.ABILITIES_PREFIX + "§cKỹ năng chưa hồi, còn lại §f§l" + U.lamTronString(getTimeLeft(p)));
	}

	private double getTimeLeft(Player p) {
		Long time = cooldownPlayers.get(p.getName());
		if(time == null) return 0;
		long now = System.currentTimeMillis();
		if (time <= now) return 0;
		return (time - now) / 1000.0; // Trả về số giây còn lại
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void cast(Player p) {
	}

	@Override
	public void onAttack(Player p, LivingEntity target, double damage) {
	}

	@Override
	public void beAttack(Player p, LivingEntity damager, double damage) {
	}

	@Override
	public boolean isCooldown(Player p) {
		return cooldownPlayers.containsKey(p.getName());
	}

	@Override
	public void setCooldown(Player p) {
		cooldownPlayers.put(p.getName(), (long) (System.currentTimeMillis() + 1000*getCooldown()));
	}

	@Override
	public double getCooldown() {
		return 0;
	}
	
	public void sendDoSkillsMsg(Player p) {
		p.sendMessage(Main.ABILITIES_PREFIX + "§aThi triển kỹ năng §b[ §a" + displayName + " §b] §a thành công (CD: " + U.lamTronString(getCooldown()) + "s)");
		U.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING);
	}

	@Override
	public double getAddDamage(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public double getAddDefense(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public double getAddCritChance(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public double getAddCritDamage(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public double getAddDodgeRate(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public double getAddBlockRate(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public double getAddBlockAmount(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public double getAddTangSatThuong(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public double getAddGiamSatThuong(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public double getRawDamage(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public int getSkillLevel(Player p) {
		return AbilityManager.getLevel(p, getName());
	}
	
	public boolean cost(Player p, int hp, int mp) {
		// Kiểm tra HP
		if (hp > 0 && p.getHealth() <= hp) {
			p.sendMessage(Main.ABILITIES_PREFIX + "§cKhông đủ HP! Cần: " + hp);
			return false;
		}
		
		// Kiểm tra MP (nếu có MMOCore)
		if (mp > 0 && neo.rpgsystem.integration.mmocore.MMOCoreIntegration.isMMOCoreAvailable()) {
			try {
				net.Indyuce.mmocore.api.player.PlayerData data = 
					net.Indyuce.mmocore.api.player.PlayerData.get(p);
				if (data.getMana() < mp) {
					p.sendMessage(Main.ABILITIES_PREFIX + "§cKhông đủ Mana! Cần: " + mp);
					return false;
				}
				// Trừ mana
				data.giveMana(-mp);
			} catch (Exception e) {
				// Fallback - bỏ qua
			}
		}
		
		// Trừ HP nếu cần
		if (hp > 0) {
			p.setHealth(Math.max(0.5, p.getHealth() - hp));
		}
		
		return true;
	}

}
