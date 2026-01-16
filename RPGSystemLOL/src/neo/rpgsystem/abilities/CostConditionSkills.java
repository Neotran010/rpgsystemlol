package neo.rpgsystem.abilities;

import org.bukkit.entity.Player;

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
		//TODO manacost
		return false;
	}

}
