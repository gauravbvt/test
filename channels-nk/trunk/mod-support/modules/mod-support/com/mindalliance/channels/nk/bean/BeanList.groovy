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
        action(args, this) // args.propName, args.parentPath
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

    public IBeanPropertyValue getActivatedItemPrototype() {   // TODO - BUG = the activated prototype has a null metadata
        IBeanPropertyValue proto = itemPrototype.deepCopy()  // get a copy, just to be safe
        // "activate" the prototype list item to fully initialize it
        proto.initialize()
        proto.accept([propName: itemName, parentPath: "${this.metadata.path}/"], {args, self ->
                self.initContextBean(this.contextBean)
                self.initMetadata(args.propName, args.parentPath, this.contextBean.defaultMetadata)
            })
        return proto
    }

}