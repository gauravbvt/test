package com.mindalliance.channels.nk.bean
/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 25, 2008
 * Time: 10:50:26 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractBean implements IBean {

    /* Metadata keys (all are optional): 
        id, label, hint, required, readonly, cssClass, appearance, anyAttribute, constraint (all)
        range, step (Numerical SimpleData)
        choices (SimpleData, BeanList) - query name, or list of strings (enumerated items), or list of lists  (XForm choices = tree of items with named branches)
        open (BeanList of SimpleData) - choices are not closed if true
        number (BeanList when no choices) - how many list items to display at once in an XForm repeat
    */
    Map defaultMetadata = [:] // [propName : [key:value,...], propName: [key:value...], ...]

    void initialize() {} // default

    boolean isComponent() {
        return false;
    }

    boolean isPersistent() {
        return false;
    }

    /*
        Map<String, IBeanPropertyValue> getBeanProperties() {
           def props = this.properties   // TODO - Goes to lah-lah land
           def bProps = props.findAll {entry -> entry.@value instanceof IBeanPropertyValue}
           return bProps
        }
    */

    def deepCopy() {
        IBean copy
        copy = (IBean) clone()
        getBeanProperties().each {propKey, propValue ->
            this."$propKey" = propValue.deepCopy();
        }
        return copy
    }

    Expando getPropertyMetaData(String propName) {
        return this."$propName".metadata
    }

}