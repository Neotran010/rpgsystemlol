package neo.rpgsystem.abilities.mmocore.humans;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.def.SimpleSkillResult;
import neo.rpgsystem.abilities.beam.AbilitiesBeamManager;
import neo.rpgsystem.abilities.beam.MovementAbilitiesBeam;
import neo.rpgsystemlol.main.DamageU;
import neo.rpgsystemlol.main.Main;
import neo.rpgsystemlol.main.Particles;

/**
 * Human Passive Skill 1: Bạt Đao Kiếm
 * Chém ngang 2 lần, sau đó bắn kiếm khí (level >= 3), hất tung (level >= 5)
 * 
 * Modifiers:
 * - slash-damage: sát thương chém ngang (%)
 * - beam-damage: sát thương kiếm khí (%)
 * - cooldown: thời gian hồi chiêu (giây)
 */
public class HumansPassive1Handler extends SkillHandler<SimpleSkillResult> {
    
    public HumansPassive1Handler() {
        super("HUMANS_PASSIVE_1");
        registerModifiers("slash-damage", "beam-damage", "cooldown");
    }
    
    @Override
    public SimpleSkillResult getResult(SkillMetadata meta) {
        // Passive skill, luôn có thể cast
        return new SimpleSkillResult(true);
    }
    
    @Override
    public void whenCast(SimpleSkillResult result, SkillMetadata meta) {
        Player p = meta.getCaster().getPlayer();
        if (p == null) return;
        
        // Lấy parameters
        int level = (int) meta.getParameter("level");
        double slashDamage = meta.getParameter("slash-damage");
        double beamDamage = meta.getParameter("beam-damage");
        
        // Default values
        if (level == 0) level = 1;
        if (slashDamage == 0) slashDamage = getChemNgangDamage(level) / 100.0;
        if (beamDamage == 0) beamDamage = getKiemKhiDamage(level) / 100.0;
        
        final int finalLevel = level;
        final double finalSlashDamage = slashDamage;
        final double finalBeamDamage = beamDamage;
        
        // Task thực hiện chém ngang 2 lần, sau đó bắn kiếm khí
        new BukkitRunnable() {
            int tick = 0;
            int phase = 0;
            
            @Override
            public void run() {
                tick++;
                if (tick >= 10) {
                    if (phase <= 1) {
                        phase++;
                        chemNgang(p, finalSlashDamage);
                        // Sound khi chém ngang
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f + phase * 0.1f);
                    } else {
                        this.cancel();
                        if (finalLevel >= 3) {
                            kiemPhep(p, finalLevel, finalLevel >= 5, finalBeamDamage);
                            // Sound khi kích hoạt kiếm phép
                            p.getWorld().playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
                        }
                        return;
                    }
                }
            }
        }.runTaskTimer(Main.pl, 0, 1);
    }
    
    /**
     * Chém ngang, gây sát thương xung quanh
     */
    private void chemNgang(Player p, double damagePercent) {
        List<LivingEntity> targets = DamageU.getTargetsAround(p, p.getLocation(), 3);
        DamageU.scaleDamageSkill(p, targets, damagePercent);
        // Sound khi gây damage
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.8f, 1.3f);
    }
    
    /**
     * Bắn kiếm khí
     */
    private void kiemPhep(Player p, int level, boolean hatTung, double beamDamage) {
        AbilitiesBeamManager.register(new CustomBeam(
            p.getLocation(), 
            p.getLocation().getDirection().clone().setY(0), 
            p, 
            DamageU.getDamage(p, beamDamage), 
            level,
            hatTung
        ));
    }
    
    /**
     * Tính sát thương chém ngang theo level
     */
    public static double getChemNgangDamage(int level) {
        if (level <= 0) return 0;
        return 100 + level * 5;
    }
    
    /**
     * Tính sát thương kiếm khí theo level
     */
    public static double getKiemKhiDamage(int level) {
        if (level <= 0) return 0;
        return 80 + level * 6;
    }
    
    /**
     * Custom Beam cho kiếm khí
     */
    private static class CustomBeam extends MovementAbilitiesBeam {
        
        private int level;
        private boolean hatTung;
        
        public CustomBeam(Location location, Vector direction, Player caster, double damage, int level, boolean hatTung) {
            super(location, direction, caster, true, false, false, damage, 16, 1);
            this.level = level;
            this.hatTung = hatTung;
            setRangeHit(hatTung ? 4 : 2);
        }
        
        @Override
        protected void particle() {
            Particles.doChemNgang(Particle.INSTANT_EFFECT, getLocation(), getDirection(), 0.5, getRangeHit());
            super.particle();
            // Sound khi particle beam xuất hiện
            getCaster().getWorld().playSound(getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 0.7f, 1.4f);
        }
        
        @Override
        protected void onHit(LivingEntity le) {
            if (hatTung) {
                // Hất tung nhẹ
                le.setVelocity(new Vector(0, 1, 0));
                // Sound khi trúng beam
                le.getWorld().playSound(le.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0f, 1.2f);
            }
        }
    }
}
