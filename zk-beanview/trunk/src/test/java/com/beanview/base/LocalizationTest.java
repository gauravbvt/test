package com.beanview.base;

import java.util.Properties;

import com.beanview.swing.SwingBeanViewPanel;
import com.beanview.test.Person;
import com.beanview.test.SimpleObject;
import com.beanview.test.SinglePersonPicker;
import com.beanview.util.LocalizationPropertyGen;

import junit.framework.TestCase;

public class LocalizationTest extends TestCase
{

	public void testGenerateKeys()
	{
		System.out.println("Checking basic localization");

		LocalizationPropertyGen tool = new LocalizationPropertyGen();
		SwingBeanViewPanel<Person> view = new SwingBeanViewPanel<Person>();
		view.setDataObject(new Person());
		tool.addBean(view);

		Properties props = tool.getProperties();

		String[] tests =
		{ "beanview.com.beanview.test.Person.ID", "I D",
				"beanview.com.beanview.test.Person.bankAccountBalance",
				"Bank Account Balance",
				"beanview.com.beanview.test.Person.likesCats", "Likes Cats",
				"beanview.com.beanview.test.Person.colorsLiked",
				"Colors Liked", "beanview.com.beanview.test.Person.nullTest",
				"Null Test", "beanview.com.beanview.test.Person.favoriteColor",
				"Favorite Color",
				"beanview.com.beanview.test.Person.birthTime", "Birth Time",
				"beanview.com.beanview.test.Person.likesDogs", "Likes Dogs",
				"beanview.com.beanview.test.Person.favoriteNumber",
				"Favorite Number",
				"beanview.com.beanview.test.Person.birthday", "Birthday",
				"beanview.com.beanview.test.Person.lastName", "Last Name",
				"beanview.com.beanview.test.Person.firstName", "First Name" };

		for (int i = 0; i < tests.length; i = i + 2)
		{
			assertNotNull(tests[i]);
			assertTrue(tests[i + 1].compareTo(props.getProperty(tests[i])) == 0);
			System.out.println(tests[i] + "=" + props.get(tests[i]));
		}
	}

	private LocalizationPropertyGen getMultipleBeanTool()
	{
		LocalizationPropertyGen tool = new LocalizationPropertyGen();

		SwingBeanViewPanel<Person> bean1 = new SwingBeanViewPanel<Person>();
		bean1.setDataObject(new Person());

		SwingBeanViewPanel<SimpleObject> bean2 = new SwingBeanViewPanel<SimpleObject>();
		bean2.setDataObject(new SimpleObject());

		SwingBeanViewPanel<SinglePersonPicker> bean3 = new SwingBeanViewPanel<SinglePersonPicker>();
		bean3.setDataObject(new SinglePersonPicker());

		tool.addBean(bean1);
		tool.addBean(bean2);
		tool.addBean(bean3);

		return tool;
	}

	public void testMultipleBeans()
	{
		System.out.println("Checking multiple bean localization");

		LocalizationPropertyGen tool = getMultipleBeanTool();

		String[] tests =
		{
				"beanview.com.beanview.test.SinglePersonPicker.peopleLetterMByObjectMethod",
				"People Letter M By Object Method",
				"beanview.com.beanview.test.SimpleObject.lastName",
				"Last Name",
				"beanview.com.beanview.test.SinglePersonPicker.peopleByObjectMethodWithContext",
				"People By Object Method With Context",
				"beanview.com.beanview.test.SinglePersonPicker.favoriteLastNameMPeople",
				"Favorite Last Name M People",
				"beanview.com.beanview.test.Person.birthday",
				"Birthday",
				"beanview.com.beanview.test.Person.favoriteColor",
				"Favorite Color",
				"beanview.com.beanview.test.Person.lastName",
				"Last Name",
				"beanview.com.beanview.test.Person.favoriteNumber",
				"Favorite Number",
				"beanview.com.beanview.test.Person.bankAccountBalance",
				"Bank Account Balance",
				"beanview.com.beanview.test.Person.likesCats",
				"Likes Cats",
				"beanview.com.beanview.test.Person.ID",
				"I D",
				"beanview.com.beanview.test.Person.colorsLiked",
				"Colors Liked",
				"beanview.com.beanview.test.Person.birthTime",
				"Birth Time",
				"beanview.com.beanview.test.SimpleObject.ID",
				"I D",
				"beanview.com.beanview.test.SinglePersonPicker.allPeople",
				"All People",
				"beanview.com.beanview.test.Person.likesDogs",
				"Likes Dogs",
				"beanview.com.beanview.test.SimpleObject.firstName",
				"First Name",
				"beanview.com.beanview.test.SinglePersonPicker.peopleByContext",
				"People By Context",
				"beanview.com.beanview.test.Person.firstName",
				"First Name",
				"beanview.com.beanview.test.Person.nullTest",
				"Null Test",
				"beanview.com.beanview.test.SinglePersonPicker.peopleByObjectMethod",
				"People By Object Method", };

		Properties props = tool.getProperties();

		for (int i = 0; i < tests.length; i = i + 2)
		{
			assertNotNull(tests[i]);
			assertTrue(tests[i + 1].compareTo(props.getProperty(tests[i])) == 0);
			System.out.println(tests[i] + "=" + props.get(tests[i]));
		}
	}

	public void testSave()
	{
		System.out.println("Save localization test");
		
//		LocalizationPropertyGen tool = getMultipleBeanTool();
//		tool.save();
	}

}
