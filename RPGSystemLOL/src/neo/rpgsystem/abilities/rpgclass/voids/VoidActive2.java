package neo.rpgsystem.abilities.rpgclass.voids;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import neo.rpgsystem.abilities.CooldownSkills;
import neo.rpgsystemlol.main.DamageU;
import neo.rpgsystemlol.main.Particles;

public class VoidActive2 extends CooldownSkills {

    public VoidActive2() {
        // Tùy chỉnh tên/hiển thị nếu cần
        super("voidactive2", "Bước Hư Vô", false);
    }

    @Override
    public void cast(Player p) {
        int level = getSkillLevel(p);
        LivingEntity target = DamageU.getTargetAhead(p, level >= 7 ? 16 : 8, 1);
        if (target == null) return;

        // teleport tới target
        teleport(p, target);

        // phase2: áp dụng slow + blindness quanh điểm đến
        phase2(p, target.getLocation());

        // mục tiêu chính nhận +1 điểm hư vô (gọi method, hiện để trống)
        addVoidPoint(target, 1);
    }

    private void teleport(Player p, LivingEntity target) {
        Location from = p.getLocation().clone().add(0, 1, 0);
        Location to = target.getLocation().clone().add(0, 1, 0);

        // Vẽ line particle từ người chơi tới mục tiêu (sử dụng Particles.line)
        Particles.line(from, to, Particle.LARGE_SMOKE, Particles.neoParticleColor.PURPLE, 0.5);

        // Âm thanh dịch chuyển
        from.getWorld().playSound(from, org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        // Teleport player tới target
        Location tpLoc = to.clone();
        tpLoc.setPitch(p.getLocation().getPitch());
        tpLoc.setYaw(p.getLocation().getYaw());
        p.teleport(tpLoc);

        // Hiệu ứng khi tới nơi
        Particles.spawnSphereParticles(tpLoc, Particle.PORTAL, 0.8, 24, Particles.neoParticleColor.PURPLE);
        Particles.DoParticle(tpLoc, Particle.ENCHANT, 6);

        // Tạm mô phỏng "stun" mục tiêu chính bằng slow mạnh 1s
        int stunTicks = 20; // 1 giây
        target.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SLOWNESS, stunTicks, 4, true, false));
        target.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SLOW_FALLING, stunTicks, 2, true, false));

        // particle ở mục tiêu
        Particles.spawnParticleColor(target.getLocation().add(0, 1, 0), Particle.CRIT, Particles.neoParticleColor.BLACK);
    }

    private void phase2(Player p, Location l) {
        int level = getSkillLevel(p);
        List<LivingEntity> targets = DamageU.getTargetsAround(p, l, 3);

        int slowTicks = 20 * (level >= 7 ? 5 : 3);
        int blindTicks = 20 * (level >= 7 ? 4 : 2);

        for (LivingEntity le : targets) {
            if (le.equals(p)) continue;
            le.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SLOWNESS, slowTicks, 2, true, true));
            le.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.BLINDNESS, blindTicks, 0, true, true));
            le.setVelocity(new Vector(0, 0, 0));

            Particles.spawnParticleColor(le.getLocation().add(0, 1, 0), Particle.LARGE_SMOKE, Particles.neoParticleColor.BLACK, 6, 0.4, 0.6, 0.4);
        }

        Particles.spawnSphereParticles(l.clone().add(0, 1, 0), Particle.DUST_COLOR_TRANSITION, 2.0, 30, Particles.neoParticleColor.BLACK);
        l.getWorld().playSound(l, org.bukkit.Sound.ENTITY_EVOKER_CAST_SPELL, 1.0f, 0.9f);
    }

    /**
     * Method addVoidPoint để trống theo yêu cầu.
     * Bạn có thể triển khai logic quản lý điểm hư vô ở nơi khác và gọi từ đây sau.
     */
    private void addVoidPoint(LivingEntity target, int amount) {
        // intentionally left empty
    }

}
