package neo.rpgsystem.integration.mmocore;

import io.lumine.mythic.lib.MythicLib;
import neo.rpgsystem.abilities.mmocore.humans.HumansActive1Handler;
import neo.rpgsystem.abilities.mmocore.humans.HumansActive2Handler;
import neo.rpgsystem.abilities.mmocore.humans.HumansPassive1Handler;

/**
 * Class đăng ký tất cả Human skill handlers với MythicLib
 */
public class HumanSkillsRegistry {
    
    /**
     * Đăng ký tất cả Human skill handlers
     */
    public static void registerAll() {
        MythicLib.plugin.getSkills().registerSkillHandler(new HumansActive1Handler());
        MythicLib.plugin.getSkills().registerSkillHandler(new HumansActive2Handler());
        MythicLib.plugin.getSkills().registerSkillHandler(new HumansPassive1Handler());
    }
}
