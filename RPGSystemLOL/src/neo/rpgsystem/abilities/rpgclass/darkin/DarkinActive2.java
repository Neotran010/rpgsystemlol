package neo.rpgsystem.abilities.rpgclass.darkin;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import neo.rpgsystem.abilities.CostConditionSkills;

public class DarkinActive2 extends CostConditionSkills {

	public DarkinActive2() {
		super("darkinactive2", "Cuồng Đạo", true, 50, 50);
	}

	@Override
	public void cast(Player p) {
		
		super.cast(p);
	}
	
	
	private static class FlyData {
		private Player caster;
		private Location location;
		private int tick = 0;
		private int maxTick; //thời gian tối đa (thay đổi theo distance)
	}
}
