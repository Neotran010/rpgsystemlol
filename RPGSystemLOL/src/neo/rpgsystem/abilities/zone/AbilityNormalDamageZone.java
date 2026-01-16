package neo.rpgsystem.abilities.zone;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import neo.rpgsystemlol.main.DamageU;
import neo.rpgsystemlol.main.Particles;

/**
 * Vùng gây sát thương liên tục bình thường.
 * Có thể dùng cho các kỹ năng như "Zone of Pain", "Impact Field", v.v.
 * Tích hợp sẵn particle, sound, quản lý thời gian, v.v.
 */
public class AbilityNormalDamageZone implements AbilityDamageZone {

    private final Player caster;
    private final Location center;
    private final double range;
    private final double damagePerTick;
    private int tickCount = 0;
    private int timeLeft;
    private int tickDamage; //thời gian sẽ call damage 1 lần

    // Lưu lại các entity đã trúng zone (nếu muốn hiệu ứng riêng)
    private final List<LivingEntity> lastTickEntities = new ArrayList<>();

    public AbilityNormalDamageZone(Player caster, Location center, double range, double damagePerTick, int durationTick, int tickDamage) {
        this.caster = caster;
        this.center = center.clone();
        this.range = range;
        this.damagePerTick = damagePerTick;
        this.timeLeft = durationTick;
        this.tickDamage = tickDamage;
    }

    @Override
    public Player getCaster() {
        return caster;
    }

    @Override
    public void onTick() {
        tickCount++;
        if(getLiveTick() > 20*60*5) return;
        if (tickCount >= timeLeft) {
            onEnd();
        }
        if(tickCount % tickDamage == 0)
        damage();

    }
    
    public void particle() {
        Particles.spawnSphereParticles(center, org.bukkit.Particle.DUST_COLOR_TRANSITION, range, 18, Particles.neoParticleColor.RED);

        // Sound nhẹ mỗi 20 tick
        if (tickCount % 20 == 0) {
            center.getWorld().playSound(center, org.bukkit.Sound.BLOCK_BEACON_ACTIVATE, 0.7f, 1.6f);
        }
    }

    @Override
    public List<LivingEntity> getEntitiesInZone() {
        // Lấy các entity sống trong vùng ảnh hưởng
        List<LivingEntity> entities = new ArrayList<>();
        for (LivingEntity le : center.getWorld().getLivingEntities()) {
            if (!le.equals(caster) && le.getLocation().distance(center) <= range) {
                entities.add(le);
            }
        }
        return entities;
    }

    @Override
    public void onEnd() {
        // Hiệu ứng kết thúc: particle + sound
        Particles.spawnSphereParticles(center, org.bukkit.Particle.DUST_COLOR_TRANSITION, range + 0.5, 32,
                Particles.neoParticleColor.YELLOW);
        center.getWorld().playSound(center, org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 1.2f, 0.7f);
        // Có thể remove khỏi manager hoặc set flag kết thúc ở đây nếu cần
    }

    @Override
    public void damage() {
        List<LivingEntity> entities = getEntitiesInZone();
        for (LivingEntity le : entities) {
            DamageU.damageSkill(caster, le, damagePerTick);
            // Particle trúng đích
            Particles.spawnParticleColor(le.getLocation().add(0, 1, 0), org.bukkit.Particle.DUST_COLOR_TRANSITION,
                    Particles.neoParticleColor.RED, 3, 0.3, 0.3, 0.3);
        }
        lastTickEntities.clear();
        lastTickEntities.addAll(entities);
        damageParticle();
    }
    
    public void damageParticle() {
    	
    }

    @Override
    public Location getLocation() {
        return center;
    }

    @Override
    public double getRange() {
        return range;
    }

	@Override
	public int getLiveTick() {
		return tickCount;
	}

	@Override
	public int getTimeLeft() {
		return timeLeft;
	}
}