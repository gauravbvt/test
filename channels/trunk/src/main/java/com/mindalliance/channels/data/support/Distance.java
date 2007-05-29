/*
 * Created on Apr 30, 2007
 */
package com.mindalliance.channels.data.support;

import java.io.Serializable;

import com.beanview.annotation.PropertyOptions;

/**
 * A distance
 * 
 * @author jf
 */
public class Distance implements Serializable {

    enum Unit {
        METER, FOOT, KILOMETER, MILE
    };

    private Double value;
    private Unit unit;

    /**
     * @return the unit
     */
    @PropertyOptions(label=" ")
    public Unit getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit( Unit unit ) {
        this.unit = unit;
    }

    /**
     * @return the value
     */
    @PropertyOptions(label=" ")
    public Double getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue( Double value ) {
        this.value = value;
    }

}
