package neo.rpgsystem.abilities.zone;

import java.util.ArrayList;
import java.util.List;

public class AbilityZoneManager {
	
	private static List<AbilityZone> zones = new ArrayList<>();
	
	public static void register(AbilityZone zone) {
		zones.add(zone);
	}

}
