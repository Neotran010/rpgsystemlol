package neo.rpgsystem.abilities;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class NeoSkillsListener implements Listener {
	
	@EventHandler
	public void a(EntityDamageByEntityEvent e) {
		Player p = null;
		if(e.getDamager() instanceof Player) {
			p = (Player) e.getDamager();
		}else if(e.getDamager() instanceof Projectile) {
			Projectile pj = (Projectile) e.getDamager();
			if(pj.getShooter() instanceof Player) p = (Player) pj.getShooter();
		}
		
		if(p == null) return;
//		if(DF.isStun(p)) {
//			e.setCancelled(true);
//		}
		
	}
	
//	@EventHandler
//	public void move(PlayerMoveEvent e) {
//		Player p = e.getPlayer();
//		if(!p.isOnGround()) {
//			if(DF.isStun(p)) {
//				e.setCancelled(true);
//			}
//		}
//	}
	

}
