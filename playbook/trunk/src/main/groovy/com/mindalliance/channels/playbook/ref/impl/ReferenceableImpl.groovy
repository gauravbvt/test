package com.mindalliance.channels.playbook.ref.impl

import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ref.Ref
import java.beans.PropertyChangeSupport
import java.beans.PropertyChangeListener
import com.mindalliance.channels.playbook.ref.Store
import com.mindalliance.channels.playbook.support.PlaybookApplication
import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.mem.ApplicationMemory
import com.mindalliance.channels.playbook.support.RefUtils
import org.apache.log4j.Logger

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 19, 2008
 * Time: 8:49:28 AM
 */
abstract class ReferenceableImpl extends BeanImpl implements Referenceable {

    String id
    String db
    private boolean constant = false    // if constant then can't be modified or persisted

    PropertyChangeSupport pcs = new PropertyChangeSupport(this)

    void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener)
    }

    void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener)
    }

    void makeConstant() {
        constant = true
    }

    boolean isConstant() {
        return constant
    }

    @Override
    Bean copy() {
        if (isConstant()) {
            return this
        }
        else {
            Referenceable copy = (Referenceable) super.copy()
            copy.id = this.@id
            copy.db = this.@db
            copy.pcs = new PropertyChangeSupport(copy)
            return copy
        }
    }

    protected List<String> transientProperties() {
        return super.transientProperties() + ['id', 'db', 'pcs', 'reference', 'type']
    }

    void changed(String propName) {// MUST be called when ifmElement is changed other than via a property get/set
        if (ApplicationMemory.DEBUG) Logger.getLogger(this.class.name).debug("^^^ changed: ${getType()}.$propName")
        propertyChanged(propName, null, this."$propName") // don't care about old value
    }

    void changed() {} // Default is do nothing

    void propertyChanged(String name, def old, def value) {
        changed()
        pcs.firePropertyChange(name, old, value)
    }

    String getId() {
        return id ?: (id = makeGuid()) // If no id is given, make one
    }

    Ref getReference() {
       return new RefImpl(id: getId(), db: getDb(), value: this)  // cache self in reference
    }

    String makeGuid() {
        String uuid = "${UUID.randomUUID()}"
        return uuid
    }

    void checkModifyingAllowed() {
        if (isConstant()) throw new NotModifiableException("Modifying ${this.toString()} not allowed (value is contant)")
        if (!this.reference.isModifiable()) {
            throw new NotModifiableException("Modifying ${this.toString()} not allowed (do ref.begin() first if not constant)")
        }
    }

    void setProperty(String name, def val) {
        checkModifyingAllowed()
        def value = (val instanceof Referenceable) ? val.reference : val
        doSetProperty(name, value)
    }

    void doSetProperty(String name, def value) {
        def old = this."$name"
        String setterName = "set${name[0].toUpperCase()}${name.substring(1)}"
        this."$setterName"(value)
        if (!['id', 'db', 'pcs'].contains(name)) {
            this.propertyChanged(name, old, value)
        }
    }

    // Adds add<Field> and remove<Field> methods if none -- expects List <fields> to be defined
    def invokeMethod(String name, def args) {
        // addField  --> fields.add(args[0])
        if (name =~ /^add.+/) {
            String field = renameListField("${RefUtils.decapitalize(name.substring(3))}")
            return doAddToField(field, args[0])
        }
        // removeField --> fields.remove(fields.indexOf(args[0]))
        if (name =~ /^remove.+/) {
            String field = renameListField("${RefUtils.decapitalize(name.substring(6))}")
            return doRemoveFromField(field, args[0])
        }
    }

    private String renameListField(String name) {
        String field = name
        if (!field.endsWith('s')) {
            if (field.endsWith('y')) {
                field="${field[0..field.size()-2]}ies"
            }
            else {
                field = "${field}s"
            }
        }
        return field
    }


    Referenceable doAddToField(String name, def val) {
        checkModifyingAllowed()
        def value = (isReferenceable(val)) ? val.reference : val
        List list = (List) this."$name"
        if (list == null) {
            throw new Exception("Expecting initialized field $name but not defined but missing in ${this}")
        }
        if (!list.contains(value)) {
            list.add(value)
            changed(name)
        }
        return this
    }

    Referenceable doRemoveFromField(String name, def val) {
        checkModifyingAllowed()
        def value = (isReferenceable(val)) ? val.reference : val
        List list = (List) this."$name"
        if (list == null) {
            throw new Exception("Expecting initialized field $name but not defined but missing in ${this}")
        }
        if (list.contains(value)) {
            list.remove(list.indexOf(value)) // works for int as well
            changed(name)
        }
        return this
    }

    static boolean isReferenceable(def obj) {
        boolean b = Referenceable.isAssignableFrom(obj.class)
        return b
    }

    public void beforeStore(ApplicationMemory memory) {
        // default is do nothing
    }

    public void afterStore() {
        // default is do nothing
    }

    public void afterRetrieve() {
        // default is do nothing
    }

    Ref persist() {
        Store store = PlaybookApplication.locateStore()
        store.persist(this)
        return this.reference
    }

    void delete() {
        this.reference.delete()
    }

    void reset() {
        this.reference.reset()
    }

    Referenceable deref() {
        return this
    }

    List<RefMetaProperty> metaProperties() {
        List<RefMetaProperty> list = []
        /*beanProperties().each {name, val ->
            MetaProperty mp = this.getMetaClass().getMetaProperty(name)
            list.add(new RefMetaProperty(mp.name, mp.type))
        }*/
        BeanImpl.writablePropertiesOf(this).each {mp ->
            list.add(new RefMetaProperty(mp.name, mp.type))
        }
        list.sort()
        return list
    }

    void commit() {
        this.reference.commit()
    }

    void refresh() {// sync state from store
        Referenceable fresh = this.reference.deref() // get fresh copy
        this.setFrom(fresh)
    }

    String getType() {// Default
        return super.shortClassName()
    }

    Class formClass() {
        String type = getType()
        return PlaybookApplication.current().formClassFor(type)
    }

    Ref find(String listPropName, Map<String, Object> args) {
        List<Ref> list = this."$listPropName"
        Ref ref = (Ref) list.find {ref ->
            args.every {prop, val ->
                def propVal = ref."$prop"
                ref."$prop" == val
            }
        }
        return ref
    }

    Map toMap() {
        Map map = super.toMap()
        map = map + [id: id, db: db]
        return map
    }

    void initFromMap(Map map) {
        super.initFromMap(map)
        id = map['id']
        db = map['db']
    }

    public boolean save() {
        this.reference.save()
    }

    public void afterDelete() {
        // Do nothing
    }
}