package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.filters.RootFilter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.Container;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.IModel;

/**
 * ...
 */
public class TreePalette extends Palette {

    private Filter filter;

    /**
     * Create a new tree palette.
     * @param id the id of the component
     * @param selections the initial selections (a model on a list of refs)
     * @param choices the available choices (a model on a list of refs)
     * @param rows how many rows to display
     */
    public TreePalette( String id, IModel selections, IModel choices, int rows ) {
        super( id,
               selections,
               choices,
               new ChoiceRenderer(){
                   public Object getDisplayValue( Object ref ) {
                       return super.getDisplayValue( ((Ref) ref).deref() );
                   }
               },
               rows, false );
    }

    public synchronized Filter getFilter() {
        if ( filter == null ) {
            filter = new RootFilter( getRemainingChoices() );
        }
        return filter;
    }

    private Container getRemainingChoices() {
        return null;
    }

    public synchronized void detachModels() {
        super.detachModels();
        filter = null;
    }
}
