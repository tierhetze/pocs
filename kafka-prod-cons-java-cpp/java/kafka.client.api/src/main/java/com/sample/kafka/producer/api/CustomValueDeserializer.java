package com.sample.kafka.producer.api;

import kafka.serializer.Decoder;

import org.apache.commons.lang3.SerializationUtils;

/**
 * 
 * Value deserializer
 *
 */
public class CustomValueDeserializer implements Decoder<CustomValue> {

	public CustomValue fromBytes(byte[] data) {
		return SerializationUtils.deserialize(data);
	}

}
