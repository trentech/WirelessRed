package com.gmail.trentech.wirelessred.data.transmitter;

import static com.gmail.trentech.wirelessred.data.DataQueries.ENABLED;
import static com.gmail.trentech.wirelessred.data.DataQueries.RANGE;
import static com.gmail.trentech.wirelessred.data.DataQueries.MULTIWORLD;
import static com.gmail.trentech.wirelessred.data.DataQueries.RECEVIERS;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

public class TransmitterBuilder extends AbstractDataBuilder<Transmitter> {

	public TransmitterBuilder() {
		super(Transmitter.class, 1);
	}

	@Override
	protected Optional<Transmitter> buildContent(DataView container) throws InvalidDataException {
		if (container.contains(ENABLED, RANGE, MULTIWORLD, RECEVIERS)) {
			@SuppressWarnings("unchecked")
			Transmitter transmitter = new Transmitter(container.getBoolean(ENABLED).get(), container.getDouble(RANGE).get(), container.getBoolean(MULTIWORLD).get(), (List<String>) container.getList(RECEVIERS).get());
			return Optional.of(transmitter);
		}
		return Optional.empty();
	}
}
