package com.mindalliance.channels.nk.bean
/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 25, 2008
 * Time: 3:56:17 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractBeanPropertyValue implements IBeanPropertyValue {

    IPersistentBean contextBean
    Expando metadata
    String calculate

    void initialize() {} // Do nothing - a hook

    boolean isCalculated() {
        return calculate != null
    }

    // Not supported: Calculated property values that have calculated properties 
    def calculate() {
        assert this.calculate
        assert contextBean
        return contextBean."$calculate"()
    }

    // Visitor pattern
    void accept(Map args, Closure action) {// DEFAULT
        action(args, this)
    }

    Expando getMetadata() {
        return metadata
    }

    void initContextBean(IPersistentBean bean) {
        assert bean
        contextBean = bean
    }

    void initMetadata(String propName, String xpath, Map defaultMetadata) {    // default
       metadata = AbstractBeanPropertyValue.prepareMetadata(propName, xpath, defaultMetadata)
       metadata.type = getSchemaType()
    }

    static Expando prepareMetadata(String propName, String xpath, Map defaultMetadata) {
        Map initial = defaultMetadata[propName] ?: [:]
        Expando meta = new Expando(initial)
        meta.propertyName = propName
        meta.path = "$xpath$propName"
        meta.id = meta.path.replaceAll('/', '.').substring(1)
        return meta
    }

    String getSchemaType() {     // DEFAULT
        return "string"
    }

    abstract def deepCopy();

    static IBeanPropertyValue newBeanPropertyValue(Class aClass) {
        IBeanPropertyValue propValue
        switch(aClass) {
            case IBeanReference.class:  propValue = new BeanReference()
                                        break
            case IBeanList.class: break
            case {IComponentBean.class.isAssignableFrom it}: break
            case {SimpleData.SUPPORTED_TYPES.contains(it)}: break
            default: throw new IllegalArgumentException("Invalid bean property value class $aClass")
        }
        return propValue
    }

}