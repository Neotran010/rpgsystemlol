package neo.rpgsystem.abilities.rpgclass.humans;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import neo.rpgsystem.abilities.CooldownSkills;
import neo.rpgsystem.abilities.beam.AbilitiesBeamManager;
import neo.rpgsystem.abilities.beam.MovementAbilitiesBeam;
import neo.rpgsystemlol.main.DamageU;
import neo.rpgsystemlol.main.Main;
import neo.rpgsystemlol.main.Particles;

public class HumansPassive1 extends CooldownSkills {

    public HumansPassive1(String name, String displayName, boolean msg) {
        super("humanspassive1", "Bạt Đao Kiếm", false);
    }

    @Override
    public void cast(Player p) {
        super.cast(p);

        int level = 0; //TODO

        new BukkitRunnable() {
            int tick = 0;
            int phase = 0;
            int level = 1;
            @Override
            public void run() {
                tick++;
                if (tick >= 10) {
                    if (phase <= 1) {
                        phase++;
                        chemNgang(p, getChemNgangDamage(level) / 100);
                        // Sound khi chém ngang
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f + phase * 0.1f);
                    } else {
                        this.cancel();
                        if (level >= 3) {
                            kiemPhep(p, level, level >= 5);
                            // Sound khi kích hoạt kiếm phép
                            p.getWorld().playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
                        }
                        return;
                    }
                }
            }
        }.runTaskTimer(Main.pl, 0, 1);
    }

    private void chemNgang(Player p, double damage) {
        List<LivingEntity> targets = DamageU.getTargetsAround(p, p.getLocation(), 3);
        DamageU.scaleDamageSkill(p, targets, damage);
        //TODO particle
        // Sound khi gây damage
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.8f, 1.3f);
    }

    private void kiemPhep(Player p, int level, boolean x2) {
        AbilitiesBeamManager.register(new CustomBeam(p.getLocation(), p.getLocation().getDirection().clone().setY(0), p, DamageU.getDamage(p, getKiemKhiDamage(level)), level));
    }

    public static double getChemNgangDamage(int level) {
        if (level <= 0) return 0;
        return 100 + level * 5;
    }

    public static double getKiemKhiDamage(int level) {
        if (level <= 0) return 0;
        return 80 + level * 6;
    }

    private static class CustomBeam extends MovementAbilitiesBeam {

        private int level;

        public CustomBeam(Location location, Vector direction, Player caster, double damage, int level) {
            super(location, direction, caster, true, false, false, damage, 16, 1);
            this.level = level;
            setRangeHit(level >= 5 ? 4 : 2);
        }

        @Override
        protected void particle() {
            Particles.doChemNgang(Particle.INSTANT_EFFECT, getLocation(), getDirection(), 0.5, getRangeHit());
            super.particle();
            // Sound khi particle beam xuất hiện
            getCaster().getWorld().playSound(getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 0.7f, 1.4f);
        }

        @Override
        protected void onHit(LivingEntity le) {
            if (level >= 5) {
                // Hất tung nhẹ
                le.setVelocity(new Vector(0, 1, 0));
                // Sound khi trúng beam
                le.getWorld().playSound(le.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0f, 1.2f);
            }
        }
    }
}