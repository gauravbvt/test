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

    void addItem(Object initializer) {
        if (isCalculated()) throw new Exception("Can't modify a calculated bean list")
        // deep copy item prototype and initialize it
        IBeanPropertyValue item = itemPrototype.deepCopy()
        item.initializeFrom(initializer)
        getList().add(item)
    }

    // initilializer must be a List of initializers
    void initializeFrom(Object initializer) {
        List values = (List)initializer
        values.each {value -> this.addItem(value)}
    }

    IBeanList deepCopy() {
        IBeanList copy
        if (isCalculated()) {
          copy = new BeanList(itemName: itemName, calculate: this.calculate)
        }
        else {
          copy = new BeanList(itemPrototype: itemPrototype.deepCopy(), itemName: itemName)
          getList().each {item ->
                copy.add(item.deepCopy())
            }
        }
        return copy
    }

    List getList() {
       if (isCalculated()) {
          return (List)this.calculate()
       }
       else {
          if (this.@list == null) this.@list = new ArrayList()
          return this.@list
       }
    }

    void accept(Map args, Closure action) {
        action(args, this) // args.propName, args.parentPath
        if (!isCalculated()) {
            getList().each {item -> item.accept([propName: itemName, parentPath: "${args.parentPath}${args.propName}/"], action)}
        }
    }

    Iterator iterator() {
        return this.getList().iterator()
    }

    Object get(String name) {
        switch (name) {
            case 'itemPrototype': return this.@itemPrototype; break;
            case 'itemName': return this.@itemName; break;
            case 'calculate': return this.@calculate; break;
            default: return this.getList()."$name"
        }
    }

    void set(String name, Object value) {
        switch (name) {
            case 'itemPrototype': this.@itemPrototype = (IBeanPropertyValue) value; break;
            case 'itemName': this.@itemName = (String) value; break;
            default:this.getList()."$name" = value
        }
    }

    Object invokeMethod(String name, Object args) {
        if (isCalculated() && (name.startsWith("add") || nameStartsWith("remove"))) throw new Exception ("Can't modify a calculated bean list") // partial protection only
        return this.getList().invokeMethod(name, args)
    }

    public IBeanPropertyValue getActivatedItemPrototype() {
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