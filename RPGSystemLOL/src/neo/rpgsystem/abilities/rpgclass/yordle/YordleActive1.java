package neo.rpgsystem.abilities.rpgclass.yordle;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import neo.rpgsystem.abilities.CostConditionSkills;
import neo.rpgsystem.abilities.beam.AbilitiesBeamManager;
import neo.rpgsystem.abilities.beam.MovementAbilitiesBeam;
import neo.rpgsystem.abilities.zone.AbilityNormalDamageZone;
import neo.rpgsystem.abilities.zone.AbilityZoneManager;
import neo.rpgsystemlol.main.DamageU;
import neo.rpgsystemlol.main.Main;
import neo.rpgsystemlol.main.Particles;
import neo.rpgsystemlol.main.U;

public class YordleActive1 extends CostConditionSkills {

    public YordleActive1(String name, String displayName, boolean msg, int mpCost, int hpCost) {
        super("yordleactive1", "§cHỏa Cầu", true, 50, 0);
    }

    @Override
    public void cast(Player p) {
        LivingEntity le = DamageU.getTargetAhead(p, 16, 1);
        if(le == null) return;

        int level = getSkillLevel(p);
        double damage = 80 + 6 * level; // ví dụ damage
        Location location = p.getEyeLocation().add(p.getLocation().getDirection().multiply(1.2));
        Vector direction = p.getLocation().getDirection();

        p.getWorld().playSound(location, Sound.ENTITY_BLAZE_SHOOT, 1.1f, 1f);
        p.sendMessage(Main.ABILITIES_PREFIX + "§6Bạn đã bắn §cHỏa Cầu!");

        AbilitiesBeamManager.register(new HoaCauBeam(location, direction, p, damage, level));
    }

    private static class HoaCauBeam extends MovementAbilitiesBeam {

        private final int level;

        public HoaCauBeam(Location location, Vector direction, Player caster, double damage, int level) {
            super(location, direction, caster, true, true, true, damage, 16, 0.5);
            this.level = level;
        }

        @Override
        protected void particle() {
            // Hình cầu lửa bán kính 1, màu cam đỏ
            Particles.spawnSphereParticles(getLocation(), Particle.DUST_COLOR_TRANSITION, 1.0, 18, Particles.neoParticleColor.ORANGE);
            Particles.spawnParticleColor(getLocation(), Particle.FLAME, Particles.neoParticleColor.ORANGE, 6, 0.6, 0.5, 0.6);
        }

        @Override
        protected void onHit(LivingEntity le) {
            double range = (level >= 7) ? 3.0 : 1.5;
            // Nổ cầu lửa
            le.getWorld().playSound(getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.3f, 0.85f);
            Particles.spawnSphereParticles(getLocation(), Particle.FLAME, range, 24, Particles.neoParticleColor.ORANGE);
            Particles.spawnSphereParticles(getLocation(), Particle.DUST, range, 18, Particles.neoParticleColor.RED);

            List<LivingEntity> targets = DamageU.getTargetsAround(getCaster(), getLocation(), range);
            for(LivingEntity le2 : targets) {
                DamageU.damageSkill(getCaster(), le2, getDamage());
                le2.setFireTicks(20 * 3);
            }
            if(level >= 5) {
            	AbilityZoneManager.register(new HoaNguc(getCaster(), getLocation(), 3));
            }
            if(level >= 7) {
                manhHoa(getCaster(), le);
            }
        }

        // Method tăng hiệu ứng khi lv >= 7 (ví dụ: tăng sát thương/làm chậm/mở rộng hiệu ứng)
        private void manhHoa(Player caster, LivingEntity le) {
            // Hiệu ứng particle và sound đặc biệt
            Particles.spawnSphereParticles(le.getLocation(), Particle.DUST, 1.2, 12, Particles.neoParticleColor.RED);
            le.getWorld().playSound(le.getLocation(), Sound.BLOCK_LAVA_POP, 1.1f, 0.6f);

            // Có thể tăng sát thương hoặc thêm hiệu ứng debuff
            le.setFireTicks(20 * 6); // cháy lâu hơn
            // Ví dụ: làm chậm hoặc thêm hiệu ứng khác nếu muốn
        }
    }
    
    private static class HoaNguc extends AbilityNormalDamageZone {

		public HoaNguc(Player caster, Location center, double damage) {
			super(caster, center, 3, damage, 20*3, 10);
		}
		
		@Override
		public void particle() {
			Particles.circle(getLocation(), Particle.DUST, getRange(), Math.PI/16);
			for(int a = 0; a < 3; a++) {
				Vector v = new Vector(U.random(0.01, 0.1), 0.5, U.random(0.01, 0.1));
				double speed = U.random(0.05, 0.3);
				Particles.movingParticle(getLocation(), Particle.FLAME, v, speed);
			}
		}
		
		@Override
		public void damageParticle() {
			Particles.DoParticle(getLocation(), Particle.SMOKE, 5, getRange(), 0.5, getRange(), 0);
		}
    }

}
