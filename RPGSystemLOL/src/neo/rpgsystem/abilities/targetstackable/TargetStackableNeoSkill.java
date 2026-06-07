package neo.rpgsystem.abilities.targetstackable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import neo.rpgsystem.abilities.CooldownSkills;
import neo.rpgsystemlol.main.Main;
import neo.rpgsystemlol.main.Particles;
import neo.rpgsystemlol.main.Particles.neoParticleColor;
import neo.rpgsystemlol.main.U;

public class TargetStackableNeoSkill extends CooldownSkills {

    private int maxStack;
    private boolean onlyOneTarget;
    public HashMap<LivingEntity, StackData> stack = new HashMap<>();
    public HashMap<LivingEntity, Long> cdTargetStack = new HashMap<LivingEntity, Long>(); //cd này sẽ làm cho target không cộng dồn dc stack

    public static class StackData {
        private int currentStack = 0;
        public LivingEntity target;
        private Player caster;
        private long nextAdd = 0;
        private long end = 0;

        public StackData(Player caster, LivingEntity target) {
            this.caster = caster;
            this.target = target;
            this.end = System.currentTimeMillis() + 10000; // default 10s duration, can be adjusted as needed
        }

        public boolean onCD() {
            return System.currentTimeMillis() < nextAdd;
        }

        public void cd() {
            nextAdd = System.currentTimeMillis() + 100;
        }

        public LivingEntity getTarget() {
            return target;
        }

        public Player getCaster() {
            return caster;
        }

        public void setEnd(long end) {
            this.end = end;
        }

        public int getCurrentStack() {
            return currentStack;
        }

        public void setCurrentStack(int currentStack) {
            this.currentStack = currentStack;
        }
    }

    public TargetStackableNeoSkill(String name, String displayName, boolean msg, int maxStack, boolean onlyOneTarget, neoParticleColor color) {
        super(name, displayName, msg);
        this.maxStack = maxStack;
        this.onlyOneTarget = onlyOneTarget;
        new BukkitRunnable() {
            int c = 0;

            @Override
            public void run() {
                c++;
                if (c > 5) c = 0;
                for (LivingEntity p : new ArrayList<>(stack.keySet())) {
                    // Remove if target is dead or invalid
                    if (p == null || !p.isValid() || p.isDead()) {
                        stack.remove(p);
                        continue;
                    }
                    StackData dt = stack.get(p);
                    // Remove if caster offline or dead
                    if (dt == null || dt.caster == null || !dt.caster.isOnline() || dt.caster.isDead()) {
                        stack.remove(p);
                        continue;
                    }
                    if (dt.end < System.currentTimeMillis()) {
                        stack.remove(p);
                        continue;
                    }
                    if (c >= 5) {
                        double d = 0;
                        for (int i = 0; i < Math.min(dt.currentStack, 3); i++) {
                            Particles.spawnParticleColor(Particles.getCircleLocation(p.getLocation().clone().add(0, 1, 0), new Vector(0,1,0), d, 1), Particle.DUST, color);
                            d += Math.PI * 2 / 3;
                        }
                    }
                }
            }
        }.runTaskTimer(Main.pl, 0, 1);
    }

    public void addStack(Player p, LivingEntity target) {
        if (p == null || target == null || !p.isOnline() || p.isDead() || !target.isValid() || target.isDead()) return;

        StackData dt;
        if (stack.containsKey(target)) {
            dt = stack.get(target);
            if (dt == null) return;
            if (dt.onCD()) return;
            if (onlyOneTarget) {
                // Remove from other targets if the player is caster elsewhere
                for (LivingEntity le : new ArrayList<>(stack.keySet())) {
                    StackData data = stack.get(le);
                    if (data != null && data.caster != null && data.caster.equals(p) && !le.equals(target)) {
                        stack.remove(le);
                    }
                }
            }
            dt.caster = p;
        } else {
            if (onlyOneTarget) {
                for (LivingEntity le : new ArrayList<>(stack.keySet())) {
                    StackData data = stack.get(le);
                    if (data != null && data.caster != null && data.caster.equals(p)) {
                        stack.remove(le);
                    }
                }
            }
            dt = new StackData(p, target);
            stack.put(target, dt);
        }
        if (dt.onCD()) return;
        if (dt.currentStack >= maxStack) return;
        dt.cd();
        if (dt.currentStack + 1 >= maxStack) {
            onMaxStack(p, target);
        }
        dt.currentStack = Math.min(maxStack, dt.currentStack + 1);
        dt.setEnd(System.currentTimeMillis() + 10000); // Reset duration on stack (example: 10s)
    }

    public List<LivingEntity> getTargetsByPlayer(Player p) {
        List<LivingEntity> targets = new ArrayList<>();
        if (p == null || !p.isOnline() || p.isDead()) return targets;
        for (LivingEntity le : new ArrayList<>(stack.keySet())) {
            StackData dt = stack.get(le);
            if (dt != null && dt.caster != null && dt.caster.equals(p)) {
                if (le != null && le.isValid() && !le.isDead()) {
                    targets.add(le);
                }
            }
        }
        return targets;
    }

    public void putStack(Player p, LivingEntity target) {
        if (p == null || target == null || !p.isOnline() || p.isDead() || !target.isValid() || target.isDead()) return;
        stack.put(target, createStackData(p, target));
    }

    public StackData createStackData(Player caster, LivingEntity le) {
        return new StackData(caster, le);
    }

    public boolean isMaxStack(LivingEntity le) {
        return getCurrentStack(le) >= maxStack;
    }

    public int getCurrentStack(LivingEntity le) {
        StackData dt = stack.get(le);
        return (dt != null ? dt.getCurrentStack() : 0);
    }

    public StackData getStackData(LivingEntity le) {
        StackData dt = stack.get(le);
        if (dt == null) return null;
        // Remove invalid/dead target or offline/dead caster
        if (le == null || !le.isValid() || le.isDead() || dt.caster == null || !dt.caster.isOnline() || dt.caster.isDead()) {
            stack.remove(le);
            return null;
        }
        return dt;
    }

    protected void onMaxStack(Player p, LivingEntity target) {
        if (p != null && p.isOnline() && !p.isDead())
            U.playSound(p, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH);
        // You can add additional logic for max stack here, involving both player and target
    }

    public void removeStack(LivingEntity target) {
        stack.remove(target);
    }
    
    public boolean isCooldownTarget(LivingEntity le) {
    	return cdTargetStack.containsKey(le);
    }
    
    /**
     * 
     * @param cd theo giây
     */
    public void setCooldownTarget(LivingEntity le, double cd) {
    	if(!isCooldownTarget(le)) {
    		cdTargetStack.put(le, System.currentTimeMillis() + ((long)(1000*cd)));
    	}
    }
    
    @Override
    public void onTick() {
    	for(LivingEntity le : new ArrayList<>(cdTargetStack.keySet())) {
    		if(System.currentTimeMillis() > cdTargetStack.get(le)) {
    			cdTargetStack.remove(le);
    		}
    	}
    	super.onTick();
    }
}