package com.mindalliance.playbook.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Common basis for media.
 */
@Entity
@Inheritance( strategy = InheritanceType.SINGLE_TABLE )
public abstract class Medium implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger( Medium.class );

    public enum MediumType {
        ADDRESS( "location", "navigate" ),
        EMAIL( "email", "compose message" ),
        PHONE( "the phone", "call" ),
        FACEBOOK( "Facebook messages", "send message" ),
        TWITTER( "Twitter direct messages", "send message" ),
        LINKEDIN( "LinkedIn messages", "send message" ),
        IM( "instant messaging", "chat" ),
        SKYPE( "Skype", "call" );
 
        private final String verb;
        private final String description;

        MediumType( String description, String verb ) {
            this.description = description;
            this.verb = verb;
        }

        public String getDescription() {
            return description;
        }

        public String getVerb() {
            return verb;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
                
        sb.append( getAddress() );
        if ( getType() != null )
            sb.append( " (" ).append( getType() ).append( ")" );

        return sb.toString();
    }

    @Transient
    public String getMediumString() {
        return "using " + getMediumType().getDescription();
    }

    private static final long serialVersionUID = -754200895087374965L;

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    private Contact contact;

    private String type;

    private boolean preferred;

    protected Medium() {
    }

    protected Medium( Contact contact, String type ) {
        this.contact = contact;
        this.type = type;
    }

    protected Medium( Contact contact, Medium medium ) {
        this( contact, medium.getType() );
    }

    /**
     * Copy a medium of another contact into specified local contact.
     * @param contact the local contact
     * @param medium a medium from another contact
     * @param <T> the medium subclass
     * @return a new medium for the local contact 
     */
    @SuppressWarnings( "unchecked" )
    public static <T extends Medium> T copy( Contact contact, T medium ) {
        Class<T> mediumClass = (Class<T>) medium.getClass();
        try {
            Constructor<T> constructor = mediumClass.getConstructor( Contact.class, mediumClass );
            return constructor.newInstance( contact, medium );
            
        } catch ( NoSuchMethodException e ) {
            // Shouldn't happen...
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException( e.getTargetException() );
        } catch ( InstantiationException e ) {
            throw new RuntimeException( e );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        }
    }

    @Transient
    public abstract MediumType getMediumType();
    
    @Transient
    public abstract Object getAddress();

    /**
     * Get a CSS class to use to differentiate this medium.
     * @return a CSS class
     */
    @Transient
    public abstract String getCssClass();

    /**
     * Get a link to include for a default action on this medium.
     * @return an href link or none if no action is applicable
     */
    @Transient 
    public String getActionUrl() {
        return null;        
    }

    public Contact getContact() {
        return contact;
    }

    public String getType() {
        return type;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred( boolean preferred ) {
        this.preferred = preferred;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null || getClass() != obj.getClass() )
            return false;

        Medium medium = (Medium) obj;

        return getAddress().equals( medium.getAddress() ) 
            && ( type == null ? medium.getType() == null :
                                type.equals( medium.getType() ) );
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + getMediumType().hashCode();
        result = 31 * result + getAddress().hashCode();
        return result;
    }
}
