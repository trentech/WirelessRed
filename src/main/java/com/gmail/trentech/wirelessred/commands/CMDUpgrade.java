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
import com.gmail.trentech.wirelessred.utils.ConfigManager;
import com.gmail.trentech.wirelessred.utils.ItemHelper;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDUpgrade implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;

		if (!args.hasAny("level")) {
			player.sendMessage(getUsage());
			return CommandResult.empty();
		}
		String level = args.<String> getOne("level").get();
		
		if(level.equalsIgnoreCase("64")) {
			if(charge(player, "64")) {
				player.getInventory().offer(ItemHelper.getUpgrade("64"));
			}		
		} else if (level.equalsIgnoreCase("128")) {
			if(charge(player, "128")) {
				player.getInventory().offer(ItemHelper.getUpgrade("128"));
			}
		} else if (level.equalsIgnoreCase("256")) {
			if(charge(player, "256")) {
				player.getInventory().offer(ItemHelper.getUpgrade("256"));
			}
		} else if (level.equalsIgnoreCase("512")) {
			if(charge(player, "512")) {
				player.getInventory().offer(ItemHelper.getUpgrade("512"));
			}
		} else if (level.equalsIgnoreCase("Unlimited")) {
			if(charge(player, "Unlimited")) {
				player.getInventory().offer(ItemHelper.getUpgrade("Unlimited"));
			}
		} else {
			player.sendMessage(getUsage());
		}

		return CommandResult.success();
	}
	
	private boolean charge(Player player, String arg) {
		ConfigurationNode config = new ConfigManager().getConfig();
		
		if(config.getNode("economy", "enable").getBoolean() && !player.hasPermission("wirelessred.admin")) {
			double cost = config.getNode("economy", "items", "upgrade_" + arg).getDouble();
			
			Optional<EconomyService> optionalEconomy = Sponge.getServiceManager().provide(EconomyService.class);

			if (!optionalEconomy.isPresent()) {
				player.sendMessage(Text.of(TextColors.RED, "Economy plugin not found!"));
				Main.getLog().error("Economy plugin not found!");
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
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("64\n128\n256\n512\nUnlimited"))).append(Text.of("<level>")).build();
		return Text.of(t1, t2);
	}
}
