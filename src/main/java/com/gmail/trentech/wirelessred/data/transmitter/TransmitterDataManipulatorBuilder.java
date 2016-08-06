package com.gmail.trentech.wirelessred.data.transmitter;

import static com.gmail.trentech.wirelessred.data.Keys.TRANSMITTER;

import java.util.Optional;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

public class TransmitterDataManipulatorBuilder extends AbstractDataBuilder<TransmitterData> implements DataManipulatorBuilder<TransmitterData, ImmutableTransmitterData> {

	public TransmitterDataManipulatorBuilder() {
		super(TransmitterData.class, 1);
	}

	@Override
	protected Optional<TransmitterData> buildContent(DataView container) throws InvalidDataException {
		if (!container.contains(TRANSMITTER.getQuery())) {
			return Optional.empty();
		}
		Transmitter sign = container.getSerializable(TRANSMITTER.getQuery(), Transmitter.class).get();
		return Optional.of(new TransmitterData(sign));
	}

//	@Override
//	public Optional<TransmitterData> build(DataView container) throws InvalidDataException {
//		if (!container.contains(TRANSMITTER.getQuery())) {
//			return Optional.empty();
//		}
//		Transmitter sign = container.getSerializable(TRANSMITTER.getQuery(), Transmitter.class).get();
//		return Optional.of(new TransmitterData(sign));
//	}

	@Override
	public TransmitterData create() {
		return new TransmitterData(new Transmitter());
	}

	@Override
	public Optional<TransmitterData> createFrom(DataHolder dataHolder) {
		return create().fill(dataHolder);
	}

	public TransmitterData createFrom(Transmitter sign) {
		return new TransmitterData(sign);
	}

}
