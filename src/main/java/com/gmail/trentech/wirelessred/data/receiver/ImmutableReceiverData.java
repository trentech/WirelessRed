package com.gmail.trentech.wirelessred.data.receiver;

import static com.gmail.trentech.wirelessred.data.Keys.RECEIVER;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class ImmutableReceiverData extends AbstractImmutableSingleData<Receiver, ImmutableReceiverData, ReceiverData> {

    protected ImmutableReceiverData(Receiver value) {
        super(value, RECEIVER);
    }

    public ImmutableValue<Receiver> receiver() {
        return Sponge.getRegistry().getValueFactory().createValue(RECEIVER, getValue(), getValue()).asImmutable();
    }
    
    @Override
    public <E> Optional<ImmutableReceiverData> with(Key<? extends BaseValue<E>> key, E value) {
        if (this.supports(key)) {
            return Optional.of(asMutable().set(key, value).asImmutable());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public int compareTo(ImmutableReceiverData arg0) {
        return 0;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    protected ImmutableValue<?> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(RECEIVER, getValue()).asImmutable();
    }

    @Override
    public ReceiverData asMutable() {
        return new ReceiverData(this.getValue());
    }
}
