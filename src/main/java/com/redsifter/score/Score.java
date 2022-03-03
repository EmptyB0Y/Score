package com.redsifter.score;

import com.redsifter.score.listeners.Listen;
import  com.redsifter.score.utils.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public final class Score extends JavaPlugin {
    public static FileManager scores;
    public static FileManager money;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new Listen(), this);
        getDataFolder().mkdir();
        try {
            scores = new FileManager("scores.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            money = new FileManager("currencies.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        switch (cmd.getName()) {
            case "setscore":
                Player pl = Bukkit.getPlayerExact(args[0]);
                if (pl != null && Integer.parseInt(args[1]) >= 0 && args.length == 3) {
                    try {
                        scores.getConfig().set(args[2] + "." + pl.getName(), Integer.parseInt(args[1]));
                        scores.saveConfig();
                        sender.sendMessage(ChatColor.GREEN+"Score set successfully !");
                    } catch (IOException e) {
                        sender.sendMessage(ChatColor.RED+"Failed to set score...");
                        e.printStackTrace();
                    }
                } else {
                    return false;
                }

                break;
            case "showscore":
                if (args.length > 2) {
                    sender.sendMessage("Wrong argument amount !");
                    return false;
                }
                try {
                    if (scores.getConfig().contains(args[0])) {
                        Set<String> keys = null;
                        try {
                            keys = Objects.requireNonNull(scores.getConfig().getConfigurationSection(args[0])).getKeys(true);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (keys != null) {
                            sender.sendMessage(ChatColor.DARK_GRAY+args[0] + " :");
                            for (String i : keys) {
                                if (args.length == 2) {
                                    if (scores.getConfig().contains(args[0] + "." + args[1])) {
                                        sender.sendMessage("Score for player " + args[1] + " is : " + scores.getConfig().get(args[0] + "." + args[1]));
                                        return true;
                                    } else {
                                        sender.sendMessage(ChatColor.RED+"Player not found !");
                                        return false;
                                    }
                                } else {
                                    try {
                                        sender.sendMessage(i + " : " + scores.getConfig().get(args[0] + "." + i));
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                break;
            case "remscore":
                try {
                    if (scores.getConfig().getString(args[0]) != null) {
                        scores.getConfig().set(args[0], null);
                        scores.saveConfig();
                        sender.sendMessage(ChatColor.GREEN+"Score removed !");
                    } else {
                        sender.sendMessage(ChatColor.RED+"Score not found !");
                        return false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "setcurrency":
                if (args.length != 2) {
                    sender.sendMessage("Set a name and a conversion factor, example : addcurrency gold 2.5");
                    return false;
                }
                if (Double.parseDouble(args[1]) > 0) {
                    try {
                        money.getConfig().set(args[0] + ".conversion_rate", Double.parseDouble(args[1]));
                        money.saveConfig();
                        sender.sendMessage(ChatColor.GREEN+"Currency set successfully !");
                    } catch (IOException e) {
                        sender.sendMessage(ChatColor.RED+"Failed to set currency...");
                        e.printStackTrace();
                    }
                } else {
                    sender.sendMessage(ChatColor.RED+"The conversion factor cannot be below or equal to 0 !");
                    return false;
                }
                break;
            case "remcurrency":
                try {
                    if (money.getConfig().getString(args[0]) != null) {
                        money.getConfig().set(args[0], null);
                        money.saveConfig();
                        sender.sendMessage(ChatColor.GREEN+"Currency removed !");
                    } else {
                        sender.sendMessage(ChatColor.RED+"Currency not found !");
                        return false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "withdraw-all":
                if (args.length != 2) {
                    sender.sendMessage("Wrong argument amount !");
                    return false;
                }
                try {
                    if (convert(args[0], args[1])) {
                        sender.sendMessage(ChatColor.GREEN+"Global conversion successful !");
                    } else {
                        sender.sendMessage(ChatColor.RED+"Global conversion failed, check that the values you entered exist !");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case "showcurrency":
                if (args.length > 2 || args.length < 1) {
                    sender.sendMessage("Wrong argument amount !");
                    return false;
                }
                try {
                    if (money.getConfig().contains(args[0])) {
                        Set<String> keys = null;
                        if(money.getConfig().isConfigurationSection(args[0]+".players")) {
                            try {
                                keys = Objects.requireNonNull(money.getConfig().getConfigurationSection(args[0] + ".players")).getKeys(true);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }

                        sender.sendMessage(ChatColor.GOLD + "["+args[0] + "] :");
                        sender.sendMessage("Conversion rate : " + money.getConfig().getDouble(args[0] + ".conversion_rate"));
                        if (keys != null) {
                            for (String i : keys) {
                                if(args.length == 2) {
                                    if (i.equals(args[1])){
                                        if(money.getConfig().isConfigurationSection(args[0]+".players."+args[1])) {
                                            sender.sendMessage(i + " : " + money.getConfig().getDouble(args[0] + ".players." + args[1]));
                                            return true;
                                        }
                                        else{
                                            sender.sendMessage(ChatColor.RED+"Player not found !");
                                            return false;
                                        }
                                    }
                                }
                                else {
                                    sender.sendMessage(i + " : " + money.getConfig().getDouble(args[0] + ".players." + i));
                                }
                            }
                            if(args.length == 2){
                                sender.sendMessage(ChatColor.RED+"Player not found !");
                                return false;
                            }
                        }
                        else{
                            return false;
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                break;
            case "setconvrate":
                if (args.length != 2) {
                    sender.sendMessage("Wrong argument amount !");
                    return false;
                }
                if(Double.parseDouble(args[1]) <= 0){
                    return false;
                }

                try {
                    if (money.getConfig().contains(args[0])) {
                        money.getConfig().set(args[0]+".conversion_rate",Double.parseDouble(args[1]));
                        money.saveConfig();
                        sender.sendMessage("Conversion rate set !");
                    }
                    else{
                        sender.sendMessage("Currency not found !");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "withdraw":
                if (args.length != 2) {
                    sender.sendMessage("Wrong argument amount !");
                    return false;
                }
                if(sender instanceof Player) {
                    try {
                        if (convertOne(args[0], args[1], (Player) sender)) {
                            sender.sendMessage(ChatColor.DARK_GREEN+"Withdrawal successful !");
                        } else {
                            sender.sendMessage(ChatColor.RED+"Withdrawal failed, check that the values you entered exist !");
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "convert":
                if (args.length > 3 || args.length < 2) {
                    sender.sendMessage("Wrong argument amount !");
                    return false;
                }
                if(sender instanceof Player) {
                    double am = 0;
                    if(args.length == 3){
                        am = Double.parseDouble(args[2]);
                    }
                    try {
                        if (convertCurrencies(args[0], args[1], (Player) sender,am)) {
                            sender.sendMessage(ChatColor.GREEN+"Conversion successful !");
                        } else {
                            sender.sendMessage(ChatColor.RED+"Conversion failed, check that the values you entered exist !");
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "sendmoney":
                if (args.length != 3) {
                    sender.sendMessage("Wrong argument amount !");
                    return false;
                }
                if(Double.parseDouble(args[2]) <= 0){
                    sender.sendMessage(ChatColor.RED+"You can't send 0 or less !");
                    return false;
                }
                if(args[0].equals(sender.getName())){
                    sender.sendMessage(ChatColor.RED+"You can't send money to yourself !");
                    return false;
                }

                if(sender instanceof Player){
                    Set<String> keys = null;
                    try {
                        keys = Objects.requireNonNull(money.getConfig().getConfigurationSection("").getKeys(true));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(keys != null) {
                        for (String i : keys) {
                            if (i.equals(args[1])) {
                                try {
                                    String receivingPlayer = i + ".players." + args[0];
                                    String sendingPlayer = args[1]+".players."+ sender.getName();
                                    if (money.getConfig().contains(receivingPlayer)) {
                                        if(money.getConfig().contains(sendingPlayer)) {

                                            double amount = money.getConfig().getDouble(sendingPlayer);
                                            double amount2 = money.getConfig().getDouble(receivingPlayer);
                                            double sending = Double.parseDouble(args[2]);

                                            if (sending <= amount) {
                                                money.getConfig().set(sendingPlayer,amount-sending);
                                                money.getConfig().set(receivingPlayer,amount2+sending);
                                                sender.sendMessage(ChatColor.GREEN+"You sent "+args[2]+" "+args[1]+" to "+i+" !");

                                                if(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayerExact(args[0]))) {
                                                    Bukkit.getPlayerExact(args[0]).sendMessage(ChatColor.DARK_GREEN + "You received " + args[2] + " from " + sender.getName()+" !");
                                                }
                                                money.saveConfig();
                                                return true;
                                            }
                                        }
                                        else{
                                            sender.sendMessage(ChatColor.RED+"You are not registered for that currency !");
                                            return false;
                                        }

                                    } else {
                                        sender.sendMessage(ChatColor.RED + "Player not found !");
                                        return false;
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        sender.sendMessage(ChatColor.RED + "Currency not found !");
                    }
                }
                break;
            case "purse":
                if (args.length > 1) {
                    sender.sendMessage("Wrong argument amount !");
                    return false;
                }
                if(sender instanceof Player) {
                    Set<String> keys = null;
                    try {
                        keys = Objects.requireNonNull(money.getConfig().getConfigurationSection("").getKeys(false));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (keys != null) {
                        for (String i : keys) {
                            this.getLogger().info(i);
                            try {
                                if (args.length == 1) {
                                    if (i.equals(args[0])) {
                                        if (money.getConfig().contains(i + ".players." + sender.getName())) {
                                            sender.sendMessage(ChatColor.GOLD + "[" + i + "] :");
                                            sender.sendMessage("" + money.getConfig().getDouble(i + ".players." + sender.getName()));
                                            return true;
                                        }
                                    }
                                } else {
                                    if (money.getConfig().contains(i + ".players." + sender.getName())) {
                                        sender.sendMessage(ChatColor.GOLD + "[" + i + "] :");
                                        sender.sendMessage("" + money.getConfig().getDouble(i + ".players." + sender.getName()));
                                    }
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
                break;
            case "listscores":
                Set<String> scorekeys = null;
                try {
                    scorekeys = Objects.requireNonNull(scores.getConfig().getConfigurationSection("").getKeys(false));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                sender.sendMessage(ChatColor.DARK_GRAY+"[SCORES]");
                if (scorekeys != null) {
                    for (String i : scorekeys) {
                        sender.sendMessage(i);
                    }
                }
                break;
            case "listcurrencies":
                Set<String> currencykeys = null;
                try {
                    currencykeys = Objects.requireNonNull(money.getConfig().getConfigurationSection("").getKeys(false));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                sender.sendMessage(ChatColor.GOLD+"[CURRENCIES]");
                if (currencykeys != null) {
                    for (String i : currencykeys) {
                        sender.sendMessage(i);
                    }
                }
                break;
        }
        return true;

    }

    public boolean convert(String score, String currency) throws FileNotFoundException {
        if (scores.getConfig().contains(score)) {
            if (money.getConfig().contains(currency)) {
                Set<String> keys = null;
                try {
                    keys = Objects.requireNonNull(scores.getConfig().getConfigurationSection(score).getKeys(true));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                double conversion = money.getConfig().getDouble(currency+".conversion_rate");
                if (keys != null) {
                    for (String i : keys) {
                        int value = scores.getConfig().getInt(score+"."+i);
                        double amount = 0;
                        if(money.getConfig().contains(currency+".players."+i)){
                            amount += money.getConfig().getDouble(currency+".players."+i);
                        }
                        money.getConfig().set(currency+".players."+i,amount +(value * conversion));
                        scores.getConfig().set(score+"."+i,0);
                    }
                }
                try {
                    money.saveConfig();
                    scores.saveConfig();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean convertOne(String score, String currency,Player pl) throws FileNotFoundException {
        if (scores.getConfig().contains(score)) {
            if (money.getConfig().contains(currency)) {
                Set<String> keys = null;
                try {
                    keys = Objects.requireNonNull(scores.getConfig().getConfigurationSection(score).getKeys(true));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                double conversion = money.getConfig().getDouble(currency+".conversion_rate");
                if (keys != null) {
                    for (String i : keys) {
                        if(i.equals(pl.getName())) {
                            int value = scores.getConfig().getInt(score + "." + i);
                            double amount = 0;
                            if (money.getConfig().contains(currency + ".players." + i)) {
                                amount += money.getConfig().getDouble(currency + ".players." + i);
                            }
                            money.getConfig().set(currency + ".players." + i, amount + (value * conversion));
                            scores.getConfig().set(score + "." + i, 0);
                            break;
                        }
                    }
                }
                try {
                    money.saveConfig();
                    scores.saveConfig();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean convertCurrencies(String currency1, String currency2, Player pl, double am) throws FileNotFoundException {
        if (money.getConfig().isConfigurationSection(currency1) && money.getConfig().isConfigurationSection(currency2)) {
            Set<String> keys = null;
            try {
                keys = Objects.requireNonNull(money.getConfig().getConfigurationSection(currency1+".players").getKeys(true));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            double conversion1 = money.getConfig().getDouble(currency1 + ".conversion_rate");
            double conversion2 = money.getConfig().getDouble(currency2 + ".conversion_rate");
            double conversion = conversion1 / conversion2;
            if (keys != null) {
                for (String i : keys) {
                    if (i.equals(pl.getName())) {
                        double value = money.getConfig().getDouble(currency1 + ".players." + i);
                        double amount = 0;
                        double total = 0;
                        double sent = value;
                        if(am != 0) {
                            if (am > 0 && am <= value) {
                                total = value - am;
                                sent = value - total;
                            } else {
                                return false;
                            }
                        }
                        if (money.getConfig().contains(currency2 + ".players." + i)) {
                            amount += money.getConfig().getDouble(currency2 + ".players." + i);
                        }
                        System.out.println(am);
                        if(am == 0){
                            System.out.println("test");
                        }
                        money.getConfig().set(currency2 + ".players." + i, amount + (sent * (1 / conversion)));
                        money.getConfig().set(currency1 + ".players." + i, total);
                        break;
                    }
                }
            }
            try {
                money.saveConfig();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

}
