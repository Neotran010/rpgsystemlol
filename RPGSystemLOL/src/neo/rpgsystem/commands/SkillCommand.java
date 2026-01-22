package neo.rpgsystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import neo.rpgsystem.abilities.NeoSkill;
import neo.rpgsystem.abilities.NeoSkills;
import neo.rpgsystem.integration.mmocore.MMOCoreIntegration;
import neo.rpgsystemlol.main.Main;

public class SkillCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.PREFIX + "§cChỉ người chơi mới sử dụng được lệnh này!");
            return true;
        }
        
        Player p = (Player) sender;
        
        if (args.length == 0) {
            sendHelp(p);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "cast":
                if (args.length < 2) {
                    p.sendMessage(Main.PREFIX + "§cSử dụng: /skill cast <tên kỹ năng>");
                    return true;
                }
                castSkill(p, args[1]);
                break;
            case "list":
                listSkills(p);
                break;
            case "info":
                if (args.length < 2) {
                    p.sendMessage(Main.PREFIX + "§cSử dụng: /skill info <tên kỹ năng>");
                    return true;
                }
                skillInfo(p, args[1]);
                break;
            default:
                sendHelp(p);
                break;
        }
        return true;
    }
    
    private void sendHelp(Player p) {
        p.sendMessage("§6§l======= Skill Commands =======");
        p.sendMessage("§e/skill cast <tên> §7- Sử dụng kỹ năng");
        p.sendMessage("§e/skill list §7- Xem danh sách kỹ năng");
        p.sendMessage("§e/skill info <tên> §7- Xem thông tin kỹ năng");
        p.sendMessage("§6§l==============================");
    }
    
    private void castSkill(Player p, String skillName) {
        NeoSkill skill = NeoSkills.get(skillName);
        if (skill == null) {
            p.sendMessage(Main.PREFIX + "§cKhông tìm thấy kỹ năng: " + skillName);
            return;
        }
        
        if (skill.isCooldown(p)) {
            p.sendMessage(Main.PREFIX + "§cKỹ năng đang trong thời gian hồi!");
            return;
        }
        
        skill.cast(p);
        skill.setCooldown(p);
    }
    
    private void listSkills(Player p) {
        p.sendMessage("§6§l======= Danh sách Kỹ năng =======");
        p.sendMessage("§eHuman Skills:");
        p.sendMessage("§7  - humansactive1 (Kiếm Kỹ: Cường Hóa)");
        p.sendMessage("§7  - humansactive2 (Tiến Công)");
        p.sendMessage("§7  - humanspassive1 (Bạt Đao Kiếm)");
        if (MMOCoreIntegration.isMythicLibAvailable()) {
            p.sendMessage("§a[MMOCore Skills đã được đăng ký]");
        }
        p.sendMessage("§6§l=================================");
    }
    
    private void skillInfo(Player p, String skillName) {
        NeoSkill skill = NeoSkills.get(skillName);
        if (skill == null) {
            p.sendMessage(Main.PREFIX + "§cKhông tìm thấy kỹ năng: " + skillName);
            return;
        }
        p.sendMessage("§6§l======= " + skill.getDisplayName() + " =======");
        p.sendMessage("§eID: §f" + skill.getName());
        p.sendMessage("§eCooldown: §f" + skill.getCooldown() + "s");
        p.sendMessage("§eLevel hiện tại: §f" + skill.getSkillLevel(p));
        p.sendMessage("§6§l==============================");
    }
}
