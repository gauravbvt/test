package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.Channels;
import com.mindalliance.channels.playbook.ifm.Participation;
import com.mindalliance.channels.playbook.ifm.User;
import com.mindalliance.channels.playbook.ifm.model.Model;
import com.mindalliance.channels.playbook.ifm.project.Project;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.ref.impl.BeanImpl;
import com.mindalliance.channels.playbook.ref.impl.ReferenceableImpl;
import com.mindalliance.channels.playbook.support.PlaybookApplication;
import com.mindalliance.channels.playbook.support.PlaybookSession;
import com.mindalliance.channels.playbook.support.models.ColumnProvider;
import com.mindalliance.channels.playbook.support.models.Container;
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
public class UserScope extends BeanImpl implements Container {

    private transient User user;
    private transient List<Ref> contents;
    private transient ColumnProvider columnProvider;
    private transient List<Class<?>> allowedClasses;

    public UserScope() {
    }

    @Override
    public Map beanProperties() {  // all bean properties are transient
        return new HashMap();
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
                        return ColumnProvider.toDisplay( o1.getSimpleName() )
                                .compareTo( ColumnProvider.toDisplay( o2.getSimpleName() ));
                    }
                }
            );
            final User u = getUser();
            if ( u.getAdmin() )
                result.addAll( Channels.adminClasses() );
            if ( u.getAnalyst() ) {
                result.addAll( Model.analystClasses() );
                boolean hasModels = getApplication().findModelsForUser( u.getReference() ).size() > 0;
                if ( hasModels )
                    result.addAll( Model.contentClasses() );
            }
            if ( u.getManager() )
                result.addAll( Project.managerClasses() );

            if ( getProject() != null ) {
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
            final User u = getUser();
            final Ref uRef = u.getReference();

            if ( u.getAdmin() )
                result.addAll( getChannels().getUsers() );

            if ( u.getAnalyst() ) {
                for ( Ref mRef: (List<Ref>) getChannels().getModels() ) {
                    Model m = (Model) mRef.deref();
                    if ( m.isAnalyst( uRef ) ) {
                        result.add( mRef );
                        m.addContents( result );
                    }
                }
            }

            if ( u.getManager() ) {
                for ( Ref pRef: (List<Ref>) getApplication().findProjectsForUser( uRef ) ) {
                    Project p = (Project) pRef.deref();
                    if ( p.isManager( uRef ) ) {
                        result.add( pRef );
                        p.addManagerContents( result );
                    }
                }
            }

            // Add assigned project contents
            for ( Ref pRef: (List<Ref>) getApplication().findProjectsForUser( uRef ) ) {
                Project project = (Project) pRef.deref();
                project.addContents( result );
            }

            // Add user tabs
            result.addAll( u.getTabs() );

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
    private Ref getTarget( Referenceable object ) {
        final Class<? extends Referenceable> objectClass = object.getClass();

        if ( Channels.contentClasses().contains( objectClass ) )
            return getChannels().getReference();

        final User user = getUser();
        if ( user != null
                && User.contentClasses().contains( objectClass ) )
            return user.getReference();

        final Ref uRef = getUser().getReference();
        final Project project = getProject();
        if ( project != null
                && project.isParticipant( uRef )
                && Project.contentClasses().contains( objectClass ) )
            return project.getReference();

        final Model model = getModel();
        if ( model != null
                && model.isAnalyst( uRef )
                && Model.contentClasses().contains( objectClass ) )
            return model.getReference();

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
        if ( columnProvider != null )
            columnProvider.detach();
    }

    public synchronized ColumnProvider getColumnProvider() {
        if ( columnProvider == null )
            columnProvider = new ColumnProvider( this );

        return columnProvider;
    }

    //================================
    private PlaybookSession getSession() {
        return (PlaybookSession) Session.get();
    }

    private Project getProject() {
        final Ref project = getSession().getProject();
        return project == null ? null : (Project) project.deref();
    }

    private Model getModel() {
        final Ref model = getSession().getModel();
        return model == null ? null : (Model) model.deref();
    }

    private Participation getParticipation() {
        final Ref ref = getSession().getParticipation();
        return ref == null ? null : (Participation) ref.deref();
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
}
