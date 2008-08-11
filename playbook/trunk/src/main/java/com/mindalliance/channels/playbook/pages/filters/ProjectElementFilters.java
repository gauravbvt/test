package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.InProject;
import com.mindalliance.channels.playbook.ifm.project.Project;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.Container;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/** ... */
public class ProjectElementFilters extends AbstractFilters {

    public ProjectElementFilters() {
    }

    @Override
    public void addFilters( Container container, List<Filter> results ) {
        addProjectElementFilters( container, results );
    }

    private static void addProjectElementFilters(
            Iterable<Ref> container, Collection<Filter> results ) {
        Collection<Ref> projectRefs = new TreeSet<Ref>(
                new Comparator<Ref>() {
                    public int compare( Ref o1, Ref o2 ) {
                        Project p1 = (Project) o1.deref();
                        Project p2 = (Project) o2.deref();
                        return p1.getName().compareTo( p2.getName() );
                    }
                } );
        for ( Ref ref : container ) {
            Object object = ref.deref();
            if ( object instanceof InProject ) {
                InProject inProject = (InProject) object;
                assert inProject.getProject() != null;
                projectRefs.add( inProject.getProject() );
            }
        }

        if ( projectRefs.size() > 1 )
            for ( Ref ref : projectRefs )
                results.add( new ProjectFilter( ref ) );
    }
}
