package neo.rpgsystem.abilities.stackable;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import neo.rpgsystem.abilities.CooldownSkills;
import neo.rpgsystemlol.main.Main;
import neo.rpgsystemlol.main.U;

public class StackableNeoSkill extends CooldownSkills {
	
	private int maxStack;
	private HashMap<Player, StackData> stack = new HashMap<Player, StackData>();
	
	public static class StackData {
		private int currentStack = 0;
		private long nextAdd = 0;
		private long end = 0;
		public StackData() {
			super();
		}
		
		public boolean onCD() {
			return System.currentTimeMillis() < nextAdd;
		}
		
		public void cd() {
			nextAdd = System.currentTimeMillis()+100;
		}
		
		public void onTick(Player p) {
			
		}
		
		public boolean isMaxStack() {
			return currentStack >= 10;
		}
		
	}

	public StackableNeoSkill(String name, String displayName, boolean msg, int maxStack) {
		super(name, displayName, msg);
		this.maxStack = maxStack;
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player p : new ArrayList<Player>(stack.keySet())) {
					StackData dt = stack.get(p);
					if(dt.end < System.currentTimeMillis()) {
						stack.remove(p);
					}
				}
			}
		}.runTaskTimer(Main.pl, 0, 1);
	}
	
	public void addStack(Player p) {
		if(!stack.containsKey(p)) {
			putStack(p);
		}
		StackData dt = stack.get(p);
		if(dt.onCD()) return;
		if(dt.currentStack >= maxStack) return;
		dt.cd();
		if(dt.currentStack+1 >= maxStack) {
			onMaxStack(p);
		}
		dt.currentStack = Math.min(maxStack, dt.currentStack+1);
	}
	public void putStack(Player p) {
		stack.put(p, createStackData());
	}
	
	public StackData createStackData() {
		return new StackData();
	}
	
	public boolean isMaxStack(Player p) {
		if(!stack.containsKey(p)) return false;
		StackData dt = stack.get(p);
		return dt.currentStack >= maxStack;
	}
	
	public int getCurrentStack(Player p) {
		if(!stack.containsKey(p)) return 0;
		return stack.get(p).currentStack;
	}
	
	protected void onMaxStack(Player p) {
		U.playSound(p, Sound.ENTITY_FIREWORK_ROCKET_BLAST);
	}
	
	@Override
	public void onTick() {
		for(Player p : new ArrayList<>(stack.keySet())) {
			if(p.isDead() || !p.isOnline()) {
				stack.remove(p);
				continue;
			}
			stack.get(p).onTick(p);
		}
		super.onTick();
	}
	
	public int getMaxStack() {
		return maxStack;
	}
}
