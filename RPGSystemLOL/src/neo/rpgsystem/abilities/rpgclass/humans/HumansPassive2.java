package neo.rpgsystem.abilities.rpgclass.humans;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import neo.rpgsystem.abilities.CooldownSkills;
import neo.rpgsystemlol.main.DamageU;
import neo.rpgsystemlol.main.Particles;

public class HumansPassive2 extends CooldownSkills {

    public HumansPassive2(String name, String displayName, boolean msg) {
        super("humanspassive2", "Bậc Thầy Đỡ Đòn", false);
    }

    @Override
    public double getCooldown() {
        return 3;
    }

    public static double getAddBlockChance(int level) {
        if (level <= 0) return 0;
        return 30 + level * 2;
    }

    @Override
    public double getAddBlockRate(Player p, LivingEntity target, double damage) {
        return getAddBlockChance(getSkillLevel(p)) / 100;
    }

    public void onBlock(Player p, LivingEntity target, double phanDonAmount) {
        if (isCooldown(p)) return;
        int level = getSkillLevel(p);
        if (level >= 3) {
            // Phản đòn
            setCooldown(p);
            double phanDonDamage = phanDonAmount * 0.3;
            phanDonDamage = Math.min(p.getAttribute(Attribute.MAX_HEALTH).getValue() * 0.1, phanDonDamage);
            DamageU.damageSkill(p, target, phanDonDamage);

            // Sound khi phản đòn
            p.getWorld().playSound(p.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.2f);

            particle(p, target);

            if (level >= 5) {
                // Heal: 30% phanDonAmount (max: 5% máu tối đa)
                double maxHeal = p.getAttribute(Attribute.MAX_HEALTH).getValue() * 0.05;
                double healAmount = Math.min(phanDonAmount * 0.3, maxHeal);
                DamageU.heal(p, healAmount);

                // Sound khi hồi máu
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 2.0f);
            }
        }
    }

    private void particle(Player p, LivingEntity target) {
        if (!p.getWorld().equals(target.getWorld())) return;
        Location l = p.getLocation().clone().add(0, 1, 0);
        Location l2 = target.getEyeLocation().clone();
        Vector v = l2.clone().subtract(l).toVector().normalize();
        Location loc = l.clone().add(v);
        Particles.circleVec(Particle.FLAME, loc, v, 0.3, Math.PI / 8);
    }
}