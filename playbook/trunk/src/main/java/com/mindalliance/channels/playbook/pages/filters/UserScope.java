package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.Channels;
import com.mindalliance.channels.playbook.ifm.User;
import com.mindalliance.channels.playbook.ifm.playbook.PlaybookElement;
import com.mindalliance.channels.playbook.ifm.project.Project;
import com.mindalliance.channels.playbook.ifm.project.ProjectElement;
import com.mindalliance.channels.playbook.ifm.taxonomy.Taxonomy;
import com.mindalliance.channels.playbook.ifm.taxonomy.TaxonomyElement;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.ref.impl.ReferenceableImpl;
import com.mindalliance.channels.playbook.support.PlaybookApplication;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.models.Container;
import com.mindalliance.channels.playbook.support.models.ContainerSummary;
import com.mindalliance.channels.playbook.support.models.RefModel;
import com.mindalliance.channels.playbook.support.persistence.Mappable;
import groovy.lang.MissingPropertyException;
import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.model.IModel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Wrapper for all contents accessible by the current user (the object of this
 * model).
 */
public class UserScope implements Container, IModel<User> {

    private transient User user;
    private transient List<Ref> contents;
    private transient ContainerSummary summary;
    private transient List<Class<? extends Referenceable>> allowedClasses;
    private static final long serialVersionUID = 6321041288321505202L;

    public UserScope() {
    }

    @Override
    public String toString() {
        return MessageFormat.format( "{0}''s scope", getUser() );
    }

    //================================
    @SuppressWarnings( { "unchecked" } )
    public synchronized List<Class<? extends Referenceable>> getAllowedClasses() {
        if ( allowedClasses == null ) {
            Collection<Class<? extends Referenceable>> result =
                new TreeSet<Class<? extends Referenceable>>(
                    new Comparator<Class<?>>() {
                        public int compare( Class<?> o1, Class<?> o2 ) {
                            return ContainerSummary
                                    .toDisplay( o1.getSimpleName() )
                                    .compareTo(
                                            ContainerSummary.toDisplay(
                                                    o2.getSimpleName() ) );
                        }
                    } );
            User u = getUser();
            if ( u.getAdmin() )
                result.addAll( Channels.adminClasses() );
            if ( u.getAnalyst() ) {
                result.addAll( Taxonomy.analystClasses() );
                boolean hasTaxonomies = getApplication()
                        .findTaxonomiesForUser( u.getReference() ).size() > 0;
                if ( hasTaxonomies )
                    result.addAll( Taxonomy.contentClasses() );
            }
            if ( u.getManager() )
                result.addAll( Project.managerClasses() );

            if ( getDefaultProject() != null )
                result.addAll( Project.contentClasses() );

            result.addAll( User.contentClasses() );
            allowedClasses = new ArrayList<Class<? extends Referenceable>>(
                    result );
        }
        return allowedClasses;
    }

    //================================
    @SuppressWarnings( { "unchecked" } )
    private synchronized List<Ref> getContents() {
        if ( contents == null ) {
            List<Ref> result = new ArrayList<Ref>();
            final Ref uRef = getSession().getUser();
            final User u = (User) uRef.deref();
            Channels channels = Channels.instance();
            if ( u.getAdmin() )
                result.addAll( channels.getUsers() );

            if ( u.getAnalyst() ) {
                for ( Ref mRef : (List<Ref>) channels.getTaxonomies() ) {
                    Taxonomy m = (Taxonomy) mRef.deref();
                    if ( m != null && m.isAnalyst( uRef ) ) {
                        result.add( mRef );
                        m.addContents( result );
                    }
                    mRef.detach();
                }
            }

            if ( u.getManager() ) {
                for ( Ref pRef : (List<Ref>) getApplication()
                        .findProjectsForUser( uRef ) ) {
                    Project p = (Project) pRef.deref();
                    if ( p != null && p.isManager( uRef ) ) {
                        result.add( pRef );
                        p.addManagerContents( result );
                    }
                    pRef.detach();
                }
            }

            // Add assigned project contents
            for ( Ref pRef : (List<Ref>) getApplication()
                    .findProjectsForUser( uRef ) ) {
                Project project = (Project) pRef.deref();
                if ( project != null )
                    project.addContents( result );
                pRef.detach();
            }

            // Add user tabs
            result.addAll( u.getTabs() );
            uRef.detach();
            contents = result;
        }

        return contents;
    }

    public Ref get( int index ) {
        return getContents().get( index );
    }

    public boolean contains( Ref ref ) {
        return getContents().contains( ref );
    }

    public Iterator<Ref> iterator( int first, int count ) {
        return getContents().subList( first, first + count ).iterator();
    }

    public Iterator<Ref> iterator() {
        return getContents().iterator();
    }

    public int size() {
        return getContents().size();
    }

    public int indexOf( Ref ref ) {
        return getContents().indexOf( ref );
    }

    //================================
    /**
     * Figure out what container to use to add/delete a given object.
     *
     * @param object the object
     * @return the likely container
     */
    private Ref getTarget( Referenceable object ) {
        final Class<? extends Referenceable> objectClass = object.getClass();

        if ( Channels.contentClasses().contains( objectClass ) )
            return Channels.reference();

        final Ref uRef = getSession().getUser();
        if ( uRef != null && User.contentClasses().contains( objectClass ) )
            return uRef;

        if ( object instanceof PlaybookElement ) {
            PlaybookElement element = (PlaybookElement) object;
            Ref pbRef = element.getPlaybook();
            Ref pRef = element.getProject();
            if ( pRef == null )
                pRef = getDefaultProject();

            Project p = (Project) pRef.deref();
            if ( p != null && p.findParticipation( uRef ) != null ) {
                if ( pbRef == null )
                    pbRef = getDefaultPlaybook( pRef );
                pRef.detach();
                return pbRef;
            }
            pRef.detach();
        } else if ( object instanceof ProjectElement ) {
            ProjectElement element = (ProjectElement) object;
            Ref pRef = element.getProject();
            if ( pRef == null )
                pRef = getDefaultProject();
            return pRef;
        }

        if ( object instanceof TaxonomyElement ) {
            TaxonomyElement element = (TaxonomyElement) object;
            Ref mRef = element.getTaxonomy();
            if ( mRef == null )
                mRef = getDefaultTaxonomy();
            if ( mRef != null ) {
                Taxonomy model = (Taxonomy) mRef.deref();
                if ( model.isAnalyst( uRef ) )
                    return mRef;
            }
        }

        throw new RuntimeException(
                MessageFormat.format(
                        "Unable to add objects of class {0}",
                        objectClass.getName() ) );
    }

    public void add( Referenceable object ) {
        Ref target = getTarget( object );
        target.begin();
        try {
            target.add( object );
        } catch ( MissingPropertyException ignored ) {
            // TODO remove this hack
            final ReferenceableImpl ri = (ReferenceableImpl) target.deref();
            ri.doAddToField( object.getType(), object );
        }
        detach();
    }

    public void remove( Referenceable ref ) {
        Ref target = getTarget( ref );
        boolean deleted = false;
        try {// remove AND delete
            target.begin();
            if ( target.isReadWrite() ) {// rw lock acquired?
                if ( ref.getReference()
                        .delete() ) {// if cascaded delete was successful
                    target.remove( ref );
                    target.commit();
                    deleted = true;
                }
            }
        } catch ( MissingPropertyException e ) {
            // TODO remove this hack
            Logger.getLogger( this.getClass() )
                    .error( "Missing property on remove: " + e );
            final ReferenceableImpl ri = (ReferenceableImpl) target.deref();
            ri.doRemoveFromField( ref.getType(), ref );
        } finally {
            target.reset();// clean up the begin no matter what
        }
        if ( deleted ) {
            detach();
        } else {
            // TODO -- Alert the user that (cascaded) delete failed because not all locks could be acquired
        }
    }

    public void remove( Ref ref ) {
        remove( ref.deref() );
    }

    public IModel<Ref> model( Ref object ) {
        return new RefModel( object );
    }

    public synchronized void detach() {
        user = null;
        contents = null;
        allowedClasses = null;
        if ( summary != null )
            summary.detach();
    }

    public synchronized ContainerSummary getSummary() {
        if ( summary == null )
            summary = new ContainerSummary( this );

        return summary;
    }

    //================================
    private PlaybookSession getSession() {
        return (PlaybookSession) Session.get();
    }

    private Ref getDefaultProject() {
        return getSession().getProject();
    }

    private Ref getDefaultTaxonomy() {
        return getSession().getTaxonomy();
    }

    private Ref getDefaultPlaybook( Ref projectRef ) {
        if ( projectRef != null ) {
            Project project = (Project) projectRef.deref();
            List pbRefs = project.getPlaybooks();
            if ( pbRefs.size() > 0 )
                return (Ref) pbRefs.get( 0 );
        }

        return null;
    }

    private PlaybookApplication getApplication() {
        return (PlaybookApplication) getSession().getApplication();
    }

    /**
     * Get the user of this session.
     *
     * @return null when no user is logged in
     */
    public synchronized User getUser() {
        if ( user == null )
            user = (User) getSession().getUser().deref();

        return user;
    }

    //================================
    public User getObject() {
        return getUser();
    }

    public void setObject( User object ) {
        throw new RuntimeException( "Can't set the user of a scope" );
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put( Mappable.CLASS_NAME_KEY, getClass().getName() );
        return map;
    }

    public void initFromMap( Map<String, Object> map ) {
    }

    public Map beanProperties() {// all bean properties are transient
        return new HashMap();
    }
}
