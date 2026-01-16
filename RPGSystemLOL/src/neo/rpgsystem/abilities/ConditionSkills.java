package neo.rpgsystem.abilities;

import org.bukkit.entity.Player;

public abstract class ConditionSkills extends CooldownSkills {

	public ConditionSkills(String name, String displayName, boolean msg) {
		super(name, displayName, msg);
	}
	
	public abstract boolean condition(Player p);
	
	@Override
	public void cast(Player p) {
	}

}
