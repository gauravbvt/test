package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ifm.playbook.PlaybookElement;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;
import com.mindalliance.channels.playbook.support.models.Container;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ...
 */
public class PlaybookElementFilters extends AbstractFilters {

    public void addFilters( Container container, List<Filter> results ) {
        Set<Ref> playbookRefs = new HashSet<Ref>();
        for ( Ref ref: container ) {
            Referenceable object = ref.deref();
            if ( object instanceof PlaybookElement ) {
                PlaybookElement pe = (PlaybookElement) object;
                playbookRefs.add( pe.getPlaybook() );
            }
        }

        if ( playbookRefs.size() > 1 )
            for ( Ref ref: playbookRefs )
                results.add( new PlaybookFilter( ref  ) );        
    }
}
