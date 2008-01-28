package com.mindalliance.channels.nk.bean
/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 25, 2008
 * Time: 10:50:26 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractBean implements IBean {

    private Map<String, IBeanPropertyMetaData> metaData;


    boolean isComponent() {
        return false;
    }

    boolean isPersistent() {
        return false;
    }

    /*
        Map<String, IBeanPropertyValue> getBeanProperties() {
           def props = this.properties   // TODO - Goes into lah-lah land
           def bProps = props.findAll {entry -> entry.@value instanceof IBeanPropertyValue}
           return bProps
        }
    */
    void accept(Closure action) {
        action(this)
        getBeanProperties().each {propKey, propValue ->
            propValue.accept(action)
        }
    }

    def deepCopy() {
        IBean copy
        copy = (IBean) clone()
        getBeanProperties().each {propKey, propValue ->
            this."$propKey" = propValue.deepCopy();
        }
        return copy
    }

    IBeanPropertyMetaData getPropertyMetaData(String propName) {
        return metaData[propName]
    }

    // Default
    Map getMetaData() {
        return [:]
    }

    // Make sure bean properties

}