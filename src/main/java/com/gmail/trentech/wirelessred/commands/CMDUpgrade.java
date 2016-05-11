package com.gmail.trentech.wirelessred.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.wirelessred.Main;

public class CMDUpgrade implements CommandExecutor {

	public CommandSpec cmdUpgrade = CommandSpec.builder().description(Text.of("Temp command to give player transmitter upgrades")).permission("wirelessred.cmd.upgrade").executor(this).build();

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;

	    ItemStack itemStack = Main.getGame().getRegistry().createBuilder(ItemStack.Builder.class).itemType(ItemTypes.PAPER).quantity(1).build();
	    itemStack.offer(Keys.DISPLAY_NAME, Text.of("Transmitter Upgrade"));
	    
	    List<Text> lore = new ArrayList<>();
	    
	    lore.add(0, Text.of(TextColors.GREEN, "Range: ", TextColors.YELLOW, "64"));
	    
	    itemStack.offer(Keys.ITEM_LORE, lore);
	    
	    player.getInventory().offer(itemStack);

	    itemStack = Main.getGame().getRegistry().createBuilder(ItemStack.Builder.class).itemType(ItemTypes.PAPER).quantity(1).build();
	    itemStack.offer(Keys.DISPLAY_NAME, Text.of("Transmitter Upgrade"));
	    
	    lore = new ArrayList<>();
	    
	    lore.add(0, Text.of(TextColors.GREEN, "Range: ", TextColors.YELLOW, "128"));
	    
	    itemStack.offer(Keys.ITEM_LORE, lore);
	    
	    player.getInventory().offer(itemStack);
	    
	    itemStack = Main.getGame().getRegistry().createBuilder(ItemStack.Builder.class).itemType(ItemTypes.PAPER).quantity(1).build();
	    itemStack.offer(Keys.DISPLAY_NAME, Text.of("Transmitter Upgrade"));
	    
	    lore = new ArrayList<>();
	    
	    lore.add(0, Text.of(TextColors.GREEN, "Range: ", TextColors.YELLOW, "256"));
	    
	    itemStack.offer(Keys.ITEM_LORE, lore);
	    
	    player.getInventory().offer(itemStack);
	    
	    itemStack = Main.getGame().getRegistry().createBuilder(ItemStack.Builder.class).itemType(ItemTypes.PAPER).quantity(1).build();
	    itemStack.offer(Keys.DISPLAY_NAME, Text.of("Transmitter Upgrade"));
	    
	    lore = new ArrayList<>();
	    
	    lore.add(0, Text.of(TextColors.GREEN, "Range: ", TextColors.YELLOW, "512"));
	    
	    itemStack.offer(Keys.ITEM_LORE, lore);
	    
	    player.getInventory().offer(itemStack);
	    
	    itemStack = Main.getGame().getRegistry().createBuilder(ItemStack.Builder.class).itemType(ItemTypes.PAPER).quantity(1).build();
	    itemStack.offer(Keys.DISPLAY_NAME, Text.of("Transmitter Upgrade"));
	    
	    lore = new ArrayList<>();
	    
	    lore.add(0, Text.of(TextColors.GREEN, "Range: ", TextColors.YELLOW, "Unlimited"));
	    
	    itemStack.offer(Keys.ITEM_LORE, lore);
	    
	    player.getInventory().offer(itemStack);

		return CommandResult.success();
	}
}
