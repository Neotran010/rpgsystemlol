package neo.rpgsystem.abilities.rpgclass.ascended;

import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import neo.rpgsystem.abilities.stackable.StackableNeoSkill;
import neo.rpgsystemlol.main.Particles;
import neo.rpgsystemlol.main.Particles.neoParticleColor;

public class AscendedPassive2 extends StackableNeoSkill {

    public AscendedPassive2() {
        super("ascendedpassive2", "Thể Thăng Hoa", false, 10);
    }

    @Override
    public void onAttack(Player p, LivingEntity target, double damage) {
        if(isCooldown(p)) return;
        if(!isMaxStack(p)) return;
        addStack(p);
    }
    
    @Override
    public double getAddDamage(Player p, LivingEntity target, double damage) {
        int stack = getCurrentStack(p);
        if(stack <= 0) return 0;
        int level = getSkillLevel(p);
        return damage * getAddDamage(level) * (stack / (double) getMaxStack());
    }
    
    @Override
    public StackData createStackData() {
        return new ThangHoaStackData();
    }
    
    @Override
    protected void onMaxStack(Player p) {
        setCooldown(p);
        // Boost: potion strength, health boost level 5 trong 10s
        p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20 * 10, 4)); // level 5 = amplifier 4
        p.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 20 * 10, 4));
        // Particle hiệu ứng thăng hoa
        Particles.spawnSphereParticles(p.getLocation().add(0,1,0), Particle.HAPPY_VILLAGER, 1.5, 30, neoParticleColor.YELLOW);
        super.onMaxStack(p);
    }
    
    @Override
    public double getCooldown() {
        return 12;
    }
    
    private static class ThangHoaStackData extends StackData {
        
        @Override
        public void onTick(Player p) {
            if(isMaxStack()) {
                // Particle đặc biệt khi đủ stack
                Particles.spawnSphereParticles(p.getLocation().add(0,1,0), Particle.END_ROD, 1.2, 8, neoParticleColor.YELLOW);
            }
            super.onTick(p);
        }
    }
    
    /**
     * @param level cấp kỹ năng (1-10)
     * @return tỉ lệ cộng thêm mỗi stack (vd: 0.01 = 1%)
     * lv1: max 10 stack = 10% => mỗi stack = 1%
     * lv10: max 10 stack = 15% => mỗi stack = 1.5%
     */
    public static double getAddDamage(int level) {
        // Tăng đều từ 10% (lv1) lên 15% (lv10)
        double min = 0.10, max = 0.15;
        double perStack = min + (max - min) * (level - 1) / 9; // nội suy tuyến tính
        return perStack / 10.0;
    }
}
