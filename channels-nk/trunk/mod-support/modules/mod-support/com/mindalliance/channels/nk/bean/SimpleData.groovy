package com.mindalliance.channels.nk.bean
/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 25, 2008
 * Time: 1:51:42 PM
 * To change this template use File | Settings | File Templates.
 */
class SimpleData  extends AbstractBeanPropertyValue implements ISimpleData {

    static final List SUPPORTED_TYPES = [Date.class, String.class, Integer.class, Boolean.class, Double.class, Float.class, BigDecimal.class] // TODO - add to this

    Class dataClass // assumes that this.class.newInstance([this.toString()] is supported
    private _str
    def value

    SimpleData(Class aClass) {
        this.dataClass = aClass
        assert SUPPORTED_TYPES.contains(aClass)
    }

    static SimpleData from(Class aClass, String val) {
        SimpleData data = new SimpleData(aClass)
        data.@_str = val
        if (aClass.name == String.class.name) data.@value = val
        return data
    }

    static SimpleData from(Object val) {
        SimpleData data = new SimpleData(val.class)
        data.@value = val
        return data
    }

    def getValue() {
        if (value == null) {
          String eval = "new ${dataClass.name}(\"$_str\")"
          value = Eval.me(eval)
        }
        return value
    }


    Object deepCopy() {
        SimpleData copy = SimpleData.from(getValue())
        return copy
    }

    String toString() {
        return getValue().toString()
    }

    Object get(String name) {
        (name == 'dataClass' ) ? this.@dataClass : getValue()."$name"
    }

    Object invokeMethod(String name, Object args) {
        return getValue().invokeMethod(name, args)
    }


}