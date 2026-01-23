package neo.rpgsystem.abilities.beam;

import java.util.ArrayList;
import java.util.List;

public class AbilitiesBeamManager {
	
	private static List<AbilitiesBeam> beamsList = new ArrayList<AbilitiesBeam>();
	
	public static void setup() {
		// Khởi tạo beam manager
		if (beamsList == null) {
			beamsList = new ArrayList<>();
		}
		// Đảm bảo runnable đã chạy
		// (có thể đã có sẵn trong static block)
	}
	
	public static void register(AbilitiesBeam beam) {
		beamsList.add(beam);
	}

}
