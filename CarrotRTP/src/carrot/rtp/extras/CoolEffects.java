package carrot.rtp.extras;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Random;

public final class CoolEffects {
    public static void SpawnLightning(Player player){
        try{
            if (player != null && player.getWorld() != null) {
                World w = player.getWorld();
                w.strikeLightningEffect(player.getLocation());
            }
        }
        catch (Exception ignored){

        }
    }

    public static String RainbowText(String text){
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(text.length() * 3);
        for(int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            switch (random.nextInt(6)) {
                case 0: buffer.append(ChatColor.RED).append(String.valueOf(character)); break;
                case 1: buffer.append(ChatColor.GOLD).append(String.valueOf(character)); break;
                case 2: buffer.append(ChatColor.YELLOW).append(String.valueOf(character)); break;
                case 3: buffer.append(ChatColor.GREEN).append(String.valueOf(character)); break;
                case 4: buffer.append(ChatColor.AQUA).append(String.valueOf(character)); break;
                case 5: buffer.append(ChatColor.BLUE).append(String.valueOf(character)); break;
                case 6: buffer.append(ChatColor.DARK_PURPLE).append(String.valueOf(character)); break;
                default: buffer.append(ChatColor.WHITE).append(String.valueOf(character)); break;
            }
        }
        return buffer.toString();
    }
}
