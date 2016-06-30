package com.gmail.trentech.wirelessred.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.wirelessred.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDWR implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		List<Text> list = new ArrayList<>();

		ConfigurationNode config = new ConfigManager().getConfig();
	
		if (src.hasPermission("wirelessred.cmd.wr.transmitter")) {
			if(config.getNode("economy", "enable").getBoolean() && !src.hasPermission("wirelessred.admin")) {
				double cost = config.getNode("economy", "items", "transmitter").getDouble();
				list.add(Text.of(TextColors.GREEN, "/wr transmitter", TextColors.YELLOW, " - $", cost));
			}else {
				list.add(Text.of(TextColors.GREEN, "/wr transmitter"));
			}
		}
		if (src.hasPermission("wirelessred.cmd.wr.receiver")) {
			if(config.getNode("economy", "enable").getBoolean() && !src.hasPermission("wirelessred.admin")) {
				double cost = config.getNode("economy", "items", "receiver").getDouble();
				list.add(Text.of(TextColors.GREEN, "/wr receiver", TextColors.YELLOW, " - $", cost));
			}else {
				list.add(Text.of(TextColors.GREEN, "/wr receiver"));
			}
		}
		if (src.hasPermission("wirelessred.cmd.wr.tool")) {
			if(config.getNode("economy", "enable").getBoolean() && !src.hasPermission("wirelessred.admin")) {
				double cost = config.getNode("economy", "items", "tool").getDouble();
				list.add(Text.of(TextColors.GREEN, "/wr tool", TextColors.YELLOW, " - $", cost));
			}else {
				list.add(Text.of(TextColors.GREEN, "/wr tool"));
			}
		}
		if (src.hasPermission("wirelessred.cmd.wr.upgrade")) {
			double cost64 = config.getNode("economy", "items", "upgrade_64").getDouble();
			double cost128 = config.getNode("economy", "items", "upgrade_128").getDouble();
			double cost256 = config.getNode("economy", "items", "upgrade_256").getDouble();
			double cost512 = config.getNode("economy", "items", "upgrade_512").getDouble();
			double costUnl = config.getNode("economy", "items", "upgrade_unlimited").getDouble();			
			
			Text level;
			if(config.getNode("economy", "enable").getBoolean() && !src.hasPermission("wirelessred.admin")) {
				level = Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("64 - $", cost64, "\n128 - $", cost128, "\n256 - $", cost256, "\n512 - $", cost512, "\nUnlimited - $", costUnl))).append(Text.of("<level>")).build();
			}else {
				level = Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("64\n128\n256\n512\nUnlimited"))).append(Text.of("<level>")).build();				
			}
			
			list.add(Text.of(TextColors.GREEN, "/wr upgrade ", level));
		}
		
		if (src instanceof Player) {
			PaginationList.Builder pages = Sponge.getServiceManager().provide(PaginationService.class).get().builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Command List")).build());

			pages.contents(list);

			pages.sendTo(src);
		} else {
			for (Text text : list) {
				src.sendMessage(text);
			}
		}
		
		return CommandResult.success();
	}

}
