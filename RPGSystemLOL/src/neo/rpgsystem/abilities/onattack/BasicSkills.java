package neo.rpgsystem.abilities.onattack;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import neo.rpgsystem.abilities.AbilityManager;
import neo.rpgsystem.abilities.NeoSkill;

public class BasicSkills implements NeoSkill {
	
	private String name;
	private String displayName;

	public BasicSkills(String name, String displayName) {
		super();
		this.name = name;
		this.displayName = displayName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void cast(Player p) {
	}

	@Override
	public void onAttack(Player p, LivingEntity target, double damage) {
	}

	@Override
	public void beAttack(Player p, LivingEntity damager, double damage) {
	}

	@Override
	public boolean isCooldown(Player p) {
		return false;
	}

	@Override
	public void setCooldown(Player p) {
	}

	@Override
	public double getCooldown() {
		return 0;
	}

	@Override
	public double getAddDamage(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public double getAddDefense(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public double getAddCritChance(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public double getAddCritDamage(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public double getAddDodgeRate(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public double getAddBlockRate(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public double getAddBlockAmount(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public double getAddTangSatThuong(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public double getAddGiamSatThuong(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public double getRawDamage(Player p, LivingEntity target, double damage) {
		return 0;
	}

	@Override
	public int getSkillLevel(Player p) {
		return AbilityManager.getLevel(p, getName());
	}
	
}
