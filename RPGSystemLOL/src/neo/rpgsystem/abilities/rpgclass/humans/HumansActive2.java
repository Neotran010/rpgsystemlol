package neo.rpgsystem.abilities.rpgclass.humans;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import neo.rpgsystem.abilities.CostConditionSkills;
import neo.rpgsystemlol.main.DamageU;
import neo.rpgsystemlol.main.Main;
import neo.rpgsystemlol.main.Particles;

public class HumansActive2 extends CostConditionSkills {

    // Enum cho các sound sử dụng trong skill
    private enum SkillSound {
        START(Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f),
        JUMP(Sound.ENTITY_ARROW_SHOOT, 0.7f, 1.3f),
        HIT(Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.0f),
        FINISH(Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.7f, 1.2f);

        public final Sound sound;
        public final float volume;
        public final float pitch;

        SkillSound(Sound sound, float volume, float pitch) {
            this.sound = sound;
            this.volume = volume;
            this.pitch = pitch;
        }

        public void play(Location loc) {
            loc.getWorld().playSound(loc, sound, volume, pitch);
        }
    }

    public HumansActive2(String name, String displayName, boolean msg) {
        super("humansactive2", "Tiến Công", true, 50, 0);
    }

    private static class HumansActive2Task extends BukkitRunnable {
        /**
         * Nhảy theo hình vòng cung (tạo biến modify góc của vòng cung), đến location theo speed
         */

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
                    SkillSound.HIT.play(target.getLocation());
                }
                SkillSound.FINISH.play(caster.getLocation());
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
            caster.getWorld().spawnParticle(org.bukkit.Particle.SWEEP_ATTACK, newLoc, 2, 0.1, 0.2, 0.1, 0);
            Particles.DoParticle(caster.getLocation().clone(), Particle.SMOKE, 5, 0.2, 0.1, 0.2, 0);
            SkillSound.JUMP.play(newLoc);

            // Nếu gần mục tiêu, kết thúc sớm
            if (newLoc.distance(end) < 1.2 && tick > maxTick / 2) {
                this.cancel();
                if (target != null && !target.isDead()) {
                    DamageU.scaleDamageSkill(caster, java.util.List.of(target), damage);
                    SkillSound.HIT.play(target.getLocation());
                }
                SkillSound.FINISH.play(caster.getLocation());
            }
        }
    }

    @Override
    public void cast(Player p) {
        LivingEntity le = DamageU.getTargetAhead(p, 16, 1);
        if (le == null) return;

        super.cast(p);

        double damage = 120.0;
        new HumansActive2Task(p, le, damage).runTaskTimer(Main.pl, 0, 1);

        // Sound khi bắt đầu kỹ năng
        SkillSound.START.play(p.getLocation());
    }

    @Override
    public boolean condition(Player p) {
        LivingEntity le = DamageU.getTargetAhead(p, 16, 1);
        if (le == null) {
            p.sendMessage(Main.ABILITIES_PREFIX + "§cKhông thấy mục tiêu!");
            return false;
        }
        return super.condition(p);
    }
}