package neo.rpgsystem.abilities.mmocore.humans;

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
import neo.rpgsystemlol.main.DamageU;
import neo.rpgsystemlol.main.Main;
import neo.rpgsystemlol.main.Particles;

/**
 * Human Active Skill 2: Tiến Công
 * Nhảy theo hình vòng cung đến mục tiêu và gây sát thương
 * 
 * Modifiers:
 * - damage: sát thương gây ra
 * - range: tầm tìm mục tiêu
 * - cooldown: thời gian hồi chiêu (giây)
 * - mana: mana cost
 */
public class HumansActive2Handler extends SkillHandler<SimpleSkillResult> {
    
    public HumansActive2Handler() {
        super("HUMANS_ACTIVE_2");
        registerModifiers("damage", "range", "cooldown", "mana");
    }
    
    @Override
    public SimpleSkillResult getResult(SkillMetadata meta) {
        Player p = meta.getCaster().getPlayer();
        if (p == null) return new SimpleSkillResult(false);
        
        // Lấy range từ config
        double range = meta.getParameter("range");
        if (range == 0) range = 16;
        
        // Kiểm tra có mục tiêu không
        LivingEntity target = DamageU.getTargetAhead(p, range, 1);
        if (target == null) {
            p.sendMessage(Main.ABILITIES_PREFIX + "§cKhông thấy mục tiêu!");
            return new SimpleSkillResult(false);
        }
        
        return new SimpleSkillResult(true);
    }
    
    @Override
    public void whenCast(SimpleSkillResult result, SkillMetadata meta) {
        Player p = meta.getCaster().getPlayer();
        if (p == null) return;
        
        // Lấy parameters
        double damage = meta.getParameter("damage");
        double range = meta.getParameter("range");
        
        // Default values
        if (damage == 0) damage = 120.0;
        if (range == 0) range = 16;
        
        // Tìm mục tiêu
        LivingEntity target = DamageU.getTargetAhead(p, range, 1);
        if (target == null) return;
        
        // Bắt đầu task nhảy đến mục tiêu
        new HumansActive2Task(p, target, damage).runTaskTimer(Main.pl, 0, 1);
        
        // Sound khi bắt đầu kỹ năng
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f);
    }
    
    /**
     * Task xử lý việc nhảy theo vòng cung đến mục tiêu
     */
    private static class HumansActive2Task extends BukkitRunnable {
        private final Player caster;
        private final LivingEntity target;
        private final Location start;
        private final Location end;
        private final double damage;
        private int tick = 0;
        private final int maxTick;
        private final double arcHeight;
        private final Vector direction;
        private final double distance;
        
        public HumansActive2Task(Player caster, LivingEntity target, double damage) {
            this.caster = caster;
            this.target = target;
            this.start = caster.getLocation().clone();
            this.end = target.getLocation().clone();
            this.damage = damage;
            
            this.direction = end.toVector().subtract(start.toVector()).normalize();
            this.distance = start.distance(end);
            
            // Tốc độ: càng xa càng nhiều tick, nhưng min = 10 tick, max = 30 tick
            this.maxTick = Math.max(10, Math.min(30, (int) (distance * 2)));
            // Độ cao vòng cung, tuỳ ý theo khoảng cách
            this.arcHeight = Math.max(1.2, Math.min(4, distance / 2));
        }
        
        @Override
        public void run() {
            tick++;
            if (tick > maxTick) {
                this.cancel();
                // Đến cuối vòng cung: gây sát thương và hiệu ứng
                if (target != null && !target.isDead()) {
                    DamageU.scaleDamageSkill(caster, java.util.List.of(target), damage);
                    caster.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.0f);
                }
                caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.7f, 1.2f);
                return;
            }
            move();
        }
        
        private void move() {
            double progress = (double) tick / maxTick;
            Vector base = start.toVector().clone().add(direction.clone().multiply(distance * progress));
            double yArc = arcHeight * Math.sin(Math.PI * progress);
            base.setY(start.getY() + (end.getY() - start.getY()) * progress + yArc);
            
            Location newLoc = base.toLocation(caster.getWorld());
            caster.teleport(newLoc);
            
            // Particle hiệu ứng bay
            caster.getWorld().spawnParticle(Particle.SWEEP_ATTACK, newLoc, 2, 0.1, 0.2, 0.1, 0);
            Particles.DoParticle(caster.getLocation().clone(), Particle.SMOKE, 5, 0.2, 0.1, 0.2, 0);
            caster.getWorld().playSound(newLoc, Sound.ENTITY_ARROW_SHOOT, 0.7f, 1.3f);
            
            // Nếu gần mục tiêu, kết thúc sớm
            if (newLoc.distance(end) < 1.2 && tick > maxTick / 2) {
                this.cancel();
                if (target != null && !target.isDead()) {
                    DamageU.scaleDamageSkill(caster, java.util.List.of(target), damage);
                    caster.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.0f);
                }
                caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.7f, 1.2f);
            }
        }
    }
}
