package com.redsifter.score.listeners;

import com.redsifter.factions.listeners.CustomEventFactions;
import com.redsifter.hideandseek.listeners.CustomEventHs;
import com.redsifter.score.Score;
import org.bukkit.Bukkit;
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
        setPoints("HideAndSeek",event.getPlayer(), amount);
    }

    @EventHandler
    public void onCustomEventFactions(CustomEventFactions event) throws IOException {
        Score.money.reloadConfig();
        if(!Score.money.getConfig().isConfigurationSection("Factions")){
            Score.money.getConfig().set("Factions.conversion_rate",1);
            Score.money.saveConfig();
        }
        switch(event.getName()){
            case "PlayerFund": {
                Score.money.reloadConfig();
                if(Score.money.getConfig().contains("Factions.players."+event.getPlayer().getName())){
                    double amount = Score.money.getConfig().getDouble("Factions.players."+event.getPlayer().getName());
                    if(amount <= event.getAmount()){
                        setMoney("Factions",event.getPlayer(),-amount);
                        //Event called
                        CustomEventScore ev = null;
                        ev = new CustomEventScore("ScorePlayerFund", event.getPlayer(), event.getAmount(), event.getFaction());
                        Bukkit.getServer().getPluginManager().callEvent(ev);
                        event.getPlayer().sendMessage(ChatColor.GREEN+"Successfully funded your faction "+event.getAmount()+" Factions coins !");
                        return;
                    }
                }
                else{
                    Score.money.getConfig().set("Factions.players."+event.getPlayer().getName(),0);
                    Score.money.saveConfig();
                }
                event.getPlayer().sendMessage(ChatColor.RED+"You don't have enough Factions money !");
                break;
            }
        }
    }

    public void setPoints(String score,Player pl, int amount) throws FileNotFoundException {
        Score.scores.reloadConfig();
        int currentAmount = 0;
        if(Score.scores.getConfig().contains(score+"."+pl.getName())) {
            currentAmount = Score.scores.getConfig().getInt(score+"." + pl.getName());
        }
        try {
            Score.scores.getConfig().set(score+"."+ pl.getName(), currentAmount + amount);
            Score.scores.saveConfig();
            pl.sendMessage(ChatColor.GOLD+"+"+amount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMoney(String currency,Player pl, double amount) throws FileNotFoundException {
        Score.money.reloadConfig();
        double currentAmount = 0;
        if(Score.money.getConfig().contains(currency+".players."+pl.getName())) {
            currentAmount = Score.money.getConfig().getDouble(currency+".players." + pl.getName());
        }
        try {
            Score.money.getConfig().set(currency+".players."+ pl.getName(), currentAmount + amount);
            Score.money.saveConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
