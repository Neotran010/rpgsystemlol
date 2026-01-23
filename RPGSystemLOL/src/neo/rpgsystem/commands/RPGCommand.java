package neo.rpgsystem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import neo.rpgsystemlol.main.Main;

public class RPGCommand implements CommandExecutor {
    
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
            case "help":
                sendHelp(p);
                break;
            case "info":
                sendInfo(p);
                break;
            case "reload":
                if (p.hasPermission("rpgsystem.admin")) {
                    Main.pl.reloadConfig();
                    p.sendMessage(Main.PREFIX + "§aĐã reload config!");
                } else {
                    p.sendMessage(Main.PREFIX + "§cBạn không có quyền!");
                }
                break;
            default:
                sendHelp(p);
                break;
        }
        return true;
    }
    
    private void sendHelp(Player p) {
        p.sendMessage("§6§l======= RPGSystemLOL =======");
        p.sendMessage("§e/rpg help §7- Hiển thị trợ giúp");
        p.sendMessage("§e/rpg info §7- Xem thông tin plugin");
        p.sendMessage("§e/rpg reload §7- Reload config (Admin)");
        p.sendMessage("§e/skill cast <tên> §7- Sử dụng kỹ năng");
        p.sendMessage("§e/skill list §7- Xem danh sách kỹ năng");
        p.sendMessage("§6§l===========================");
    }
    
    private void sendInfo(Player p) {
        p.sendMessage("§6§l======= RPGSystemLOL =======");
        p.sendMessage("§ePhiên bản: §f1.0.0");
        p.sendMessage("§eTác giả: §fNeotran010");
        p.sendMessage("§eMô tả: §fHệ thống RPG Skills tích hợp MMOCore");
        p.sendMessage("§6§l===========================");
    }
}
