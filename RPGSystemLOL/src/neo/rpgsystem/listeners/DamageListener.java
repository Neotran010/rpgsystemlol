package neo.rpgsystem.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import neo.rpgsystem.abilities.NeoSkill;
import neo.rpgsystem.abilities.NeoSkills;

public class DamageListener implements Listener {
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        Player attacker = getAttacker(e);
        if (attacker == null) return;
        
        if (!(e.getEntity() instanceof LivingEntity)) return;
        LivingEntity target = (LivingEntity) e.getEntity();
        
        double damage = e.getDamage();
        
        // Trigger onAttack cho tất cả registered skills
        // Có thể mở rộng logic ở đây
    }
    
    private Player getAttacker(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            return (Player) e.getDamager();
        }
        if (e.getDamager() instanceof Projectile) {
            Projectile proj = (Projectile) e.getDamager();
            if (proj.getShooter() instanceof Player) {
                return (Player) proj.getShooter();
            }
        }
        return null;
    }
}
