package com.beanview.test;

import java.lang.annotation.Annotation;
import java.util.ArrayList;

import com.beanview.BeanView;
import com.beanview.ValidatorInterface;
import com.beanview.swing.SwingBeanViewPanel;
import com.beanview.util.FactoryResolver;

import junit.framework.TestCase;

public class ValidationCoreTest extends TestCase
{
    BeanView<ValidationExample> bean;
    
    @Override
    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception
    {
        super.setUp();
        bean = new SwingBeanViewPanel<ValidationExample>();
    }

    public void testResolver()
    {
        System.out.println("TEST: basic validation resolver test");
        bean.setDataObject(new ValidationExample());

        FactoryResolver fr = new FactoryResolver();

        Annotation[] annotations = fr.getValidatorAnnotations("emailAddress",
                bean);
        assertNotNull(annotations);

        ArrayList<ValidatorInterface> list = fr.getValidators("emailAddress", bean);
        
        for(ValidatorInterface vi : list)
        {
            System.out.println("Found email validation annotation impl: " + vi.getClass().getSimpleName());
            assertEquals("EmailAddressImpl", vi.getClass().getSimpleName());
        }
    }
}
