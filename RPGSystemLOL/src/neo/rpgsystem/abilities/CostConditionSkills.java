package neo.rpgsystem.abilities;

import org.bukkit.entity.Player;

import neo.rpgsystem.integration.mmocore.MMOCoreIntegration;
import neo.rpgsystemlol.main.Main;

public class CostConditionSkills extends ConditionSkills {
	
	private int mpCost;
	private int hpCost;

	public CostConditionSkills(String name, String displayName, boolean msg, int mpCost, int hpCost) {
		super(name, displayName, msg);
		this.mpCost = mpCost;
		this.hpCost = hpCost;
	}

	@Override
	public boolean condition(Player p) {
		// Kiểm tra HP cost
		if (hpCost > 0 && p.getHealth() <= hpCost) {
			p.sendMessage(Main.ABILITIES_PREFIX + "§cKhông đủ HP! Cần: " + hpCost);
			return false;
		}
		
		// Kiểm tra MP cost (nếu có MMOCore)
		if (mpCost > 0 && MMOCoreIntegration.isMMOCoreAvailable()) {
			try {
				net.Indyuce.mmocore.api.player.PlayerData data = 
					net.Indyuce.mmocore.api.player.PlayerData.get(p);
				if (data.getMana() < mpCost) {
					p.sendMessage(Main.ABILITIES_PREFIX + "§cKhông đủ Mana! Cần: " + mpCost);
					return false;
				}
			} catch (Exception e) {
				// Fallback - bỏ qua check mana
			}
		}
		
		return true;
	}
	
	@Override
	public void cast(Player p) {
		// Trừ HP nếu cần
		if (hpCost > 0) {
			p.setHealth(Math.max(0.5, p.getHealth() - hpCost));
		}
		
		// Trừ MP nếu có MMOCore
		if (mpCost > 0 && MMOCoreIntegration.isMMOCoreAvailable()) {
			try {
				net.Indyuce.mmocore.api.player.PlayerData data = 
					net.Indyuce.mmocore.api.player.PlayerData.get(p);
				data.giveMana(-mpCost);
			} catch (Exception ignored) {}
		}
		
		super.cast(p);
	}
	
	public int getMpCost() { return mpCost; }
	public int getHpCost() { return hpCost; }

}
