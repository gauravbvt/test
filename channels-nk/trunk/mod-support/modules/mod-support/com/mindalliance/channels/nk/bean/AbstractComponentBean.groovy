package com.mindalliance.channels.nk.bean
/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 21, 2008
 * Time: 1:45:08 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractComponentBean extends AbstractBean implements IComponentBean {

    IPersistentBean contextBean
    Expando metadata
    String calculate

    boolean isComponent() {
        return true;
    }

    boolean isCalculated() {
        return calculate != null
    }

    // Calculate does not apply to bean components
    def calculate() {
        throw new Exception("Not supported")
    }

    // initializer must be a Map
    void initializeFrom(Object initializer) {
        Map values = (Map)initializer
        values.each {key,value ->
            this."$key".initializeFrom(value)
        }
    }

    Expando getMetadata() {
        return metadata
    }

    void accept(Map args, Closure action) {
        action(args, this)
        getBeanProperties().each { key, val ->
            val.accept([propName:key, parentPath:"${args.parentPath}${args.propName}/"], { args1, self ->   
                    self.initContextBean(this.contextBean)
                    self.initMetadata(args1.propName, args1.parentPath, this.defaultMetadata)  // Use component bean's metadata
            })
        }
    }

    void initMetadata(String propName, String xpath, Map defaultMetadata) {
        initialize()
        metadata = AbstractBeanPropertyValue.prepareMetadata(propName, xpath, defaultMetadata)
    }

    void initContextBean(IPersistentBean bean)  {
        contextBean = bean
    }

    String getSchemaType() {
        return null
    }
}