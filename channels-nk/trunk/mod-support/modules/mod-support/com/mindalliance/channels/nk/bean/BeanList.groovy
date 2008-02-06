package com.mindalliance.channels.nk.bean
/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 14, 2008
 * Time: 8:21:07 PM
 * To change this template use File | Settings | File Templates.
 */

// A List with an attribute naming the class of its items
class BeanList extends AbstractBeanPropertyValue implements IBeanList {

    private List list = new ArrayList()
    String itemName = 'item' // default
    IBeanPropertyValue itemPrototype // class name of bean items

    IBeanList deepCopy() {
        IBeanList copy = new BeanList(itemPrototype: itemPrototype.deepCopy(), itemName: itemName)
        list.each {item ->
            copy.add(item.deepCopy())
        }
        return copy
    }

    void accept(Map args, Closure action) {
        action(args.propName, args.parentPath, this)
        list.each {item -> item.accept([propName: itemName, parentPath: "${args.parentPath}${args.propName}/"], action)}
    }

    Iterator iterator() {
        return this.@list.iterator()
    }

    Object get(String name) {
        switch (name) {
            case 'itemPrototype': return this.@itemPrototype; break;
            case 'itemName': return this.@itemName; break;
            default: return this.@list."$name"
        }
    }

    void set(String name, Object value) {
        switch (name) {
            case 'itemPrototype': this.@itemPrototype = (IBeanPropertyValue) value; break;
            case 'itemName': this.@itemName = (String) value; break;
            default:this.@list."$name" = value
        }
    }

    Object invokeMethod(String name, Object args) {
        return this.@list.invokeMethod(name, args)
    }

    public IBeanPropertyValue getActivatedItemPrototype() {
        IBeanPropertyValue proto = itemPrototype.deepCopy()  // get a copy, just to be safe
        if (proto instanceof IComponentBean) {   // If component bean, make sure it's metadata is fully initialized
            proto.initialize()
            String xpath = "${this.metadata.path}/"
            proto.getBeanProperties().each {key, val ->
                val.accept([propName:key, parentPath:xpath], { propKey, propPath, propValue ->
                            propValue.initMetadata(propKey, propPath, proto.defaultMetadata) })
            }
        }
        return proto
    }

}