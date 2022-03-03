package com.redsifter.score.listeners;

import com.redsifter.hideandseek.listeners.CustomEventHs;
import com.redsifter.score.Score;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Listen implements Listener {

    @EventHandler
    public void onCustomEventHs(CustomEventHs event) throws FileNotFoundException {
        int amount = 0;
        switch(event.getName()){
            case "SeekerFoundHider": {
                amount = 20;
                break;
            }
            case "ThermoSignalSeeker": {
                amount = 40;
                break;
            }
            case "ThermoSignalHider": {
                amount = 30;
                break;
            }
            case "MysteryChestUse": {
                amount = 5;
                break;
            }
            case "SpectralArrowHit": {
                amount = 10;
                break;
            }
            case "SlownessArrowHit": {
                amount = 15;
                break;
            }
            case "PlayerTripped": {
                amount = -10;
                break;
            }
            case "TeamWin": {
                amount = 200;
                break;
            }
        }
        setPoints(event.getPlayer(), amount);
    }

    public void setPoints(Player pl, int amount) throws FileNotFoundException {
        int currentAmount = 0;
        if(Score.scores.getConfig().contains("HideAndSeek."+pl.getName())) {
            currentAmount = Score.scores.getConfig().getInt("HideAndSeek." + pl.getName());
        }
        try {
            Score.scores.getConfig().set("HideAndSeek."+ pl.getName(), currentAmount + amount);
            Score.scores.saveConfig();
            pl.sendMessage(ChatColor.GOLD+"+"+amount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
