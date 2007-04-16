package com.beanview.zk;

import java.util.Collection;

import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;

import com.beanview.test.PeoplePicker;
import com.beanview.test.SimpleObject;
import com.beanview.test.SimpleObjectFactory;
import com.beanview.util.FactoryResolver;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;

public class ZkSimpleObjectFactoryTest extends AbstractZkTest<ZkSimpleObjectFactoryTest.TestRichlet>
{
	public ZkSimpleObjectFactoryTest() {
		super(new TestRichlet());
	}
	
	private static class TestRichlet extends GenericRichlet {
		protected ZkBeanViewPanel<PeoplePicker> bean;
	
	    protected PeoplePicker picker;

		public void service(Page page) {
			bean = new ZkBeanViewPanel<PeoplePicker>();
			bean.setPage(page);
			picker = new PeoplePicker();
			bean.setDataObject(picker);
		}
	
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

        assertNotNull(factoryResolver.getValues("allPeople", richlet.bean));
        assertEquals(20, factoryResolver.getValues("allPeople", richlet.bean).size());

        assertNotNull(factoryResolver.getValues("peopleByObjectMethod", richlet.bean));
        assertEquals(20, factoryResolver
                .getValues("peopleByObjectMethod", richlet.bean).size());

        assertNotNull(factoryResolver
                .getValues("favoriteLastNameMPeople", richlet.bean));
        assertEquals(4, factoryResolver.getValues("favoriteLastNameMPeople",
        		richlet.bean).size());

        assertNotNull(factoryResolver.getValues("peopleLetterMByObjectMethod",
        		richlet.bean));
        assertEquals(4, factoryResolver.getValues(
                "peopleLetterMByObjectMethod", richlet.bean).size());
    }

    public void testFactoryResolverContextualMethod()
    {
        assertNotNull(factoryResolver.getValues("peopleByContext", richlet.bean));
        assertEquals(20, factoryResolver.getValues("peopleByContext", richlet.bean)
                .size());

        richlet.bean.setContext("userID", "1");
        factoryResolver = new FactoryResolver();

        assertNotNull(factoryResolver.getValues("peopleByContext", richlet.bean));
        assertEquals(2, factoryResolver.getValues("peopleByContext", richlet.bean)
                .size());

        assertNotNull(factoryResolver.getValues(
                "peopleByObjectMethodWithContext", richlet.bean));
        assertEquals(2, factoryResolver.getValues(
                "peopleByObjectMethodWithContext", richlet.bean).size());

        richlet.bean.setContext("userID", "2");
        factoryResolver = new FactoryResolver();

        assertNotNull(factoryResolver.getValues("peopleByContext", richlet.bean));
        assertEquals(4, factoryResolver.getValues("peopleByContext", richlet.bean)
                .size());

        assertNotNull(factoryResolver.getValues(
                "peopleByObjectMethodWithContext", richlet.bean));
        assertEquals(4, factoryResolver.getValues(
                "peopleByObjectMethodWithContext", richlet.bean).size());
    }
}
