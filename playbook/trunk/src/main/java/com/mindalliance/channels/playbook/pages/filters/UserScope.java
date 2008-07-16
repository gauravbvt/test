package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.Channels;
import com.mindalliance.channels.playbook.ifm.User;
import com.mindalliance.channels.playbook.ifm.model.ModelElement;
import com.mindalliance.channels.playbook.ifm.model.PlaybookModel;
import com.mindalliance.channels.playbook.ifm.playbook.PlaybookElement;
import com.mindalliance.channels.playbook.ifm.project.Project;
import com.mindalliance.channels.playbook.ifm.project.ProjectElement;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.ref.impl.ReferenceableImpl;
import com.mindalliance.channels.playbook.support.PlaybookApplication;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.persistence.Mappable;
import com.mindalliance.channels.playbook.support.models.Container;
import com.mindalliance.channels.playbook.support.models.ContainerSummary;
import com.mindalliance.channels.playbook.support.models.RefModel;
import groovy.lang.MissingPropertyException;
import org.apache.wicket.Session;
import org.apache.wicket.model.IModel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Wrapper for all contents accessible by the current user (the object
 * of this model).
 */
public class UserScope implements Container {

    private transient User user;
    private transient List<Ref> contents;
    private transient ContainerSummary summary;
    private transient List<Class<?>> allowedClasses;

    public UserScope() {
    }

    public String toString() {
        return MessageFormat.format( "{0}''s scope", getUser() );
    }

    //================================
    public synchronized List<Class<?>> getAllowedClasses() {
        if ( allowedClasses == null ) {
            Set<Class<?>> result = new TreeSet<Class<?>>(
                new Comparator<Class<?>>(){
                    public int compare( Class<?> o1, Class<?> o2 ) {
                        return ContainerSummary.toDisplay( o1.getSimpleName() )
                                .compareTo( ContainerSummary.toDisplay( o2.getSimpleName() ));
                    }
                }
            );
            final User u = getUser();
            if ( u.getAdmin() )
                result.addAll( Channels.adminClasses() );
            if ( u.getAnalyst() ) {
                result.addAll( PlaybookModel.analystClasses() );
                boolean hasModels = getApplication().findModelsForUser( u.getReference() ).size() > 0;
                if ( hasModels )
                    result.addAll( PlaybookModel.contentClasses() );
            }
            if ( u.getManager() )
                result.addAll( Project.managerClasses() );

            if ( getDefaultProject() != null ) {
                // Project contents
                result.addAll( Project.contentClasses() );
            }

            result.addAll( User.contentClasses() );
            allowedClasses = new ArrayList<Class<?>>( result );
        }
        return allowedClasses;
    }

    //================================
    private synchronized List<Ref> getContents() {
        if ( contents == null ) {
            List<Ref> result = new ArrayList<Ref>();
            final Ref uRef = getSession().getUser();
            final User u = (User) uRef.deref();
            Channels channels = Channels.instance();
            if ( u.getAdmin() )
                result.addAll( channels.getUsers() );

            if ( u.getAnalyst() ) {
                for ( Ref mRef: (List<Ref>) channels.getModels() ) {
                    PlaybookModel m = (PlaybookModel) mRef.deref();
                    if ( m.isAnalyst( uRef ) ) {
                        result.add( mRef );
                        m.addContents( result );
                    }
                    mRef.detach();
                }
            }

            if ( u.getManager() ) {
                for ( Ref pRef: (List<Ref>) getApplication().findProjectsForUser( uRef ) ) {
                    Project p = (Project) pRef.deref();
                    if ( p.isManager( uRef ) ) {
                        result.add( pRef );
                        p.addManagerContents( result );
                    }
                    pRef.detach();
                }
            }

            // Add assigned project contents
            for ( Ref pRef: (List<Ref>) getApplication().findProjectsForUser( uRef ) ) {
                Project project = (Project) pRef.deref();
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
        return getContents().subList( first, first+count ).iterator();
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
            Ref pRef  = element.getProject();
            if ( pRef == null )
                pRef = getDefaultProject();

            Project p = (Project) pRef.deref();
            if ( p.findParticipation( uRef ) != null ) {
                if ( pbRef == null )
                    pbRef = getDefaultPlaybook( pRef  );
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

        if ( object instanceof ModelElement ) {
            ModelElement element = (ModelElement) object;
            Ref mRef = element.getModel();
            if ( mRef == null )
                mRef = getDefaultModel();
            if ( mRef != null ) {
                PlaybookModel model = (PlaybookModel) mRef.deref();
                if ( model.isAnalyst( uRef ) )
                    return mRef;
            }
        }

        throw new RuntimeException(
            MessageFormat.format(
                "Unable to add objects of class {0}",
                objectClass.getName() )
            );
    }

    public void add( Referenceable object ) {
        Ref target = getTarget( object );
        target.begin();
        try {
            target.add( object );
        } catch ( MissingPropertyException e ) {
            // TODO remove this hack
            final ReferenceableImpl ri = (ReferenceableImpl) target.deref();
            ri.doAddToField( object.getType(), object );
        }
        detach();
    }

    public void remove( Referenceable ref ) {
        Ref target = getTarget( ref );
        target.begin();
        try {
            target.remove( ref );
        } catch ( MissingPropertyException e ) {
            // TODO remove this hack
            final ReferenceableImpl ri = (ReferenceableImpl) target.deref();
            ri.doRemoveFromField( ref.getType(), ref );
        }
        detach();
    }

    public void remove( Ref ref ) {
        remove( ref.deref() );
    }

    public IModel model( Object object ) {
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

    private Ref getDefaultModel() {
        return getSession().getModel();
    }

    private Ref getDefaultPlaybook( Ref projectRef ) {
        if ( projectRef != null ) {
            Project project = (Project) projectRef.deref();
            List<Ref> pbRefs = project.getPlaybooks();
            if ( pbRefs.size() > 0 )
                return pbRefs.get(0);
        }

        return null;
    }

    private PlaybookApplication getApplication() {
        return (PlaybookApplication) getSession().getApplication();
    }

    private Channels getChannels() {
        return (Channels) getApplication().getChannels().deref();
    }

    /**
     * Get the user of this session.
     * @return null when no user is logged in
     */
    public synchronized User getUser() {
        if ( user == null )
            user = (User) getSession().getUser().deref();

        return user;
    }

    //================================
    public Object getObject() {
        return getUser();
    }

    public void setObject( Object object ) {
        throw new RuntimeException( "Can't set the user of a scope");
    }

    public Map toMap() {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put( Mappable.CLASS_NAME_KEY, getClass().getName() );
        return map;
    }

    public void initFromMap( Map map ) {
    }

    public Map beanProperties() {  // all bean properties are transient
        return new HashMap();
    }

}
