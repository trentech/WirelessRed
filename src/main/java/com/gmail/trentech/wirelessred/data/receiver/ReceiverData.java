package com.gmail.trentech.wirelessred.data.receiver;
import static com.gmail.trentech.wirelessred.data.Keys.RECEIVER;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import com.google.common.base.Preconditions;

public class ReceiverData extends AbstractSingleData<Receiver, ReceiverData, ImmutableReceiverData> {

    public ReceiverData() {
        super(new Receiver(), RECEIVER);
    }

    public ReceiverData(Receiver value) {
        super(value, RECEIVER);
    }
    
    public Value<Receiver> receiver() {
        return Sponge.getRegistry().getValueFactory().createValue(RECEIVER, getValue(), getValue());
    }
    
    @Override
    public ReceiverData copy() {
        return new ReceiverData(this.getValue());
    }

    @Override
    public Optional<ReceiverData> fill(DataHolder dataHolder, MergeFunction mergeFn) {
        ReceiverData signData = Preconditions.checkNotNull(mergeFn).merge(copy(), dataHolder.get(ReceiverData.class).orElse(copy()));
        return Optional.of(set(RECEIVER, signData.get(RECEIVER).get()));
    }

    @Override
    public Optional<ReceiverData> from(DataContainer container) {
        if (container.contains(RECEIVER.getQuery())) {
            return Optional.of(set(RECEIVER, container.getSerializable(RECEIVER.getQuery(), Receiver.class).orElse(getValue())));
        }
        return Optional.empty();
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public ImmutableReceiverData asImmutable() {
        return new ImmutableReceiverData(this.getValue());
    }

    @Override
    public int compareTo(ReceiverData value) {
    	return value.compareTo(this);
    }

    @Override
    protected Value<Receiver> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(RECEIVER, getValue(), getValue());
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(RECEIVER, getValue());
    }

}
