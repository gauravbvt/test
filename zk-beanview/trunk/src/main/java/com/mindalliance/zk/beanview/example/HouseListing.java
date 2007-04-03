package com.mindalliance.zk.beanview.example;

import java.sql.Date;

public class HouseListing
{
	private int referenceNumber;

	private String market;

	private String briefDescription;
	
	private String agentId;

	private String addressLine1;

	private String addressLine2;

	private Date datePlacedOnMarket;

	private boolean offerPending;

	private boolean recentlyListed;

	public enum HouseType
	{
		SingleFamily, Condo, MultiFamiliy
	};

	private HouseType houseType;

	public String getAddressLine1()
	{
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1)
	{
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2()
	{
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2)
	{
		this.addressLine2 = addressLine2;
	}

	public Date getDatePlacedOnMarket()
	{
		return datePlacedOnMarket;
	}

	public void setDatePlacedOnMarket(Date datePlacedOnMarket)
	{
		this.datePlacedOnMarket = datePlacedOnMarket;
	}

	public boolean isRecentlyListed()
	{
		return recentlyListed;
	}

	public void setRecentlyListed(boolean listedLast180Days)
	{
		this.recentlyListed = listedLast180Days;
	}

	public boolean isOfferPending()
	{
		return offerPending;
	}

	public void setOfferPending(boolean offerPending)
	{
		this.offerPending = offerPending;
	}

	public String getAgentId()
	{
		return agentId;
	}

	public void setAgentId(String agentId)
	{
		this.agentId = agentId;
	}

	public String getBriefDescription()
	{
		return briefDescription;
	}

	public void setBriefDescription(String briefDescription)
	{
		this.briefDescription = briefDescription;
	}

	public HouseType getHouseType()
	{
		return houseType;
	}

	public void setHouseType(HouseType houseType)
	{
		this.houseType = houseType;
	}

	public String getMarket()
	{
		return market;
	}

	public void setMarket(String market)
	{
		this.market = market;
	}

	public int getReferenceNumber()
	{
		return referenceNumber;
	}

	public void setReferenceNumber(int referenceNumber)
	{
		this.referenceNumber = referenceNumber;
	}
}
