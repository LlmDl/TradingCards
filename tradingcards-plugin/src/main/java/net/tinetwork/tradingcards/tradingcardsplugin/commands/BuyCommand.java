package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.economy.ResponseWrapper;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.PlaceholderUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author sarhatabaot
 */
@CommandAlias("cards")
public class BuyCommand extends BaseCommand {
    private final TradingCards plugin;

    public BuyCommand(final TradingCards plugin) {
        this.plugin = plugin;
    }

    @Subcommand("buy")
    @CommandPermission(Permissions.BUY)
    public class BuySubCommand extends BaseCommand {

        @Subcommand("pack")
        @CommandPermission(Permissions.BUY_PACK)
        @CommandCompletion("@packs")
        @Description("Buy a pack.")
        public void onBuyPack(final Player player, final String packId) {
            if (CardUtil.noEconomy(player))
                return;

            if (plugin.getPackManager().getPack(packId) == null) {
                player.sendMessage(plugin.getMessagesConfig().packDoesntExist());
                return;
            }

            Pack pack = plugin.getPackManager().getPack(packId);

            if (pack.getBuyPrice() <= 0.0D && pack.getTradeCards().isEmpty()) {
                player.sendMessage(plugin.getMessagesConfig().cannotBeBought());
                return;
            }


            if (!pack.getTradeCards().isEmpty() && !hasCardsInInventory(player, pack.getTradeCards())) {
                player.sendMessage("Not enough cards to perform a trade in.");
                return;
            }

            ResponseWrapper economyResponse = plugin.getEconomyWrapper().withdraw(player, pack.getCurrencyId(), pack.getBuyPrice());
            if (economyResponse.success()) {

                if (plugin.getGeneralConfig().closedEconomy()) {
                    plugin.getEconomyWrapper().depositAccount(plugin.getGeneralConfig().serverAccount(), pack.getCurrencyId(), pack.getBuyPrice());
                }

                if (!pack.getTradeCards().isEmpty()) {
                    for (Pack.PackEntry packEntry : pack.getPackEntryList()) {
                        removeCardsMatchingEntry(player, packEntry);
                    }
                    //add bought for cards etc
                }

                player.sendMessage(plugin.getMessagesConfig().boughtCard().replaceAll(PlaceholderUtil.AMOUNT.asRegex(), String.valueOf(pack.getBuyPrice())));

                CardUtil.dropItem(player, plugin.getPackManager().getPackItem(packId));
                return;
            }

            player.sendMessage(plugin.getMessagesConfig().notEnoughMoney());
        }

        private void removeCardsMatchingEntry(final @NotNull Player player, final Pack.@NotNull PackEntry packEntry) {
            PlayerInventory inventory = player.getInventory();
            /*
            packentry: common:7:default
            inventory:
            zombie:common:2:default, skeleton:common:5:default
             */
            int countLeftToRemove = packEntry.amount();
            for (ItemStack itemStack: inventory.getContents()) {
                if(matchesEntry(itemStack,packEntry)) {
                    //accounts for zombie:common:10:default
                    if(itemStack.getAmount() > countLeftToRemove) {
                        itemStack.setAmount(itemStack.getAmount() - countLeftToRemove);
                        plugin.debug(BuyCommand.class,"Removed %d from ItemStack %s, new amount: %s".formatted(countLeftToRemove, itemStack.toString(), itemStack.getAmount()));
                        break;
                    }

                    countLeftToRemove -= itemStack.getAmount();
                    inventory.remove(itemStack);
                    plugin.debug(BuyCommand.class, "Removed ItemStack %s, amount left to remove %d".formatted(itemStack.toString(),countLeftToRemove));
                }
            }
        }

        private int countCardsInInventory(final @NotNull Player player, final Pack.PackEntry packEntry) {
            int count = 0;
            PlayerInventory inventory = player.getInventory();

            for (ItemStack itemStack : inventory.getContents()) {
                if(matchesEntry(itemStack,packEntry)) {
                    count += itemStack.getAmount();
                }
            }
            return count;
        }

        private boolean matchesEntry(final ItemStack itemStack, final Pack.PackEntry packEntry) {
            NBTItem nbtItem = new NBTItem(itemStack);
            if (!CardUtil.isCard(nbtItem)) {
                return false;
            }


            NBTCompound tcCompound = nbtItem.getCompound(NbtUtils.TC_COMPOUND);

            //don't count if it's shiny.
            if (tcCompound.getBoolean(NbtUtils.TC_CARD_SHINY))
                return false;

            final String nbtRarity = tcCompound.getString(NbtUtils.TC_CARD_RARITY);
            final String nbtSeries = tcCompound.getString(NbtUtils.TC_CARD_SERIES);

            return packEntry.seriesId().equals(nbtSeries) && packEntry.getRarityId().equals(nbtRarity);
        }

        private boolean hasCardsInInventory(final Player player, final @NotNull List<Pack.PackEntry> tradeCards) {
            for (Pack.PackEntry packEntry : tradeCards) {
                if (packEntry.amount() > countCardsInInventory(player, packEntry))
                    return false;
            }
            return true;
        }


        @Subcommand("card")
        @CommandPermission(Permissions.BUY_CARD)
        @Description("Buy a card.")
        @CommandCompletion("@rarities @cards")
        public void onBuyCard(final Player player, @NotNull final String rarityId, @NotNull final String cardId, @NotNull final String seriesId) {
            if (CardUtil.noEconomy(player))
                return;

            if (plugin.getCardManager().getCard(cardId, rarityId, seriesId) instanceof EmptyCard) {
                player.sendMessage(plugin.getMessagesConfig().cardDoesntExist());
                return;
            }

            final TradingCard tradingCard = plugin.getCardManager().getCard(cardId, rarityId, seriesId);
            final double buyPrice = tradingCard.getBuyPrice();
            final String currencyId = tradingCard.getCurrencyId();

            ResponseWrapper economyResponse = plugin.getEconomyWrapper().withdraw(player, currencyId, buyPrice);
            if (economyResponse.success()) {
                if (plugin.getGeneralConfig().closedEconomy()) {
                    plugin.getEconomyWrapper().depositAccount(plugin.getGeneralConfig().serverAccount(), currencyId, buyPrice);
                }
                CardUtil.dropItem(player, tradingCard.build(false));
                player.sendMessage(plugin.getMessagesConfig().boughtCard().replaceAll(PlaceholderUtil.AMOUNT.asRegex(), String.valueOf(buyPrice)));
                return;
            }
            player.sendMessage(plugin.getMessagesConfig().notEnoughMoney());
        }
    }
}
