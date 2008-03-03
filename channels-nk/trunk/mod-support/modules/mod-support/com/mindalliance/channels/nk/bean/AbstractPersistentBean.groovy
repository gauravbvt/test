package com.mindalliance.channels.nk.bean

import com.mindalliance.channels.nk.bean.IPersistentBean
import com.mindalliance.channels.nk.Action
import com.mindalliance.channels.nk.IAction
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.nk.Registry

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 16, 2008
* Time: 1:12:16 PM
*/
abstract class AbstractPersistentBean extends AbstractBean implements IPersistentBean {

    String db
    String id
    Date createdOn = new Date()
    String version
    private boolean rooted = false
    Context context


    private static final String DEFAULT_VERSION = "1.0.0"

    boolean isPersistent() {
        return true
    }

    // Default
    String getVersion() {
        return version ?: DEFAULT_VERSION
    }

    boolean isRooted() {
        return rooted
    }

    boolean setRooted(boolean val) {
        rooted = val
    }

    String elementName() {
        Registry registry = Registry.getRegistry()
        String name = registry.nameFor(this.class).tokenize('.').reverse()[0]
        return "${name[0].toLowerCase()}${name[1..<name.size()]}"
    }


    // TODO - implement deepCopy using visitor
    IBean deepCopy() {
        IPersistentBean copy = (IPersistentBean) super.deepCopy()
        copy.db = db
        copy.id = id
        copy.createdOn = new Date(createdOn.toString())
        copy.setRooted(isRooted())
        copy.version = version
        copy.context = context
        return copy
    }

    // Make the bean ready for use
    void activate() {
        initialize()
        getBeanProperties().each {key, val ->
            String xpath = "/${elementName()}/"
            val.accept([propName: key, parentPath: xpath], {args, self ->
                self.initContextBean(this)
                self.initMetadata(args.propName, args.parentPath, this.defaultMetadata)
            })
        }
    }

    List getActions() {    // DEFAULT
        Action edit = new Action(name:'edit',label:'Edit',hint:'Edit this')
        Action delete = new Action(name:'delete',label:'Delete.',hint:'Permanently delete this')
        return [edit,delete]
    }

    void executeAction(IAction action, Map args) {
       this."${action.name}"(args)
    }

    boolean isWritable() { // DEFAULT
        return true
    }

    boolean isDeletable() { // DEFAULT
        return true
    }

    ISimpleData getName() {
        SimpleData sd = new SimpleData(dataClass:String.class)
        sd.value = "UNNAMED"
        return sd
    }
    
    ISimpleData getDescription() {
        SimpleData sd = new SimpleData(dataClass:String.class)
        sd.value = "NO DESCRIPTION"
        return sd
    }
}