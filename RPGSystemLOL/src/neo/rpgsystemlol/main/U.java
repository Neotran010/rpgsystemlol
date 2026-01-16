package neo.rpgsystemlol.main;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class U {
	
	public static List<String> colorList(List<String> list){
		if(list == null || list.isEmpty()) return list;
		List<String> list2 = new ArrayList<>(list.size());
		list.forEach(s -> {
			list2.add(ChatColor.translateAlternateColorCodes('&', s));
		});
		
		return list2;
	}
	
	/**
	 * 
	 * @return thời gian còn lại cho đến nextTime
	 */
	public static long getRemainingTime(long nextTime) {
		return Math.max(0, TimeUnit.SECONDS.convert(nextTime-System.currentTimeMillis(), TimeUnit.MILLISECONDS));
	}

	/**
	 * 
	 * @return thời gian từ from cho đến now
	 */
	public static long getSecondTime(long fromTime) {
		return Math.max(0, TimeUnit.SECONDS.convert(System.currentTimeMillis()-fromTime, TimeUnit.MILLISECONDS));
	}
	
	public static class TargetResult {
		private LivingEntity target;
		private Block block;
		public TargetResult(LivingEntity target, Block block) {
			super();
			this.target = target;
			this.block = block;
		}
		public LivingEntity getTarget() {
			return target;
		}
		public void setTarget(LivingEntity target) {
			this.target = target;
		}
		public Block getBlock() {
			return block;
		}
		public void setBlock(Block block) {
			this.block = block;
		}
	}
	
	public static TargetResult getLineTarget(Player p, Location start, Vector dir, double range, double distance) {
		for(double d = 0 ; d<distance; d+=range) {
			Location loc = start.clone().add(dir.clone().multiply(d));
			List<LivingEntity> targets = getTargets(p, loc, range);
			if(!targets.isEmpty()) {
				return new TargetResult(targets.get(0), null);
			}
			Block b = loc.getBlock();
			if(b != null && !b.getType().equals(Material.AIR)) {
				return new TargetResult(null, b);
			}
		}

		double d = distance;
		Location loc = start.clone().add(dir.clone().multiply(d));
		return new TargetResult(null, loc.getBlock());
	}
	
	public static List<LivingEntity> getTargets(Player p, Location l, double range){
		List<LivingEntity> list = new ArrayList<>();
		for(Entity e : l.getWorld().getNearbyEntities(l, range, range, range)) {
			if(isTarget(p, e)) list.add((LivingEntity) e);
		}
		
		return list;
	}
	
	public static boolean isTarget(Player p, Entity e) {
		if(e instanceof LivingEntity && !p.equals(e)) return true;
		
		return false;
	}
	
	public static boolean isClickTop(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		InventoryView iv = e.getView();
		if(inv == null) return false;
		if(iv.getTopInventory() == null) return false;
		
		return inv.equals(iv.getTopInventory());
	}
	
	public static void fakePickupItem(Item i, Player taker) {
//		PacketPlayOutCollect packet = new PacketPlayOutCollect(i.getEntityId(), taker.getEntityId(), 1);
//		((CraftPlayer)taker).getHandle().playerConnection.sendPacket(packet);
//		
//		U.playSound(taker, Sound.ENTITY_ITEM_PICKUP, null);
	}
	
	public static void holo(Location l, String msg, int liveTime) {
//		Hologram hd = HologramsAPI.createHologram(Main.pl, l);
//		hd.appendTextLine(msg);
//		new BukkitRunnable() {
//			
//			@Override
//			public void run() {
//				hd.delete();
//			}
//		}.runTaskLater(Main.pl, liveTime);
	}
	
	public static double calc(final String str) {
	    return new Object() {
	        int pos = -1, ch;
	        
	        void nextChar() {
	            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	        }
	        
	        boolean eat(int charToEat) {
	            while (ch == ' ') nextChar();
	            if (ch == charToEat) {
	                nextChar();
	                return true;
	            }
	            return false;
	        }
	        
	        double parse() {
	            nextChar();
	            double x = parseExpression();
	            if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
	            return x;
	        }
	        
	        // Grammar:
	        // expression = term | expression `+` term | expression `-` term
	        // term = factor | term `*` factor | term `/` factor
	        // factor = `+` factor | `-` factor | `(` expression `)` | number
	        //        | functionName `(` expression `)` | functionName factor
	        //        | factor `^` factor
	        
	        double parseExpression() {
	            double x = parseTerm();
	            for (;;) {
	                if      (eat('+')) x += parseTerm(); // addition
	                else if (eat('-')) x -= parseTerm(); // subtraction
	                else return x;
	            }
	        }
	        
	        double parseTerm() {
	            double x = parseFactor();
	            for (;;) {
	                if      (eat('*')) x *= parseFactor(); // multiplication
	                else if (eat('/')) x /= parseFactor(); // division
	                else return x;
	            }
	        }
	        
	        double parseFactor() {
	            if (eat('+')) return +parseFactor(); // unary plus
	            if (eat('-')) return -parseFactor(); // unary minus
	            
	            double x;
	            int startPos = this.pos;
	            if (eat('(')) { // parentheses
	                x = parseExpression();
	                if (!eat(')')) throw new RuntimeException("Missing ')'");
	            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
	                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
	                x = Double.parseDouble(str.substring(startPos, this.pos));
	            } else if (ch >= 'a' && ch <= 'z') { // functions
	                while (ch >= 'a' && ch <= 'z') nextChar();
	                String func = str.substring(startPos, this.pos);
	                if (eat('(')) {
	                    x = parseExpression();
	                    if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
	                } else {
	                    x = parseFactor();
	                }
	                if (func.equals("sqrt")) x = Math.sqrt(x);
	                else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
	                else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
	                else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
	                else throw new RuntimeException("Unknown function: " + func);
	            } else {
	                throw new RuntimeException("Unexpected: " + (char)ch);
	            }
	            
	            if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation
	            
	            return x;
	        }
	    }.parse();
	}
	
	public static boolean is(InventoryView iv, String name) {
		if(iv == null || iv.getTitle() == null) return false;
		
		return iv.getTitle().equals(name);
	}
	
	public static double getMoney(Player p) {
//		return Main.econ.getBalance(p);
		return 0;
	}
	
	public static boolean costMoney(Player p, int cost) {
//		return Main.econ.withdrawPlayer(p, cost).transactionSuccess();
		return false;
	}
	
	public static Location stringToLocation(String s) {
		
		String[] ss = s.split("\\s");
		return new Location(Bukkit.getWorld(ss[0]), 
				Double.parseDouble(ss[1]), 
				Double.parseDouble(ss[2]), 
				Double.parseDouble(ss[3]));
	}
	
	public static ItemStack buildItem(Material m, String displayName, List<String> lore, boolean enchant, int amount) {
		ItemStack i = new ItemStack(m);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName(displayName);
		im.setLore(lore);
		im.addItemFlags(ItemFlag.values());
		if(enchant) {
			im.addEnchant(Enchantment.UNBREAKING, 1, false);
		}
		
		i.setItemMeta(im);
		return i;
	}
	
	public static void playSound(Player p, Sound s, Location l) {
		if(p == null) {
			if(l == null) return;
			l.getWorld().playSound(l, s, 16, 16);
		}
		if(l == null) {
			p.playSound(p.getLocation(), s, (float)U.randomD(1, 16), (float)U.randomD(1, 16));
		}else {
			p.playSound(l, s, (float)U.randomD(1, 16), (float)U.randomD(1, 16));
		}
	}
	
	public static void playSound(Player entity, Sound sound, float volume, float pitch) {
        entity.playSound(entity.getLocation(), sound, volume, pitch);
    }	
	
	public static double getHPPercent(LivingEntity le) {
		double hp = le.getHealth();
		double maxHP = le.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
		return hp/maxHP;
	}
	
	public static String lamtronString2(double d) {
	    if (d >= 1) {
	        return String.format("%.0f", d);
	    } else if (d >= 0.1) {
	        return String.format("%.1f", d);
	    } else {
		    DecimalFormat decimalFormat = new DecimalFormat("#.###");
	        return decimalFormat.format(d);
	    }
	}
	
	private static ItemStack quitItem = null;
	public static ItemStack getQuitItem() {
		if(quitItem == null) {
			quitItem = new ItemStack(Material.BARRIER);
			ItemMeta im = quitItem.getItemMeta();
			im.setDisplayName("§cThoát");
			quitItem.setItemMeta(im);
		}
		
		return quitItem.clone();
	}
	
    public static String lamTronString(final double i) {
        final DecimalFormat df = new DecimalFormat("#.##");
        return df.format(i).replace(",", ".");
    }
	
	public static double getDRandom(double min, double max) {
		if(max <= min) return min;
		
		return ThreadLocalRandom.current().nextDouble(min, max);
	} 
	
	public static int getRandom(int min, int max) {
		if(max <= min) return min;
		return ThreadLocalRandom.current().nextInt(min, max);
	}
	
	public static String locationToString(Location l) {
		
		return l.getWorld().getName() + " " 
		+ l.getX() + " " + l.getY() + " " + l.getZ();
	}
	
	public static String convertSecondsToTimeFormat(long secondTime) {
	    long minutes = TimeUnit.SECONDS.toMinutes(secondTime);
	    long seconds = secondTime - TimeUnit.MINUTES.toSeconds(minutes);
	    return String.format("%02d:%02d", minutes, seconds);
	}

	/**
	 * 
	 * @return dd-MM-yyyy
	 */
	public static String formatDate(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(time);
        return dateFormat.format(date);
    }
	
	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static double randomD(double min, double max) {
		if(max <= min) return min;
		
		return ThreadLocalRandom.current().nextDouble(min, max);
	}

	public static String getLama(int rank) {
	    switch (rank) {
	        case 1: return "I";
	        case 2: return "II";
	        case 3: return "III";
	        case 4: return "IV";
	        case 5: return "V";
	        case 6: return "VI";
	        case 7: return "VII";
	        case 8: return "VIII";
	        case 9: return "IX";
	        case 10: return "X";
	        default: return "";
	    }
	}

	public static void sendMSG(Player p, String string) {
		p.sendMessage(string);
		
	}

	public static ItemStack getDone() {
		return buildItem(Material.ACACIA_DOOR, "§c§lThoát", null, false, 1);
	}

	public static void playSound(Player p, Sound sound) {
		playSound(p, sound, p.getLocation());
	}
	

	public static void addPotion(LivingEntity le, PotionEffectType type, int level, int duration) {
		le.addPotionEffect(new PotionEffect(type, duration, level));
	}
	
	public static double getMaxHealth(LivingEntity le) {
		return le.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
	}

	public static double random(double min, double max) {
		if(max <= min) return min;
		
		return ThreadLocalRandom.current().nextDouble(min, max);
	}

	public static void playSound(Location location, Sound blockTripwireClickOn) {
		location.getWorld().playSound(location, blockTripwireClickOn, 16, 16);
	}

}
