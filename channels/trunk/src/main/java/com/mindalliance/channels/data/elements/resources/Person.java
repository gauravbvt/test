/*
 * Created on Apr 26, 2007
 */
package com.mindalliance.channels.data.elements.resources;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.reference.Type;
import com.mindalliance.channels.data.support.TypeSet;
import com.mindalliance.channels.util.GUID;

public class Person extends ContactableResource {

    private String firstName;
    private String middleName;
    private String lastName;
    private URL photo;
    private List<Role> roles;
    private TypeSet clearances;

    public Person() {
        super();
    }

    public Person( GUID guid ) {
        super( guid );
        roles = new ArrayList<Role>();
        clearances = new TypeSet(Type.CLEARANCE);
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    /**
     * @return the middleName
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * @param middleName the middleName to set
     */
    public void setMiddleName( String middleName ) {
        this.middleName = middleName;
    }

    /**
     * @return the photo
     */
    public URL getPhoto() {
        return photo;
    }

    /**
     * @param photo the photo to set
     */
    public void setPhoto( URL photo ) {
        this.photo = photo;
    }

    /**
     * @return the roles
     */
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles( List<Role> roles ) {
        this.roles = roles;
    }

    /**
     * @param role
     */
    public void addRole( Role role ) {
        roles.add( role );
    }

    /**
     * @param role
     */
    public void removeRole( Role role ) {
        roles.remove( role );
    }

    
    /**
     * Return the value of clearances.
     */
    public TypeSet getClearances() {
        return clearances;
    }

    
    /**
     * Set the value of clearances.
     * @param clearances The new value of clearances
     */
    public void setClearances( TypeSet clearances ) {
        this.clearances = clearances;
    }
    

}
