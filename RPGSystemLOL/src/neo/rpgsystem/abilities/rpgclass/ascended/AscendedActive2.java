package neo.rpgsystem.abilities.rpgclass.ascended;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import neo.rpgsystem.abilities.CostConditionSkills;
import neo.rpgsystem.abilities.beam.AbilitiesBeamManager;
import neo.rpgsystem.abilities.beam.MovementAbilitiesBeam;
import neo.rpgsystemlol.main.DamageU;
import neo.rpgsystemlol.main.Particles;
import neo.rpgsystemlol.main.Particles.neoParticleColor;

public class AscendedActive2 extends CostConditionSkills {

    public AscendedActive2() {
        // Tự động điền các biến cần thiết
        super("ascendedactive2", "Nguyên Rủa", true, 60, 0); // ví dụ: tên, tên hiển thị, hiện thông báo, tiêu hao MP, tiêu hao HP
    }

    @Override
    public void cast(Player p) {
        LivingEntity target = DamageU.getTargetAhead(p, 16, 1);
        if (target == null) return;

        int level = getSkillLevel(p);
        Location location = p.getEyeLocation().clone();
        double damage = DamageU.getDamage(p, getDamage(level));

        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, 1, 1.3f); // Âm thanh

        // Tạo viên đạn đầu tiên
        NguyenRuaBullet bl = new NguyenRuaBullet(location, p, damage, target, 3, level, new ArrayList<>());
        AbilitiesBeamManager.register(bl);
    }

    // Đạn nguyên rủa
    private static class NguyenRuaBullet extends MovementAbilitiesBeam {
        private LivingEntity target;
        private int boundLeft; // Số lần nảy còn lại
        private int level;
        private List<LivingEntity> hitEntities; // Danh sách mục tiêu đã nảy qua

        public NguyenRuaBullet(Location location, Player caster, double damage, LivingEntity target, int boundLeft, int level, List<LivingEntity> hitEntities) {
            super(location, null, caster, false, false, false, damage, 32, 0.4);
            this.target = target;
            this.boundLeft = boundLeft;
            this.level = level;
            this.hitEntities = new ArrayList<>(hitEntities); // copy để tránh trùng lặp
        }

        @Override
        public Vector getDirection() {
            Location l = target.getLocation().clone().add(0, 1, 0);
            Location loc = getLocation().clone();
            Vector v = l.subtract(loc).toVector().normalize();
            return v;
        }

        @Override
        protected void particle() {
            Particles.DoParticle(getLocation(), Particle.FLAME, 1);
        }

        @Override
        protected void onHit(LivingEntity le) {
            if (le.equals(target)) {
                // Gây sát thương + hiệu ứng suy yếu
                le.damage(getDamage(), getCaster());
                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 1)); // 3s suy yếu

                // Đánh dấu đã trúng mục tiêu này
                hitEntities.add(le);

                // Nếu là lần đầu tiên và đủ cấp thì gây sát thương diện rộng
                if (boundLeft == 3 && level >= 7) {
                    // Gây sát thương diện rộng quanh mục tiêu đầu tiên
                    for (LivingEntity areaLe : le.getWorld().getLivingEntities()) {
                        if (areaLe.equals(le) || areaLe.equals(getCaster())) continue;
                        if (areaLe.getLocation().distance(le.getLocation()) <= 3) {
                            areaLe.damage(getDamage() * 0.5, getCaster());
                            areaLe.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0));
                            Particles.spawnParticleColor(areaLe.getLocation().add(0, 1, 0), Particle.DUST_COLOR_TRANSITION, neoParticleColor.BLACK, 10, 0.5, 0.7, 0.5);
                        }
                    }
                }

                delete();
                onEnd();
            }
        }

        @Override
        protected void onEnd() {
            if (boundLeft > 0) {
                // Tìm mục tiêu mới quanh target hiện tại
                List<LivingEntity> candidates = new ArrayList<>();
                for (LivingEntity entity : target.getWorld().getLivingEntities()) {
                    if (entity.equals(getCaster())) continue;
                    if (entity.equals(target)) continue; // Không nảy lại chính mình trong lần này, nhưng có thể nảy lại các mục tiêu cũ
                    if (entity.getLocation().distance(target.getLocation()) <= 4) {
                        candidates.add(entity);
                    }
                }
                // Nếu không còn ai, cho phép nảy lại các mục tiêu cũ
                if (candidates.isEmpty()) {
                    for (LivingEntity entity : target.getWorld().getLivingEntities()) {
                        if (entity.equals(getCaster())) continue;
                        if (entity.getLocation().distance(target.getLocation()) <= 4 && !entity.equals(target)) {
                            candidates.add(entity);
                        }
                    }
                }
                // Chọn tối đa 3 mục tiêu (số nảy còn lại)
                int numBounce = Math.min(boundLeft, 3);
                Collections.shuffle(candidates);
                for (int i = 0; i < numBounce && i < candidates.size(); i++) {
                    LivingEntity nextTarget = candidates.get(i);
                    // Tạo viên đạn mới tới mục tiêu tiếp theo
                    NguyenRuaBullet nextBl = new NguyenRuaBullet(target.getLocation().clone().add(0, 1, 0), getCaster(), getDamage(), nextTarget, boundLeft - 1, level, hitEntities);
                    AbilitiesBeamManager.register(nextBl);
                }
            }
        }
    }

    public static double getDamage(int level) {
        if (level <= 0) return 0;
        return 120 + level * 3;
    }
}