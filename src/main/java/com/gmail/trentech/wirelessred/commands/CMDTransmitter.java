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

import com.gmail.trentech.wirelessred.data.transmitter.TransmitterData;
import com.gmail.trentech.wirelessred.utils.ItemHelper;

public class CMDTransmitter implements CommandExecutor {

	public CommandSpec cmdTransmitter = CommandSpec.builder().description(Text.of("Temp command to get transmitter")).permission("wirelessred.cmd.transmitter").executor(this).build();

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;

	    player.getInventory().offer(ItemHelper.getTransmitter(new TransmitterData()));
	    
		return CommandResult.success();
	}
}
