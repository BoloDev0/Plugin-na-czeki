package bolodev.bolodevczek.cmd;

import bolodev.bolodevczek.Main;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;

public class CzekCommand implements CommandExecutor, Listener {

    private final Main plugin;

    public CzekCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.sendMessage(sender, "pojebalo cie? w konsoli chcesz komende dla graczy wpisywac ogarnij sie ziomus ;d");
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 1) {
            return false;
        }

        double amount;

        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            plugin.sendMessage(sender, "Podana kwota jest nieprawidłowa!");
            return true;
        }

        if (plugin.getEconomy().has(player, amount)) {
            EconomyResponse response = plugin.getEconomy().withdrawPlayer(player, amount);
            if (response.transactionSuccess()) {
                ItemStack czek = new ItemStack(Material.PAPER);
                ItemMeta meta = czek.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(ChatColor.GOLD.toString() + "Czek");
                    meta.setLore(java.util.Arrays.asList(
                            "",
                            ChatColor.GREEN.toString() + "Czek na: " + ChatColor.GOLD.toString() + amount,
                            ""
                    ));
                    czek.setItemMeta(meta);
                }
                player.getInventory().addItem(czek);
                plugin.sendSuccessMessage(player, "Wypłacono czek na kwotę " + amount + "!");
            } else {
                plugin.sendMessage(player, response.errorMessage);
            }
        } else {
            plugin.sendMessage(player, "Nie posiadasz tyle pieniędzy!");
        }
        return true;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.PAPER && item.hasItemMeta() && (ChatColor.GOLD.toString() + "Czek").equals(item.getItemMeta().getDisplayName())) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getLore() != null) {
                String lore = meta.getLore().get(1);
                if (lore.startsWith(ChatColor.GREEN.toString() + "Czek na: " + ChatColor.GOLD.toString())) {
                    double amount;
                    try {
                        amount = Double.parseDouble(ChatColor.stripColor(lore.replace(ChatColor.GREEN.toString() + "Czek na: " + ChatColor.GOLD.toString(), "")));
                    } catch (NumberFormatException e) {
                        plugin.sendMessage(player, "Nieprawidłowy czek!");
                        return;
                    }

                    EconomyResponse response = plugin.getEconomy().depositPlayer(player, amount);
                    if (response.transactionSuccess()) {
                        player.getInventory().removeItem(item);
                        plugin.sendSuccessMessage(player, "Wypłacono czek na " + amount + "!");
                    } else {
                        plugin.sendMessage(player, response.errorMessage);
                    }
                }
            }
        }
    }
}
