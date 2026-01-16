package neo.rpgsystem.abilities.rpgclass.darkin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import neo.rpgsystem.abilities.boost.BoostNeoSkill;
import neo.rpgsystemlol.main.Main;
import neo.rpgsystemlol.main.Particles;
import neo.rpgsystemlol.main.U;
import neo.rpgsystemlol.main.Particles.neoParticleColor;

public class DarkinActive1 extends BoostNeoSkill {

    public DarkinActive1(String name, String displayName, boolean msg, int duration) {
        super("darkinactive1", "Huyết Chiến", true, neoParticleColor.RED, 20*6);
    }
    
    @Override
    public void cast(Player p) {
        addBoostData(p, createBoostData(p));
    }
    
    @Override
    public BoostData createBoostData(Player p) {
        int level = getSkillLevel(p);
        List<PotionEffect> list = new ArrayList<>();
        list.add(new PotionEffect(PotionEffectType.SPEED, 20*6, level/2 + 1));
        list.add(new PotionEffect(PotionEffectType.STRENGTH, 20*6, level/2 + 1));
        return new PotionsBoostData(list, 20*6);
    }
    
    @Override
    public double getAddDamage(Player p, LivingEntity target, double damage) {
        if(!onBoost(p)) return 0;
        // TODO: vẽ particle redstone quanh target (y+1)
        int level = getSkillLevel(p);
        if(level >= 7) {
            huyetKiem(p, target, damage);
        }
        return super.getAddDamage(p, target, damage);
    }
    
    private void huyetKiem(Player caster, LivingEntity target, double damage) {
        new BukkitRunnable() {
            int t = 0;
            double phi = 0;
            @Override
            public void run() {
                t++;
                phi += Math.PI/5; // animates rotation
                if(t >= 10) {
                    // TODO bắn huyết kiếm
                    Location l = target.getLocation().clone().add(0,2.5,0);
                    Location loc = target.getLocation().clone().add(0, U.random(0.8, 1.2), 0);
                    // Bạn có thể spawn một projectile, hoặc thực hiện hiệu ứng ở đây
                    this.cancel();
                    return;
                }
                Location l = target.getLocation().clone().add(0,2.5,0);
                for(int i=0; i<4; i++) {
                    double angle = phi + i * Math.PI/2;
                    Location circleLoc = Particles.getCircleLocation(l, new Vector(0,1,0), angle, 1);
                    Particles.spawnParticleColor(circleLoc, Particle.DUST_COLOR_TRANSITION, neoParticleColor.RED);
                }
            }
        }.runTaskTimer(Main.pl, 0, 1);
    }
}