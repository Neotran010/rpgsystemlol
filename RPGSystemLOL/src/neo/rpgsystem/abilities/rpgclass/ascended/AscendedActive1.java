package neo.rpgsystem.abilities.rpgclass.ascended;

import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import neo.rpgsystem.abilities.boost.BoostNeoSkill;
import neo.rpgsystemlol.main.Particles;
import neo.rpgsystemlol.main.Particles.neoParticleColor;
import neo.rpgsystemlol.main.U;

public class AscendedActive1 extends BoostNeoSkill {

    public AscendedActive1() {
        super("ascendedactive1", "Hồn Trượng", true, neoParticleColor.YELLOW, 20*5);
    }

    @Override
    public BoostData createBoostData(Player p) {
        return new HonTruongBoostData(new PotionEffect(PotionEffectType.STRENGTH, 20*5, 3), 20*5);
    }

    @Override
    public void onAttack(Player p, LivingEntity target, double damage) {
        if (!onBoost(p)) return;
        int level = getSkillLevel(p);
        HonTruongBoostData dt = (HonTruongBoostData) getBoostData(p).get(0);
        dt.onAttack(p, target, damage, level);
    }

    private static class HonTruongBoostData extends PotionsBoostData {

        private boolean firstHit = false;

        public HonTruongBoostData(PotionEffect pe, int timeLeft) {
            super(pe, timeLeft);
        }

        // Được gọi trong onAttack của skill
        public void onAttack(Player p, LivingEntity le, double damage, int level) {
            if (!firstHit) {
                firstHit = true;
                // 2. Đòn đánh đầu tiên gây thêm sát thương chuẩn theo % máu đối phương
                double percentTrueDamage = 0.08 + 0.02 * Math.max(0, level - 1); // ví dụ: 8% + 2% mỗi level > 1
                double trueDamage = U.getMaxHealth(le) * percentTrueDamage;
                le.damage(trueDamage, p); // sát thương chuẩn

                // 3. Hút máu theo sát thương chuẩn đó
                double lifesteal = trueDamage * 0.5; // ví dụ 50% máu hút lại
                p.setHealth(Math.min(p.getHealth() + lifesteal, U.getMaxHealth(p)));

                // Particle hút máu từ mục tiêu về player
                Particles.hoiTuSingle(le.getLocation().add(0, 1, 0), Particle.FLAME, 1.5, 0.5, 1.3, 12);
                Particles.spawnParticleColor(p.getLocation().add(0, 1, 0), Particle.DUST_COLOR_TRANSITION, neoParticleColor.YELLOW, 10, 0.3, 0.7, 0.3);

                // Có thể thêm thông báo cho người chơi nếu muốn
                p.sendMessage("§cĐòn đầu tiên gây " + String.format("%.1f", trueDamage) + " sát thương chuẩn và hồi " + String.format("%.1f", lifesteal) + " máu!");
            }
        }
    }
}