package neo.rpgsystem.abilities.rpgclass.darkin;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import neo.rpgsystem.abilities.CooldownSkills;
import neo.rpgsystemlol.main.Main;
import neo.rpgsystemlol.main.Particles;
import neo.rpgsystemlol.main.U;
import neo.rpgsystemlol.main.Particles.neoParticleColor;

public class DarkinPassive1 extends CooldownSkills {

	public DarkinPassive1() {
		super("darkinpassive1", "Lưỡi Kiếm Của Quỷ", false);
	}
	
	@Override
	public void onAttack(Player p, LivingEntity target, double damage) {
		if(isCooldown(p)) return;
		if(U.random(0, 100) < 50) {
			setCooldown(p);
			
			Location l = target.getLocation().clone();
			particle(l);
			damage(l, damage);
		}
	}

	private void damage(Location l, double damage) {
		// TODO Auto-generated method stub
		
	}

	private void particle(Location l) {
		for(double r = 0; r < 6; r+=0.5) {
			for(double phi = 0; phi < Math.PI*2; phi+=Math.PI/6) {
				Location loc = Particles.getCircleLocation(l, new Vector(0,1,0), phi, r+U.random(-0.25, 0.25));
				Particles.spawnParticleColor(loc, Particle.DUST_COLOR_TRANSITION, neoParticleColor.RED, 3, 0.1, 0, 0.1);
			}
		}
		//TODO sound
		new BukkitRunnable() {
			double d = 0;
			@Override
			public void run() {
				d+=Math.PI/2;
				double startD = d-Math.PI/2;
				for(double phi = startD; phi<d;phi+=Math.PI/16) {
					Location loc = Particles.getCircleLocation(l, new Vector(0,1,0), phi, 1.5);
					Particles.spawnParticleColor(loc, Particle.DUST_COLOR_TRANSITION, neoParticleColor.RED);
				}
			}
		}.runTaskTimer(Main.pl, 0, 1);
	}

}
