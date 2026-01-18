package neo.rpgsystem.abilities.mmocore.humans;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import neo.rpgsystemlol.main.Main;
import neo.rpgsystemlol.main.Particles;
import neo.rpgsystemlol.main.Particles.neoParticleColor;

/**
 * Human Active Skill 1: Kiếm Kỹ: Cường Hóa
 * Buff Strength (luôn có), Speed (level >= 3), Health Boost (level >= 5)
 * 
 * Modifiers:
 * - duration: thời gian buff (giây)
 * - strength: cấp độ Strength
 * - speed: cấp độ Speed
 * - health-boost: cấp độ Health Boost
 * - cooldown: thời gian hồi chiêu (giây)
 * - mana: mana cost
 */
public class HumansActive1Handler extends SkillHandler<SimpleSkillResult> {
    
    public HumansActive1Handler() {
        super("HUMANS_ACTIVE_1");
        registerModifiers("duration", "strength", "speed", "health-boost", "cooldown", "mana");
    }
    
    @Override
    public SimpleSkillResult getResult(SkillMetadata meta) {
        // Kiểm tra điều kiện cast (có thể thêm check mana, cooldown, etc.)
        return new SimpleSkillResult(true);
    }
    
    @Override
    public void whenCast(SimpleSkillResult result, SkillMetadata meta) {
        Player p = meta.getCaster().getPlayer();
        if (p == null) return;
        
        // Lấy parameters từ MMOCore config
        int level = (int) meta.getParameter("level"); // Skill level từ MMOCore
        double duration = meta.getParameter("duration"); // Thời gian buff (giây)
        int strengthLvl = (int) meta.getParameter("strength");
        int speedLvl = (int) meta.getParameter("speed");
        int healthBoostLvl = (int) meta.getParameter("health-boost");
        
        // Nếu không config thì dùng default từ level
        if (duration == 0) duration = getBoostTickTime(level);
        if (strengthLvl == 0) strengthLvl = Math.max(1, level / 3);
        if (speedLvl == 0) speedLvl = Math.max(1, level / 3);
        if (healthBoostLvl == 0) healthBoostLvl = Math.max(1, level / 3);
        
        // Particle hiệu ứng trước khi buff
        particle2(p, level);
        
        // Áp dụng buff sau 10 ticks (0.5 giây)
        int finalStrengthLvl = strengthLvl;
        int finalSpeedLvl = speedLvl;
        int finalHealthBoostLvl = healthBoostLvl;
        double finalDuration = duration;
        
        new BukkitRunnable() {
            @Override
            public void run() {
                List<PotionEffect> potions = new ArrayList<>();
                
                // Strength luôn có
                potions.add(new PotionEffect(PotionEffectType.STRENGTH, 
                    (int)(finalDuration * 20), finalStrengthLvl - 1));
                
                // Speed từ level 3 trở lên
                if (level >= 3) {
                    potions.add(new PotionEffect(PotionEffectType.SPEED, 
                        (int)(finalDuration * 20), finalSpeedLvl - 1));
                }
                
                // Health Boost từ level 5 trở lên
                if (level >= 5) {
                    potions.add(new PotionEffect(PotionEffectType.HEALTH_BOOST, 
                        (int)(finalDuration * 20), finalHealthBoostLvl - 1));
                }
                
                // Áp dụng tất cả potions
                for (PotionEffect pe : potions) {
                    p.addPotionEffect(pe);
                }
                
                // Thông báo
                p.sendMessage(Main.ABILITIES_PREFIX + "§6Kích hoạt hiệu ứng kỹ năng §fKiếm Kỹ: Cường Hóa!");
            }
        }.runTaskLater(Main.pl, 10);
    }
    
    /**
     * Particle hiệu ứng khi cast
     */
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
    
    /**
     * Particle hiệu ứng Strength
     */
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
    
    /**
     * Particle hiệu ứng Speed
     */
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
                    Location loc2 = getCirclePoint(loc, d + i * Math.PI * 2 / 3, r);
                    Particles.spawnParticleColor(loc2, Particle.DUST_COLOR_TRANSITION, neoParticleColor.AQUA);
                }
            }
        }.runTaskTimer(Main.pl, 0, 1);
    }
    
    /**
     * Particle hiệu ứng Health Boost
     */
    private void particleHeathBoost(Location l) {
        Particles.DoParticle(l.clone().add(0, 1, 0), Particle.HEART, 5, 0.5, 1, 0.5, 0);
    }
    
    /**
     * Tính thời gian buff theo level
     */
    public int getBoostTickTime(int level) {
        if (level < 1) level = 1;
        if (level > 10) level = 10;
        return 6 + (level - 1) * 4 / 9; // Linear scale, min 6, max 10
    }
    
    /**
     * Helper method để lấy điểm trên đường tròn
     */
    private Location getCirclePoint(Location center, double angle, double radius) {
        double x = Math.cos(angle) * radius;
        double z = Math.sin(angle) * radius;
        return center.clone().add(x, 0, z);
    }
}
