package neo.rpgsystem.abilities.rpgclass.voids;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import neo.rpgsystem.abilities.CooldownSkills;
import neo.rpgsystemlol.main.Main;
import neo.rpgsystemlol.main.Particles;
import neo.rpgsystemlol.main.U;

public class VoidPassive2 extends CooldownSkills {

	public VoidPassive2() {
		// Tên/hiển thị/hiện message
		super("voidpassive2", "Dấu Hư Vô", false);
	}
	
	private static List<DiemYeu> diemYeuList = new ArrayList<>();
	private static List<LivingEntity> cooldownDiemYeu = new ArrayList<LivingEntity>();
	
	@Override
	public void onAttack(Player p, LivingEntity target, double damage) {
		if (isCooldown(p)) return;
		if (target == null) return;

		// Nếu target đang có DiemYeu thì return
		for (DiemYeu dy : diemYeuList) {
			if (dy.target.equals(target)) return;
		}
		// Nếu đang trong cooldown điểm yếu thì return
		if (cooldownDiemYeu.contains(target)) return;
		// Kiểm tra số điểm yếu hiện có do player tạo: nếu >= 3 thì return
		int count = 0;
		for (DiemYeu dy : diemYeuList) {
			if (dy.caster.equals(p)) count++;
		}
		if (count >= 3) return;

		// đặt cooldown cho người chơi, tạo điểm yếu và thêm vào list
		setCooldown(p);
		DiemYeu newDy = new DiemYeu(p, target, damage);
		diemYeuList.add(newDy);

		// Start ticking cho điểm yếu
		new BukkitRunnable() {
			@Override
			public void run() {
				// nếu đã chết hoặc đã vượt thời gian => remove và cancel
				if (newDy.dead || newDy.tick >= DiemYeu.endTime) {
					diemYeuList.remove(newDy);
					this.cancel();
					return;
				}
				newDy.onTick();
			}
		}.runTaskTimer(Main.pl, 0, 1);
	}
	
	@Override
	public double getAddDamage(Player p, LivingEntity target, double damage) {
		if (target == null) return 0;
		// Tìm DiemYeu với target và caster = p
		DiemYeu found = null;
		for (DiemYeu dy : diemYeuList) {
			if (dy.target.equals(target) && dy.caster.equals(p)) {
				found = dy;
				break;
			}
		}
		if (found == null) return 0;
		double extra = found.onAttack();
		// nếu kích hoạt (trả về >0) thì remove khỏi danh sách
		if (extra > 0) {
			diemYeuList.remove(found);
		}
		return extra;
	}
	
	@Override
	public double getCooldown() {
		return 3;
	}
	
	private static class DiemYeu {
		private int tick = 0;
		private Player caster;
		private LivingEntity target;
		private static final int endTime = 20*8;
		private static final int triggerTime = 20*3;
		private double damage;
		private boolean dead = false;
		public DiemYeu(Player caster, LivingEntity target, double damage) {
			this.caster = caster;
			this.target = target;
			this.damage = damage;
		}
		
		private void onTick() {
			tick++;
			if(dead) return;
			if(tick >= endTime) {
				// hết hạn, sẽ được cleanup bởi runnable bên ngoài
				return;
			}
			particle();
		}
		
		private void particle() {
			// r tăng tuyến tính từ 0.1 -> 1.0 khi tick từ 0 -> triggerTime
			double progress = Math.min(1.0, tick / (double) triggerTime);
			double r = 0.1 + progress * (1.0 - 0.1);
			// Dùng màu blue cho hiệu ứng
			Particles.circle(target.getLocation().clone().add(0,1,0), Particle.DUST_COLOR_TRANSITION, r, Math.PI / U.random(8, 10));
		}
		
		public double onAttack() {
			if(dead) return 0;
			if(tick >= triggerTime) {
				dead = true;
				particleTrigger();
				// thêm target vào cooldownDiemYeu để không bị gán lại ngay
				cooldownDiemYeu.add(target);
				new BukkitRunnable() {
					@Override
					public void run() {
						cooldownDiemYeu.remove(target);
					}
				}.runTaskLater(Main.pl, 20*6);
				return damage;
			}
			return 0;
		}
		
		private void particleTrigger() {
			Location l = target.getLocation().clone().add(0,1,0);
			Particles.DoParticle(l, Particle.SOUL_FIRE_FLAME, 10, 0.1, 0.1, 0.1, 0.5);
			Particles.DoParticle(l, Particle.EXPLOSION, 5, 0.2, 0.3, 0.1, 0);
			// sound
			l.getWorld().playSound(l, org.bukkit.Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.8f);
		}
		
	}
	
}