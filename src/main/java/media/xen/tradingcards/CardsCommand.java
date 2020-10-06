package media.xen.tradingcards;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import net.milkbowl.vault.economy.EconomyResponse;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.smartcardio.Card;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static media.xen.tradingcards.TradingCards.econ;
import static media.xen.tradingcards.TradingCards.sendMessage;

@CommandAlias("cards")
public class CardsCommand extends BaseCommand {
	private final TradingCards plugin;
	private final boolean showUsage;

	public CardsCommand(final TradingCards plugin) {
		this.plugin = plugin;
		this.showUsage = plugin.getConfig().getBoolean("General.Show-Command-Usage", true);
	}

	@CatchUnknown
	@HelpCommand
	public void onHelp(final CommandSender sender, CommandHelp help) {
		help.showHelp();
	}

	@Subcommand("version|ver")
	@CommandPermission("cards.version")
	public void onVersion(final CommandSender sender){
		final String format = "%s %s API-%s";
		sendMessage(sender,String.format(format,plugin.getName(), plugin.getDescription().getVersion(),plugin.getDescription().getAPIVersion()));
	}

	@Subcommand("reload")
	@CommandPermission("cards.reload")
	public void onReload(final CommandSender sender) {
		final String format = "%s %s";
		sendMessage(sender, String.format(format, plugin.getMessagesConfig().getConfig().getString("Messages.Prefix"), plugin.getMessagesConfig().getConfig().getString("Messages.Reload")));
		plugin.reloadAllConfig();
		if (plugin.getConfig().getBoolean("General.Schedule-Cards")) {
			plugin.startTimer();
		}
	}


	@Subcommand("resolve")
	@CommandPermission("cards.resolve")
	public void onResolve(final CommandSender sender, final Player player) {
		sendMessage(sender, plugin.getMessagesConfig().getConfig().getString("Messages.ResolveMsg").replaceAll("%name%", player.getName()).replaceAll("%uuid%", player.getUniqueId().toString()));
	}

	@Subcommand("toggle")
	@CommandPermission("cards.toggle")
	public void onToggle(final Player player) {
		if (plugin.isOnList(player) && plugin.blacklistMode() == 'b') {
			plugin.removeFromList(player);
			sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().getConfig().getString("Messages.ToggleEnabled")));
		} else if (plugin.isOnList(player) && plugin.blacklistMode() == 'w') {
			plugin.removeFromList(player);
			sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().getConfig().getString("Messages.ToggleDisabled")));
		} else if (!plugin.isOnList(player) && plugin.blacklistMode() == 'b') {
			plugin.addToList(player);
			sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().getConfig().getString("Messages.ToggleDisabled")));
		} else if (!plugin.isOnList(player) && plugin.blacklistMode() == 'w') {
			plugin.addToList(player);
			sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().getConfig().getString("Messages.ToggleEnabled")));
		}
	}

	@Subcommand("create")
	@CommandPermission("cards.create")
	public void onCreate(final Player player, final String rarity, final String name, final String series, final String type, final boolean shiny, final String info, final String about) {
		CardUtil.createCard(player, rarity.replaceAll("_", " "), name, series.replaceAll("_", " "), type.replaceAll("_", " "), shiny, info.replaceAll("_", " "), about.replaceAll("_", " "));
	}

	@Subcommand("givecard")
	@CommandPermission("cards.givecard")
	public void onGiveCard(final Player player, final String name, final String rarity) {
		if (plugin.getCardsConfig().getConfig().contains("Cards." + rarity + "." + name)) {
			player.getInventory().addItem(CardManager.getCard(name, rarity));
			return;
		}
		sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().getConfig().getString("Messages.NoCard")));
	}


	@Subcommand("giveshinycard")
	@CommandPermission("cards.giveshinycard")
	public void onGiveShinyCard(final Player player, final String name, final String rarity) {
		if (plugin.getCardsConfig().getConfig().contains("Cards." + rarity + "." + name)) {
			player.getInventory().addItem(CardManager.getCard(name, rarity, true));
			return;
		}
		sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().getConfig().getString("Messages.NoCard")));

	}

	@Subcommand("giveboosterpack")
	@CommandPermission("cards.giveboosterpack")
	public void onGiveBoosterpack(final CommandSender sender, final Player player, final String boosterpack) {
		World curWorld;
		if (player.getInventory().firstEmpty() != -1) {
			sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().getConfig().getString("Messages.BoosterPackMsg")));
			player.getInventory().addItem(plugin.createBoosterPack(boosterpack));
			return;
		}

		curWorld = player.getWorld();
		if (player.getGameMode() == GameMode.SURVIVAL) {
			sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().getConfig().getString("Messages.BoosterPackMsg")));
			curWorld.dropItem(player.getLocation(), plugin.createBoosterPack(boosterpack));
		}

	}

	private void sendPrefixedMessage(final CommandSender toWhom, final String message) {
		sendMessage(toWhom, plugin.getPrefixedMessage(message));
	}

	@Subcommand("debug")
	@CommandPermission("cards.admin.debug")
	public class DebugCommands extends BaseCommand{
		@Subcommand("showcache")
		@CommandPermission("cards.admin.debug.showcache")
		public void showCache(final CommandSender sender){
			sender.sendMessage(StringUtils.join(CardManager.getCards().keySet(), ","));
		}

		@Subcommand("modules")
		@CommandPermission("cards.admin.debug.modules")
		public void onModules(final CommandSender sender){
			final StringBuilder builder = new StringBuilder("Enabled Modules:");
			builder.append("\n");
			for(String depend: plugin.getDescription().getSoftDepend()){
				if(Bukkit.getPluginManager().getPlugin(depend) == null)
					builder.append(ChatColor.GRAY);
				else {
					builder.append(ChatColor.GREEN);
				}
				builder.append(depend).append(" ");
			}
			sender.sendMessage(builder.toString());
		}


		@Subcommand("rarities")
		@CommandPermission("cards.admin.debug.rarities")
		public void onRarities(final CommandSender sender) {
			final List<String> rarities = new ArrayList<>(plugin.getCardsConfig().getConfig().getConfigurationSection("Cards").getKeys(false));
			sender.sendMessage(StringUtils.join(rarities, ", "));
		}

		@Subcommand("exists")
		@CommandPermission("cards.admin.debug.exists")
		public void onExists(final CommandSender sender, final String card, final String rarity){
			if(plugin.getCardsConfig().getConfig().contains("Cards."+rarity+"."+card)) {
				sender.sendMessage(String.format("Card %s.%s exists",rarity,card));
				return;
			}
			sender.sendMessage(String.format("Card %s.%s does not exist",rarity,card));
		}
	}



	@Subcommand("getdeck")
	@CommandPermission("cards.decks.get")
	public void onGetDeck(final Player player, final int deckNumber) {
		World curWorld;
		if (player.hasPermission("cards.decks." + deckNumber)) {
			if (plugin.getConfig().getBoolean("General.Use-Deck-Item")) {
				if (!plugin.hasDeck(player, deckNumber)) {
					if (player.getInventory().firstEmpty() != -1) {
						sendPrefixedMessage(player, plugin.getMessagesConfig().getConfig().getString("Messages.GiveDeck"));
						player.getInventory().addItem(plugin.createDeck(player, deckNumber));
					} else {
						curWorld = player.getWorld();
						if (player.getGameMode() == GameMode.SURVIVAL) {
							sendPrefixedMessage(player, plugin.getMessagesConfig().getConfig().getString("Messages.GiveDeck"));

							curWorld.dropItem(player.getLocation(), plugin.createDeck(player, deckNumber));
						}
					}
				} else {
					sendPrefixedMessage(player, plugin.getMessagesConfig().getConfig().getString("Messages.AlreadyHaveDeck"));
				}
				return;
			}

			if (player.getGameMode() == GameMode.CREATIVE) {
				if (plugin.getConfig().getBoolean("General.Decks-In-Creative")) {
					plugin.openDeck(player, deckNumber);
					return;
				}
				sendPrefixedMessage(player, plugin.getMessagesConfig().getConfig().getString("Messages.DeckCreativeError"));
				return;
			}
			plugin.openDeck(player, deckNumber);
			return;
		}

		sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().getConfig().getString("Messages.MaxDecks")));
	}

	@Subcommand("giverandomcard")
	@CommandPermission("cards.randomcard")
	public void onGiveRandomCard(final CommandSender sender, final Player player, final String entityType) {
		try {
			EntityType.valueOf(entityType.toUpperCase());
			String rare = CardUtil.calculateRarity(EntityType.valueOf(entityType.toUpperCase()), true);
			plugin.debug("onCommand.rare: " + rare);
			sendPrefixedMessage(sender, plugin.getMessagesConfig().getConfig().getString("Messages.GiveRandomCardMsg").replaceAll("%player%", player.getName()));

			if (player.getInventory().firstEmpty() != -1) {
				sendPrefixedMessage(player, plugin.getMessagesConfig().getConfig().getString("Messages.GiveRandomCard"));
				player.getInventory().addItem(CardUtil.getRandomCard(rare, false));
			} else {
				World curWorld2 = player.getWorld();
				if (player.getGameMode() == GameMode.SURVIVAL) {
					sendPrefixedMessage(player, plugin.getMessagesConfig().getConfig().getString("Messages.GiveRandomCard"));
					curWorld2.dropItem(player.getLocation(), CardUtil.getRandomCard(rare, false));
				}
			}
		} catch (IllegalArgumentException exception) {
			sendPrefixedMessage(player, plugin.getMessagesConfig().getConfig().getString("Messages.NoEntity"));
		}
	}
	@Subcommand("list")
	@CommandPermission("cards.list")
	public class ListSubCommand extends BaseCommand{
		@Default
		public void onList(final CommandSender sender,@Optional final String rarity){
			onListPlayer(sender, (Player) sender,rarity);
		}

		@Subcommand("player")
		@CommandPermission("cards.list.player|cards.list.others")
		public void onListPlayer(final CommandSender sender, final Player target, @Optional final String rarity) {
			if(rarity == null || plugin.isRarity(rarity).equals("None")){
				final String sectionFormat = String.format("&e&l------- &7(&6&l%s's Collection&7)&e&l -------", target.getName());
				sendMessage(sender, String.format(sectionFormat,target.getName()));
				for(String raritySection: plugin.getCardsConfig().getConfig().getConfigurationSection("Cards").getKeys(false)){
					listRarity(sender,target,raritySection);
				}
				return;
			}
			listRarity(sender, target, rarity);
		}

		@Subcommand("pack")
		@CommandPermission("cards.listpacks|cards.list.pack")
		public void onListPack(final CommandSender sender){
			ConfigurationSection rarities = plugin.getConfig().getConfigurationSection("BoosterPacks");
			Set<String> rarityKeys = rarities.getKeys(false);
			int k = 0;
			sendMessage(sender, "&6--- Booster Packs --- ");
			boolean canBuy2 = false;
			boolean hasExtra = false;

			for (Iterator<String> iterator = rarityKeys.iterator(); iterator.hasNext(); hasExtra = false) {
				String rarity = iterator.next();
				if (plugin.getConfig().getBoolean("PluginSupport.Vault.Vault-Enabled") && plugin.getConfig().contains("BoosterPacks." + rarity + ".Price") && plugin.getConfig().getDouble("BoosterPacks." + rarity + ".Price") > 0.0D) {
					canBuy2 = true;
				}

				if (plugin.getConfig().contains("BoosterPacks." + rarity + ".ExtraCardRarity") && plugin.getConfig().contains("BoosterPacks." + rarity + ".NumExtraCards")) {
					hasExtra = true;
				}

				++k;
				if (canBuy2) {
					sendPrefixedMessage(sender, "&6" + k + ") &e" + rarity + " &7(&aPrice: " + plugin.getConfig().getDouble("BoosterPacks." + rarity + ".Price") + "&7)");
				} else {
					sendPrefixedMessage(sender, "&6" + k + ") &e" + rarity);
				}

				if (hasExtra) {
					sendMessage(sender, "  &7- &f&o" + plugin.getConfig().getInt("BoosterPacks." + rarity + ".NumNormalCards") + " " + plugin.getConfig().getString("BoosterPacks." + rarity + ".NormalCardRarity") + ", " + plugin.getConfig().getInt("BoosterPacks." + rarity + ".NumExtraCards") + " " + plugin.getConfig().getString("BoosterPacks." + rarity + ".ExtraCardRarity") + ", " + plugin.getConfig().getInt("BoosterPacks." + rarity + ".NumSpecialCards") + " " + plugin.getConfig().getString("BoosterPacks." + rarity + ".SpecialCardRarity"));
				} else {
					sendMessage(sender, "  &7- &f&o" + plugin.getConfig().getInt("BoosterPacks." + rarity + ".NumNormalCards") + " " + plugin.getConfig().getString("BoosterPacks." + rarity + ".NormalCardRarity") + ", " + plugin.getConfig().getInt("BoosterPacks." + rarity + ".NumSpecialCards") + " " + plugin.getConfig().getString("BoosterPacks." + rarity + ".SpecialCardRarity"));
				}

				canBuy2 = false;
			}
		}


		private void listRarity(final CommandSender sender, final Player target, final String rarity){
			final StringBuilder stringBuilder = new StringBuilder();
			final String sectionFormat = "&6--- %s &7(&c%d&f/&a%d&7)&6 ---";
			final String sectionFormatComplete = "&6--- %s &7(%sComplete&7)&6 ---";
			final ConfigurationSection rarityCardSection = plugin.getCardsConfig().getConfig().getConfigurationSection("Cards."+rarity);
			final int cardTotal = rarityCardSection.getKeys(false).size();
			int cardCounter = 0;

			for(String cardName: rarityCardSection.getKeys(false)){
				if(cardCounter > 32){
					if (plugin.hasCard(target, cardName, rarity) > 0) {
						++cardCounter;
					}
					stringBuilder.append(cardName).append("&7and more!");
				} else {
					plugin.debug(rarity + ", " + cardName);

					String colour = plugin.getConfig().getString("Colours.ListHaveCard");
					if (plugin.hasShiny(target, cardName, rarity)) {
						++cardCounter;
						colour = plugin.getConfig().getString("Colours.ListHaveShinyCard");
						stringBuilder.append(colour).append(cardName.replaceAll("_", " ")).append("&f, ");
					} else if (plugin.hasCard(target, cardName, rarity) > 0 && !plugin.hasShiny(target, cardName, rarity)) {
						++cardCounter;
						stringBuilder.append(colour).append(cardName.replaceAll("_", " ")).append("&f, ");
					} else {
						stringBuilder.append("&7").append(cardName.replaceAll("_", " ")).append("&f, ");
					}
				}
			}
			//send title
			if(cardCounter == cardTotal){
				sendMessage(sender,String.format(sectionFormatComplete, plugin.isRarity(rarity),plugin.getConfig().getString("Colours.ListRarityComplete")));
			} else {
				sendMessage(sender,String.format(sectionFormat,plugin.isRarity(rarity),cardCounter,cardTotal));
			}

			sendMessage(sender,stringBuilder.toString());
		}
	}

	private void execCommand(final CommandSender sender, final String rarity, final String path) {
		if (plugin.getConfig().contains("Rarities." + plugin.isRarity(rarity) + path) && !plugin.getConfig().getString("Rarities." + plugin.isRarity(rarity) + path).equalsIgnoreCase("None")) {
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), plugin.getConfig().getString("Rarities." + plugin.isRarity(rarity + path).replaceAll("%player%", sender.getName())));
		}
	}

	@Subcommand("reward")
	@CommandPermission("cards.reward")
	public void onReward(final CommandSender sender, final String rarity) {
		if (!plugin.getConfig().getBoolean("General.Allow-Rewards")) {
			sendMessage(sender, plugin.getPrefixedMessage(plugin.getMessagesConfig().getConfig().getString("Messages.RewardDisabled")));
			return;
		}
		if (plugin.isRarity(rarity).equalsIgnoreCase("None")) {
			sendPrefixedMessage(sender, plugin.getMessagesConfig().getConfig().getString("Messages.RewardError"));
			return;
		}

		if (plugin.completedRarity((Player) sender, plugin.isRarity(rarity))) {
			execCommand(sender, rarity, ".RewardCmd1");
			execCommand(sender, rarity, ".RewardCmd2");
			execCommand(sender, rarity, ".RewardCmd3");

			if (plugin.getConfig().getBoolean("General.Reward-Broadcast")) {
				Bukkit.broadcastMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().getConfig().getString("Messages.RewardBroadcast").replaceAll("%player%", sender.getName()).replaceAll("%rarity%", plugin.isRarity(rarity))));
			}

			if (!plugin.deleteRarity((Player) sender, plugin.isRarity(rarity)) && plugin.getConfig().getBoolean("General.Debug-Mode")) {
				plugin.getLogger().warning("Cannot delete rarity: " + plugin.isRarity(rarity));
			}
		} else if (plugin.getConfig().getBoolean("General.Eat-Shiny-Cards")) {
			sendPrefixedMessage(sender, plugin.getMessagesConfig().getConfig().getString("Messages.RewardError2"));
		} else {
			sendPrefixedMessage(sender, plugin.getMessagesConfig().getConfig().getString("Messages.RewardError3").replaceAll("%shinyName%", plugin.getConfig().getString("General.Shiny-Name")));
		}


	}

	@Subcommand("giveaway")
	@CommandPermission("cards.giveaway")
	public void onGiveaway(final CommandSender sender, final String rarity) {
		ConfigurationSection rarities = plugin.getCardsConfig().getConfig().getConfigurationSection("Cards");
		Set<String> rarityKeys = rarities.getKeys(false);
		String keyToUse = "";
		if (plugin.isMob(rarity)) {
			if (sender instanceof ConsoleCommandSender) {
				plugin.giveawayNatural(EntityType.valueOf(rarity.toUpperCase()), null);
			} else {
				plugin.giveawayNatural(EntityType.valueOf(rarity.toUpperCase()), (Player) sender);
			}
		} else {

			for (final String rarityKey : rarityKeys) {
				if (rarityKey.equalsIgnoreCase(rarity.replaceAll("_", " "))) {
					keyToUse = rarityKey;
				}
			}

			if (!keyToUse.equals("")) {
				Bukkit.broadcastMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().getConfig().getString("Messages.Giveaway").replaceAll("%player%", sender.getName()).replaceAll("%rarity%", keyToUse)));

				for (final Player p5 : Bukkit.getOnlinePlayers()) {
					ConfigurationSection cards4 = plugin.getCardsConfig().getConfig().getConfigurationSection("Cards." + keyToUse);
					Set<String> cardKeys4 = cards4.getKeys(false);
					int rIndex = plugin.r.nextInt(cardKeys4.size());
					int l = 0;
					String cardName = "";

					for (Iterator<String> var51 = cardKeys4.iterator(); var51.hasNext(); ++l) {
						String theCardName = var51.next();
						if (l == rIndex) {
							cardName = theCardName;
							break;
						}
					}
					CardUtil.dropItem(p5, CardManager.getCard(cardName,keyToUse));
				}
			} else {
				sendPrefixedMessage(sender, plugin.getMessagesConfig().getConfig().getString("Messages.NoRarity"));
			}
		}


	}

	@Subcommand("worth")
	@CommandPermission("cards.worth")
	public void onWorth(final Player player) {
		if (!hasVault(player)) {
			return;
		}
		if (player.getInventory().getItemInMainHand().getType() != Material.valueOf(plugin.getConfig().getString("General.Card-Material"))) {
			sendPrefixedMessage(player, plugin.getMessagesConfig().getConfig().getString("Messages.NotACard"));
			return;
		}

		ItemStack itemInHand = player.getInventory().getItemInMainHand();
		final String keyToUse = itemInHand.getItemMeta().getDisplayName();
		plugin.debug(keyToUse);
		plugin.debug(ChatColor.stripColor(keyToUse));

		String[] splitName = ChatColor.stripColor(keyToUse).split(" ");
		String cardName2 = "";
		if (splitName.length > 1) {
			cardName2 = splitName[1];
		} else {
			cardName2 = splitName[0];
		}
		plugin.debug(cardName2);


		List<String> lore = itemInHand.getItemMeta().getLore();
		String rarity = ChatColor.stripColor(lore.get(3));
		plugin.debug(rarity);

		boolean canBuy = false;
		double buyPrice = 0.0D;
		if (plugin.getCardsConfig().getConfig().contains("Cards." + rarity + "." + cardName2 + ".Buy-Price")) {
			buyPrice = plugin.getCardsConfig().getConfig().getDouble("Cards." + rarity + "." + cardName2 + ".Buy-Price");
			if (buyPrice > 0.0D) {
				canBuy = true;
			}
		}

		if (canBuy) {
			sendPrefixedMessage(player, plugin.getMessagesConfig().getConfig().getString("Messages.CanBuy").replaceAll("%buyAmount%", String.valueOf(buyPrice)));
		} else {
			sendPrefixedMessage(player, plugin.getMessagesConfig().getConfig().getString("Messages.CanNotBuy"));
		}

	}

	private boolean hasVault(final Player player) {
		if (!plugin.hasVault) {
			sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().getConfig().getString("Messages.NoVault")));
			return false;
		}
		return true;
	}


	@Subcommand("buy")
	@CommandPermission("cards.buy")
	public class BuySubCommand extends BaseCommand {

		@Subcommand("pack|boosterpack")
		@CommandPermission("cards.buy.pack")
		public void onBuyPack(final Player player, final String name) {
			if (!hasVault(player))
				return;
			if (!plugin.getConfig().contains("BoosterPacks." + name)) {
				sendPrefixedMessage(player, plugin.getMessagesConfig().getConfig().getString("Messages.PackDoesntExist"));
				return;
			}


			double buyPrice2 = plugin.getConfig().getDouble("BoosterPacks." + name + ".Price", 0.0D);

			if (buyPrice2 <= 0.0D) {
				sendPrefixedMessage(player, plugin.getMessagesConfig().getConfig().getString("Messages.CannotBeBought"));
				return;
			}

			EconomyResponse economyResponse = econ.withdrawPlayer(player, buyPrice2);
			if (economyResponse.transactionSuccess()) {
				if (plugin.getConfig().getBoolean("PluginSupport.Vault.Closed-Economy")) {//TODO
					econ.bankDeposit(plugin.getConfig().getString("PluginSupport.Vault.Server-Account"), buyPrice2);
				}
				sendPrefixedMessage(player, plugin.getMessagesConfig().getConfig().getString("Messages.BoughtCard").replaceAll("%amount%", String.valueOf(buyPrice2)));
				CardUtil.dropItem(player, plugin.createBoosterPack(name));
				return;
			}

			sendPrefixedMessage(player, plugin.getMessagesConfig().getConfig().getString("Messages.NotEnoughMoney"));
		}


		@Subcommand("card")
		@CommandPermission("cards.buy.card")
		public void onBuyCard(final Player player, @NotNull final String rarity, @NotNull final String card) {
			if (!hasVault(player))
				return;

			if (!plugin.getCardsConfig().getConfig().contains("Cards." + rarity + "." + card)) {
				sendPrefixedMessage(player, plugin.getMessagesConfig().getConfig().getString("Messages.CardDoesntExist"));
				return;
			}


			double buyPrice2 = plugin.getCardsConfig().getConfig().getDouble("Cards." + rarity + "." + card + ".Buy-Price", 0.0D);

			EconomyResponse economyResponse = econ.withdrawPlayer(player, buyPrice2);
			if (economyResponse.transactionSuccess()) {
				if (plugin.getConfig().getBoolean("PluginSupport.Vault.Closed-Economy")) {//TODO
					econ.bankDeposit(plugin.getConfig().getString("PluginSupport.Vault.Server-Account"), buyPrice2);
				}
				CardUtil.dropItem(player, CardManager.getCard(card, rarity));
				sendPrefixedMessage(player, plugin.getMessagesConfig().getConfig().getString("Messages.BoughtCard").replaceAll("%amount%", String.valueOf(buyPrice2)));
				return;
			}
			sendPrefixedMessage(player, plugin.getMessagesConfig().getConfig().getString("Messages.NotEnoughMoney"));
		}
	}
}





