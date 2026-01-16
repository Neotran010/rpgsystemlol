package neo.rpgsystem.abilities.rpgclass.targon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import neo.rpgsystem.abilities.CostConditionSkills;
import neo.rpgsystemlol.main.DamageU;
import neo.rpgsystemlol.main.Main;
import neo.rpgsystemlol.main.Particles;
import neo.rpgsystemlol.main.U;

public class TargonActive1 extends CostConditionSkills {

    // Lưu danh sách người chơi đang được cường hóa
    private static Map<Player, PassiveData> activePlayers = new HashMap<>();

    public TargonActive1() {
        super("targonactive1", "Cơn Thịnh Nộ Của Thần Linh", true, 50, 0);
    }

    // Dữ liệu cường hóa cho từng người chơi
    private static class PassiveData {
        private Player caster;
        private int timeLeft;
        private int count = 0;
        private int delay = 0;
        private int level;
        public PassiveData(Player caster, int timeLeft, int level) {
            this.caster = caster;
            this.timeLeft = timeLeft;
            this.level = level;
        }

        public void addCount() {
            timeLeft = 20 * 6; // reset thời gian mỗi lần đánh
            delay = 10; // ngăn spam (0.5s)
            count++;
        }

        public void onAttack(LivingEntity le, double damage) {
            if (delay > 0) return;
            addCount();

            // Gây thêm sát thương diện rộng (cho mỗi đòn)
            List<LivingEntity> around = DamageU.getTargetsAround(caster, le.getLocation(), 3);
            double extraDmg = 30 + level * 8; // sát thương thêm, có thể chỉnh lại
            for (LivingEntity entity : around) {
                DamageU.damageSkill(caster, entity, extraDmg);
            }
            Particles.circle(le.getLocation(), Particle.CRIT, 3, Math.PI / 6);

            // lv >= 5: hồi phục cho caster mỗi phát
            if (level >= 5) {
                double healAmount = 0.08 * caster.getAttribute(Attribute.MAX_HEALTH).getBaseValue(); // hồi phục 8% máu
                DamageU.heal(caster, healAmount);
                Particles.DoParticle(caster.getLocation(), Particle.HEART, 3, 0.5, 1, 0.5, 0);
            }

            // Đòn thứ 3: lv >= 7 gây hất tung lan tỏa
            if (count >= 3) {
                if (level >= 7) {
                    List<LivingEntity> knockTargets = DamageU.getTargetsAround(caster, le.getLocation(), 6);
                    for (LivingEntity entity : knockTargets) {
                        DamageU.hatTung(entity, 1.2); // hất lên 1.2 block
                        // Hiệu ứng lan dần dần: mỗi entity trễ thêm 2 ticks
                        int delayTick = knockTargets.indexOf(entity) * 2;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Particles.DoParticle(entity.getLocation(), Particle.CLOUD, 6);
                            }
                        }.runTaskLater(Main.pl, delayTick);
                    }
                    le.getWorld().playSound(le.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1.2f);
                }
                count = 99; // đánh dấu đã xong, sẽ remove sau
            }
        }

        public void onTick() {
            timeLeft--;
            delay = Math.max(0, delay - 1);
            if (timeLeft <= 0) return;
            // Particle hiệu ứng quanh người chơi khi đang cường hóa
            Particles.circle(caster.getLocation().add(0, 1.2, 0), Particle.GLOW, 1.2, Math.PI / 5);
        }
    }

    @Override
    public void cast(Player p) {
        if (activePlayers.containsKey(p)) return;
        int level = getSkillLevel(p);
        PassiveData data = new PassiveData(p, 20 * 6, level);
        activePlayers.put(p, data);
        U.playSound(p.getLocation(), Sound.ITEM_TRIDENT_THUNDER);
        super.cast(p);

        // Hiệu ứng tick
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!activePlayers.containsKey(p)) {
                    this.cancel();
                    return;
                }
                PassiveData d = activePlayers.get(p);
                d.onTick();
                if (d.timeLeft <= 0 || d.count >= 3) {
                    activePlayers.remove(p);
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.pl, 0, 1);
    }

    @Override
    public void onAttack(Player p, LivingEntity target, double damage) {
        PassiveData data = activePlayers.get(p);
        if (data == null) return;
        data.onAttack(target, damage);

        // Remove sau 3 lần đánh hoặc hết thời gian
        if (data.count >= 3 || data.timeLeft <= 0) {
            activePlayers.remove(p);
        }
    }
}
