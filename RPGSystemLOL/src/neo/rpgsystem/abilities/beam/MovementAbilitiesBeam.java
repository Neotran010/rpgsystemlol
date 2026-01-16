package neo.rpgsystem.abilities.beam;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import neo.rpgsystemlol.main.DamageU;
import neo.rpgsystemlol.main.Particles;
import neo.rpgsystemlol.main.U;

public abstract class MovementAbilitiesBeam implements AbilitiesBeam {
	
	private static final int MAX_LIVETIME = 20*60;
	
	private boolean delete = false;
	private int liveTime = 0;
	private double rangeHit = 0.5; //bán kính trúng của beam
	private double speed = 0.1; //tốc độ theo tick
	private double currentDistance = 0;
	
	private Location location;
	private Vector direction; //hướng bắn
	private Player caster;
	private boolean damageOnPath; //damage trên đường đi của beam
	private boolean stopOnFirstHit; //dừng khi trúng bất kì LE nào
	private boolean stopOnFirstHitPlayer; //dừng khi trúng player
	private double damage;
	private double maxDistance;
	
	public MovementAbilitiesBeam(Location location, Vector direction, Player caster, double damage,
			double maxDistance, double speed) {
		super();
		this.speed = speed;
		this.location = location;
		this.direction = direction;
		this.caster = caster;
		this.damage = damage;
		this.maxDistance = maxDistance;
		this.damageOnPath = true;
		this.stopOnFirstHit = false;
		this.stopOnFirstHitPlayer = false;
		onStart();
	}

	public MovementAbilitiesBeam(Location location, Vector direction, Player caster, boolean damageOnPath,
			boolean stopOnFirstHit, boolean stopOnFirstHitPlayer, double damage, double maxDistance, double speed) {
		super();
		this.speed = speed;
		this.location = location;
		this.direction = direction;
		this.caster = caster;
		this.damageOnPath = damageOnPath;
		this.stopOnFirstHit = stopOnFirstHit;
		this.stopOnFirstHitPlayer = stopOnFirstHitPlayer;
		this.damage = damage;
		this.maxDistance = maxDistance;
		onStart();
	}

	private List<LivingEntity> hits = new ArrayList<LivingEntity>();

	@Override
	public void onTick() {
		if(delete) return;
		liveTime++;
		if(liveTime > MAX_LIVETIME) {
			delete();
			return;
		}
		
		move();
		particle();
		if(damageOnPath)
		hit();
		if(currentDistance >= maxDistance) {
			delete();
			onEnd();
		}
	}
	
	protected void onEnd() {
		
	}

	protected void hit() {
		List<LivingEntity> targets = DamageU.getTargetsAround(caster, getLocation(), rangeHit, hits);
		if(!targets.isEmpty()) {
			for(LivingEntity le : targets) {
				hits.add(le);
				onHit(le);
				if(stopOnFirstHit) delete();
				if(stopOnFirstHitPlayer && le instanceof Player) delete();
			}
		}
	}
	
	protected void particle() {
		Particles.DoParticle(getLocation(), Particle.FLAME, 1);
	}
	
	protected void onHit(LivingEntity le) {
		DamageU.damageSkill(caster, le, damage);
	}
	
	protected void particleHit() {
		
	}
	
	protected void onStart() {
		U.playSound(getLocation(), Sound.ENTITY_ARROW_SHOOT);
	}
	
	protected void move() {
		currentDistance+=speed;
		Location localLocation = getLocation();
		Location newLocation = localLocation.clone().add(getDirection().clone().normalize().multiply(speed));
		location = newLocation;
	}
	
	public Vector getDirection() {
		return direction;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public Player getCaster() {
		return caster;
	}

	@Override
	public void delete() {
		delete = true;
	}
	
	@Override
	public boolean isAlive() {
		return !delete;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public double getRangeHit() {
		return rangeHit;
	}

	public void setRangeHit(double rangeHit) {
		this.rangeHit = rangeHit;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getCurrentDistance() {
		return currentDistance;
	}

	public void setCurrentDistance(double currentDistance) {
		this.currentDistance = currentDistance;
	}

	public boolean isDamageOnPath() {
		return damageOnPath;
	}

	public void setDamageOnPath(boolean damageOnPath) {
		this.damageOnPath = damageOnPath;
	}

	public boolean isStopOnFirstHit() {
		return stopOnFirstHit;
	}

	public void setStopOnFirstHit(boolean stopOnFirstHit) {
		this.stopOnFirstHit = stopOnFirstHit;
	}

	public boolean isStopOnFirstHitPlayer() {
		return stopOnFirstHitPlayer;
	}

	public void setStopOnFirstHitPlayer(boolean stopOnFirstHitPlayer) {
		this.stopOnFirstHitPlayer = stopOnFirstHitPlayer;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public double getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(double maxDistance) {
		this.maxDistance = maxDistance;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setDirection(Vector direction) {
		this.direction = direction;
	}

}
