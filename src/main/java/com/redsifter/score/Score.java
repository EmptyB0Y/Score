package com.redsifter.score;

import com.redsifter.score.listeners.Listen;
import  com.redsifter.score.utils.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public final class Score extends JavaPlugin {
    public static FileManager scores;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new Listen(), this);
        getDataFolder().mkdir();
        try {
            scores = new FileManager("scores.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        if (cmd.getName().equals("setscore")) {
            Player pl = Bukkit.getPlayerExact(args[0]);
            if (pl != null && Integer.parseInt(args[1]) >= 0 && args.length == 3) {
                try {
                    scores.getConfig().set(args[2] +"."+ pl.getName(), Integer.parseInt(args[1]));
                    scores.saveConfig();
                    sender.sendMessage("Score set successfully !");
                } catch (IOException e) {
                    sender.sendMessage("Failed to set score...");
                    e.printStackTrace();
                }
            }
            else {
                return false;
            }

        }
        else if(cmd.getName().equals("showscore")){
            if(args.length >= 1) {
                try {
                    if (scores.getConfig().contains(args[0])) {
                        Set<String> keys = null;
                        try {
                            keys = Objects.requireNonNull(scores.getConfig().getConfigurationSection(args[0])).getKeys(true);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (keys != null) {
                            sender.sendMessage(args[0] + " :");
                            for (String i : keys) {
                                if(args.length == 2){
                                    if(scores.getConfig().contains(args[0]+"."+args[1])){
                                        sender.sendMessage("Score for player "+args[1]+" is : "+scores.getConfig().get(args[0] + "." + args[1]));
                                        return true;
                                    }
                                    else{
                                        sender.sendMessage("Player not found !");
                                        return false;
                                    }
                                }
                                else {
                                    try {
                                        sender.sendMessage(i + " : " + scores.getConfig().get(args[0] + "." + i));
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(cmd.getName().equals("remcurrency")) {
            try {
                if(scores.getConfig().getString(args[0]) != null){
                    scores.getConfig().set(args[0],null);
                    scores.saveConfig();
                    sender.sendMessage("Currency removed !");
                }
                else{
                    sender.sendMessage("Currency not found !");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;

    }
}

