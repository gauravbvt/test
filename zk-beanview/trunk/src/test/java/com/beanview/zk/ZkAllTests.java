/**
 * @author $Author: wiverson $
 * @version $Revision: 1.1.1.1 $, $Date: 2006/09/19 04:41:57 $
 */
package com.beanview.zk;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author $Author: wiverson $
 * @version $Revision: 1.1.1.1 $, $Date: 2006/09/19 04:41:57 $
 */

public class ZkAllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.beanview.zk");
		//$JUnit-BEGIN$
		suite.addTestSuite(ZkSingleObjectSelectTest.class);
		suite.addTestSuite(ZkSortOptionsTest.class);
		suite.addTestSuite(ZkPeopleFactoryTest.class);
		suite.addTestSuite(ZkPanelTest.class);
		suite.addTestSuite(ZkSimpleObjectFactoryTest.class);
		suite.addTestSuite(ZkCollectionSupportTest.class);
		suite.addTestSuite(ZkConfigurationTest.class);
		suite.addTestSuite(MenuSelectionTest.class);
		suite.addTestSuite(ZkPerformanceTest.class);
		suite.addTestSuite(ZkBeanViewGroupTest.class);
		//$JUnit-END$
		return suite;
	}

}
