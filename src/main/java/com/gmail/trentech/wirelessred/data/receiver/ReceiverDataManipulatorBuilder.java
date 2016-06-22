package com.gmail.trentech.wirelessred.data.receiver;

import static com.gmail.trentech.wirelessred.data.Keys.RECEIVER;

import java.util.Optional;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

public class ReceiverDataManipulatorBuilder implements DataManipulatorBuilder<ReceiverData, ImmutableReceiverData> {

	@Override
	public Optional<ReceiverData> build(DataView container) throws InvalidDataException {
		if (!container.contains(RECEIVER.getQuery())) {
			return Optional.empty();
		}
		Receiver sign = container.getSerializable(RECEIVER.getQuery(), Receiver.class).get();
		return Optional.of(new ReceiverData(sign));
	}

	@Override
	public ReceiverData create() {
		return new ReceiverData(new Receiver());
	}

	@Override
	public Optional<ReceiverData> createFrom(DataHolder dataHolder) {
		return create().fill(dataHolder);
	}

	public ReceiverData createFrom(Receiver sign) {
		return new ReceiverData(sign);
	}

}
