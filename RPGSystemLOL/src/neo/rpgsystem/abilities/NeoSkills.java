package neo.rpgsystem.abilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import neo.rpgsystem.abilities.NeoSkills.DurationSkills.DurationOnAttack;
import neo.rpgsystemlol.main.Main;

public class NeoSkills {
	
	private static List<NeoSkill> registeredNeoSkills;
	
	private static List<DurationSkills> durationSkills = new ArrayList<NeoSkills.DurationSkills>();
	
	public static void setup() {
	    // boost
	    registeredNeoSkills = new ArrayList<NeoSkill>();
	}

	
	public static NeoSkill get(String name) {
	    return registeredNeoSkills.stream()
	            .filter(neoSkill -> neoSkill.getName().equals(name))
	            .findFirst()
	            .orElse(null);
	}
	
	public static void castDurationSkills(Player caster, LivingEntity target, int duration, double damage, int tickPerDamage, int tickParticle, 
			DurationOnAttack onAttack, DurationParticle particle) {
		durationSkills.add(new DurationSkills(duration, caster, Arrays.asList(target), damage, tickPerDamage, tickParticle, onAttack, particle));
	}
	
	public static class DurationSkills {
		private int timeLeft;
		private Player caster;
		private List<LivingEntity> targets;
		private double damage;
		private int tickPerDamage;
		private int tickParticle;
		private DurationOnAttack onAttack;
		private DurationParticle particle;
		public boolean end = false;
		
		private int localTickParticle = 0;
		private int localTickDamage = 0;
		public DurationSkills(int timeLeft, Player caster, List<LivingEntity> targets, double damage, int tickPerDamage,
				int tickParticle, DurationOnAttack onAttack, DurationParticle particle) {
			super();
			this.timeLeft = timeLeft;
			this.caster = caster;
			this.targets = targets;
			this.damage = damage;
			this.tickPerDamage = tickPerDamage;
			this.tickParticle = tickParticle;
			this.onAttack = onAttack;
		}
		
		public void onTick() {
			if(end) return;
			timeLeft--;
			if(timeLeft <= 0) {
				end = true;
				return;
			}
			localTickParticle++;
			localTickDamage++;
			if(localTickParticle >= tickParticle) {
				localTickParticle = 0;
				particle();
			}
			
			if(localTickDamage >= tickPerDamage) {
				localTickDamage = 0;
				damage();
			}
		}
		
		public void damage() {
			if(targets.size() == 1)
			onAttack.attack(caster, targets.get(0), damage);
		}
		
		public void particle() {
			particle.particle(targets.get(0).getLocation().clone());
		}
		public static interface DurationOnAttack {
			public void attack(Player caster, LivingEntity le, double damage);
		}
		
		
	}
	public static interface DurationParticle {
		public void particle(Location l);
	}
	
	static {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				for(DurationSkills ds : new ArrayList<>(durationSkills)) {
					ds.onTick();
					if(ds.end) {
						durationSkills.remove(ds);
					}
				}
			}
		}.runTaskTimer(Main.pl, 0, 1);
	}

}
