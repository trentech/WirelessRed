package com.gmail.trentech.wirelessred.data.transmitter;

import static com.gmail.trentech.wirelessred.data.Keys.TRANSMITTER;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public class ImmutableTransmitterData extends AbstractImmutableSingleData<Transmitter, ImmutableTransmitterData, TransmitterData> {

	protected ImmutableTransmitterData(Transmitter value) {
		super(value, TRANSMITTER);
	}

	public ImmutableValue<Transmitter> transmitter() {
		return Sponge.getRegistry().getValueFactory().createValue(TRANSMITTER, getValue(), getValue()).asImmutable();
	}

	@Override
	public <E> Optional<ImmutableTransmitterData> with(Key<? extends BaseValue<E>> key, E value) {
		if (this.supports(key)) {
			return Optional.of(asMutable().set(key, value).asImmutable());
		} else {
			return Optional.empty();
		}
	}

	@Override
	public int getContentVersion() {
		return 1;
	}

	@Override
	protected ImmutableValue<?> getValueGetter() {
		return Sponge.getRegistry().getValueFactory().createValue(TRANSMITTER, getValue()).asImmutable();
	}

	@Override
	public TransmitterData asMutable() {
		return new TransmitterData(this.getValue());
	}
}
