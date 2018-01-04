package com.gmail.trentech.wirelessred.data;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;

import com.gmail.trentech.wirelessred.data.receiver.Receiver;
import com.gmail.trentech.wirelessred.data.transmitter.Transmitter;
import com.google.common.reflect.TypeToken;

public class Keys {

	private static final TypeToken<Value<Transmitter>> VALUE_TRANSMITTER = new TypeToken<Value<Transmitter>>() {
		private static final long serialVersionUID = 395242399877312340L;
    };
	private static final TypeToken<Value<Receiver>> VALUE_RECEIVER = new TypeToken<Value<Receiver>>() {
		private static final long serialVersionUID = 395242399877312340L;
    };    

    public static final Key<Value<Transmitter>> TRANSMITTER = Key.builder().type(VALUE_TRANSMITTER).query(DataQuery.of("transmitter")).id("wirelessred:transmitter").name("transmitter").build();
    public static final Key<Value<Receiver>> RECEIVER = Key.builder().type(VALUE_RECEIVER).query(DataQuery.of("receiver")).id("wirelessred:receiver").name("receiver").build();
}
