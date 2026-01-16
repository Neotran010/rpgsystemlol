package neo.rpgsystemlol.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Particles {
	
	
	public static class neoParticleColor{

		public static final neoParticleColor RED = new neoParticleColor(255, 0, 0);
		public static final neoParticleColor BLACK = new neoParticleColor(1, 1, 1);
		public static final neoParticleColor YELLOW = new neoParticleColor(255, 255, 0);
		public static final neoParticleColor AQUA = new neoParticleColor(0, 255, 255);
		public static final neoParticleColor PURPLE = new neoParticleColor(128, 0, 128);
		public static final neoParticleColor BLUE = new neoParticleColor(0, 0, 255);
		public static final neoParticleColor DARKBLUE = new neoParticleColor(56, 39, 122);
		public static final neoParticleColor ORANGE = new neoParticleColor(255, 165, 0);
		public static final neoParticleColor GREEN = new neoParticleColor(0, 255, 0);
		public static final neoParticleColor WHITE = new neoParticleColor(255, 255, 255);
		public static final neoParticleColor PINK = new neoParticleColor(255, 192, 203);
		public static final neoParticleColor GRAY = new neoParticleColor(128, 128, 128);
		
		private int r;
		private int g;
		private int b;
		public int getR() {
			return r;
		}
		public int getG() {
			return g;
		}
		public int getB() {
			return b;
		}
		public neoParticleColor(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}
		
		public static neoParticleColor valueOf(String s) {
			switch (s.toUpperCase()) {
			case "RED":
				return RED;
			case "BLACK":
				return BLACK;
			case "YELLOW":
				return YELLOW;
			case "AQUA":
				return AQUA;
			case "PURPLE":
				return PURPLE;
			case "BLUE":
				return BLUE;

			default:
				break;
			}
			return RED;
		}
	}
	
	public static void aura(Player p, Particle particle, int soluongPerTick, neoParticleColor color, int tickDuration) {
		new BukkitRunnable() {
			int c = 0;
			@Override
			public void run() {
				c++;
				if(c >= tickDuration) {
					this.cancel();
					return;
				}
				Location l = p.getLocation().clone().add(0,1,0);
				DoParticle(l, particle, soluongPerTick, 0.5, 1, 0.5, 0);
				spawnParticleColor(l, Particle.DUST, color, soluongPerTick, 0.5, 1, 0.5);
			}
		}.runTaskTimer(Main.pl, 0, 1);
	}
	
	public static void spawnParticleColor(Location loc, Particle particle, neoParticleColor color, int amount, double xOff, double yOff, double zOff) {
		for(int i = 0; i<amount;i++) {
			spawnParticleColor(loc.clone().add(U.random(-xOff, xOff),U.random(-yOff, yOff),U.random(-zOff, zOff)), particle, color);
		}
	}
	
	
	public static void hoitu(Location l, Particle particle, double r) {
		Location loc = l.clone().add(U.randomD(-r, r), U.randomD(-1, r/2), U.randomD(-r, r));
		Vector v = l.clone().subtract(loc).toVector().normalize();
		double distance = loc.clone().distance(l);
		int livingTime = 1*20;
		double speed = distance/livingTime;
		Particles.movingParticle(loc, particle, v, speed);
	}
	
	public static void spawnSphereParticles(Location center, Particle particle, double radius, int amount) {
	    Random random = new Random();

	    for (int i = 0; i < amount; i++) {
	        double angle1 = Math.PI * 2 * random.nextDouble();
	        double angle2 = Math.PI * 2 * random.nextDouble();
	        double x = radius * Math.sin(angle1) * Math.cos(angle2);
	        double y = radius * Math.sin(angle1) * Math.sin(angle2);
	        double z = radius * Math.cos(angle1);
	        Location particleLoc = center.clone().add(x, y, z);
	        DoParticle(particleLoc, particle, 1);
	    }
	}

	public static void spawnSphereParticles(Location center, Particle particle, double radius, int amount, neoParticleColor color) {
	    Random random = new Random();

	    for (int i = 0; i < amount; i++) {
	        double angle1 = Math.PI * 2 * random.nextDouble();
	        double angle2 = Math.PI * 2 * random.nextDouble();
	        double x = radius * Math.sin(angle1) * Math.cos(angle2);
	        double y = radius * Math.sin(angle1) * Math.sin(angle2);
	        double z = radius * Math.cos(angle1);
	        Location particleLoc = center.clone().add(x, y, z);
	        spawnParticleColor(particleLoc, particle, color);
	    }
	}
	
	public static void sphereWithDirection(Location l, Particle particle, double speed) {
		double r = 1;
		for(double p = 0; p<=Math.PI; p+=Math.PI/6)
		for(double d = 0; d < Math.PI*2; d+=Math.PI/32) {
			for(int i = 0; i < 3; i++) {
				double addD = U.randomD(-Math.PI/16, Math.PI/16);
				if(i == 0) addD = 0;

				double x = r*Math.cos(d+addD)*Math.sin(p+addD);
				double y = r*Math.cos(p+addD);
				double z = r*Math.sin(d+addD)*Math.sin(p+addD);
			
				Location loc = l.clone().add(x,y,z);
				Vector v = loc.clone().subtract(l).toVector().normalize().multiply(-1);
				movingParticle(loc, particle, v, speed);
			}
		}
	}
	
	public static void star(Location l, Particle particle, double phiPerTime, double startRange, double maxRange, double addRangePerTime, int tick) {
		new BukkitRunnable() {
			double limitRange = tick == -1 ? limitRange=maxRange : startRange;
			double currentRange = 0;
			@Override
			public void run() {
				currentRange+=addRangePerTime;
				if(currentRange > maxRange || limitRange > maxRange) {
					this.cancel();
					return;
				}
				
				for(double d = startRange; d<limitRange; d+=addRangePerTime) {
					circle(l, particle, d, phiPerTime);
				}
				limitRange+=addRangePerTime;
			}
		}.runTaskTimer(Main.pl, 0, tick);
	}

	public static void circleVec(Particle particle, Location l, Vector direction,
			double range, double dpt) {
		Location loc = l.clone().setDirection(direction);
		for (double t = 0.0D; t <= 6.283185307179586D; t += dpt) {
			double x = cos(t) * range;
			double y = 0.0D;
			double z = sin(t) * range;
			Vector v = rotateFunction(new Vector(x, y, z), loc.clone());
			Location loc2 = loc.clone().add(v).add(0,1,0);
			DoParticle(loc2, particle, 1);
		}
	}
	
	/**
	 * Trả về một Location ngẫu nhiên trên nửa đường tròn (bán kính range, rotate theo vector v, chỉ lấy điểm có y mới > y gốc)
	 * @param l điểm gốc (Location)
	 * @param v vector hướng
	 * @param range bán kính đường tròn
	 * @return Location ngẫu nhiên phía trên nửa đường tròn
	 */
	public static Location getRandomCircleHalfUpLocation(Location l, Vector v, double range) {
	    Location base = l.clone().setDirection(v);
	    double yBase = base.getY();

	    // Chia nửa đường tròn: chỉ lấy góc từ 0 đến PI (0 -> 180 độ)
	    int step = 24; // số điểm trên nửa đường tròn, càng lớn càng mịn
	    double dpt = Math.PI / step;

	    List<Location> validLocations = new ArrayList<>();

	    for (double t = 0.0D; t <= Math.PI; t += dpt) {
	        double x = Math.cos(t) * range;
	        double y = 0.0D;
	        double z = Math.sin(t) * range;
	        Vector rotated = rotateFunction(new Vector(x, y, z), base);
	        Location loc2 = base.clone().add(rotated);
	        if (loc2.getY() > yBase) {
	            validLocations.add(loc2);
	        }
	    }
	    if (validLocations.isEmpty()) return base;
	    return validLocations.get(U.getRandom(0, validLocations.size() - 1));
	}
	
	public static void line(Location start, Location end, Particle particle, double xOff, double yOff, double zOff, double speed, int amount, double distancePerTime) {
		Vector v = end.clone().subtract(start).toVector().normalize();
		double dis = start.clone().distance(end);
		for(double d = 0; d<dis;d+=distancePerTime) {
			Location l = start.clone().add(v.clone().multiply(d));
			DoParticle(l, particle, amount, xOff, yOff, zOff, speed);
		}
	}
	
	public static void circleBigger(Location l, Particle particle, 
			double startRange, double endRange, double addRangePerTime,
			double phi, int tick, Sound s) {
		new BukkitRunnable() {
			double currentRange = startRange;
			@Override
			public void run() {
				currentRange+=addRangePerTime;
				if(currentRange > endRange) {
					this.cancel();
					return;
				}
				
				if(s != null) l.getWorld().playSound(l, s, 16, 16);
				circle(l, particle, currentRange, phi);
			}
		}.runTaskTimer(Main.pl, 0, tick);
	}
	
	public static void line(Location start, Location end, Particle particle, neoParticleColor color, double distancePerTime) {
		Vector v = end.clone().subtract(start).toVector().normalize();
		double distance = end.clone().distance(start);
		for(double d = 0; d <= distance; d+=distancePerTime) {
			spawnParticleColor(start.clone().add(v.clone().multiply(distancePerTime)), particle, color);
		}
	}
	
	public static void spawnParticleColor(Location loc, Particle particle, neoParticleColor color) {
		if(color == null) {
			DoParticle(loc, particle, 1);
			return;
		}
		if(particle.equals(Particle.DUST)) {
//			DustOptions dust = new DustOptions(color.getB(), color.getG(), color.getR(), 1);
			for(Player p : loc.getWorld().getPlayers()) {
				p.spawnParticle(particle, loc.getX(), loc.getY(), loc.getZ(), 0, (double)color.getR()/256
						, (double)color.getG()/256, (double)color.getB()/256, 1);
			}
		}else {
			for(Player p : loc.getWorld().getPlayers()){
				p.spawnParticle(particle, loc.getX(), loc.getY(), loc.getZ(), 0, (double)color.getR()/256
					, (double)color.getG()/256, (double)color.getB()/256, 1);	
			}
		}
	}
	
	public static void movingParticle(Location loc, Particle particle, Vector v, double speed) {
		loc.getWorld().spawnParticle(particle, loc.getX(), loc.getY(), loc.getZ(), 0, v.getX()
				, v.getY(), v.getZ(), speed);	
	}
	
	/**
	 * 
	 * @param phiPerTime thường là Pi/64
	 */
	public static void circle(Location l, Particle particle, double range, double phiPerTime) {
		for(double d = 0 ;d<Math.PI*2; d+=phiPerTime) {
			DoParticle(l.clone().add(Math.sin(d)*range, 0 , Math.cos(d)*range), particle, 1);
		}
	}
	
	public static void circle(Location l, Particle particle, double range, double phiPerTime, neoParticleColor color) {
		for(double d = 0 ;d<Math.PI*2; d+=phiPerTime) {
			spawnParticleColor(l.clone().add(Math.sin(d)*range, 0 , Math.cos(d)*range), particle, color);
		}
	}
	

	/*     */ public static void getChemNghieng1(Particle particle, Location l,
			Vector direction, neoParticleColor color, double phi) {
		Location loc = l.clone();
		/* 164 */ loc.setDirection(direction);
		double pphi = phi;
		if(pphi <= 0) pphi=0.09817477042468103D;
		/* 167 */ for (double t = -1.2762720155208536D; t <= 1.2762720155208536D; t += pphi) {
			/*     */
			/* 169 */ double x = Math.sin(t) * 1.0D;
			/* 170 */ double y = Math.cos(t) * 2.75D;
			/* 171 */ double z = Math.sin(t) * 1.0D;
			Location l2 = l.clone();
			/* 172 */ Vector v = rotateFunction(new Vector(x, y, z), l2.clone());
			spawnParticleColor(loc.clone().add(v).add(0,1,0), particle, color);;
			/*     */ }
		/*     */ }
	/*     */ private static Vector rotateY(Vector v, double a) {
		/* 356 */ double x = cos(a) * v.getX() + sin(a) * v.getZ();
		/* 357 */ double z = -sin(a) * v.getX() + cos(a) * v.getZ();
		/* 358 */ return v.setX(x).setZ(z);
		/*     */ }

	/*     */
	/*     */ private static Vector rotateZ(Vector v, double a) {
		/* 362 */ double x = cos(a) * v.getX() - sin(a) * v.getY();
		/* 363 */ double y = sin(a) * v.getX() + cos(a) * v.getY();
		/* 364 */ return v.setX(x).setY(y);
		/*     */ }

	/*     */
	/*     */ public static Vector rotateFunction(Vector v, Location l) {
		/* 368 */ double yawR = ((l.getYaw() + 90.0F) / 180.0F) * Math.PI;
		/* 369 */ double pitchR = ((l.getPitch() + 90.0F) / 180.0F) * Math.PI;
		/* 370 */ v = rotateZ(v, -pitchR);
		/* 371 */ v = rotateY(v, -yawR);
		/* 372 */ return v;
		/*     */ }
	/*     */ private static double sin(double a) {
		/* 376 */ return Math.sin(a);
		/*     */ }

	/*     */ private static double cos(double a) {
		/* 379 */ return Math.cos(a);
		/*     */ }

	public static void DoParticle(Location l, Particle particle, int soluong) {
		DoParticle(l, particle, soluong, 0, 0, 0, 0);
	 }
	
	public static void DoParticle(Location l, Particle particle, int soluong, double xoff, double yoff,
			double zoff, double speed) {
		//Particle particle, double x, double y, double z,
		//int count, double offsetX, double offsetY, double offsetZ
		if(particle.equals(Particle.DUST)) {
			Location loc = l.clone().add(U.randomD(-xoff, xoff), U.randomD(-yoff, yoff), U.randomD(-zoff, zoff));
			for(int i = 0; i<soluong; i++)
				spawnParticleColor(loc, particle, neoParticleColor.RED);
			return;
		}
		for(Player p : l.getWorld().getPlayers()) {
			p.spawnParticle(particle, l.getX(), l.getY(), l.getZ(),
				soluong, xoff, yoff, zoff, speed);
			}
		}
	

	/*     */ public static void doChemNgang(Particle enumParticle, Location l,
			Vector direction, double rangez, double rangey) {
		Location loc = l.clone();
		/* 214 */ loc.setDirection(direction);
		/* 216 */ int d = 0;
		/* 217 */ for (double t = -1.2762720155208536D; t <= 1.2762720155208536D; t += 0.09817477042468103D) {
			/*     */
			/* 219 */ double x = Math.sin(t) * 1.0D * 0.0D;
			/* 220 */ double y = Math.cos(t) * rangey;
			/* 221 */ double z = Math.sin(t) * rangez;
			/* 222 */ Location l2 = loc.clone();
			/* 223 */ Vector v = rotateFunction(new Vector(x, y, z), l2.clone());
			DoParticle(loc.clone().add(v).add(0.0D, 1.0D, 0.0D), enumParticle, d);
			/* 225 */ d++;
			/*     */ }
		/*     */ }
	
	public static void fallingBlock(Location l, Vector v) {
        
	}

	public static Location getCenterBlock(Block b) {
		Location l = b.getLocation().clone();
//		double x = l.getX();
//		double z = l.getZ();
//		return l.clone().add(x > 0 ? 0.5 : -0.5, 0.0, z > 0 ? 0.5 : -0.5);
		return l.clone().add(0.5,0,0.5);
	}
	
	public static Location getCircleLocation(Location l, Vector dir, double phi, double range) {
		Location loc = l.clone().setDirection(dir);
		double x = cos(phi) * range;
		double y = 0.0D;
		double z = sin(phi) * range;
		Vector v = rotateFunction(new Vector(x, y, z), loc.clone());
		Location loc2 = loc.clone().add(v);
		return loc2;
	}
	
	public static void hoiTuSingle(Location l, Particle particle, double range, double yMin, double yMax, int amount) {
		for(int i = 0; i<amount; i++) {
			Location loc = l.clone().add(U.random(-range, range), U.random(yMin, yMax), U.random(-range, range));
			Vector v = l.clone().subtract(loc).toVector().normalize();
			double distance = loc.clone().distance(l);
			int livingTime = 1*20;
			double speed = distance/livingTime;
			Particles.movingParticle(loc, particle, v, speed);
		}
	}
}
