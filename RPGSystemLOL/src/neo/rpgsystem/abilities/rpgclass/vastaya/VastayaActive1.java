package neo.rpgsystem.abilities.rpgclass.vastaya;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import neo.rpgsystem.abilities.boost.BoostNeoSkill;
import neo.rpgsystemlol.main.Main;
import neo.rpgsystemlol.main.Particles.neoParticleColor;

public class VastayaActive1 extends BoostNeoSkill {

    public VastayaActive1() {
        super("vastayaactive1", "Phi Vũ Tiễn", true, neoParticleColor.RED, 20 * 6);
    }

    @Override
    public void cast(Player p) {
        if (isCooldown(p)) {
            sendCDMessage(p);
            return;
        }

        if (!cost(p, 0, 30)) {
            return;
        }

        addBoostData(p, createBoostData(p));

        // Sound kích hoạt kỹ năng
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.2f);

        // Thông báo kích hoạt
        p.sendMessage(Main.ABILITIES_PREFIX + "§aBạn đã kích hoạt §cPhi Vũ Tiễn§a! Tốc độ tấn công được tăng mạnh trong thời gian ngắn.");
    }

    @Override
    protected void particle(Player p) {
        // Hiệu ứng mô tả bản thân đang được cường hóa: vòng xoáy lông vũ màu đỏ quanh người
    	for (int i = 0; i < 6; i++) {
    	    double angle = Math.toRadians((double) i / 6 * 360);
    	    double x = Math.cos(angle) * 0.7;
    	    double z = Math.sin(angle) * 0.7;
    	    p.getWorld().spawnParticle(
    	        org.bukkit.Particle.DUST, // DUST thay cho REDSTONE
    	        p.getLocation().add(x, 1.2, z),
    	        1,
    	        1, 0, 0, 1 // Màu đỏ (R=1, G=0, B=0), size=1
    	    );
    	}
        super.particle(p);
    }

    @Override
    public BoostData createBoostData(Player p) {
        return new PotionsBoostData(new PotionEffect(PotionEffectType.HASTE, 20 * 6, 3), 20 * 6);
    }

    public double getAddSpeed(Player p) {
        if (!onBoost(p)) return 0;
        int level = getSkillLevel(p);
        return getAddATSpeed(level) / 100;
    }

    public static double getAddATSpeed(int level) {
        if (level <= 0) return 0;
        return 30 + level * 2;
    }
}