package com.mindalliance.channels.playbook.ref.impl

import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Ref
import java.beans.PropertyChangeSupport
import java.beans.PropertyChangeListener
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 8:49:28 AM
*/
/*abstract*/ class ReferenceableImpl implements Referenceable, GroovyInterceptable {

    String id
    String db

    PropertyChangeSupport pcs = new PropertyChangeSupport(this)

    void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener)
    }

    void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener)
    }

    Referenceable copy() {
        Referenceable copy = (Referenceable) this.class.newInstance()
        copy.id = this.@id
        copy.db = this.@db
        copy.pcs = new PropertyChangeSupport(copy)
        getProperties().each {name, val ->
            if (!['id', 'db', 'pcs', 'class', 'reference', 'metaClass'].contains(name)) {
                try {
                    copy."$name" = val
                }
                catch (Exception e) {   // Read-only/computed field
                    // TODO -- put a warning in log
                    System.out.println("Can't set field $name in $copy")
                }
            }
        }
        return copy
    }

    void changed(String propName) {// MUST be called when ifmElement is changed other than via a property get/set
        propertyChanged(propName, null, this.@"$propName") // don't care about old value
    }

    void propertyChanged(String name, def old, def value) {
        pcs.firePropertyChange(name, old, value)
    }

    String getId() {
        return id ?: (id = makeGuid()) // If no id is given, make one
    }

    Ref getReference() {
        return new RefImpl(id: getId(), db: getDb())
    }

    String makeGuid() {
        String uuid = "${UUID.randomUUID()}"
        return uuid
    }

    void setProperty(String name, def val) {
        def value = (val instanceof Referenceable) ? val.reference : val
        doSetProperty(name, value)
    }

    void doSetProperty(String name, def value) {
        String setterName = "set${name[0].toUpperCase()}${name.substring(1)}"
        this."$setterName"(value)
    }

    // Adds add<Field> and remove<Field> methods if none -- expects List <fields> to be defined
    def invokeMethod(String name, def args) {
        def metamethod = this.class.metaClass.getMetaMethod(name, args)
        if (metamethod == null) {// don't override a defined method
            // addField  --> fields.add(args[0])
            if (name =~ /^add.+/) {
                String field = "${name.substring(3).toLowerCase()}s"
                return doAddToField(field, args[0].reference)
            }
            // removeField --> fields.remove(fields.indexOf(args[0]))
            if (name =~ /^remove.+/) {
                String field = "${name.substring(6).toLowerCase()}s"
                return doRemoveFromField(field, args[0].reference)
            }
        }
        if (metamethod == null) {
            throw new Exception("No method named $name")
        }
        return metamethod.invoke(this, args)
    }

    Referenceable doAddToField(String name, def val) {
        def value = (isReferenceable(val)) ? val.reference : val
        List list = (List) this."$name"
        if(list == null) {
            throw new Exception("Expecting initialized field $name but not defined but missing in ${this}" )
        }
        if (!list.contains(value)) {
            list.add(value)
            changed(name)
        }
        return this
    }

    Referenceable doRemoveFromField(String name, def val) {
        def value = (isReferenceable(val)) ? val.reference : val
        List list = (List) this."$name"
        if(list == null) {
            throw new Exception("Expecting initialized field $name but not defined but missing in ${this}" )
        }
        if (list.contains(val)) {
            list.remove(list.indexOf(val)) // works for int as well
            changed(name)
        }
        return this
    }

    static boolean isReferenceable(def obj) {
        boolean b = Referenceable.isAssignableFrom(obj.class)
        return b
    }

//    // Kludges for stub generation
//    public void setMetaClass(MetaClass metaClass) {
//        super.setMetaClass(metaClass);
//    }
//
//    public MetaClass getMetaClass() {
//        return super.getMetaClass();
//    }
//
//    public Object getProperty(String s) {
//        return super.getProperty(s);
//    }

}