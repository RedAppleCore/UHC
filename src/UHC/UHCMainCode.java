package UHC;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.CylinderRegionSelector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class UHCMainCode extends JavaPlugin implements Listener {

    public static final Logger logger = Logger.getLogger("Minecraft");
    public static UHCMainCode plugin;
    List<String> players = new ArrayList<String>();
    List<String> spec = new ArrayList<String>();
    List<String> dis = new ArrayList<String>();
    List<String> rr = new ArrayList<String>();
    public boolean ingame = false;
    public boolean pvp = false;
    public int counter;
    public static HashMap<String, ItemStack[]> inventoryContents = new HashMap<String, ItemStack[]>();
    public static HashMap<String, ItemStack[]> inventoryArmorContents = new HashMap<String, ItemStack[]>();
    public static HashMap<String, Location> location = new HashMap<String, Location>();
    private ItemStack skull;
    private ItemStack ga;

    @Override
    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        UHCMainCode.logger.info(pdfFile.getName() + " Has Been Disabled!");
    }

    @Override
    public void onEnable() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(this, this);
        PluginDescriptionFile pdfFile = this.getDescription();
        UHCMainCode.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " Has Been Enabled!");
        Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.GOLD + "Matty's UHC Plugin Version " + pdfFile.getVersion() + " : Online");

        counter = 0;

        for (World w : getServer().getWorlds()) {
            for (Chunk c : w.getLoadedChunks()) {
                for (Entity e : c.getEntities()) {
                    if (e.getType() == EntityType.VILLAGER) {
                        e.remove();
                    }
                }
            }
        }

        ga = new ItemStack(Material.GOLDEN_APPLE, 1);
        ItemMeta appleMeta = (ItemMeta) ga.getItemMeta();
        appleMeta.setDisplayName(ChatColor.DARK_PURPLE + "Golden Player Head");
        ga.setItemMeta(appleMeta);
        ShapedRecipe ghead = new ShapedRecipe(ga);
        ghead.shape("AAA", "ABA", "AAA");
        ghead.setIngredient('A', Material.GOLD_INGOT);
        ghead.setIngredient('B', Material.SKULL_ITEM, 3);
        getServer().addRecipe(ghead);
    }

    @EventHandler
    public void onBreakBlockEvent(BlockBreakEvent b) {
        if (ingame == false && !b.getPlayer().isOp()) {
            b.setCancelled(true);
        }
        if (spec.contains(b.getPlayer().getName())) {
            b.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreakPlaceEvent(BlockPlaceEvent p) {
        if (ingame == false && !p.getPlayer().isOp()) {
            p.setCancelled(true);
        }
        if (spec.contains(p.getPlayer().getName())) {
            p.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent mo) {
        Player player = mo.getPlayer();
        if (spec.contains(player.getName())) {
            for (Player everyone : Bukkit.getOnlinePlayers()) {
                everyone.hidePlayer(player);
            }
            player.setFoodLevel(60);
        } else {
            if (player.getName().length() >= 8) {
                if (player.getHealth() <= 20 && player.getHealth() >= 14) {
                    player.setPlayerListName(player.getName().substring(0, 7) + ChatColor.GREEN + " - " + mo.getPlayer().getHealth() / 2);
                } else if (player.getHealth() <= 13 && player.getHealth() >= 7) {
                    player.setPlayerListName(player.getName().substring(0, 7) + ChatColor.GOLD + " - " + mo.getPlayer().getHealth() / 2);
                } else if (player.getHealth() <= 6 && player.getHealth() >= 0) {
                    player.setPlayerListName(player.getName().substring(0, 7) + ChatColor.RED + " - " + mo.getPlayer().getHealth() / 2);
                }
            } else {
                if (player.getHealth() <= 20 && player.getHealth() >= 14) {
                    player.setPlayerListName(player.getName() + ChatColor.GREEN + " - " + mo.getPlayer().getHealth() / 2);
                } else if (player.getHealth() <= 13 && player.getHealth() >= 7) {
                    player.setPlayerListName(player.getName() + ChatColor.GOLD + " - " + mo.getPlayer().getHealth() / 2);
                } else if (player.getHealth() <= 6 && player.getHealth() >= 0) {
                    player.setPlayerListName(player.getName() + ChatColor.RED + " - " + mo.getPlayer().getHealth() / 2);
                }
            }
        }
    }

    @EventHandler
    public void rightclick(PlayerInteractEntityEvent e) {
        if (ingame == false) {
            if (e.getRightClicked() instanceof Villager) {
                Player p = (Player) e.getPlayer();
                e.setCancelled(true);
                p.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.GOLD + "Shop is closed right now. Did you seriously think you could buy stuff anyway?");
            }
            if (e.getRightClicked() instanceof Slime) {
                Player p = (Player) e.getPlayer();
                List<String> SlimeyMsgs = new ArrayList<String>();
                SlimeyMsgs.add(ChatColor.GREEN + "" + ChatColor.BOLD + "Slimey" + ChatColor.RESET + "" + ChatColor.GRAY + " » " + ChatColor.GOLD + "Hi there " + p.getDisplayName() + ChatColor.GOLD + " :)");
                SlimeyMsgs.add(ChatColor.GREEN + "" + ChatColor.BOLD + "Slimey" + ChatColor.RESET + "" + ChatColor.GRAY + " » " + ChatColor.GOLD + "I'm Slimy! Emilee's pet Slime! :D");
                SlimeyMsgs.add(ChatColor.GREEN + "" + ChatColor.BOLD + "Slimey" + ChatColor.RESET + "" + ChatColor.GRAY + " » " + ChatColor.GOLD + "Do you want to be best friends? :3");
                SlimeyMsgs.add(ChatColor.GREEN + "" + ChatColor.BOLD + "Slimey" + ChatColor.RESET + "" + ChatColor.GRAY + " » " + ChatColor.GOLD + "I love Emilee so much! She's the best! <3");
                SlimeyMsgs.add(ChatColor.GREEN + "" + ChatColor.BOLD + "Slimey" + ChatColor.RESET + "" + ChatColor.GRAY + " » " + ChatColor.GOLD + "Hug me! I love hugs! ^-^");
                SlimeyMsgs.add(ChatColor.GREEN + "" + ChatColor.BOLD + "Slimey" + ChatColor.RESET + "" + ChatColor.GRAY + " » " + ChatColor.GOLD + "I'm like a bouncy ball! :)");
                Random rand = new Random();
                int i = rand.nextInt(5);
                p.sendMessage(SlimeyMsgs.get(i));
            }

        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (spec.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (spec.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void ClearSpec(InventoryOpenEvent i) {
        if (spec.contains(i.getPlayer().getName())) {
            i.getPlayer().getInventory().clear();
        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(final PlayerRespawnEvent r) {

        if (spec.contains(r.getPlayer().getName())) {
            final Player player = r.getPlayer();
            for (Player everyone : Bukkit.getOnlinePlayers()) {
                everyone.hidePlayer(player);
            }
            player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.GOLD + "You are now a spectator! Spectate with /spec <player>");
            player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.GOLD + "Bullshit death? Request a revive with /requestrevive <message>");
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.getInventory().clear();

            World w = r.getPlayer().getLocation().getWorld();
            Location spawn = new Location(w, 0, 77, 0); //done
            r.setRespawnLocation(spawn);
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (spec.contains(p.getName()) || ingame == false) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent i) {
        if (i.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (spec.contains(i.getPlayer().getName()) || ingame == false) {
                if (i.getPlayer().isOp() == false) {
                    if (i.getClickedBlock().getType() == Material.CHEST) {
                        i.setCancelled(true);
                    }
                    if (i.getClickedBlock().getType() == Material.WORKBENCH) {
                        i.setCancelled(true);
                    }
                    if (i.getClickedBlock().getType() == Material.FURNACE) {
                        i.setCancelled(true);
                    }
                    if (i.getClickedBlock().getType() == Material.ENDER_CHEST) {
                        i.setCancelled(true);
                    }
                    if (i.getClickedBlock().getType() == Material.ANVIL) {
                        i.setCancelled(true);
                    }
                    if (i.getClickedBlock().getType() == Material.BREWING_STAND) {
                        i.setCancelled(true);
                    }
                    if (i.getClickedBlock().getType() == Material.BURNING_FURNACE) {
                        i.setCancelled(true);
                    }
                    if (i.getClickedBlock().getType() == Material.CAKE_BLOCK) {
                        i.setCancelled(true);
                    }
                    if (i.getClickedBlock().getType() == Material.DISPENSER) {
                        i.setCancelled(true);
                    }
                    if (i.getClickedBlock().getType() == Material.DROPPER) {
                        i.setCancelled(true);
                    }
                    if (i.getClickedBlock().getType() == Material.WOODEN_DOOR) {
                        i.setCancelled(true);
                    }
                    if (i.getClickedBlock().getType() == Material.WOOD_BUTTON) {
                        i.setCancelled(true);
                    }
                    if (i.getClickedBlock().getType() == Material.STONE_BUTTON) {
                        i.setCancelled(true);
                    }
                    if (i.getClickedBlock().getType() == Material.WOOD_PLATE) {
                        i.setCancelled(true);
                    }
                    if (i.getClickedBlock().getType() == Material.STONE_PLATE) {
                        i.setCancelled(true);
                    }
                    if (i.getClickedBlock().getType() == Material.LEVER) {
                        i.setCancelled(true);
                    }
                    if (i.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE) {
                        i.setCancelled(true);
                    }
                    if (i.getClickedBlock().getType() == Material.ITEM_FRAME) {
                        i.setCancelled(true);
                    }
                    if (i.getClickedBlock().getType() == Material.BED_BLOCK) {
                        i.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player p = (Player) event.getTarget();
            if (event.getEntity() instanceof Slime && ingame == false && p != Bukkit.getPlayerExact("UnexpectedTurn")) {
                event.setCancelled(true);
                event.setTarget(Bukkit.getPlayerExact("UnexpectedTurn"));
            }
            if (spec.contains(p.getName())) {
                Entity e = event.getEntity();
                if (e instanceof ExperienceOrb) {
                    event.setCancelled(true);
                    event.setTarget(null);
                }
                event.setCancelled(true);
                event.setTarget(null);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player d = (Player) e.getDamager();
            if (spec.contains(d.getName())) {
                e.setCancelled(true);
            }
        }
        if (ingame == false) {
            e.setCancelled(true);
        }
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            if (ingame == false) {
                e.setCancelled(true);
            }
            if (pvp == false) {
                e.setCancelled(true);
            }
            Player p = (Player) e.getEntity();
            if (pvp == true && spec.contains(p.getName())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (e.getEntity() instanceof Player && spec.contains(p.getName())) {
                e.setCancelled(true);
            }
        }
        if (ingame == false) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent e) {
        if (e.getItem().equals(ga)) {
            e.setCancelled(true);
            e.getPlayer().getInventory().remove(ga);
            e.getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.GOLD + "You just ate a player head!");
            e.getPlayer().setFoodLevel(e.getPlayer().getFoodLevel() + 6);
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2, true));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 1, true));
        }
    }

    @EventHandler
    public void onPing(ServerListPingEvent e) {
        if (ingame == false) {
            e.setMotd(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.GREEN + "LOBBY");
        } else {
            e.setMotd(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.DARK_RED + "IN PROGRESS");
        }
    }

    @EventHandler
    public void onChatEvent(PlayerChatEvent pc) {
        String msg = pc.getMessage();
        pc.setCancelled(true);
        if (spec.contains(pc.getPlayer().getName()) && ingame == true) {
            Player[] playerss = Bukkit.getOnlinePlayers();
            for (Player p : playerss) {
                if (spec.contains(p.getName())) {
                    p.sendMessage(ChatColor.DARK_RED + "DEAD " + ChatColor.YELLOW + pc.getPlayer().getName() + ChatColor.GRAY + " » " + ChatColor.WHITE + msg);
                }
            }
        } else {
            Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + pc.getPlayer().getName() + ChatColor.GRAY + " » " + ChatColor.WHITE + msg);
        }
    }

    @EventHandler
    public void onPlayerLeaveEvent(final PlayerQuitEvent q) {
        q.setQuitMessage("");
        if (ingame == true) {
            if (players.contains(q.getPlayer().getName())) {
                players.remove(q.getPlayer().getName());
                dis.add(q.getPlayer().getName());
                Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + q.getPlayer().getName() + ChatColor.GOLD + " has disconnected!");
                Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.GOLD + "They have" + ChatColor.AQUA + " 30 " + ChatColor.GOLD + "minutes to reconnect!");
                this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                    @Override
                    public void run() {
                        if (players.contains(q.getPlayer().getName()) || spec.contains(q.getPlayer().getName())) {
                            UHCMainCode.logger.info("EVENT CANCELLED");
                        } else {
                            spec.add(q.getPlayer().getName());
                            dis.remove(q.getPlayer().getName());
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + q.getPlayer().getName() + ChatColor.GOLD + " has been removed from the game for not reconnecting!");
                            if (players.size() == 1 && spec.size() == 23) {
                                Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + players.get(1) + ChatColor.GOLD + " has won UHC!");
                                ingame = false;
                            }
                        }
                    }
                }, 36000L);
            }
        } else {
            players.remove(q.getPlayer().getName());
            if (q.getPlayer().getName().equalsIgnoreCase("Unexpectedturn")) {
                for (World w : getServer().getWorlds()) {
                    for (Chunk c : w.getLoadedChunks()) {
                        for (Entity e : c.getEntities()) {
                            if (e.getType() == EntityType.SLIME) {
                                e.remove();
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(final PlayerLoginEvent k) {
        if (ingame == true && dis.contains(k.getPlayer().getName())) {
            dis.remove(k.getPlayer().getName());
            players.add(k.getPlayer().getName());
            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + k.getPlayer().getName() + ChatColor.GOLD + " has reconnected!");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        if (ingame == true) {
            if (!players.contains(e.getPlayer().getName())) {
                e.setJoinMessage("");
                spec.add(e.getPlayer().getName());
                final Player player = e.getPlayer();
                for (Player everyone : Bukkit.getOnlinePlayers()) {
                    everyone.hidePlayer(player);
                }
                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.GOLD + "You are now a spectator! Use /spec <player>");
                player.setGameMode(GameMode.ADVENTURE);
                player.setAllowFlight(true);
                player.setFlying(true);
                player.getInventory().clear();
                World w = e.getPlayer().getLocation().getWorld();
                Location spawn = new Location(w, 0, 77, 0); //done
                e.getPlayer().teleport(spawn);
            } else {
                e.setJoinMessage("");
            }
        } else {
            e.getPlayer().getInventory().clear();
            players.add(e.getPlayer().getName());
            e.setJoinMessage("");
            e.getPlayer().setGameMode(GameMode.ADVENTURE);
            World w = e.getPlayer().getLocation().getWorld();
            if (e.getPlayer().getName().equalsIgnoreCase("MattyBainy")) {
                Location spawn = new Location(w, 1091, 128, -8); //done
                e.getPlayer().teleport(spawn);
            } else {
                Location spawn = new Location(w, 1585.5, 104, -80.5); //done
                e.getPlayer().teleport(spawn);
            }
            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.GOLD + "Player " + ChatColor.AQUA + e.getPlayer().getDisplayName() + ChatColor.GOLD + " has joined UHC!");
        }

    }

    @EventHandler
    public void onPlayerRegainHealthEvent(EntityRegainHealthEvent event) {
        if (ingame == true) {
            if (event.getRegainReason() == RegainReason.SATIATED || event.getRegainReason() == RegainReason.REGEN || event.getRegainReason() == RegainReason.EATING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (ingame == false) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        final Player p = e.getEntity();
        final Player k = e.getEntity().getKiller();
        saveInventory(p);
        saveLocation(p);
        EntityDamageEvent.DamageCause cause = e.getEntity().getLastDamageCause().getCause();

        Player[] playerss = Bukkit.getOnlinePlayers();
        for (Player pl : playerss) {
            World w = pl.getWorld();
            Location l = pl.getLocation();
            w.playSound(l, Sound.AMBIENCE_THUNDER, 10, 1);
        }

        skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setDisplayName(p.getName());
        skullMeta.setOwner(p.getName());
        skull.setItemMeta(skullMeta);
        if (ingame == true) {
            spec.add(p.getName());
            players.remove(p.getName());
            if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK && k != null) {
                if (players.size() >= 2) {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " was killed by " + ChatColor.RESET + ChatColor.AQUA + k.getDisplayName() + ChatColor.GOLD + "! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " players remain!");
                } else {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " was killed by " + ChatColor.RESET + ChatColor.AQUA + k.getDisplayName() + ChatColor.GOLD + "! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " player remains!");
                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + k.getDisplayName() + ChatColor.GOLD + " has won UHC!");
                            ingame = false;
                        }
                    }, 3L);
                }
            } else if (cause == EntityDamageEvent.DamageCause.STARVATION) {
                if (players.size() >= 2) {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " has fallen! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " players remain!");
                    p.sendMessage(ChatColor.GOLD + "Nice going Bear Grylls...");
                } else {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " has fallen! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " player remains!");
                    p.sendMessage(ChatColor.GOLD + "Nice going Bear Grylls...");
                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + players.get(0) + ChatColor.GOLD + " has won UHC!");
                        }
                    }, 3L);
                }
            } else if (cause == EntityDamageEvent.DamageCause.LAVA) {
                if (players.size() >= 2) {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " was incinerated! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " players remain!");
                } else {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " was incinerated! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " player remains!");
                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + players.get(0) + ChatColor.GOLD + " has won UHC!");
                        }
                    }, 3L);
                }
            } else if (cause == EntityDamageEvent.DamageCause.DROWNING) {
                if (players.size() >= 2) {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " drowned! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " players remain!");
                } else {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " drowned! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " player remains!");
                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + players.get(0) + ChatColor.GOLD + " has won UHC!");
                        }
                    }, 3L);
                }
            } else if (cause == EntityDamageEvent.DamageCause.VOID) {
                if (players.size() >= 2) {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " fell into the void! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " players remain!");
                } else {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " fell into the void! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " player remains!");
                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + players.get(0) + ChatColor.GOLD + " has won UHC!");
                        }
                    }, 3L);
                }
            } else if (cause == EntityDamageEvent.DamageCause.FIRE) {
                if (players.size() >= 2) {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " burned to death! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " players remain!");
                } else {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " burned to death! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " player remains!");
                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + players.get(0) + ChatColor.GOLD + " has won UHC!");
                        }
                    }, 3L);
                }
            } else if (cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
                if (players.size() >= 2) {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " burned to death! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " players remain!");
                } else {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " burned to death! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " player remains!");
                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + players.get(0) + ChatColor.GOLD + " has won UHC!");
                        }
                    }, 3L);
                }
            } else if (cause == EntityDamageEvent.DamageCause.PROJECTILE && k != null) {
                if (players.size() >= 2) {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " was shot in the face by " + ChatColor.WHITE + ChatColor.GOLD + k.getDisplayName() + ChatColor.GOLD + "! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " players remain!");
                } else {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " was shot in the face by " + ChatColor.WHITE + ChatColor.GOLD + k.getDisplayName() + ChatColor.GOLD + "! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " player remains!");
                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + players.get(0) + ChatColor.GOLD + " has won UHC!");
                        }
                    }, 3L);
                }
            } else if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK && k == null) {
                if (players.size() >= 2) {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " was killed! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " players remain!");
                } else {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " was killed! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " player remains!");
                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + players.get(0) + ChatColor.GOLD + " has won UHC!");
                        }
                    }, 3L);
                }
            } else if (cause == EntityDamageEvent.DamageCause.PROJECTILE && k == null) {
                if (players.size() >= 2) {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " got 360 no scope'd! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " players remain!");
                } else {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " got 360 no scope'd! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " player remains!");
                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + players.get(0) + ChatColor.GOLD + " has won UHC!");
                        }
                    }, 3L);
                }
            } else if (cause == EntityDamageEvent.DamageCause.FALL) {
                if (players.size() >= 2) {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " fell to their death... What a scrub! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " players remain!");
                } else {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " fell to their death... What a scrub! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " player remains!");
                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + players.get(0) + ChatColor.GOLD + " has won UHC!");
                        }
                    }, 3L);
                }
            } else if (cause == EntityDamageEvent.DamageCause.SUFFOCATION) {
                if (players.size() >= 2) {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " tried to inhale a block... " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " players remain!");
                } else {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " tried to inhale a block... " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " player remains!");
                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + players.get(0) + ChatColor.GOLD + " has won UHC!");
                        }
                    }, 3L);
                }
            } else if (cause == EntityDamageEvent.DamageCause.MAGIC) {
                if (players.size() >= 2) {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " died? From Magic!? What is this sourcery!? " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " players remain!");
                } else {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " died? From Magic!? What is this sourcery!? " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " player remains!");
                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + players.get(0) + ChatColor.GOLD + " has won UHC!");
                        }
                    }, 3L);
                }
            } else if (cause == EntityDamageEvent.DamageCause.CONTACT) {
                if (players.size() >= 2) {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " hugged a Cactus... gg... " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " players remain!");
                } else {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " hugged a Cactus... gg... " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " player remains!");
                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + players.get(0) + ChatColor.GOLD + " has won UHC!");
                        }
                    }, 3L);
                }
            } else if (cause == EntityDamageEvent.DamageCause.SUICIDE) {
                if (players.size() >= 2) {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " took their own life! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " players remain!");
                } else {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " took their own life! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " player remains!");
                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + players.get(0) + ChatColor.GOLD + " has won UHC!");
                        }
                    }, 3L);
                }
            } else {
                if (players.size() >= 2) {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " has fallen! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " players remain!");
                } else {
                    p.getWorld().dropItemNaturally(p.getLocation(), skull);
                    e.setDeathMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + p.getDisplayName() + ChatColor.GOLD + " has fallen! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " player remains!");
                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + players.get(0) + ChatColor.GOLD + " has won UHC!");
                        }
                    }, 3L);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player[] playersss = Bukkit.getServer().getOnlinePlayers();

        for (Player player : playersss) {
            if (commandLabel.equalsIgnoreCase("sedate")) {
                if (args.length == 0) {
                    PotionEffect blind = new PotionEffect(PotionEffectType.BLINDNESS, 99999, 128);
                    PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 99999, 7);
                    PotionEffect jump = new PotionEffect(PotionEffectType.JUMP, 99999, 128);
                    PotionEffect night = new PotionEffect(PotionEffectType.NIGHT_VISION, 99999, 128);
                    blind.apply(player);
                    slow.apply(player);
                    jump.apply(player);
                    night.apply(player);
                    player.getInventory().clear();
                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.DARK_RED + "YOU HAVE BEEN SEDATED!");
                }
            }
        }

        for (Player player : playersss) {
            if (commandLabel.equalsIgnoreCase("unsedate")) {
                if (args.length == 0) {
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                    player.removePotionEffect(PotionEffectType.SLOW);
                    player.removePotionEffect(PotionEffectType.JUMP);
                    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                } else if (args.length == 1) {
                    try {
                        Player targetPlayer = player.getServer().getPlayer(args[0]);
                        targetPlayer.removePotionEffect(PotionEffectType.BLINDNESS);
                        targetPlayer.removePotionEffect(PotionEffectType.SLOW);
                        targetPlayer.removePotionEffect(PotionEffectType.JUMP);
                        targetPlayer.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    } catch (Exception e) {
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.RED + "PLAYER NOT ONLINE!");
                    }
                }
            }
        }

        if (commandLabel.equalsIgnoreCase("spec")) {
            Player player = (Player) sender;
            if (spec.contains(player.getName())) {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.RED + "Usage: /spec <player>");
                } else if (args.length == 1) {
                    try {
                        Player targetPlayer = player.getServer().getPlayer(args[0]);
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.GOLD + "You are now spectating " + ChatColor.AQUA + targetPlayer.getDisplayName() + ChatColor.GOLD + "!");
                        player.teleport(targetPlayer);
                    } catch (Exception e) {
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.RED + "PLAYER NOT ONLINE!");
                    }
                }
            }
        }

        if (commandLabel.equalsIgnoreCase("requestrevive")) {
            Player player = (Player) sender;
            if (rr.contains(player.getName())) {
                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + "You've already requested to be revived!");
            } else {
                if (spec.contains(player.getName())) {
                    if (args.length == 0) {
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.RED + "Usage /requestrevive [message]");
                    } else {
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + "Request Sent!");
                        String msg = "";
                        for (String s : args) {
                            msg = msg + " " + s;
                        }
                        Bukkit.getPlayer("MattyBainy").sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + "Revive Request(" + ChatColor.YELLOW + player.getName() + ChatColor.AQUA + ")" + ChatColor.GRAY + " »" + ChatColor.GOLD + msg);
                        rr.add(player.getName());
                    }
                } else {
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + "You must be dead to use this command.");
                }
            }
        }

        if (commandLabel.equalsIgnoreCase("deny")) {
            Player player = (Player) sender;
            if (player.getName().equals("MattyBainy")) {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.RED + "Come on... You made this plugin... You should know how to use it...");
                } else if (args.length == 1) {
                    try {
                        Player targetPlayer = player.getServer().getPlayer(args[0]);
                        targetPlayer.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + "Your revival request was denied.");
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.RED + "Successfully denied " + ChatColor.AQUA + targetPlayer.getName());
                    } catch (Exception e) {
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.RED + "PLAYER NOT ONLINE!");
                    }
                }
            }
        }

        if (commandLabel.equalsIgnoreCase("alive")) {
            Player player = (Player) sender;
            if (player.getName().equals("MattyBainy")) {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.RED + "Come on... You made this plugin... You should know how to use it...");
                } else if (args.length == 1) {
                    try {
                        Player targetPlayer = player.getServer().getPlayer(args[0]);
                        spec.remove(targetPlayer.getName());
                        rr.remove(targetPlayer.getName());
                        players.add(targetPlayer.getName());
                        restoreLocation(targetPlayer);
                        restoreInventory(targetPlayer);
                        targetPlayer.setHealth(20.0);
                        targetPlayer.setFoodLevel(20);
                        targetPlayer.setGameMode(GameMode.SURVIVAL);
                        targetPlayer.setFlying(false);
                        Player[] playerarray = Bukkit.getServer().getOnlinePlayers();
                        for (Player pl : playerarray) {
                            if (pl.canSee(targetPlayer) == false) {
                                pl.showPlayer(targetPlayer);
                            }
                        }
                        targetPlayer.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + "You have been revived! Your inventory has been restored!");
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Successfully revived " + ChatColor.AQUA + targetPlayer.getName());
                        Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + targetPlayer.getName() + ChatColor.GOLD + " has been revived! " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " players remain!");
                    } catch (Exception e) {
                        player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.RED + "PLAYER NOT ONLINE!");
                    }
                }
            }
        }

        if (commandLabel.equalsIgnoreCase("startuhc")) {
            if (args.length == 0) {
                if (ingame == false) {
                    String pl = "";
                    Player[] playe = Bukkit.getServer().getOnlinePlayers();
                    for (World w : getServer().getWorlds()) {
                        w.setTime(0L);
                        for (Chunk c : w.getLoadedChunks()) {
                            for (Entity e : c.getEntities()) {
                                if (e.getType() == EntityType.SLIME) {
                                    e.remove();
                                }
                            }
                        }
                    }
                    for (Player p : playe) {
                        pl = pl + " " + p.getName();
                        p.setHealth(20.0);
                        p.setFoodLevel(60);
                        PotionEffect blind = new PotionEffect(PotionEffectType.BLINDNESS, 99999, 128);
                        PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 99999, 7);
                        PotionEffect jump = new PotionEffect(PotionEffectType.JUMP, 99999, 128);
                        PotionEffect night = new PotionEffect(PotionEffectType.NIGHT_VISION, 99999, 128);
                        blind.apply(p);
                        slow.apply(p);
                        jump.apply(p);
                        night.apply(p);
                        p.getInventory().clear();
                        p.setGameMode(GameMode.SURVIVAL);
                    }
                    Bukkit.dispatchCommand(sender, "spreadplayers 0 0 200 600 false " + pl);
                    this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                        @Override
                        public void run() {
                            counter = counter + 20;
                            if (pvp == false) {
                                pvp = true;
                                Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + counter + ChatColor.GOLD + " minutes have passed!");
                                Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.GOLD + "PvP is now" + ChatColor.AQUA + " enabled" + ChatColor.GOLD + "!");
                            } else {
                                Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.AQUA + counter + ChatColor.GOLD + " minutes have passed!");
                            }
                        }
                    }, 24200L, 24000L);
                    ingame = true;
                    Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.GOLD + "The game is about to begin!");
                    this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.GOLD + "The game has begun! Good luck to all " + ChatColor.AQUA + players.size() + ChatColor.GOLD + " of you!");
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.GOLD + "You have recieved" + ChatColor.AQUA + " 20 " + ChatColor.GOLD + "minutes of no PvP to help you along.");
                            pvp = false;
                            Player[] playerss = Bukkit.getServer().getOnlinePlayers();
                            for (Player p : playerss) {
                                World w = p.getWorld();
                                Location l = p.getLocation();
                                w.playSound(l, Sound.AMBIENCE_THUNDER, 10, 1);
                                p.setHealth(20.0);
                                p.setFoodLevel(60);
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "unsedate " + p.getName());
                            }
                        }
                    }, 200L);
                } else {
                    Player player = (Player) sender;
                    player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.GOLD + "THERE IS ALREADY A GAME IN PROGRESS!");
                }
            }
        }

        if (commandLabel.equalsIgnoreCase("sethealth")) {
            Player player = (Player) sender;
            if (args.length < 2) {
                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.RED + "Too few arguments");
            }
            if (args.length == 2) {
                Player targetPlayer = player.getServer().getPlayer(args[0]);
                int i = Integer.parseInt(args[1]);
                targetPlayer.setHealth((double) i);
                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.GOLD + "Player " + ChatColor.AQUA + targetPlayer.getName() + ChatColor.GOLD + " was set to " + ChatColor.AQUA + i + ChatColor.GOLD + " health!");
            }
        }

        if (commandLabel.equalsIgnoreCase("msg") || commandLabel.equalsIgnoreCase("tell")) {
            Player player = (Player) sender;
            final HashMap<String, String> hashmap = new HashMap<String, String>();
            if (args.length < 2) {
                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.RED + "Too few arguments");
                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.RED + "/msg <player> <message>");
                return true;
            }
            if (Bukkit.getPlayer(args[0]) == null) {
                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.RED + "That player is not online");
                return true;
            }
            String msg = "";
            for (String s : args) {
                msg = msg + " " + s;
            }
            Bukkit.getPlayer(args[0]).sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " »» " + ChatColor.YELLOW + args[0] + ChatColor.GOLD + "]" + ChatColor.WHITE + "" + ChatColor.BOLD + msg.replaceFirst(" " + args[0], ""));
            player.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " »» " + ChatColor.YELLOW + args[0] + ChatColor.GOLD + "]" + ChatColor.WHITE + "" + ChatColor.BOLD + msg.replaceFirst(" " + args[0], ""));
            hashmap.put(player.getName(), args[0]);
            hashmap.put(args[0], player.getName());
            World w = Bukkit.getPlayer(args[0]).getWorld();
            Location l = Bukkit.getPlayer(args[0]).getLocation();
            w.playSound(l, Sound.ORB_PICKUP, 10, 1);
            return true;
        }

        if (commandLabel.equalsIgnoreCase("heal")) {
            if (args.length == 0) {
                Player player = (Player) sender;
                Player[] playerlist = Bukkit.getOnlinePlayers();
                for (Player p : playerlist) {
                    p.setHealth(20);
                    p.setFoodLevel(60);
                    p.sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + "UHC" + ChatColor.GRAY + "] " + ChatColor.GOLD + "You were healed and fed!");
                }
            }
        }

        if (commandLabel.equalsIgnoreCase("slimey")) {
            Player player = (Player) sender;
            if (player.getName().equalsIgnoreCase("UnexpectedTurn")) {
                if (ingame == false) {
                    for (World w : getServer().getWorlds()) {
                        for (Chunk c : w.getLoadedChunks()) {
                            for (Entity e : c.getEntities()) {
                                if (e.getType() == EntityType.SLIME) {
                                    e.remove();
                                }
                            }
                        }
                    }
                    Slime slimey = (Slime) player.getWorld().spawnCreature(player.getLocation(), EntityType.SLIME);
                    slimey.setSize(1);
                    slimey.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + "Slimey");
                    player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Slimey" + ChatColor.RESET + "" + ChatColor.GRAY + " » " + ChatColor.WHITE + "I'm here Emilee! <3");
                } else {
                    player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Slimey" + ChatColor.RESET + "" + ChatColor.GRAY + " » " + ChatColor.WHITE + "I can't get to you right now Emilee! There's a game in progress! ;-;");
                }
            } else {
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Slimey" + ChatColor.RESET + "" + ChatColor.GRAY + " » " + ChatColor.WHITE + "Sorry " + player.getName() + ", I only respond to Emilee! :c");
            }
        }

        return false;

    }

    public static void saveInventory(Player player) {
        inventoryContents.put(player.getName(), player.getInventory().getContents());
        inventoryArmorContents.put(player.getName(), player.getInventory().getArmorContents());
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }

    public static void saveLocation(Player player) {
        location.put(player.getName(), player.getLocation());
    }

    public static void restoreInventory(Player player) {
        if (inventoryContents.containsKey(player.getName())) {
            player.getInventory().clear();
            player.getInventory().setContents(inventoryContents.get(player.getName()));
            player.getInventory().setArmorContents(inventoryArmorContents.get(player.getName()));
        }
    }

    public static void restoreLocation(Player player) {
        if (location.containsKey(player.getName())) {
            CylinderRegion cr = new CylinderRegion();
            Location l = location.get(player.getName());
            double x = l.getX();
            double y = l.getY();
            double z = l.getZ();
            Vector2D v1 = new Vector2D(x, z);
            Vector2D v2 = new Vector2D(10, 10);
            cr.setCenter(v1);
            cr.setRadius(v2);
            cr.setMaximumY(256);
            cr.setMinimumY(1);
            player.teleport(location.get(player.getName()));
        }
    }
}
