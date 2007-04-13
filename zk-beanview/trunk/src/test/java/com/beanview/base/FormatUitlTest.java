package com.beanview.base;

import com.beanview.util.FormatUtils;

import junit.framework.TestCase;

public class FormatUitlTest extends TestCase
{
    public void testTypical()
    {
        assertEquals("First Name", FormatUtils.formatName("firstName"));
    }

    public void testUnderscore()
    {
        assertEquals("First name", FormatUtils.formatName("first_name"));
    }

    public void testFirstCap()
    {
        assertEquals("I D", FormatUtils.formatName("ID"));
    }

    public void testDigits()
    {
        assertEquals("Lucky 7", FormatUtils.formatName("lucky7"));
    }
}
