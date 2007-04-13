package com.beanview.zk;

import java.util.List;

import org.zkoss.zk.ui.Page;

import com.beanview.base.FieldCheckUtil;
import com.beanview.test.PeoplePicker;
import com.beanview.test.Person;
import com.beanview.test.PersonFactory;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;

/**
 * @author $Author: wiverson $
 * @version $Revision: 1.1.1.1 $, $Date: 2006/09/19 04:41:57 $
 */

public class ZkPeopleFactoryTest extends AbstractZkTest
{

	/* (non-Javadoc)
	 * @see com.beanview.zk.AbstractZkTest#service(org.zkoss.zk.ui.Page)
	 */
	@Override
	public void service(Page page) {
		bean = new ZkBeanViewPanel<PeoplePicker>();
		picker = new PeoplePicker();
		bean.setDataObject(picker);
		bean.setPage(page);
	}

	private static PeoplePicker picker;

	private static ZkBeanViewPanel<PeoplePicker> bean;

	@SuppressWarnings("unchecked")
	protected void setUp() throws Exception
	{
		super.setUp();

	}

	public void testBeanViewPanelComponents()
	{
		System.out.println("TEST: testBeanViewPanelComponents");

		FieldCheckUtil.checkFields(bean, picker);
	}

	public void testGetPersonArray()
	{
		System.out.println("TEST: testGetPersonArray");

		Person[] people = PersonFactory.getPersonArray();
		assertNotNull(people);
		assertEquals(people.length, 5);
	}

	public void testGetPersonList()
	{
		System.out.println("TEST: testGetPersonList");

		List<Person> people = PersonFactory.getPersonList();
		assertNotNull(people);
		assertEquals(people.size(), 5);
	}
}
