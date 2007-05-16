/*
 * Created on Apr 30, 2007
 */
package com.mindalliance.channels.data.support;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.mindalliance.channels.User;
import com.mindalliance.channels.util.GUID;

/**
 * The change history of an element.
 * 
 * @author jf
 */
public class VersionHistory implements Serializable {

    private GUID elementGUID; // guid of element
    private List<Version> priorVersions;

    public boolean isDeleted() {
        return false; // TODO
    }

    public boolean isFrozen() {
        return false; // TODO
    }

    public Date whenLastChanged() {
        return null; // TODO
    }

    public Date whenCreated() {
        return null; // TODO
    }

    public List<User> getContributors() {
        return null; // TODO
    }

    /**
     * @return the elementGUID
     */
    public GUID getElementGUID() {
        return elementGUID;
    }

    /**
     * @param elementGUID the elementGUID to set
     */
    public void setElementGUID( GUID elementGUID ) {
        this.elementGUID = elementGUID;
    }

    /**
     * @return the priorVersions
     */
    public List<Version> getPriorVersions() {
        return priorVersions;
    }

    /**
     * @param priorVersions the priorVersions to set
     */
    public void setPriorVersions( List<Version> priorVersions ) {
        this.priorVersions = priorVersions;
    }

    /**
     * @param version
     */
    public void addPriorVersion( Version version ) {
        priorVersions.add( version );
    }

    /**
     * @param version
     */
    public void removePriorVersion( Version version ) {
        priorVersions.remove( version );
    }
}
