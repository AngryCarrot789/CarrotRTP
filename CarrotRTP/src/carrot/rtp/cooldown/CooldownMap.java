package carrot.rtp.cooldown;

import carrot.rtp.CarrotRTP;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class CooldownMap {
    public static HashMap<Player, Integer> Cooldowns;

    public static Runnable CooldownCounter;
    public static int CooldownCountdownInterval = 2;
    public static int RunnerID = 0;

    public static void Initialise(){
        Cooldowns = new HashMap<Player, Integer>();
        CooldownCounter = new Runnable() {
            @Override
            public void run() {
                DecreaseCooldownTimes();
            }
        };
        RunnerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(CarrotRTP.instance, CooldownCounter, 0, 20L * CooldownCountdownInterval);
    }

    public static boolean CanRTP(Player player){
        try{
            return !Cooldowns.containsKey(player);
        }
        catch (Exception e){
            return false;
        }
    }

    public static void DecreaseCooldownTimes(){
        try {
            ArrayList<Player> toBeRemoved = new ArrayList<Player>(4);
            for (Map.Entry<Player, Integer> pair : Cooldowns.entrySet()) {
                int cooldown = pair.getValue() - CooldownCountdownInterval;
                if (cooldown <= 0) {
                    toBeRemoved.add(pair.getKey());
                }
                pair.setValue(cooldown);
            }
            for (Player player : toBeRemoved) {
                Cooldowns.remove(player);
            }
        }
        catch (Exception e){

        }
    }

    public static void CancelRunner(){
        Bukkit.getScheduler().cancelTask(RunnerID);
    }
}
