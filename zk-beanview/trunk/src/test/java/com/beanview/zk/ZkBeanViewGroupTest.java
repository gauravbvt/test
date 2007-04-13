package com.beanview.zk;

import com.beanview.BeanView;
import com.beanview.base.BeanViewGroupTestBase;
import com.beanview.test.Person;
import com.mindalliance.zk.beanview.ZkBeanViewPanel;

/**
 * @author $Author: wiverson $
 * @version $Revision: 1.1.1.1 $, $Date: 2006/09/19 04:41:57 $
 */

public class ZkBeanViewGroupTest extends BeanViewGroupTestBase
{

    @SuppressWarnings("unchecked")
    @Override
    protected void setUp() throws Exception
    {
        beans = new BeanView[5];

        for (int i = 0; i < beans.length; i++)
        {
            beans[i] = new ZkBeanViewPanel<Person>();
        }

        this.configBeans();
    }
}
