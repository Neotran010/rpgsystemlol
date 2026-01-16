package neo.rpgsystem.abilities.boost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import neo.rpgsystem.abilities.CooldownSkills;
import neo.rpgsystemlol.main.Main;
import neo.rpgsystemlol.main.Particles;
import neo.rpgsystemlol.main.U;

public class BoostNeoSkill extends CooldownSkills {

    private int boostTickTime;
    private Particles.neoParticleColor particleColor;
    // Mỗi player có thể có nhiều BoostData khác nhau
    private HashMap<Player, List<BoostData>> boostHashmap = new HashMap<>();

    private BoostNeoSkillType type = BoostNeoSkillType.NONE;

    public BoostNeoSkill(String name, String displayName, boolean msg, Particles.neoParticleColor particleColor, int boostTickTime) {
        super(name, displayName, msg);
        this.particleColor = particleColor;
        this.boostTickTime = boostTickTime;
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player p : new ArrayList<>(boostHashmap.keySet())) {
                    List<BoostData> dataList = boostHashmap.get(p);
                    if (dataList == null || dataList.isEmpty()) {
                        boostHashmap.remove(p);
                        continue;
                    }
                    Iterator<BoostData> it = dataList.iterator();
                    while (it.hasNext()) {
                        BoostData bd = it.next();
                        int a = bd.getTimeLeft();
                        a--;
                        if (a <= 0) {
                            onEnd(p, bd);
                            it.remove();
                            continue;
                        }
                        bd.setTimeLeft(a);
                        particle(p);
                        // Nếu là PotionsBoostData thì tái áp dụng hiệu ứng (potion) mỗi tick nếu cần
                        if (bd instanceof PotionsBoostData) {
                            for (PotionEffect pe : ((PotionsBoostData) bd).getPe()) {
                                if (!p.hasPotionEffect(pe.getType()) || p.getPotionEffect(pe.getType()).getDuration() < 2) {
                                    p.addPotionEffect(pe);
                                }
                            }
                        }
                    }
                    // Remove player entry if all boostData have ended
                    if (dataList.isEmpty()) {
                        boostHashmap.remove(p);
                    }
                }
            }
        }.runTaskTimer(Main.pl, 1, 1);
    }

    public BoostNeoSkill(String name, String displayName, boolean msg, Particles.neoParticleColor particleColor, int boostTickTime, BoostNeoSkillType type) {
        this(name, displayName, msg, particleColor, boostTickTime);
        this.type = type;
    }

    public static class PotionsBoostData implements BoostData {
        private List<PotionEffect> pe;
        private int timeLeft;

        public PotionsBoostData(List<PotionEffect> pe, int timeLeft) {
            this.pe = pe;
            this.timeLeft = timeLeft;
        }

        public PotionsBoostData(PotionEffect pe, int timeLeft) {
            this.pe = Arrays.asList(pe);
            this.timeLeft = timeLeft;
        }

        public List<PotionEffect> getPe() {
            return pe;
        }

        @Override
        public int getTimeLeft() {
            return timeLeft;
        }

        @Override
        public void setTimeLeft(int timeLeft) {
            this.timeLeft = timeLeft;
        }
    }

    public interface BoostData {
        int getTimeLeft();
        void setTimeLeft(int timeLeft);
    }

    // Kiểm tra player có ít nhất 1 boost
    public boolean onBoost(Player p) {
        List<BoostData> list = boostHashmap.get(p);
        return list != null && !list.isEmpty();
    }

    public int getBoostTickTime() {
        return boostTickTime;
    }

    public int getBoostTickTime(int level) {
        return boostTickTime;
    }

    @Override
    public void cast(Player p) {
        List<BoostData> datas = new ArrayList<>();
        datas.add(createBoostData(p));
        for (BoostData data : datas) {
            addBoostData(p, data);
        }
        onCast(p);
        super.cast(p);
    }

    // Thêm boostData mới cho player
    public void addBoostData(Player p, BoostData data) {
        List<BoostData> list = boostHashmap.getOrDefault(p, new ArrayList<>());
        list.add(data);
        boostHashmap.put(p, list);
    }

    // Backward compatibility - nếu chỉ cần 1 boost
    public BoostData createBoostData(Player p) {
    	return new PotionsBoostData(new ArrayList<PotionEffect>(), 20*3);
    }

    // Lấy tất cả boostData của player (có thể lọc theo instanceof nếu cần)
    public List<BoostData> getBoostData(Player p) {
        return boostHashmap.getOrDefault(p, Collections.emptyList());
    }

    // Lấy PotionsBoostData đầu tiên (nếu có)
    public PotionsBoostData getPotionsBoostData(Player p) {
        for (BoostData bd : getBoostData(p)) {
            if (bd instanceof PotionsBoostData) return (PotionsBoostData) bd;
        }
        return null;
    }

    // Gọi khi bắt đầu 1 hiệu ứng (truyền BoostData để biết loại boost gì)
    protected void onCast(Player p) {
        p.sendMessage(Main.ABILITIES_PREFIX + "§6Kích hoạt hiệu ứng kỹ năng §f" + getDisplayName() + "!");
        U.playSound(p, Sound.ENTITY_PLAYER_LEVELUP);
    }

    // Gọi khi kết thúc 1 hiệu ứng (truyền BoostData để biết loại boost gì)
    protected void onEnd(Player p, BoostData data) {
        p.sendMessage(Main.ABILITIES_PREFIX + "§2Kết thúc hiệu ứng kỹ năng §f" + getDisplayName() + "!");
        U.playSound(p, Sound.BLOCK_TRIPWIRE_CLICK_OFF);
        // Nếu là PotionsBoostData, remove potion effect
        if (data instanceof PotionsBoostData) {
            for (PotionEffect eff : ((PotionsBoostData) data).getPe()) {
                p.removePotionEffect(eff.getType());
            }
        }
    }

    protected void particle(Player p) {
        Particles.spawnParticleColor(p.getLocation().clone(), Particle.DUST, particleColor, 5, 0.5, 1, 0.5);
    }

    @Override
    public void onAttack(Player p, LivingEntity target, double damage) {
        if (type.equals(BoostNeoSkillType.ATTACK)) {
            cast(p);
        }
        super.onAttack(p, target, damage);
    }
}