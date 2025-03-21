package me.zivush.redstonedisabler;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class RedstoneDisabler extends JavaPlugin implements Listener {

    private Map<String, Boolean> disabledComponents;
    private String disabledMessage;
    private boolean debug;

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();

        // Load configuration
        loadConfiguration();

        // Register events
        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("RedstoneDisabler has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("RedstoneDisabler has been disabled!");
    }

    /**
     * Loads the configuration from config.yml
     */
    private void loadConfiguration() {
        // Reload the config from disk
        reloadConfig();
        FileConfiguration config = getConfig();

        // Initialize the map of disabled components
        disabledComponents = new HashMap<>();

        // Load disabled components from config
        if (config.contains("disabled-components")) {
            for (String key : config.getConfigurationSection("disabled-components").getKeys(false)) {
                boolean isDisabled = config.getBoolean("disabled-components." + key);
                disabledComponents.put(key, isDisabled);

                if (debug) {
                    getLogger().info("Component " + key + " is " + (isDisabled ? "disabled" : "enabled"));
                }
            }
        }

        // Load disabled message
        String configMessage = config.getString("disabled-message", "");
        if (configMessage != null && !configMessage.isEmpty()) {
            disabledMessage = ChatColor.translateAlternateColorCodes('&', configMessage);
        } else {
            disabledMessage = null;
        }

        // Load debug setting
        debug = config.getBoolean("debug", false);

        if (debug) {
            getLogger().info("Configuration loaded with " + disabledComponents.size() + " components configured");
            getLogger().info("Disabled message: " + (disabledMessage != null ? disabledMessage : "None"));
        }
    }

    /**
     * Handles the redstone event to prevent disabled components from activating
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRedstoneEvent(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        String materialName = block.getType().name();

        // Check if the block type is in our disabled list
        if (isComponentDisabled(materialName)) {
            // Cancel the redstone event by setting the new current to the old current
            event.setNewCurrent(event.getOldCurrent());

            if (debug) {
                getLogger().info("Blocked redstone activation for " + materialName + " at " +
                        block.getLocation().getBlockX() + ", " +
                        block.getLocation().getBlockY() + ", " +
                        block.getLocation().getBlockZ());
            }
        }
    }

    /**
     * Handles piston extension events
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        Block block = event.getBlock();
        if (isPistonDisabled(block.getType().name())) {
            event.setCancelled(true);

            if (debug) {
                getLogger().info("Blocked piston extension at " +
                        block.getLocation().getBlockX() + ", " +
                        block.getLocation().getBlockY() + ", " +
                        block.getLocation().getBlockZ());
            }
        }
    }

    /**
     * Handles piston retraction events
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        Block block = event.getBlock();
        if (isPistonDisabled(block.getType().name())) {
            event.setCancelled(true);

            if (debug) {
                getLogger().info("Blocked piston retraction at " +
                        block.getLocation().getBlockX() + ", " +
                        block.getLocation().getBlockY() + ", " +
                        block.getLocation().getBlockZ());
            }
        }
    }

    /**
     * Handles hopper item movement
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHopperMove(InventoryMoveItemEvent event) {
        // Check for regular hoppers
        if ((event.getSource().getHolder() instanceof org.bukkit.block.Hopper ||
                event.getDestination().getHolder() instanceof org.bukkit.block.Hopper) &&
                isComponentDisabled("HOPPER")) {
            event.setCancelled(true);
            if (debug) {
                getLogger().info("Blocked hopper item movement");
            }
            return;
        }

        // Check for hopper minecarts
        if ((event.getSource().getHolder() instanceof HopperMinecart ||
                event.getDestination().getHolder() instanceof HopperMinecart) &&
                isComponentDisabled("HOPPER_MINECART")) {
            event.setCancelled(true);
            if (debug) {
                getLogger().info("Blocked hopper minecart item movement");
            }
        }
    }

    /**
     * Handles note block events
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNoteBlockPlay(NotePlayEvent event) {
        if (isComponentDisabled("NOTE_BLOCK")) {
            event.setCancelled(true);

            if (debug) {
                Block block = event.getBlock();
                getLogger().info("Blocked note block at " +
                        block.getLocation().getBlockX() + ", " +
                        block.getLocation().getBlockY() + ", " +
                        block.getLocation().getBlockZ());
            }
        }
    }

    /**
     * Handles TNT and other explosions
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntityType() == EntityType.PRIMED_TNT && isComponentDisabled("TNT")) {
            event.setCancelled(true);
            if (debug) {
                getLogger().info("Blocked TNT explosion at " +
                        event.getLocation().getBlockX() + ", " +
                        event.getLocation().getBlockY() + ", " +
                        event.getLocation().getBlockZ());
            }
        } else if (event.getEntityType() == EntityType.MINECART_TNT && isComponentDisabled("TNT_MINECART")) {
            event.setCancelled(true);
            if (debug) {
                getLogger().info("Blocked TNT minecart explosion at " +
                        event.getLocation().getBlockX() + ", " +
                        event.getLocation().getBlockY() + ", " +
                        event.getLocation().getBlockZ());
            }
        }
    }

    /**
     * Handles TNT ignition
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getBlock().getType() == Material.TNT && isComponentDisabled("TNT")) {
            event.setCancelled(true);
            if (debug) {
                Block block = event.getBlock();
                getLogger().info("Blocked TNT ignition at " +
                        block.getLocation().getBlockX() + ", " +
                        block.getLocation().getBlockY() + ", " +
                        block.getLocation().getBlockZ());
            }
        }
    }

    /**
     * Handles detector rail activation
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleMove(VehicleMoveEvent event) {
        // Check if the vehicle is moving onto a detector rail
        Block to = event.getTo().getBlock();
        if (to.getType().name().contains("DETECTOR_RAIL") && isComponentDisabled("DETECTOR_RAIL")) {
            // We can't cancel the event directly, but we can teleport the vehicle back
            event.getVehicle().teleport(event.getFrom());
            if (debug) {
                getLogger().info("Blocked vehicle movement over detector rail at " +
                        to.getLocation().getBlockX() + ", " +
                        to.getLocation().getBlockY() + ", " +
                        to.getLocation().getBlockZ());
            }
        }
    }

    /**
     * Handles dragon egg teleportation
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (event.getBlock().getType() == Material.DRAGON_EGG && isComponentDisabled("DRAGON_EGG")) {
            event.setCancelled(true);
            if (debug) {
                Block block = event.getBlock();
                getLogger().info("Blocked dragon egg teleportation at " +
                        block.getLocation().getBlockX() + ", " +
                        block.getLocation().getBlockY() + ", " +
                        block.getLocation().getBlockZ());
            }
        }
    }

    /**
     * Handles player interaction with redstone components
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasBlock() && event.getClickedBlock() != null) {
            Block block = event.getClickedBlock();
            String materialName = block.getType().name();

            // Check if the block is a redstone component that can be directly activated by a player
            if (isDirectlyActivatableComponent(materialName) && isComponentDisabled(materialName)) {
                // Cancel the event
                event.setCancelled(true);

                // Send message to player only if it's not null or empty
                if (disabledMessage != null) {
                    event.getPlayer().sendMessage(disabledMessage);
                }

                if (debug) {
                    getLogger().info("Blocked player interaction with " + materialName + " by " +
                            event.getPlayer().getName() + " at " +
                            block.getLocation().getBlockX() + ", " +
                            block.getLocation().getBlockY() + ", " +
                            block.getLocation().getBlockZ());
                }
            }
        }
    }

    /**
     * Checks if a component is disabled based on its material name
     */
    private boolean isComponentDisabled(String materialName) {
        // First try exact match
        Boolean isDisabled = disabledComponents.get(materialName);

        if (isDisabled != null) {
            return isDisabled;
        }

        // Check for generic types
        if (materialName.contains("BUTTON") && disabledComponents.containsKey("WOODEN_BUTTON")) {
            return materialName.contains("WOOD") && disabledComponents.get("WOODEN_BUTTON");
        }

        if (materialName.contains("BUTTON") && disabledComponents.containsKey("STONE_BUTTON")) {
            return !materialName.contains("WOOD") && disabledComponents.get("STONE_BUTTON");
        }

        if (materialName.contains("PRESSURE_PLATE") && disabledComponents.containsKey("PRESSURE_PLATE")) {
            return disabledComponents.get("PRESSURE_PLATE");
        }

        // Check for doors, trapdoors, and fence gates
        if (materialName.endsWith("_DOOR") && disabledComponents.containsKey("DOOR")) {
            return disabledComponents.get("DOOR");
        }

        if (materialName.endsWith("_TRAPDOOR") && disabledComponents.containsKey("TRAPDOOR")) {
            return disabledComponents.get("TRAPDOOR");
        }

        if (materialName.endsWith("_FENCE_GATE") && disabledComponents.containsKey("FENCE_GATE")) {
            return disabledComponents.get("FENCE_GATE");
        }

        // For other components, check if the material name contains the config key
        for (Map.Entry<String, Boolean> entry : disabledComponents.entrySet()) {
            if (entry.getValue() && materialName.contains(entry.getKey())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Specifically checks if pistons are disabled
     */
    private boolean isPistonDisabled(String materialName) {
        if (materialName.contains("STICKY_PISTON") || materialName.equals("MOVING_PISTON")) {
            return disabledComponents.getOrDefault("STICKY_PISTON", false);
        } else if (materialName.contains("PISTON")) {
            return disabledComponents.getOrDefault("PISTON", false);
        }
        return false;
    }

    /**
     * Checks if a component can be directly activated by a player
     */
    private boolean isDirectlyActivatableComponent(String materialName) {
        return materialName.contains("BUTTON") ||
                materialName.contains("LEVER") ||
                materialName.contains("PRESSURE_PLATE") ||
                materialName.equals("TRIPWIRE_HOOK") ||
                materialName.equals("DAYLIGHT_DETECTOR") ||
                materialName.equals("TARGET") ||
                materialName.endsWith("_DOOR") ||
                materialName.endsWith("_TRAPDOOR") ||
                materialName.endsWith("_FENCE_GATE") ||
                materialName.equals("BELL") ||
                materialName.equals("LECTERN") ||
                materialName.equals("JUKEBOX") ||
                materialName.equals("DRAGON_EGG") ||
                materialName.equals("CAMPFIRE") ||
                materialName.equals("SOUL_CAMPFIRE");
    }

    /**
     * Command handler for the plugin
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("redstonedisabler")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("redstonedisabler.reload")) {
                    loadConfiguration();
                    sender.sendMessage(ChatColor.GREEN + "RedstoneDisabler configuration reloaded!");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
