package com.beanview.test;

import java.sql.Time;

/**
 * @author $Author: wiverson $
 * @version $Revision: 1.1.1.1 $, $Date: 2006/09/19 04:41:58 $
 */
@com.beanview.annotation.ClassOptions
public class Person
{

	/** Creates a new instance of Person */
	public Person()
	{
	}

	public Person(String firstName, String lastName)
	{
		this.firstName = firstName;
		this.lastName = lastName;
	}

	private String firstName = "Bob";

	private String lastName = "Smith";

	private String nullTest = null;

	private java.sql.Date birthday = new java.sql.Date(new java.util.Date()
			.getTime());

	private Time birthTime = new java.sql.Time(new java.util.Date().getTime());

	private float favoriteNumber = 0.0f;

	private boolean likesCats = false;

	private boolean likesDogs = true;

	private double bankAccountBalance = 100;

	FavoriteColor favoriteColor = FavoriteColor.Blue;

	FavoriteColor[] colorsLiked = null;

	String id;

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public java.sql.Date getBirthday()
	{
		return birthday;
	}

	public void setBirthday(java.sql.Date birthday)
	{
		this.birthday = birthday;
	}

	public float getFavoriteNumber()
	{
		return favoriteNumber;
	}

	public void setFavoriteNumber(float favoriteNumber)
	{
		this.favoriteNumber = favoriteNumber;
	}

	public boolean isLikesCats()
	{
		return likesCats;
	}

	public void setLikesCats(boolean likesCats)
	{
		this.likesCats = likesCats;
	}

	public double getBankAccountBalance()
	{
		return bankAccountBalance;
	}

	public void setBankAccountBalance(double bankAccountBalance)
	{
		this.bankAccountBalance = bankAccountBalance;
	}

	public String getNullTest()
	{
		return nullTest;
	}

	public void setNullTest(String nullTest)
	{
		this.nullTest = nullTest;
	}

	public boolean isLikesDogs()
	{
		return likesDogs;
	}

	public void setLikesDogs(boolean likesDogs)
	{
		this.likesDogs = likesDogs;
	}

	public String getID()
	{
		if (id == null)
			id = 1 + "";
		return id;
	}

	public void setID(String in)
	{
		this.id = in;
	}

	public FavoriteColor getFavoriteColor()
	{
		return favoriteColor;
	}

	public void setFavoriteColor(FavoriteColor colors)
	{
		this.favoriteColor = colors;
	}

	public Time getBirthTime()
	{
		return birthTime;
	}

	public void setBirthTime(Time birthTime)
	{
		this.birthTime = birthTime;
	}

	public FavoriteColor[] getColorsLiked()
	{
		return colorsLiked;
	}

	public void setColorsLiked(FavoriteColor[] colorsLiked)
	{
		this.colorsLiked = colorsLiked;
	}

    @Override
    public String toString()
    {
        return this.lastName + ", " + this.firstName + "(" + this.getID() + ")";
    }
    
}
