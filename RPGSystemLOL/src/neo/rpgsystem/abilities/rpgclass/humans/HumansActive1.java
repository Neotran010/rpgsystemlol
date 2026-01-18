package neo.rpgsystem.abilities.rpgclass.humans;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import neo.rpgsystem.abilities.boost.BoostNeoSkill;
import neo.rpgsystem.abilities.boost.BoostNeoSkillType;
import neo.rpgsystemlol.main.Main;
import neo.rpgsystemlol.main.Particles;
import neo.rpgsystemlol.main.Particles.neoParticleColor;

// Assumes BoostNeoSkill, BoostNeoSkillType, neoParticleColor, Main, Particles, PotionsBoostData, BoostData are defined.

/**
 * @deprecated This class is deprecated. Please use {@link neo.rpgsystem.abilities.mmocore.humans.HumansActive1Handler} 
 * which integrates with MMOCore/MythicLib skill system.
 */
@Deprecated
public class HumansActive1 extends BoostNeoSkill {

    public HumansActive1() {
        super("humansactive1", "Kiếm Kỹ: Cường Hóa", true, neoParticleColor.YELLOW, 20 * 6, BoostNeoSkillType.NONE);
    }

    @Override
    public void cast(Player p) {
        int level = getSkillLevel(p);
        particle2(p, level);
        new BukkitRunnable() {
            @Override
            public void run() {
                List<BoostData> datas = new ArrayList<>();
                datas.add(createBoostData(p));
                for (BoostData data : datas) {
                    addBoostData(p, data);
                }
                onCast(p);
            }
        }.runTaskLater(Main.pl, 10);
    }

    private void particle2(Player p, int level) {
        new BukkitRunnable() {
            int t = 0;

            @Override
            public void run() {
                t++;
                if (t > 10) {
                    if (level >= 5) {
                        particleHeathBoost(p.getLocation());
                    }
                    if (level >= 3) {
                        particleSpeed(p.getLocation());
                    }
                    particleStrong(p.getLocation());
                    this.cancel();
                    return;
                } else {
                    int amount = 1;
                    if (t < 3) amount = 1;
                    else if (t < 5) amount = 2;
                    else if (t < 7) amount = 3;
                    else amount = 5;
                    Particles.hoiTuSingle(p.getLocation().clone().add(0, 1, 0), Particle.FLAME, 1.5, 0.2, 0.5, amount);
                    float pitch = 1.0f + 0.1f * t;
                    p.getWorld().playSound(p.getLocation(), "entity.player.attack.sweep", 1.0f, pitch);
                }
            }
        }.runTaskTimer(Main.pl, 1, 1);
    }

    private void particleStrong(Location l) {
        new BukkitRunnable() {
            double d = -0.2;
            Location loc = l.clone();

            @Override
            public void run() {
                d += 0.2;
                if (d > 2.1) {
                    this.cancel();
                    return;
                }
                Particles.circle(loc.clone().add(0, d, 0), Particle.DUST, 1, Math.PI / 6);
            }
        }.runTaskTimer(Main.pl, 0, 1);
    }

    private void particleSpeed(Location l) {
        new BukkitRunnable() {
            double d = 0;
            double r = 1;
            Location loc = l.clone();

            @Override
            public void run() {
                d += Math.PI / 8;
                if (d > Math.PI * 3) {
                    this.cancel();
                    return;
                }
                if (d > Math.PI * 2) {
                    r += 0.1;
                }
                for (int i = 0; i < 3; i++) {
                    Location loc2 = Particles.getCirclePoint(loc, d + i * Math.PI * 2 / 3, r);
                    Particles.spawnParticleColor(loc2, Particle.DUST_COLOR_TRANSITION, neoParticleColor.AQUA);
                }
            }
        }.runTaskTimer(Main.pl, 0, 1);
    }

    private void particleHeathBoost(Location l) {
        Particles.DoParticle(l.clone().add(0, 1, 0), Particle.HEART, 5, 0.5, 1, 0.5, 0);
    }

    @Override
    public BoostData createBoostData(Player p) {
        List<PotionEffect> potionList = new ArrayList<>();
        int level = getSkillLevel(p);

        if (level >= 3) {
            int speedLvl = Math.max(1, level / 3);
            potionList.add(new PotionEffect(PotionEffectType.SPEED, getBoostTickTime(level) * 20, speedLvl - 1));
        }

        int strengthLvl = Math.max(1, level / 3);
        potionList.add(new PotionEffect(PotionEffectType.STRENGTH, getBoostTickTime(level) * 20, strengthLvl - 1));

        if (level >= 5) {
            int healthBoostLvl = Math.max(1, level / 3);
            potionList.add(new PotionEffect(PotionEffectType.HEALTH_BOOST, getBoostTickTime(level) * 20, healthBoostLvl - 1));
        }

        return new PotionsBoostData(potionList, getBoostTickTime(level));
    }

    public int getBoostTickTime(int level) {
        if (level < 1) level = 1;
        if (level > 10) level = 10;
        return 6 + (level - 1) * 4 / 9; // Linear scale, min 6, max 10
    }

    @Override
    public void onAttack(Player p, LivingEntity target, double damage) {
        super.onAttack(p, target, damage);
        Location loc = target.getLocation().clone().add(0, 1, 0);
        Particles.hoiTuSingle(loc, Particle.SWEEP_ATTACK, 1.2, 0.2, 0.7, 3);
        Particles.DoParticle(loc, Particle.CRIT, 5, 0.3, 0.5, 0.3, 0);
        p.getWorld().playSound(loc, "entity.player.attack.sweep", 0.8f, 1.2f);
    }
}
