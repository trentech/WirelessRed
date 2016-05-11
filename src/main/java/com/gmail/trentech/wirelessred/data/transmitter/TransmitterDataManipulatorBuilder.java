package com.gmail.trentech.wirelessred.data.transmitter;

import static com.gmail.trentech.wirelessred.data.Keys.TRANSMITTER;

import java.util.Optional;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

public class TransmitterDataManipulatorBuilder implements DataManipulatorBuilder<TransmitterData, ImmutableTransmitterData> {

    @Override
    public Optional<TransmitterData> build(DataView container) throws InvalidDataException {
        if (!container.contains(TRANSMITTER.getQuery())) {
            return Optional.empty();
        }
        Transmitter sign = container.getSerializable(TRANSMITTER.getQuery(), Transmitter.class).get();
        return Optional.of(new TransmitterData(sign));
    }

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
