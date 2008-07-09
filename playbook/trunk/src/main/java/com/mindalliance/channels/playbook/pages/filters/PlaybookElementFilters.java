package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.playbook.Playbook;
import com.mindalliance.channels.playbook.ifm.playbook.PlaybookElement;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.support.models.Container;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * ...
 */
public class PlaybookElementFilters extends AbstractFilters {

    public void addFilters( Container container, List<Filter> results ) {
        Set<Ref> playbookRefs = new TreeSet<Ref>( new Comparator<Ref>(){
            public int compare( Ref o1, Ref o2 ) {
                Playbook p1 = (Playbook) o1.deref();
                Playbook p2 = (Playbook) o2.deref();
                return p1.getName().compareTo( p2.getName() );
            }
        } );
        for ( Ref ref: container ) {
            Referenceable object = ref.deref();
            if ( object instanceof PlaybookElement ) {
                PlaybookElement pe = (PlaybookElement) object;
                playbookRefs.add( pe.getPlaybook() );
            }
        }

        if ( playbookRefs.size() > 1 )
            for ( Ref ref: playbookRefs ) {
                results.add( new PlaybookFilter( ref  ) );
            }
    }
}
