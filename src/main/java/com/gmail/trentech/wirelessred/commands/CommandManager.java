package com.gmail.trentech.wirelessred.commands;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandManager {

	private CommandSpec cmdReceiver = CommandSpec.builder()
		    .permission("wirelessred.cmd.wr.receiver")
		    .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("quantity"))))
		    .executor(new CMDReceiver())
		    .build();
	
	private CommandSpec cmdTransmitter = CommandSpec.builder()
		    .permission("wirelessred.cmd.wr.transmitter")
		    .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("quantity"))))
		    .executor(new CMDTransmitter())
		    .build();

	private CommandSpec cmdUpgrade = CommandSpec.builder()
		    .permission("wirelessred.cmd.wr.upgrade")
		    .arguments(GenericArguments.string(Text.of("level")), GenericArguments.optional(GenericArguments.integer(Text.of("quantity"))))
		    .executor(new CMDUpgrade())
		    .build();
	
	public CommandSpec cmdWR = CommandSpec.builder()
		    .permission("wirelessred.cmd.wr")
		    .child(cmdReceiver, "receiver", "r")
		    .child(cmdTransmitter, "transmitter", "t")
		    .child(cmdUpgrade, "upgrade", "u")
		    .executor(new CMDWR())
		    .build();
}
