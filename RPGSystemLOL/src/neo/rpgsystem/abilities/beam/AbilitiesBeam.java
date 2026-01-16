package neo.rpgsystem.abilities.beam;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public interface AbilitiesBeam {
	
	public void onTick();
	public Location getLocation();
	public LivingEntity getCaster();
	public void delete();
	public boolean isAlive();

}
