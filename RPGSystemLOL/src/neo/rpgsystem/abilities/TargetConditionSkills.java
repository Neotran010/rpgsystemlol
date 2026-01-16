package neo.rpgsystem.abilities;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import neo.rpgsystemlol.main.DamageU;
import neo.rpgsystemlol.main.Main;

public class TargetConditionSkills extends CostConditionSkills {
	
	private double maxDistance;
	private double rangeBox;

	public TargetConditionSkills(String name, String displayName, boolean msg, int mpCost, int hpCost, double maxDistance, double rangeBox) {
		super(name, displayName, msg, mpCost, hpCost);
		this.maxDistance = maxDistance;
		this.rangeBox = rangeBox;
	}
	

    @Override
    public boolean condition(Player p) {
        LivingEntity le = DamageU.getTargetAhead(p, maxDistance, rangeBox);
        if (le == null) {
            p.sendMessage(Main.ABILITIES_PREFIX + "§cKhông thấy mục tiêu!");
            return false;
        }
        return super.condition(p);
    }

}
