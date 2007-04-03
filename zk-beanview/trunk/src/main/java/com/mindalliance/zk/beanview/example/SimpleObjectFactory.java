package com.mindalliance.zk.beanview.example;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import com.beanview.BeanView;

/**
 * @author $Author$
 * @version $Revision$, $Date$
 */

public class SimpleObjectFactory
{
	static String[] names =
	{ "Emily", "Anderson", "Christopher", "Brown", "Nicholas", "Davis",
			"Abigail", "Garcia", "Alexis", "Harris", "Hannah", "Johnson",
			"Michael", "Jackson", "Matthew", "Jones", "Samantha", "Martin",
			"Elizabeth", "Martinez", "Andrew", "Miller", "Daniel", "Moore",
			"Jessica", "Robinson", "Jacob", "Smith", "William", "Taylor",
			"Madison", "Thomas", "Sarah", "Thompson", "Ashley", "White",
			"Joshua", "Williams", "Joseph", "Wilson" };

	static HashMap<SimpleObject, String> potentialObjects;

	public static void initPotentialObjects()
	{
		potentialObjects = new HashMap<SimpleObject, String>();
		SimpleObject obj = new SimpleObject();
		for (int i = 0; i < names.length / 2; i++)
		{
			obj = new SimpleObject();
			obj.setFirstName(names[i]);
			obj.setLastName(names[i + 1]);
			potentialObjects.put(obj, obj.getID());
		}
	}

	public static Collection<SimpleObject> getPotentialObjects()
	{
		if (potentialObjects == null)
			initPotentialObjects();

		return potentialObjects.keySet();
	}

	public static Collection<SimpleObject> getLastNameStartsWithM()
	{
		if (potentialObjects == null)
			initPotentialObjects();

		HashSet<SimpleObject> temp = new HashSet<SimpleObject>();
		for (SimpleObject obj : potentialObjects.keySet())
		{
			if (obj.getLastName().startsWith("M"))
				temp.add(obj);
		}

		return temp;
	}

	public static Collection<SimpleObject> getFromUserIDContext(BeanView bean)
	{
		if (potentialObjects == null)
			initPotentialObjects();
		
		String userID = (String)bean.getContext("userID");
		if(userID == null)
			userID = "";

		if (userID.compareTo("1") == 0)
		{
			HashSet<SimpleObject> temp = new HashSet<SimpleObject>();
			SimpleObject obj = new SimpleObject();
			obj.setFirstName("Big");
			obj.setLastName("BEM");
			temp.add(obj);
			obj = new SimpleObject();
			obj.setFirstName("Scary");
			obj.setLastName("McMonster");
			temp.add(obj);
			return temp;
		}

		if (userID.compareTo("2") == 0)
			return getLastNameStartsWithM();

		return getPotentialObjects();
	}

}
