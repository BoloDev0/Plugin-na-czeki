package bolodev.bolodevczek;

import bolodev.bolodevczek.cmd.CzekCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Main extends JavaPlugin {

    private Economy economy;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Nie znalezionu pluginu VAULT i ESSENTIALSX!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        CzekCommand czekCommand = new CzekCommand(this);
        getCommand("wyplac").setExecutor(czekCommand);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Nie znaleziono VAULT!");
            return false;
        }

        if (Bukkit.getServicesManager().isProvidedFor(Economy.class)) {
            economy = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
        } else {
            getLogger().severe("Nie znaleziono VAULT");
            return false;
        }

        return (economy != null);
    }

    public Economy getEconomy() {
        return economy;
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.RED + message);
    }

    public void sendSuccessMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.GREEN + message);
    }
}
