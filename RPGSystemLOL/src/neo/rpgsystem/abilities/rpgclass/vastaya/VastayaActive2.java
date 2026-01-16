package neo.rpgsystem.abilities.rpgclass.vastaya;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import neo.rpgsystem.abilities.CostConditionSkills;
import neo.rpgsystemlol.main.DamageU;

public class VastayaActive2 extends CostConditionSkills {

    public VastayaActive2(String name, String displayName, boolean msg, int mpCost, int hpCost) {
        super("vastayaactive2", "Đào Thoát/Phản Công", true, mpCost, hpCost);
    }

    /*
     * Active2: Nhắm vào kẻ địch (hoặc không), nếu bản thân ở gần <5 block so với kẻ địch, sẽ bắn liên lục tên và nhảy lên đẩy lùi bản thân;
     * nếu ở xa hơn: sẽ ám sát phía sau kẻ địch;
     * level≥5: Khi bắn lùi: tại vị trí cast sẽ làm chậm và gây sát thương diện rộng;
     * level≥7: Ám sát sẽ tăng tốc và mù kẻ địch
     */

    @Override
    public void cast(Player p) {
        LivingEntity target = DamageU.getTargetAhead(p, 12, 1);
        if (target == null) return;
        int level = getSkillLevel(p);

        double distance = p.getLocation().distance(target.getLocation());
        if (distance < 5) {
            // Bắn liên tục tên và nhảy lùi
            shootFeatherVolley(p, target, level);
            knockbackPlayer(p);

            // level >= 5: tại vị trí cast làm chậm và gây sát thương diện rộng
            if (level >= 5) {
                slowAndDamageArea(p.getLocation(), p, level);
            }
        } else {
            // Ám sát phía sau kẻ địch
            teleportBehind(p, target);

            // level >= 7: tăng tốc và mù kẻ địch
            if (level >= 7) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 3, 2));
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 2, 1));
                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1.0f, 1.4f);
            }
        }

        // Sound khi kích hoạt
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 1.0f, 1.1f);

        // Hiệu ứng khi kích hoạt
        p.getWorld().spawnParticle(Particle.SWEEP_ATTACK, p.getLocation().add(0, 1, 0), 12, 0.5, 0.3, 0.5, 0);
    }

    private void shootFeatherVolley(Player p, LivingEntity target, int level) {
        for (int i = 0; i < 4 + level / 2; i++) {
            Vector direction = p.getLocation().getDirection().clone().normalize();
            Location start = p.getLocation().clone().add(0, 1.2, 0);
            // Tạo hiệu ứng lông vũ bắn về phía kẻ địch
            p.getWorld().spawnParticle(Particle.DUST, start, 2, 1, 0, 0, 1);
            p.getWorld().playSound(start, Sound.ENTITY_ARROW_SHOOT, 0.7f, 1.1f + 0.1f * i);

            // Gây sát thương nhỏ mỗi lần bắn
//            DamageU.damageSkill(p, target, 30 + level * 2);
            
            //TODO bắn lông vũ
        }
    }

    private void knockbackPlayer(Player p) {
        Vector dir = p.getLocation().getDirection().clone().multiply(-1).setY(0.5);
        p.setVelocity(dir);
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 1.0f, 1.2f);
    }

    private void slowAndDamageArea(Location loc, Player caster, int level) {
        double radius = 4;
        for (LivingEntity le : DamageU.getTargetsAround(caster, loc, radius)) {
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 3, 2));
            DamageU.damageSkill(caster, le, 40 + level * 2);
            le.getWorld().spawnParticle(Particle.DUST, le.getLocation().add(0, 1, 0), 2, 1, 0, 0, 1);
        }
        loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.4f);
    }

    private void teleportBehind(Player p, LivingEntity target) {
        Vector dir = target.getLocation().getDirection().clone().normalize();
        Location behind = target.getLocation().clone().add(dir.multiply(-1.5)).add(0, 0.5, 0);
        p.teleport(behind);
        p.getWorld().playSound(behind, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.5f);
    }
}
