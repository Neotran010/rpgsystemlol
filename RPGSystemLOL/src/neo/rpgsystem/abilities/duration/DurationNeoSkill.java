package neo.rpgsystem.abilities.duration;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Particle;
import org.bukkit.entity.Player;

import neo.rpgsystem.abilities.CooldownSkills;
import neo.rpgsystemlol.main.Particles;
import neo.rpgsystemlol.main.Particles.neoParticleColor;

public class DurationNeoSkill extends CooldownSkills {
	
	protected static List<DurationData> durationList = new ArrayList<>();
	
	protected static class DurationData {
		protected Player caster;
		private int timeLeft;
		private neoParticleColor color;
		protected boolean end = false;
		
		public DurationData(Player caster, int duration, neoParticleColor color) {
			super();
			this.caster = caster;
			this.timeLeft = duration;
			this.color = color;
			onStart();
		}
		
		protected void onStart() {
			
		}

		protected void onTick() {
			if(end) return;
			timeLeft--;
			if(timeLeft <= 0) {
				end = true;
				onEnd();
				return;
			}
			particle();
		}
		
		protected void particle() {
			Particles.spawnParticleColor(caster.getLocation().clone().add(0,1,0), Particle.DUST, color, 5, 0.5, 1, 0.5);
		}
		
		protected void onEnd() {
			
		}
		
		protected void setTimeLeft(int timeLeft) {
			this.timeLeft = timeLeft;
		}
		
		public int getTimeLeft() {
			return timeLeft;
		}

		public Player getCaster() {
			return caster;
		}

		public neoParticleColor getColor() {
			return color;
		}

		public boolean isEnd() {
			return end;
		}
	}
	
	public void startDuration(Player caster) {
		durationList.add(createDuration(caster));
	}
	
	protected DurationData createDuration(Player p) {
		return new DurationData(p, getDuration(1), neoParticleColor.AQUA);
	}
	
	protected int getDuration(int level) {
		return duration;
	}
	
	private int duration;

	public DurationNeoSkill(String name, String displayName, boolean msg, int duration) {
		super(name, displayName, msg);
		this.duration = duration;
	}
	
	public DurationData getDurationData(Player p) {
	    for (DurationData dt : durationList) {
	        if (dt.caster.equals(p) && !dt.end) {
	            return dt;
	        }
	    }
	    return null;
	}
	
	@Override
	public void onTick() {
		for(DurationData dt : new ArrayList<>(durationList)) {
			dt.onTick();
			if(dt.end) durationList.remove(dt);
		}
		super.onTick();
	}

}
