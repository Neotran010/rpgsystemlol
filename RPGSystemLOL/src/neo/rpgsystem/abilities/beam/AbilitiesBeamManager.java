package neo.rpgsystem.abilities.beam;

import java.util.ArrayList;
import java.util.List;

public class AbilitiesBeamManager {
	
	private static List<AbilitiesBeam> beamsList = new ArrayList<AbilitiesBeam>();
	
	public static void register(AbilitiesBeam beam) {
		beamsList.add(beam);
	}

}
