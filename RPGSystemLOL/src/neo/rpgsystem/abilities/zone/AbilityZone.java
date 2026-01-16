package neo.rpgsystem.abilities.zone;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface AbilityZone {
	
	public Player getCaster();
	public Location getLocation();
	public double getRange();
	public void onTick();
	public int getLiveTick();
	public int getTimeLeft();
	public List<LivingEntity> getEntitiesInZone();
	public void onEnd();

}
