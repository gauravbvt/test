package com.mindalliance.channels.nk.bean
/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 25, 2008
 * Time: 1:51:42 PM
 * To change this template use File | Settings | File Templates.
 */
class SimpleData extends AbstractBeanPropertyValue implements ISimpleData {

    static final List SUPPORTED_TYPES = [Date.class, String.class, Integer.class, Boolean.class, Double.class, Float.class, BigDecimal.class] // TODO - add to this

    Class dataClass // assumes that this.class.newInstance([this.toString()] is supported
    def value

    void initializeFrom(Object value) {
       setValue(value) 
    }

    def getValue() {
        if (isCalculated()) {
            return calculate()
        }
        else {
            return value
        }
    }

    String getSchemaType() {
        switch (dataClass) {
            case Date.class: return 'date'; break
            case Boolean.class: return 'boolean'; break
            case BigDecimal.class: return 'decimal'; break
            case Integer.class: return 'integer'; break
            case Double.class: return 'double'; break
            case Float.class: return 'double'; break
            default: return super.getSchemaType()
        }
    }

    Object deepCopy() {
        SimpleData sd
        if (isCalculated()) {
            sd = new SimpleData(dataClass:dataClass, calculate:this.calculate)
        }
        else {
            sd = new SimpleData(dataClass:dataClass)
            sd.value = getValue()
        }
        return sd
    }

    String toString() {
        def val = getValue()
        return (val == null) ? '' : "$val"
    }

    void setValue(Object value) {
        if (!isCalculated()) {
           this.value = value
           if (value != null && !dataClass.isAssignableFrom(value.class)) {
              throw new IllegalArgumentException("Simple data type mismatch. $value is a ${value.class.name} and expecting a ${dataClass.name}")
           }
        }
        else {
            throw new Exception("Can't assign to a derived simple data.")
        }
    }

    Object get(String name) {
        (name == 'dataClass') ? this.@dataClass : getValue()."$name"
    }

    Object invokeMethod(String name, Object args) {
        return getValue().invokeMethod(name, args)
    }

}