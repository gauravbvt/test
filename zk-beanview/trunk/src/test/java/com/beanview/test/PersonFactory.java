package com.beanview.test;


import java.util.ArrayList;
import java.util.List;

/**
 * @author $Author: wiverson $
 * @version $Revision: 1.1.1.1 $, $Date: 2006/09/19 04:41:58 $
 */

public class PersonFactory
{
	public static Person[] getPersonArray()
	{
		Person[] people = new Person[5];

		for (int i = 0; i < people.length; i++)
		{
			people[i] = new Person();
			people[i].setFirstName("Phil the " + i);
			people[i].setLastName("A " + i + " stab");
		}
		return people;
	}
	
	public static List<Person> getPersonList()
	{
		ArrayList<Person> list = new ArrayList<Person>();
		Person[] people = getPersonArray();
		
		for(Person guy : people)
		{
			list.add(guy);
		};
		return list;
	}

}
