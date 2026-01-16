package neo.rpgsystem.abilities.rpgclass.voids;

import java.util.ArrayList;
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
import neo.rpgsystemlol.main.U;

public class VoidActive1 extends CostConditionSkills {

	public VoidActive1() {
		//TODO bổ sung biến
		super("voidactive1", "Lưỡi Đao Hư Vô", true, 50, 0);
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
	                delete();
	                onEnd();
	            }
	        }

	        @Override
	        protected void onEnd() {
	            if (boundLeft > 0) {
	                // Tìm mục tiêu mới quanh target hiện tại
	                List<LivingEntity> candidates = new ArrayList<>();
	                boolean reHit = level >= 5;
	                for (LivingEntity entity : target.getWorld().getLivingEntities()) {
	                    if (entity.equals(getCaster())) continue;
	                    if (entity.equals(target)) continue; // Không nảy lại chính mình trong lần này, nhưng có thể nảy lại các mục tiêu cũ
	                    if (entity.getLocation().distance(target.getLocation()) <= 4) {
	                        candidates.add(entity);
	                    }
	                }
	                if(reHit && candidates.size() <= 0) candidates.add(target);
	                // Chọn tối đa 3 mục tiêu (số nảy còn lại)
	                if(candidates.size() > 0) {
	                    LivingEntity nextTarget = candidates.get(U.getRandom(0, candidates.size()-1));
	                    NguyenRuaBullet nextBl = new NguyenRuaBullet(target.getLocation().clone().add(0, 1, 0), getCaster(), getDamage(), nextTarget, boundLeft - 1, level, hitEntities);
	                    AbilitiesBeamManager.register(nextBl);
	                }
	            }else {
	            	if(level >= 7) {
	            		//Bắn ra lưỡi dao gây sát thương diện rộng
	            	}
	            }
	        }
	    }

	    public static double getDamage(int level) {
	        if (level <= 0) return 0;
	        return 120 + level * 3;
	    }

}
