package com.gmail.trentech.wirelessred.init;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.wirelessred.data.receiver.Receiver;
import com.gmail.trentech.wirelessred.data.receiver.ReceiverData;
import com.gmail.trentech.wirelessred.data.transmitter.Transmitter;
import com.gmail.trentech.wirelessred.data.transmitter.TransmitterData;

public class Items {

	public static ItemStack getTransmitter(TransmitterData transmitterData) {
		Transmitter transmitter = transmitterData.transmitter().get();

		ItemStack itemStack = getEmptyTransmitter();
		itemStack.offer(new TransmitterData());
		itemStack.offer(Keys.DISPLAY_NAME, Text.of("Transmitter Circuit"));

		List<Text> lore = new ArrayList<>();

		if (transmitter.getRange() == 60000000) {
			lore.add(0, Text.of(TextColors.GREEN, "Range: ", TextColors.YELLOW, "Unlimited"));
			lore.add(1, Text.of(TextColors.GREEN, "Mutli-World: ", TextColors.YELLOW, true));
		} else {
			lore.add(0, Text.of(TextColors.GREEN, "Range: ", TextColors.YELLOW, transmitter.getRange()));
			lore.add(1, Text.of(TextColors.GREEN, "Mutli-World: ", TextColors.YELLOW, false));
		}

		itemStack.offer(Keys.ITEM_LORE, lore);

		return itemStack;
	}
	
	public static ItemStack getEmptyTransmitter() {
		ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.SIGN).build();
		itemStack.offer(Keys.DISPLAY_NAME, Text.of("Transmitter Circuit"));

		return itemStack;
	}

	public static ItemStack getReceiver(Receiver receiver) {
		ItemStack itemStack = getEmptyReceiver();
		itemStack.offer(Keys.DISPLAY_NAME, Text.of("Receiver Circuit"));

		if (receiver != null) {
			List<Text> lore = new ArrayList<>();

			Optional<Location<World>> optionalTransmitter = receiver.getTransmitter();

			if (optionalTransmitter.isPresent()) {
				Location<World> transmitter = optionalTransmitter.get();

				if (transmitter.get(TransmitterData.class).isPresent()) {
					itemStack.offer(new ReceiverData(receiver));
					lore.add(0, Text.of(TextColors.GREEN, "Transmitter: ", TextColors.YELLOW, transmitter.getExtent().getName(), " ", transmitter.getBlockX(), " ", transmitter.getBlockY(), " ", transmitter.getBlockZ()));
				} else {
					itemStack.offer(new ReceiverData());
				}
			} else {
				lore.add(0, Text.of(TextColors.GREEN, "Transmitter: ", TextColors.RED, "Location error"));
			}

			itemStack.offer(Keys.ITEM_LORE, lore);
		} else {
			itemStack.offer(new ReceiverData());
		}

		return itemStack;
	}

	public static ItemStack getEmptyReceiver() {
		ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.STONE_BUTTON).build();
		itemStack.offer(Keys.DISPLAY_NAME, Text.of("Receiver Circuit"));

		return itemStack;
	}
	
	public static ItemStack getTool(boolean tool) {
		ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.STICK).quantity(1).build();
		itemStack.offer(Keys.DISPLAY_NAME, Text.of("Screw Driver"));

		List<Text> lore = new ArrayList<>();

		if (tool) {
			lore.add(0, Text.of(TextColors.GREEN, "Mode: ", TextColors.YELLOW, "Tool"));
		} else {
			lore.add(0, Text.of(TextColors.GREEN, "Mode: ", TextColors.YELLOW, "Information"));
		}

		itemStack.offer(Keys.ITEM_LORE, lore);

		return itemStack;
	}

	public static ItemStack getUpgrade(String range) {
		ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.PAPER).build();
		itemStack.offer(Keys.DISPLAY_NAME, Text.of("Transmitter Upgrade"));

		List<Text> lore = new ArrayList<>();

		lore.add(0, Text.of(TextColors.GREEN, "Range: ", TextColors.YELLOW, range));

		itemStack.offer(Keys.ITEM_LORE, lore);

		return itemStack;
	}
}
