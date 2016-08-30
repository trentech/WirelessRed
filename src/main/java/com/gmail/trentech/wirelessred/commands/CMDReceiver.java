package com.gmail.trentech.wirelessred.commands;

import java.math.BigDecimal;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.wirelessred.Main;
import com.gmail.trentech.wirelessred.init.Items;
import com.gmail.trentech.wirelessred.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDReceiver implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;

		int quantity = 1;
		
		if (args.hasAny("quantity")) {
			quantity = args.<Integer> getOne("quantity").get();
		}

		ConfigurationNode config = ConfigManager.get().getConfig();
		
		if(config.getNode("settings", "economy", "enable").getBoolean() && !src.hasPermission("wirelessred.admin")) {
			double cost = config.getNode("settings", "economy", "items", "receiver").getDouble() * quantity;
			
			Optional<EconomyService> optionalEconomy = Sponge.getServiceManager().provide(EconomyService.class);

			if (!optionalEconomy.isPresent()) {
				player.sendMessage(Text.of(TextColors.RED, "Economy plugin not found!"));
				Main.instance().getLog().error("Economy plugin not found!");
				return CommandResult.empty();
			}
			
			EconomyService economy = optionalEconomy.get();
			
			if(economy.getOrCreateAccount(player.getUniqueId()).get().withdraw(economy.getDefaultCurrency(), new BigDecimal(cost), Cause.of(NamedCause.source(Main.instance().getPlugin()))).getResult() == ResultType.FAILED) {
				player.sendMessage(Text.of(TextColors.RED, "Not enough money. Need ", TextColors.YELLOW, "$", cost));
				return CommandResult.empty();
			}
			
			player.sendMessage(Text.of(TextColors.GREEN, "You were charged ", TextColors.YELLOW, "$", cost));
		}

		player.getInventory().offer(Items.getReceiver(null, quantity));

		return CommandResult.success();
	}
}
