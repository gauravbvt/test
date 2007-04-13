package com.beanview.test;


/**
 * Base class for tests.
 * 
 * @author $Author: wiverson $
 * @version $Revision: 1.1.1.1 $, $Date: 2006/09/19 04:41:58 $
 */
public class ArrayTypes
{
    double[] doubleArrayTest = new double[] {0.0};
    
    float[] floatArrayTest = new float[] {0.0f};

    int[] integerArrayTest = new int[] {0};

    long[] longArrayTest = new long[] {0};

    String[] stringArrayTest = new String[] {""};

    public double[] getDoubleArrayTest()
    {
        return this.doubleArrayTest;
    }

    public void setDoubleArrayTest(double[] doubleArrayTest)
    {
        this.doubleArrayTest = doubleArrayTest;
    }

    public float[] getFloatArrayTest()
    {
        return this.floatArrayTest;
    }

    public void setFloatArrayTest(float[] floatArrayTest)
    {
        this.floatArrayTest = floatArrayTest;
    }

    public int[] getIntegerArrayTest()
    {
        return this.integerArrayTest;
    }

    public void setIntegerArrayTest(int[] integerArrayTest)
    {
        this.integerArrayTest = integerArrayTest;
    }

    public long[] getLongArrayTest()
    {
        return this.longArrayTest;
    }

    public void setLongArrayTest(long[] longArrayTest)
    {
        this.longArrayTest = longArrayTest;
    }

    public String[] getStringArrayTest()
    {
        return this.stringArrayTest;
    }

    public void setStringArrayTest(String[] stringArrayTest)
    {
        this.stringArrayTest = stringArrayTest;
    }

}
