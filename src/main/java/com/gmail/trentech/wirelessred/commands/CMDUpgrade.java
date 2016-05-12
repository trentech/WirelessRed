package com.gmail.trentech.wirelessred.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.wirelessred.utils.ItemHelper;

public class CMDUpgrade implements CommandExecutor {

	public CommandSpec cmdUpgrade = CommandSpec.builder().description(Text.of("Temp command to give player transmitter upgrades")).permission("wirelessred.cmd.upgrade").executor(this).build();

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;

	    player.getInventory().offer(ItemHelper.getUpgrade("64"));
	    player.getInventory().offer(ItemHelper.getUpgrade("128"));
	    player.getInventory().offer(ItemHelper.getUpgrade("256"));
	    player.getInventory().offer(ItemHelper.getUpgrade("512"));
	    player.getInventory().offer(ItemHelper.getUpgrade("Unlimited"));

		return CommandResult.success();
	}
}
