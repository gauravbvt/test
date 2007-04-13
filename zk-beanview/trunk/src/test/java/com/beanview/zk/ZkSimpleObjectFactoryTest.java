package com.beanview.zk;

import java.util.Collection;

import org.zkoss.zk.ui.Page;

import com.beanview.test.PeoplePicker;
import com.beanview.test.SimpleObject;
import com.beanview.test.SimpleObjectFactory;
import com.beanview.util.FactoryResolver;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;

public class ZkSimpleObjectFactoryTest extends AbstractZkTest
{

	private static ZkBeanViewPanel<PeoplePicker> bean;

    private static PeoplePicker picker;
    /* (non-Javadoc)
	 * @see com.beanview.zk.AbstractZkTest#service(org.zkoss.zk.ui.Page)
	 */
	@Override
	public void service(Page page) {
		bean = new ZkBeanViewPanel<PeoplePicker>();
		bean.setPage(page);
		picker = new PeoplePicker();
		bean.setDataObject(picker);
	}
	

    public void testInitPotentialObjects()
    {
        Collection<SimpleObject> result = SimpleObjectFactory
                .getPotentialObjects();
        assertNotNull(result);
        assertEquals(20, result.size());
    }

    public void testObjectsWithLastNameM()
    {
        Collection<SimpleObject> result = SimpleObjectFactory
                .getLastNameStartsWithM();
        assertNotNull(result);
        assertEquals(4, result.size());
    }



    FactoryResolver factoryResolver;

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception
    {
        super.setUp();

        factoryResolver = new FactoryResolver();

    }
    


	public void testFactoryResolverStaticMethod()
    {

        assertNotNull(factoryResolver.getValues("allPeople", bean));
        assertEquals(20, factoryResolver.getValues("allPeople", bean).size());

        assertNotNull(factoryResolver.getValues("peopleByObjectMethod", bean));
        assertEquals(20, factoryResolver
                .getValues("peopleByObjectMethod", bean).size());

        assertNotNull(factoryResolver
                .getValues("favoriteLastNameMPeople", bean));
        assertEquals(4, factoryResolver.getValues("favoriteLastNameMPeople",
                bean).size());

        assertNotNull(factoryResolver.getValues("peopleLetterMByObjectMethod",
                bean));
        assertEquals(4, factoryResolver.getValues(
                "peopleLetterMByObjectMethod", bean).size());
    }

    public void testFactoryResolverContextualMethod()
    {
        assertNotNull(factoryResolver.getValues("peopleByContext", bean));
        assertEquals(20, factoryResolver.getValues("peopleByContext", bean)
                .size());

        bean.setContext("userID", "1");
        factoryResolver = new FactoryResolver();

        assertNotNull(factoryResolver.getValues("peopleByContext", bean));
        assertEquals(2, factoryResolver.getValues("peopleByContext", bean)
                .size());

        assertNotNull(factoryResolver.getValues(
                "peopleByObjectMethodWithContext", bean));
        assertEquals(2, factoryResolver.getValues(
                "peopleByObjectMethodWithContext", bean).size());

        bean.setContext("userID", "2");
        factoryResolver = new FactoryResolver();

        assertNotNull(factoryResolver.getValues("peopleByContext", bean));
        assertEquals(4, factoryResolver.getValues("peopleByContext", bean)
                .size());

        assertNotNull(factoryResolver.getValues(
                "peopleByObjectMethodWithContext", bean));
        assertEquals(4, factoryResolver.getValues(
                "peopleByObjectMethodWithContext", bean).size());
    }
}
