package neo.rpgsystem.abilities.rpgclass.yordle;

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
import neo.rpgsystemlol.main.U;
import neo.rpgsystemlol.main.Particles.neoParticleColor;

public class YordlePassive1 extends CooldownSkills {

    public YordlePassive1(String name, String displayName, boolean msg) {
        super("yordlepassive1", "Phép Thuật Băng Hỏa", msg);
    }

    public void cast(Player caster, LivingEntity le) {
        new BukkitRunnable() {
            Location loc = le.getLocation().clone();
            int a = 0;

            @Override
            public void run() {
                a++;
                if (a > 2) {
                    this.cancel();
                    return;
                }
                if (!le.isDead() && le.getWorld().equals(caster.getWorld())) {
                    loc = le.getLocation().clone();
                }
                
                // Tính maxTime dựa vào khoảng cách giữa caster và target, min = 10, max = 30
                double distance = caster.getLocation().distance(loc);
                int maxTime = (int) Math.max(10, Math.min(30, distance * 2.5)); // scale tùy ý, bạn có thể chỉnh lại hệ số

                Location baseLocation = caster.getLocation().clone().add(0, 1, 0);
                Location endLocation = loc;
                Location controlLocation = Particles.getRandomCircleHalfUpLocation(
                        loc,
                        caster.getLocation().getDirection(),
                        U.random(6, 10)
                );

                BangHoaBeam beam = new BangHoaBeam(
                        baseLocation,
                        caster,
                        100, // damage
                        maxTime,
                        controlLocation,
                        endLocation,
                        a == 1
                );
                AbilitiesBeamManager.register(beam);
            }
        }.runTaskTimer(Main.pl, 0, 20);
    }

    private static class BangHoaBeam extends MovementAbilitiesBeam {

        private int maxTime;
        private Location controlLocation; // vị trí điều khiển
        private Location baseLocation;
        private Location endLocation;
        private boolean isFreeze;
        private int t = 0;

        public BangHoaBeam(Location location, Player caster, double damage, int maxTime, Location controlLocation, Location endLocation, boolean isFreeze) {
            super(location, null, caster, false, false, false, damage, -1, -1);
            this.maxTime = maxTime;
            this.baseLocation = location;
            this.controlLocation = controlLocation;
            this.endLocation = endLocation;
            this.isFreeze = isFreeze;
        }

        @Override
        public void onTick() {
            t++;
            super.onTick();
        }

        @Override
        protected void onStart() {
            // Sound bắn phù hợp
            baseLocation.getWorld().playSound(
                baseLocation,
                isFreeze ? Sound.ENTITY_SNOW_GOLEM_SHOOT : Sound.ENTITY_BLAZE_SHOOT,
                1f, 1.15f
            );
        }

        /**
         * Tính vị trí theo Bezier Curve (base -> control -> end), t chạy từ 0 đến maxTime.
         * Sử dụng công thức: B(t) = (1-u)^2 * P0 + 2(1-u)u * P1 + u^2 * P2
         */
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
            Particles.spawnParticleColor(
                getBezierLocation(),
                Particle.DUST_COLOR_TRANSITION,
                isFreeze ? neoParticleColor.BLUE : neoParticleColor.RED
            );
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
            List<LivingEntity> targets = DamageU.getTargetsAround(getCaster(), endLocation, 1.5);
            DamageU.damageSkill(getCaster(), targets, getDamage());
            Particles.circle(
                endLocation,
                isFreeze ? Particle.SNOWFLAKE : Particle.FLAME,
                1.5,
                Math.PI / 6
            );
            endLocation.getWorld().playSound(
                endLocation,
                isFreeze ? Sound.BLOCK_GLASS_BREAK : Sound.ENTITY_BLAZE_SHOOT,
                1f, 1f
            );
            super.onEnd();
        }
    }
}