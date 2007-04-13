package com.beanview.base;

import junit.framework.TestCase;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.WrapDynaBean;

import com.beanview.BeanView;
import com.beanview.PropertyComponent;

public class FieldCheckUtil
{
    public static void checkFields(BeanView panel, Object in)
    {
        DynaBean bean = new WrapDynaBean(in);
        DynaProperty[] props = bean.getDynaClass().getDynaProperties();
        String[] keys = new String[props.length];
        int i = 0;
        for (DynaProperty key : props)
        {
            keys[i++] = key.getName();
        }

        for (String field : keys)
        {
            if (!field.equals("class"))
            {
                try
                {
                    PropertyComponent prop = panel.getPropertyComponent(field);
                    TestCase.assertNull(panel.getError(field), panel
                            .getError(field));
                    TestCase.assertNotNull(field, prop);
                } catch (Exception e)
                {
                    System.err.println("Bad field: " + field);
                    e.printStackTrace();
                }
            }
        }
        System.out.println("  fields checked:" + (keys.length - 1));
    }

}
