package neo.rpgsystem.abilities.rpgclass.targon;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import neo.rpgsystem.abilities.boost.BoostNeoSkill;
import neo.rpgsystemlol.main.Particles.neoParticleColor;

public class TargonPassive1 extends BoostNeoSkill {

	public TargonPassive1() {
		super("targonpassive1", "Bảo Hộ Của Thần", false, neoParticleColor.PURPLE, 20*6);
	}
	
	@Override
	public void cast(Player p) {
		addBoostData(p, createBoostData(p));
		//TODO add shield
	}
	
	@Override
	public BoostData createBoostData(Player p) {
		return new PotionsBoostData(new PotionEffect(PotionEffectType.HEALTH_BOOST, 20*6, 3), 20*6);
	}
	
	@Override
	protected void particle(Player p) {
		super.particle(p);
	}
	

}
