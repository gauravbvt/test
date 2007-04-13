package com.beanview.zk;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;

import com.beanview.test.SortExample;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;

public class ZkSortOptionsTest extends AbstractZkTest
{
	private static ZkBeanViewPanel bean;
	
	private static String[] subview;
	private static boolean includeAll;
	private static boolean sortAll;
	
	private static Button setSubViewButton;
    /* (non-Javadoc)
	 * @see com.beanview.zk.AbstractZkTest#service(org.zkoss.zk.ui.Page)
	 */
	@Override
	public void service(Page page) {
        bean = new ZkBeanViewPanel<SortExample>();
        bean.setDataObject(new SortExample());
        bean.setPage(page);
        setSubViewButton = new Button();
        setSubViewButton.addEventListener("onClick", new EventListener() {

			public boolean isAsap() {
				return false;
			}

			public void onEvent(Event arg0) {
		        bean.setSubView(subview, includeAll, sortAll);
			}});
        setSubViewButton.setPage(page);
	}
	   public void testDefault() throws Exception
	    {
	        System.out.println("TEST: Default sort test " + bean.getClass().getSimpleName());
	        String[] values =
	        { "alpha", "beta", "delta", "gamma", "zeta" };

	        String[] keys = bean.getBeanViewConfiguration().keys();

	        for (int i = 0; i < keys.length; i++)
	            assertEquals(values[i], keys[i]);
	    }

	    public void testExclude() throws Exception
	    {
	        System.out.println("TEST: Exclude properties test");
	        String[] values =
	        { "beta", "delta", "zeta" };
	        String[] exclude =
	        { "alpha", "gamma", "class" };

	        bean.setExcludeProperties(exclude);

	        String[] keys = bean.getBeanViewConfiguration().keys();

	        for (int i = 0; i < values.length; i++)
	            assertEquals(values[i], keys[i]);
	    }

	    public void testSubviewIncludeLimitedSorted() throws Exception
	    {
	        System.out.println("TEST: Subview Include Limited Sorted");
	        String[] subview =
	        { "alpha", "delta", "zeta", "beta" };
	        String[] values =
	        { "alpha", "beta", "delta", "zeta" };
	        setSubView(subview, false, true);

	        String[] keys = bean.getBeanViewConfiguration().keys();

	        for (int i = 0; i < values.length; i++)
	            assertEquals(values[i], keys[i]);
	    }

	    public void testSubviewIncludeLimitedUnSorted() throws Exception
	    {
	        System.out.println("TEST: Subview Include Limited Unsorted");
	        String[] subview =
	        { "alpha", "delta", "zeta", "beta" };
	        setSubView(subview, false, false);

	        String[] keys = bean.getBeanViewConfiguration().keys();

	        for (int i = 0; i < keys.length; i++)
	            assertEquals(subview[i], keys[i]);
	    }

	    public void testSubviewIncludeAllUnSorted() throws Exception
	    {
	        System.out.println("TEST: Subview Include All Unsorted");
	        String[] subview =
	        { "alpha", "zeta", "beta" };

	        String[] values =
	        { "alpha", "zeta", "beta", "delta", "gamma" };

	        setSubView(subview, true, false);

	        String[] keys = bean.getBeanViewConfiguration().keys();

	        for (int i = 0; i < keys.length; i++)
	            assertEquals(values[i], keys[i]);
	    }

	    public void testSubviewIncludeAllSorted() throws Exception
	    {
	        System.out.println("TEST: Subview Include All Sorted (Default)");
	        String[] values =
	        { "alpha", "beta", "delta", "gamma", "zeta" };

	        String[] subview =
	        { "alpha", "beta", "delta", "zeta", "gamma" };

	        setSubView(subview, true, true);

	        String[] keys = bean.getBeanViewConfiguration().keys();

	        for (int i = 0; i < keys.length; i++)
	            assertEquals(values[i], keys[i]);
	    }
	    
	    private void setSubView(String [] sub, boolean include, boolean sort){
	    	try {
				subview = sub;
				includeAll = include;
				sortAll = sort;
				selenium.click("id=" + setSubViewButton.getUuid());
				Thread.sleep(500);
			} catch (Exception e) {
				fail();
			}
	    }
}
