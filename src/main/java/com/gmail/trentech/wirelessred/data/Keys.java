package com.gmail.trentech.wirelessred.data;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.Value;

import com.gmail.trentech.wirelessred.data.receiver.Receiver;
import com.gmail.trentech.wirelessred.data.transmitter.Transmitter;
import com.google.common.reflect.TypeToken;

public class Keys {

	private static final TypeToken<Value<Transmitter>> VALUE_TRANSMITTER = new TypeToken<Value<Transmitter>>() {
		private static final long serialVersionUID = 395242399877312340L;
    };    
	private static final TypeToken<Transmitter> TRANSMITTER_TOKEN = new TypeToken<Transmitter>() {
		private static final long serialVersionUID = -8726734755833911770L;
    };
	private static final TypeToken<Value<Receiver>> VALUE_RECEIVER = new TypeToken<Value<Receiver>>() {
		private static final long serialVersionUID = 395242399877312340L;
    };    
	private static final TypeToken<Receiver> RECEIVER_TOKEN = new TypeToken<Receiver>() {
		private static final long serialVersionUID = -8726734755833911770L;
    };
    
	public static final Key<Value<Transmitter>> TRANSMITTER = KeyFactory.makeSingleKey(TRANSMITTER_TOKEN, VALUE_TRANSMITTER, DataQuery.of("transmitter"), "wirelessred:transmitter", "transmitter");
	public static final Key<Value<Receiver>> RECEIVER = KeyFactory.makeSingleKey(RECEIVER_TOKEN, VALUE_RECEIVER, DataQuery.of("receiver"), "wirelessred:receiver", "receiver");
}
