package neo.rpgsystem.abilities.rpgclass.vastaya;

import org.bukkit.entity.Player;

import neo.rpgsystem.abilities.CooldownSkills;

public class VastayaPassive1 extends CooldownSkills {

	public VastayaPassive1() {
		super("vastayapassive1", "Bạo Phong Vũ", false);
	}
	
	public double getAddCrit(Player p) {
		int level = getSkillLevel(p);
		return getAddCrit(level);
	}
	
	public static double getAddCrit(int level) {
		if(level <= 0) return 0;
		return 10+level;
	}

}
