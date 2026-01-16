package neo.rpgsystem.abilities.rpgclass.ascended;

import org.bukkit.entity.Player;

import neo.rpgsystem.abilities.CooldownSkills;

public class AscendedPassive1 extends CooldownSkills {

	public AscendedPassive1() {
		super("ascendedpassive1", "Hút Máu", false);
	}
	
	public double getAddHutMau(Player p) {
		int level = getSkillLevel(p);
		return getAddHutMau(level);
	}
	
	
	public static double getAddHutMau(int level) {
		if(level <= 0) return 0;
		
		return 5+level*0.5;
	}

}
