package com.mindalliance.channels.nk.bean
/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 25, 2008
 * Time: 11:03:08 AM
 * To change this template use File | Settings | File Templates.
 */
class BeanPropertyMetaData implements IBeanPropertyMetaData {

        String propertyName
        IBeanPropertyMetaData parent // "Discovered" on activate
        String label
        private boolean required = false
        private boolean readOnly = false
        Object domain
        String hint
        Map presentation = [ : ]
        private boolean advanced = false

    boolean isRequired() {
        return required; //To change body of implemented methods use File | Settings | File Templates.
    }

    void setRequired(boolean val) {
        required = val
    }

    boolean isReadOnly() {
        return readOnly; //To change body of implemented methods use File | Settings | File Templates.
    }

    void setReadOnly(boolean val) {
        readOnly = val
    }

    boolean isAdvanced() {
        return advanced; //To change body of implemented methods use File | Settings | File Templates.
    }

    void setAdvanced(boolean val) {
        advanced = val
    }

    String getLabel() {
        assert propertyName
        return label ?: "${propertyName[0].toUpperCase()}${propertyName[1..<propertyName.size()]}"
    }

}