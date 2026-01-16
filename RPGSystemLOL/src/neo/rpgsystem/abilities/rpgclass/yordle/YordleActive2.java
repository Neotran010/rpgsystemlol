package neo.rpgsystem.abilities.rpgclass.yordle;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import neo.rpgsystem.abilities.TargetConditionSkills;
import neo.rpgsystem.abilities.beam.AbilitiesBeamManager;
import neo.rpgsystem.abilities.beam.MovementAbilitiesBeam;
import neo.rpgsystemlol.main.DamageU;
import neo.rpgsystemlol.main.Main;
import neo.rpgsystemlol.main.Particles;
import neo.rpgsystemlol.main.Particles.neoParticleColor;
import neo.rpgsystemlol.main.U;

public class YordleActive2 extends TargetConditionSkills {

    public YordleActive2(String name, String displayName, boolean msg) {
        super("yordleactive2", "Triệu Hồi Lôi Long", true, 50, 0, 16, 1);
    }

    @Override
    public void cast(Player p) {
        LivingEntity le = DamageU.getTargetAhead(p, 16, 1);
        if (le == null) return;
        int level = getSkillLevel(p);

        Location l = le.getLocation().clone();
        Location loc = l.clone().add(0,1,0);
        Location controlLocation = loc.clone().add(0, U.random(7, 10), 0);
        double baseRange = 2;
        double range = level >= 5 ? baseRange * 2 : baseRange;

        double distance = p.getLocation().distance(loc);
        int maxTime = (int) Math.max(10, Math.min(30, distance * 2.5));

        // Vẽ vòng tròn điện
        Particles.circleBigger(le.getLocation(), Particle.ELECTRIC_SPARK, 0.1, range, 0.5, Math.PI/6, 0, null);

        // Tạo beam Lôi Long
        SkillsBeam beam = new SkillsBeam(loc, p, 100, maxTime, controlLocation, le.getLocation().clone(), level, range);
        AbilitiesBeamManager.register(beam);

        // Hiệu ứng thiên lôi lv>=7
        if (level >= 7) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Location strikeLoc = le.getLocation().clone().add(0, 0.5, 0);
                    // Hiệu ứng ánh sáng
                    le.getLocation().getWorld().strikeLightningEffect(le.getLocation());
                    U.playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER);
                    // Instant lightning bolt phóng lên trên
                    for (double spd = 0.1; spd <= 0.5; spd += 0.1) {
                        Particles.movingParticle(le.getLocation().clone().add(U.random(-0.5, 0.5), U.random(0.1, 2), U.random(-0.5, 0.5)), Particle.INSTANT_EFFECT, new Vector(0,1,0), U.random(0.1, 0.5));
                    }
                    // Gây choáng các entity còn trong phạm vi
                    List<LivingEntity> targets = DamageU.getTargetsAround(p, strikeLoc, range);
                    for (LivingEntity target : targets) {
//                        DamageU.stun(target, 40); // 40 ticks = 2s, chỉnh lại nếu muốn
                    	//TODO stun
                    }
                    strikeLoc.getWorld().playSound(strikeLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 1f);
                }
            }.runTaskLater(Main.pl, 30); // delay 1.5s sau khi beam
        }
    }

    private static class SkillsBeam extends MovementAbilitiesBeam {

        private int maxTime;
        private Location controlLocation;
        private Location baseLocation;
        private Location endLocation;
        private int level;
        private double range;
        private int t = 0;

        public SkillsBeam(Location location, Player caster, double damage, int maxTime, Location controlLocation, Location endLocation, int level, double range) {
            super(location, null, caster, false, false, false, damage, -1, -1);
            this.maxTime = maxTime;
            this.baseLocation = location;
            this.controlLocation = controlLocation;
            this.endLocation = endLocation;
            this.level = level;
            this.range = range;
        }

        @Override
        public void onTick() {
            t++;
            super.onTick();
        }

        @Override
        protected void onStart() {
            baseLocation.getWorld().playSound(baseLocation, Sound.ENTITY_ENDER_DRAGON_SHOOT, 1f, 1.2f);
        }

        public Location getBezierLocation() {
            double u = Math.min((double) t / maxTime, 1.0);
            Location p0 = baseLocation;
            Location p1 = controlLocation;
            Location p2 = endLocation;

            double x = Math.pow(1-u,2) * p0.getX() + 2*(1-u)*u*p1.getX() + Math.pow(u,2)*p2.getX();
            double y = Math.pow(1-u,2) * p0.getY() + 2*(1-u)*u*p1.getY() + Math.pow(u,2)*p2.getY();
            double z = Math.pow(1-u,2) * p0.getZ() + 2*(1-u)*u*p1.getZ() + Math.pow(u,2)*p2.getZ();

            return new Location(p0.getWorld(), x, y, z);
        }

        @Override
        protected void particle() {
            Particles.spawnParticleColor(getBezierLocation(), Particle.DUST_COLOR_TRANSITION, neoParticleColor.AQUA);
            Vector v = getDirection();
            for (int i = 0; i < 4; i++) {
                double angle = t*Math.PI/6 + i*Math.PI/2;
                Particles.DoParticle(Particles.getCircleLocation(getBezierLocation(), v, angle, 0.5), Particle.CRIT, 1);
            }
        }

        @Override
        public Vector getDirection() {
            Location current = getBezierLocation().clone();
            double nextT = Math.min(t + 1, maxTime);
            double uNext = nextT / maxTime;
            Location next = new Location(
                current.getWorld(),
                Math.pow(1-uNext,2)*baseLocation.getX() + 2*(1-uNext)*uNext*controlLocation.getX() + Math.pow(uNext,2)*endLocation.getX(),
                Math.pow(1-uNext,2)*baseLocation.getY() + 2*(1-uNext)*uNext*controlLocation.getY() + Math.pow(uNext,2)*endLocation.getY(),
                Math.pow(1-uNext,2)*baseLocation.getZ() + 2*(1-uNext)*uNext*controlLocation.getZ() + Math.pow(uNext,2)*endLocation.getZ()
            );
            return next.subtract(current).toVector();
        }

        @Override
        protected void onEnd() {
            // Gây sát thương diện rộng và làm chậm
            List<LivingEntity> targets = DamageU.getTargetsAround(getCaster(), endLocation, range);
            DamageU.damageSkill(getCaster(), targets, getDamage());
            for (LivingEntity target : targets) {
//                DamageU.slow(target, 60, 1); // 60 ticks = 3s, level 1 slow
            	target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20*3, 3));
            }
            Particles.circle(endLocation, Particle.ELECTRIC_SPARK, range, Math.PI / 6);
            endLocation.getWorld().playSound(endLocation, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);

            // level ≥ 7: thiên lôi giáng xuống sau ít giây, làm choáng kẻ còn trong phạm vi
            if (level >= 7) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Location strikeLoc = endLocation.clone().add(0, 0.5, 0);
                        // Hiệu ứng ánh sáng
                        strikeLoc.getWorld().strikeLightningEffect(strikeLoc);
                        U.playSound(strikeLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER);

                        // Instant lightning bolt phóng lên trên
                        for (double spd = 0.1; spd <= 0.5; spd += 0.1) {
                            Particles.movingParticle(
                                strikeLoc.clone().add(U.random(-0.5, 0.5), U.random(0.1, 2), U.random(-0.5, 0.5)),
                                Particle.INSTANT_EFFECT,
                                new Vector(0, 1, 0),
                                U.random(0.1, 0.5)
                            );
                        }

                        // Làm choáng các entity còn trong phạm vi
                        List<LivingEntity> stunnedTargets = DamageU.getTargetsAround(getCaster(), strikeLoc, range);
                        for (LivingEntity target : stunnedTargets) {
//                            DamageU.stun(target, 40); // 40 ticks = 2s
                        	//TODO stun
                        }
                        strikeLoc.getWorld().playSound(strikeLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 1f);
                    }
                }.runTaskLater(Main.pl, 30); // delay 1.5s sau khi beam
            }
            super.onEnd();
        }
    }
}