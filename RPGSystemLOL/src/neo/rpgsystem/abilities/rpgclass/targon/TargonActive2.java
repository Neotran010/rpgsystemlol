package neo.rpgsystem.abilities.rpgclass.targon;

import java.util.List;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import neo.rpgsystem.abilities.CostConditionSkills;
import neo.rpgsystemlol.main.DamageU;
import neo.rpgsystemlol.main.Particles;
import neo.rpgsystemlol.main.U;

public class TargonActive2 extends CostConditionSkills {

    public TargonActive2() {
        super("targonactive2", "Khiêu Khích", true, 50, 0);
    }

    @Override
    public void cast(Player p) {
        int level = getSkillLevel(p);

        double baseRange = 6;
        double range = baseRange;
        if (level >= 7) {
            range *= 2; // tăng x2 tầm với lv>=7
        }

        List<LivingEntity> targets = DamageU.getTargetsAround(p, p.getLocation(), range);

        if (targets.isEmpty()) return;

        // Kéo kẻ địch vào gần bản thân và khiêu khích
        for (LivingEntity target : targets) {
            // Kéo vào: di chuyển kẻ địch về gần vị trí p trong bán kính 1.5 block
            Vector pull = p.getLocation().toVector().subtract(target.getLocation().toVector()).normalize().multiply(1.5);
            target.setVelocity(pull);

            // Khiêu khích (aggro): cho target tấn công p trong thời gian ngắn
            DamageU.taunt(target, p, 40); // 2s khiêu khích

            // Gây sát thương (có thể scale theo level)
            double damage = 50 + level * 12;
            DamageU.damageSkill(p, target, damage);

            // lv≥7: Làm chậm kẻ địch
            if (level >= 7) {
                DamageU.slow(target, 60, 1); // 3s slow level 1
            }

            Particles.DoParticle(target.getLocation(), Particle.SMOKE, 8);
            Particles.DoParticle(target.getLocation(), Particle.ASH, 4);
        }

        // lv≥5: Nhận thêm kháng sát thương
        if (level >= 5) {
            DamageU.addGiamSatThuong(p, 60, 0.25); // 3s, giảm 25% sát thương nhận vào
            Particles.circle(p.getLocation().add(0, 1, 0), Particle.SHRIEK, 2, Math.PI / 6);
        }

        // Hiệu ứng âm thanh khi kích hoạt
        U.playSound(p.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK);
    }
}