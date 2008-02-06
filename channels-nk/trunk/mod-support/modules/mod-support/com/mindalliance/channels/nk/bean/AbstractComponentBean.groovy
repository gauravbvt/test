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

    boolean isComponent() {
        return true;
    }

    Expando getMetadata() {
        return metadata
    }

    void accept(Map args, Closure action) {
        action(args.propName, args.parentPath, this)
        getBeanProperties().each { key, val ->
            val.accept([propName:key, parentPath:"${args.parentPath}/${args.propName}/"], { propKey, propPath, propValue ->
                    propValue.initContextBean(this.contextBean)
                    propValue.initMetadata(propKey, propPath, this.defaultMetadata)  // Use component bean's metadata
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