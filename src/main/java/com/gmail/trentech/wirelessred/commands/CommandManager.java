package com.gmail.trentech.wirelessred.commands;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandManager {

	private CommandSpec cmdReceiver = CommandSpec.builder()
		    .permission("wirelessred.cmd.wr.receiver")
		    .executor(new CMDReceiver())
		    .build();
	
	private CommandSpec cmdTransmitter = CommandSpec.builder()
		    .permission("wirelessred.cmd.wr.transmitter")
		    .executor(new CMDTransmitter())
		    .build();
	
	private CommandSpec cmdTool = CommandSpec.builder()
		    .permission("wirelessred.cmd.wr.tool")
		    .executor(new CMDTool())
		    .build();
	
	private CommandSpec cmdUpgrade = CommandSpec.builder()
		    .permission("wirelessred.cmd.wr.upgrade")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("level"))))
		    .executor(new CMDUpgrade())
		    .build();
	
	public CommandSpec cmdWR = CommandSpec.builder()
		    .permission("worldbackup.cmd.wr")
		    .child(cmdReceiver, "receiver", "r")
		    .child(cmdTransmitter, "transmitter", "t")
		    .child(cmdTool, "tool", "tl")
		    .child(cmdUpgrade, "upgrade", "u")
		    .executor(new CMDWR())
		    .build();
}
