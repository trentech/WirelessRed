package com.gmail.trentech.wirelessred.data.transmitter;
import static com.gmail.trentech.wirelessred.data.Keys.TRANSMITTER;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import com.google.common.base.Preconditions;

public class TransmitterData extends AbstractSingleData<Transmitter, TransmitterData, ImmutableTransmitterData> {

    public TransmitterData() {
        super(new Transmitter(), TRANSMITTER);
    }
    
    protected TransmitterData(Transmitter value) {
        super(value, TRANSMITTER);
    }

    public Value<Transmitter> transmitter() {
        return Sponge.getRegistry().getValueFactory().createValue(TRANSMITTER, getValue(), getValue());
    }
    
    @Override
    public TransmitterData copy() {
        return new TransmitterData(this.getValue());
    }

    @Override
    public Optional<TransmitterData> fill(DataHolder dataHolder, MergeFunction mergeFn) {
        TransmitterData signData = Preconditions.checkNotNull(mergeFn).merge(copy(), dataHolder.get(TransmitterData.class).orElse(copy()));
        return Optional.of(set(TRANSMITTER, signData.get(TRANSMITTER).get()));
    }

    @Override
    public Optional<TransmitterData> from(DataContainer container) {
        if (container.contains(TRANSMITTER.getQuery())) {
            return Optional.of(set(TRANSMITTER, container.getSerializable(TRANSMITTER.getQuery(), Transmitter.class).orElse(getValue())));
        }
        return Optional.empty();
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public ImmutableTransmitterData asImmutable() {
        return new ImmutableTransmitterData(this.getValue());
    }

    @Override
    public int compareTo(TransmitterData value) {
    	return value.compareTo(this);
    }

    @Override
    protected Value<Transmitter> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(TRANSMITTER, getValue(), getValue());
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(TRANSMITTER, getValue());
    }

}
