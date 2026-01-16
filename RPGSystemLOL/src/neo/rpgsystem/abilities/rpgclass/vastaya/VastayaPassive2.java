package neo.rpgsystem.abilities.rpgclass.vastaya;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import neo.rpgsystem.abilities.CooldownSkills;
import neo.rpgsystemlol.main.DamageU;

public class VastayaPassive2 extends CooldownSkills {

    // Quản lý hiệu ứng xuất huyết
    private final List<XuatHuyetData> xuatHuyetList = new LinkedList<>();

    public VastayaPassive2(String name, String displayName, boolean msg) {
        super(name, displayName, msg);
    }

    /*
     *  Tấn công kẻ địch dưới 30% gây thêm sát thương;
     *  lv>=5: gây thêm xuất huyết: gây sát thương theo % máu tối đa trong 3s: tối đa 50% damage của bản thân;
     *  lv>=7 kích hoạt khi dưới 50%
     */

    @Override
    public double getAddDamage(Player p, LivingEntity target, double damage) {
        int level = getSkillLevel(p);
        if (level <= 0 || target == null) return 0;

        double hpPercent = target.getHealth() / target.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double threshold = (level >= 7) ? 0.5 : 0.3;
        if (hpPercent > threshold) return 0;

        double addDamage = getAddDamage(level) / 100.0 * damage;

        if (level >= 5) {
            double bleedPercent = getXuatHuyetDamage(level) / 100.0;
            double maxBleed = damage * 0.5;
            double bleedDamage = Math.min(target.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue() * bleedPercent, maxBleed);
            // Gây xuất huyết 3s (60 tick)
            addOrRefreshBleed(p, target, bleedDamage, 60);

            // Sound khi bắt đầu xuất huyết (nổi bật)
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_WITHER_HURT, 1.2f, 0.65f);
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 1.2f);

            // Particle mạnh tại vị trí mục tiêu
            for (int i = 0; i < 8; i++) {
                double angle = Math.toRadians(i * 45);
                double x = Math.cos(angle) * 0.6;
                double z = Math.sin(angle) * 0.6;
                target.getWorld().spawnParticle(
                        Particle.DUST, 
                        target.getLocation().add(x, 1.1, z),
                        1,
                        0.8, 0, 0, 0.7 // đỏ đậm, size 0.7
                );
            }
            // Particle drip_lava (giọt máu) cho cảm giác máu nhỏ
            target.getWorld().spawnParticle(Particle.DRIPPING_LAVA, target.getLocation().add(0, 1.2, 0), 5, 0.4, 0.2, 0.4, 0.02);
        }

        return addDamage;
    }
    
    @Override
    public void onTick() {
        super.onTick();
        onTickBleed();
    }

    // Gọi mỗi tick từ onTick()
    public void onTickBleed() {
        Iterator<XuatHuyetData> iter = xuatHuyetList.iterator();
        while (iter.hasNext()) {
            XuatHuyetData data = iter.next();
            if (data.getTarget().isDead() || data.isExpired()) {
                iter.remove();
            } else {
                data.onTick();
            }
        }
    }

    // Thêm hoặc làm mới hiệu ứng xuất huyết
    private void addOrRefreshBleed(Player caster, LivingEntity target, double totalDamage, int durationTicks) {
        XuatHuyetData existed = null;
        for (XuatHuyetData data : xuatHuyetList) {
            if (data.getTarget().equals(target)) {
                existed = data;
                break;
            }
        }
        if (existed != null) {
            if (!existed.getCaster().equals(caster)) {
                xuatHuyetList.remove(existed);
                xuatHuyetList.add(new XuatHuyetData(caster, target, totalDamage, durationTicks));
            } else {
                existed.restoreTime();
            }
        } else {
            xuatHuyetList.add(new XuatHuyetData(caster, target, totalDamage, durationTicks));
        }
    }

    public static double getAddDamage(int level) {
        if (level <= 0) return 0;
        return 30 + level * 2;
    }

    public static double getXuatHuyetDamage(int level) {
        if (level <= 0) return 0;
        return 3 + level * 0.2;
    }

    // Class dữ liệu hiệu ứng xuất huyết
    private static class XuatHuyetData {
        private final Player caster;
        private final LivingEntity target;
        private final double totalDamage;
        private final int durationTicks;
        private int tickCount;
        private final Random rand = new Random();

        public XuatHuyetData(Player caster, LivingEntity target, double totalDamage, int durationTicks) {
            this.caster = caster;
            this.target = target;
            this.totalDamage = totalDamage;
            this.durationTicks = durationTicks;
            this.tickCount = 0;
        }

        public Player getCaster() {
            return caster;
        }

        public LivingEntity getTarget() {
            return target;
        }

        public boolean isExpired() {
            return tickCount >= durationTicks;
        }

        public void restoreTime() {
            this.tickCount = 0;
        }

        public void onTick() {
            if (target.isDead()) return;
            double perTickDamage = totalDamage / durationTicks;
            DamageU.damageSkill(caster, target, perTickDamage);

            // Particle DUST (đỏ tươi) và DRIP_LAVA ngẫu nhiên quanh thân mục tiêu, nổi bật
            for (int i = 0; i < 4; i++) {
                double angle = rand.nextDouble() * Math.PI * 2;
                double radius = 0.6 + rand.nextDouble() * 0.25;
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;
                target.getWorld().spawnParticle(
                        Particle.DUST,
                        target.getLocation().add(x, 0.95 + rand.nextDouble() * 0.3, z),
                        1,
                        1, 0, 0, 0.8 // đỏ tươi, size 0.8
                );
            }
            target.getWorld().spawnParticle(Particle.DRIPPING_LAVA, target.getLocation().add(0, 1.05, 0), 2, 0.18, 0.08, 0.18, 0.01);

            // Thỉnh thoảng có sound nhỏ để cảm giác "máu chảy" (mỗi 12 tick)
            if (tickCount % 12 == 0) {
                target.getWorld().playSound(target.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 0.5f, 0.6f + rand.nextFloat()*0.3f);
            }

            tickCount++;
        }
    }
}