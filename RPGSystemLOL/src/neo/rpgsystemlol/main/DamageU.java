package neo.rpgsystemlol.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class DamageU {
	
	public static List<LivingEntity> getTargetsAround(Player p, Location l, double range){
		return getTargetsAround(p, l, range, null);
	}
	
	public static List<LivingEntity> getTargetsAround(Player p, Location l, double range, List<LivingEntity> sub) {
	    List<LivingEntity> list = new ArrayList<LivingEntity>();
	    for (Entity en : l.getWorld().getNearbyEntities(l, range, range, range)) {
	        if (en instanceof LivingEntity) {
	            LivingEntity le = (LivingEntity) en;
	            if (sub != null && sub.contains(le)) {
	                continue; // Nếu en có trong sub thì bỏ qua
	            }
	            if (isTarget(p, le)) {
	                list.add(le);
	            }
	        }
	    }
	    return list;
	}
	
	public static boolean isTarget(Player p, LivingEntity le) {
		//TODO hook party...
		return true;
	}

	public static void damageSkill(Player caster, LivingEntity le, double rawdamage) {
		//TODO damage custom with MMOCore
		le.damage(rawdamage, le); 
	}
	
	public static void damageSkill(Player caster, List<LivingEntity> list, double rawdamage) {
		for(LivingEntity le : list) {
			damageSkill(caster, le, rawdamage);
		}
	}
	
	/**
	 * 
	 * @param scale 0-100
	 * @return
	 */
	public static double getDamage(Player p, double scale) {
		//TODO hook mmocore
		return 0;
	}
	
	public static void scaleDamageSkill(Player caster, LivingEntity le, double scaleDamage) {
		damageSkill(caster, le, getDamage(caster, scaleDamage));
	}
	
	public static void scaleDamageSkill(Player caster, List<LivingEntity> list, double scaleDamage) {
		damageSkill(caster, list, getDamage(caster, scaleDamage));
	}
	

	public static LivingEntity getTargetAhead(Player p, double distance, double r) {
		Location l = p.getEyeLocation().clone();
		Vector v = l.getDirection().clone().normalize();
		for(double d = 0; d<distance;d+=r*1.9) {
			for(LivingEntity target : getTargetsAround(p, l.clone().add(v.clone().multiply(d)), r)) {
				return target;
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return target hoặc endLocation
	 */
	public static Location getTargetAheadEndLocation(Player p, double distance, double r) {
		LivingEntity le = getTargetAhead(p, distance, r);
		if(le == null) {
			Block b = p.getTargetBlock(null, (int) distance);
			if(b == null) return p.getLocation().clone();
			return b.getLocation().clone().add(0.5,0,0.5);
		}else return le.getLocation().clone();
	}

	public static void heal(Player p, double amount) {
		//TODO heal
	}

	public static void hatTung(LivingEntity entity, double d) {
		// TODO Auto-generated method stub
		
	}

	public static void taunt(LivingEntity target, Player p, int i) {
		if(target instanceof Creature) {
			((Creature)target).setTarget(p);
		}
	}

	public static void slow(LivingEntity target, int i, int j) {
		//TODO potion slow
	}

	public static void addGiamSatThuong(Player p, int i, double d) {
		// TODO Auto-generated method stub
		
	}

}
