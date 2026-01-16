package neo.rpgsystem.abilities;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface NeoSkill {
	
	public String getName();
	public String getDisplayName();
	public void cast(Player p);
	public void onAttack(Player p, LivingEntity target, double damage);
	public void beAttack(Player p, LivingEntity damager, double damage);
	public boolean isCooldown(Player p);
	public void setCooldown(Player p);
	public double getCooldown();
	public int getSkillLevel(Player p);
	
	public double getAddDamage(Player p, LivingEntity target, double damage);
	public double getAddDefense(Player p, LivingEntity target, double damage);
	public double getAddCritChance(Player p, LivingEntity target, double damage);
	public double getAddCritDamage(Player p, LivingEntity target, double damage);
	public double getAddDodgeRate(Player p, LivingEntity target, double damage);
	public double getAddBlockRate(Player p, LivingEntity target, double damage);
	public double getAddBlockAmount(Player p, LivingEntity target, double damage);
	public double getAddTangSatThuong(Player p, LivingEntity target, double damage);
	public double getAddGiamSatThuong(Player p, LivingEntity target, double damage);
	public double getRawDamage(Player p, LivingEntity target, double damage);

}
