package com.gmail.trentech.wirelessred.data;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.Value;

import com.gmail.trentech.wirelessred.data.receiver.Receiver;
import com.gmail.trentech.wirelessred.data.transmitter.Transmitter;

public class Keys {

	public static final Key<Value<Transmitter>> TRANSMITTER = KeyFactory.makeSingleKey(Transmitter.class, Value.class, DataQuery.of("transmitter"));
	public static final Key<Value<Receiver>> RECEIVER = KeyFactory.makeSingleKey(Receiver.class, Value.class, DataQuery.of("receiver"));
}
