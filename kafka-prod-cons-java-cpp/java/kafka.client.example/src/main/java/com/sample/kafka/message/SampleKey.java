package com.sample.kafka.message;

import com.sample.kafka.producer.api.CustomKey;

public class SampleKey extends CustomKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5411461028982115914L;
	
	private String region;
	private char   side;
	private int    securityId;
	private String producerId;
	
	
	public SampleKey(final String region, final char side, final int securityId, final String producerId) {
		super();
		this.region = region;
		this.side = side;
		this.securityId = securityId;
		this.producerId = producerId;
	}
	
	public SampleKey(){}
	
	
    /**
    * {@inheritDoc}
    */
	@Override
    protected int getUniqueTopicSegmentId() {
    	
    	final int prime = 31;
		int result = 1;
		result = prime * result + ((region == null) ? 0 : region.hashCode());
		result = prime * result + securityId;
		result = prime * result + side;
		result = prime * result + ((producerId == null) ? 0 : producerId.hashCode());
		return result;
		
	}


	public String getRegion() {
		return region;
	}


	public char getSide() {
		return side;
	}


	public int getSecurityId() {
		return securityId;
	}

	public String getProducerId() {
		return producerId;
	}

	@Override
	public String toString() {
		return "SampleKey [region=" + region + ", side=" + side
				+ ", securityId=" + securityId + ", producerId=" + producerId
				+ "]";
	}


	
	
	
	
	
}

