package com.gmail.trentech.wirelessred.data.receiver;

import static com.gmail.trentech.wirelessred.data.DataQueries.ENABLED;
import static com.gmail.trentech.wirelessred.data.DataQueries.TRANSMITTER;

import java.util.Optional;

import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

public class ReceiverBuilder extends AbstractDataBuilder<Receiver> {

	public ReceiverBuilder() {
		super(Receiver.class, 1);
	}

	@Override
	protected Optional<Receiver> buildContent(DataView container) throws InvalidDataException {
		if (container.contains(ENABLED, TRANSMITTER)) {
			Receiver receiver = new Receiver(container.getBoolean(ENABLED).get(), container.getString(TRANSMITTER).get());
			return Optional.of(receiver);
		}
		return Optional.empty();
	}
}
