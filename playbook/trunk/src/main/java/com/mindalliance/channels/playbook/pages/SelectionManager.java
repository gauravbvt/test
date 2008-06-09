package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * A controller of a master selection.
 */
public interface SelectionManager {

    Ref getSelected();

    void setSelected( Ref ref );

    void doAjaxSelection( Ref ref, AjaxRequestTarget target );
}
