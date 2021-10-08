package net.tinetwork.tradingcards.api;

import net.tinetwork.tradingcards.api.blacklist.Blacklist;
import net.tinetwork.tradingcards.api.config.SimpleConfigurate;
import net.tinetwork.tradingcards.api.config.settiings.GeneralConfigurate;
import net.tinetwork.tradingcards.api.config.settiings.RarityConfigurate;
import net.tinetwork.tradingcards.api.config.settiings.SeriesConfigurate;
import net.tinetwork.tradingcards.api.manager.CardManager;
import net.tinetwork.tradingcards.api.manager.DeckManager;
import net.tinetwork.tradingcards.api.manager.PackManager;
import net.tinetwork.tradingcards.api.manager.TypeManager;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public abstract class TradingCardsPlugin<T> extends JavaPlugin {
    public TradingCardsPlugin() {
        super();
    }

    public TradingCardsPlugin(final @NotNull JavaPluginLoader loader, final @NotNull PluginDescriptionFile description, final @NotNull File dataFolder, final @NotNull File file) {
        super(loader, description, dataFolder, file);
    }

    /**
     * Send a debug message in the log.
     * @param message Message to send.
     */
    public abstract void debug(Class<?> clazz, String message);

    /**
     *
     * @param string
     * @return
     */
    public abstract boolean isMob(String string);


    /**
     *
     * @param type
     * @return
     */
    public abstract boolean isMob(EntityType type);

    /**
     *
     * @return
     */
    public abstract CardManager<T> getCardManager();

    /**
     *
     * @return
     */
    public abstract PackManager getPackManager();

    /**
     *
     * @return
     */
    public abstract DeckManager getDeckManager();

    /**
     *
     * @return
     */
    public abstract Blacklist<Player> getPlayerBlacklist();


    /**
     *
     * @return
     */
    public abstract Blacklist<World> getWorldBlacklist();


    /**
     *
     * @return
     */
    public abstract TradingCardsPlugin<T> get();

    public abstract SimpleConfigurate getDeckConfig();

    public abstract GeneralConfigurate getGeneralConfig();

    public abstract RarityConfigurate getRaritiesConfig();

    public abstract SeriesConfigurate getSeriesConfig();

    public abstract TypeManager getDropTypeManager();
}
