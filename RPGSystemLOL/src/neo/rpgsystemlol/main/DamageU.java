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
		// Không target chính mình
		if (le.equals(p)) return false;
		
		// Không target player cùng party (nếu có MMOCore)
		if (le instanceof Player) {
			Player target = (Player) le;
			if (neo.rpgsystem.integration.mmocore.MMOCoreIntegration.isMMOCoreAvailable()) {
				try {
					net.Indyuce.mmocore.api.player.PlayerData pData = 
						net.Indyuce.mmocore.api.player.PlayerData.get(p);
					net.Indyuce.mmocore.api.player.PlayerData tData = 
						net.Indyuce.mmocore.api.player.PlayerData.get(target);
					if (pData.hasParty() && tData.hasParty()) {
						if (pData.getParty().equals(tData.getParty())) {
							return false; // Cùng party
						}
					}
				} catch (Exception ignored) {}
			}
		}
		return true;
	}

	public static void damageSkill(Player caster, LivingEntity le, double rawdamage) {
		if (neo.rpgsystem.integration.mmocore.MMOCoreIntegration.isMythicLibAvailable()) {
			try {
				// Sử dụng MythicLib damage system
				io.lumine.mythic.lib.api.player.MMOPlayerData mmoCaster = 
					io.lumine.mythic.lib.api.player.MMOPlayerData.get(caster);
				io.lumine.mythic.lib.damage.DamageMetadata damage = 
					new io.lumine.mythic.lib.damage.DamageMetadata(rawdamage, 
						io.lumine.mythic.lib.damage.DamageType.SKILL);
				io.lumine.mythic.lib.damage.AttackMetadata attack = 
					new io.lumine.mythic.lib.damage.AttackMetadata(damage, le, mmoCaster.getStatMap().cache());
				attack.damage(le);
				return;
			} catch (Exception e) {
				// Fallback to vanilla
			}
		}
		le.damage(rawdamage, caster);
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
		double baseDamage = 10; // Base damage mặc định
		
		if (neo.rpgsystem.integration.mmocore.MMOCoreIntegration.isMythicLibAvailable()) {
			try {
				io.lumine.mythic.lib.api.player.MMOPlayerData mmoData = 
					io.lumine.mythic.lib.api.player.MMOPlayerData.get(p);
				// Lấy attack damage từ stats
				baseDamage = mmoData.getStatMap().getStat("ATTACK_DAMAGE");
			} catch (Exception ignored) {}
		}
		
		return baseDamage * (scale / 100.0);
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
		double maxHealth = p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue();
		double newHealth = Math.min(p.getHealth() + amount, maxHealth);
		p.setHealth(newHealth);
	}

	public static void hatTung(LivingEntity entity, double power) {
		org.bukkit.util.Vector velocity = entity.getVelocity();
		velocity.setY(power);
		entity.setVelocity(velocity);
	}

	public static void taunt(LivingEntity target, Player p, int i) {
		if(target instanceof Creature) {
			((Creature)target).setTarget(p);
		}
	}

	public static void slow(LivingEntity target, int amplifier, int durationTicks) {
		target.addPotionEffect(new org.bukkit.potion.PotionEffect(
			org.bukkit.potion.PotionEffectType.SLOW, durationTicks, amplifier));
	}

	public static void addGiamSatThuong(Player p, int durationTicks, double percent) {
		// Thêm resistance effect tương đương
		int amplifier = (int) Math.min(4, percent / 20); // 20% = 1 level
		p.addPotionEffect(new org.bukkit.potion.PotionEffect(
			org.bukkit.potion.PotionEffectType.DAMAGE_RESISTANCE, durationTicks, amplifier));
	}

}
