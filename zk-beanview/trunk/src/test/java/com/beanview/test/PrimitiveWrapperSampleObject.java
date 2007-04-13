package com.beanview.test;

/**
 * @author $Author: wiverson $
 * @version $Revision: 1.1.1.1 $, $Date: 2006/09/19 04:41:58 $
 */

public class PrimitiveWrapperSampleObject
{
    Boolean booleanTest = new Boolean(false);

    Short shortTest = new Short("0");

    Integer intTest = new Integer(0);

    Long longTest = new Long(0);

    Float floatTest = new Float(0.0);

    Double doubleTest = new Double(0.0);
    
    String stringTest = "";

    public Boolean getBooleanTest()
    {
        return this.booleanTest;
    }

    public void setBooleanTest(Boolean booleanTest)
    {
        this.booleanTest = booleanTest;
    }

    public Double getDoubleTest()
    {
        return this.doubleTest;
    }

    public void setDoubleTest(Double doubleTest)
    {
        this.doubleTest = doubleTest;
    }

    public Float getFloatTest()
    {
        return this.floatTest;
    }

    public void setFloatTest(Float floatTest)
    {
        this.floatTest = floatTest;
    }

    public Integer getIntTest()
    {
        return this.intTest;
    }

    public void setIntTest(Integer intTest)
    {
        this.intTest = intTest;
    }

    public Long getLongTest()
    {
        return this.longTest;
    }

    public void setLongTest(Long longTest)
    {
        this.longTest = longTest;
    }

    public Short getShortTest()
    {
        return this.shortTest;
    }

    public void setShortTest(Short shortTest)
    {
        this.shortTest = shortTest;
    }

    public String getStringTest()
    {
        return this.stringTest;
    }

    public void setStringTest(String stringTest)
    {
        this.stringTest = stringTest;
    }
}
