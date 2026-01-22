package neo.rpgsystem.abilities;

import org.bukkit.entity.Player;

import neo.rpgsystem.integration.mmocore.MMOCoreIntegration;

public class AbilityManager {
	
	/**
	 * Lấy skill level của player
	 * Ưu tiên lấy từ MMOCore nếu có, fallback về config
	 */
	public static int getLevel(Player p, String skillName) {
		// Thử lấy từ MMOCore trước
		if (MMOCoreIntegration.isMMOCoreAvailable()) {
			try {
				int level = getMMOCoreSkillLevel(p, skillName);
				if (level > 0) return level;
			} catch (Exception ignored) {}
		}
		
		// Fallback: trả về level mặc định từ config hoặc 1
		return 1;
	}
	
	private static int getMMOCoreSkillLevel(Player p, String skillName) {
		try {
			net.Indyuce.mmocore.api.player.PlayerData playerData = 
				net.Indyuce.mmocore.api.player.PlayerData.get(p);
			if (playerData == null) return 0;
			
			// Tìm skill trong class của player
			for (net.Indyuce.mmocore.skill.ClassSkill classSkill : playerData.getProfess().getSkills()) {
				String handlerId = classSkill.getSkill().getHandler().getId();
				if (handlerId.equalsIgnoreCase(skillName) || 
					handlerId.equalsIgnoreCase(convertToMMOCoreId(skillName))) {
					return playerData.getSkillLevel(classSkill.getSkill());
				}
			}
		} catch (Exception e) {
			// MMOCore không available hoặc lỗi
		}
		return 0;
	}
	
	private static String convertToMMOCoreId(String skillName) {
		return skillName.toUpperCase()
			.replaceAll("([a-z])([A-Z])", "$1_$2")
			.replaceAll("(\\D)(\\d)", "$1_$2");
	}

}
