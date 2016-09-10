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
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.wirelessred.Main;
import com.gmail.trentech.wirelessred.init.Items;
import com.gmail.trentech.wirelessred.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDUpgrade implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;

		String level = args.<String> getOne("level").get();
		
		int quantity = 1;
		
		if (args.hasAny("quantity")) {
			quantity = args.<Integer> getOne("quantity").get();	
		}
		
		if(level.equalsIgnoreCase("64")) {
			if(charge(player, "64", quantity)) {
				player.getInventory().offer(Items.getUpgrade("64", quantity));
			}		
		} else if (level.equalsIgnoreCase("128")) {
			if(charge(player, "128", quantity)) {
				player.getInventory().offer(Items.getUpgrade("128", quantity));
			}
		} else if (level.equalsIgnoreCase("256")) {
			if(charge(player, "256", quantity)) {
				player.getInventory().offer(Items.getUpgrade("256", quantity));
			}
		} else if (level.equalsIgnoreCase("512")) {
			if(charge(player, "512", quantity)) {
				player.getInventory().offer(Items.getUpgrade("512", quantity));
			}
		} else if (level.equalsIgnoreCase("Unlimited")) {
			if(charge(player, "Unlimited", quantity)) {
				player.getInventory().offer(Items.getUpgrade("Unlimited", quantity));
			}
		} else {
			player.sendMessage(getUsage());
		}

		return CommandResult.success();
	}
	
	private boolean charge(Player player, String arg, int quantity) {
		ConfigurationNode config = ConfigManager.get().getConfig();
		
		if(config.getNode("settings", "economy", "enable").getBoolean() && !player.hasPermission("wirelessred.admin")) {
			double cost = config.getNode("settings", "economy", "items", "upgrade_" + arg).getDouble() * quantity;
			
			Optional<EconomyService> optionalEconomy = Sponge.getServiceManager().provide(EconomyService.class);

			if (!optionalEconomy.isPresent()) {
				player.sendMessage(Text.of(TextColors.RED, "Economy plugin not found!"));
				Main.instance().getLog().error("Economy plugin not found!");
				return false;
			}
			
			EconomyService economy = optionalEconomy.get();
			
			if(economy.getOrCreateAccount(player.getUniqueId()).get().withdraw(economy.getDefaultCurrency(), new BigDecimal(cost), Cause.of(NamedCause.source(Main.getPlugin()))).getResult() == ResultType.FAILED) {
				player.sendMessage(Text.of(TextColors.RED, "Not enough money. Need ", TextColors.YELLOW, "$", cost));
				return false;
			}
			
			player.sendMessage(Text.of(TextColors.GREEN, "You were charged ", TextColors.YELLOW, "$", cost));
		}
		
		return true;
	}
	
	private Text getUsage() {
		Text t1 = Text.of(TextColors.YELLOW, "/wr upgrade ");
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("64\n128\n256\n512\nUnlimited"))).append(Text.of("<level> ")).build();
		Text t3 = Text.of(TextColors.YELLOW, "[quantity]");
		return Text.of(t1, t2, t3);
	}
}
