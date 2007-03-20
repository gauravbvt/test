// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.system;

import java.beans.PropertyVetoException;
import java.util.Set;
import java.util.TreeSet;

/**
 * A non-human resource of some kind, such as a database, Web portal
 * or library, that is administered by an organization, and holds
 * and accepts information it then would make accessible.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attributes
 * @navassoc - administrators * Role
 * @composed - - * InformationAsset
 */
public class InformationResource extends Agent {

    private Set<Role> administrators = new TreeSet<Role>();
    private Set<InformationAsset> assets = new TreeSet<InformationAsset>();

    /**
     * Default constructor.
     */
    public InformationResource() {
        super();
    }

    /**
     * Default constructor.
     * @param name the name of the resource
     * @throws PropertyVetoException on name clashes
     */
    public InformationResource( String name ) throws PropertyVetoException {
        super( name );
    }

    /**
     * Return the value of administrators.
     */
    public Set<Role> getAdministrators() {
        return this.administrators;
    }

    /**
     * Set the value of administrators.
     * @param administrators The new value of administrators
     */
    public void setAdministrators( Set<Role> administrators ) {
        this.administrators = administrators;
    }

    /**
     * Return the value of assets.
     */
    public Set<InformationAsset> getAssets() {
        return this.assets;
    }

    /**
     * Set the value of assets.
     * @param assets The new value of assets
     */
    public void setAssets( Set<InformationAsset> assets ) {
        this.assets = assets;
    }

    /**
     * Add an information asset.
     * @param asset the asset
     */
    public void addAsset( InformationAsset asset ) {
        this.assets.add( asset );
    }

    /**
     * Remove an information asset.
     * @param asset the asset
     */
    public void removeAsset( InformationAsset asset ) {
        this.assets.remove( asset );
    }
}
