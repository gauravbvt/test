package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.FilteredContainer;
import com.mindalliance.channels.playbook.support.models.RefContainer;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A filter tree that computes itself given two collections of refs.
 */
public class DynamicFilterTree extends FilterTree {

    private IModel choices;
    private IModel selections;
    private transient Filter computedFilter;

    public DynamicFilterTree( String id, IModel selections, IModel choices ) {
        this( id, selections, choices, false );
    }

    public DynamicFilterTree( String id, IModel selections, IModel choices, boolean singleSelect ) {
        super( id, new RootFilter(), singleSelect );
        this.selections = selections;
        this.choices = choices;
    }

    public void detachModels() {
        super.detachModels();
        computedFilter = null;
        choices.detach();
        selections.detach();
    }

    public synchronized Filter getFilter() {
        if ( computedFilter == null ) {
            List<Ref> choiceList = (List<Ref>) choices.getObject();
            List<Ref> selectionList = (List<Ref>) selections.getObject();

            Filter filter = new RootFilter(
                new RefContainer( choiceList ) );
            filter.setShowingLeaves( true );

            // Set selections
            for ( Ref sel : selectionList ) {
                if ( choiceList.contains( sel ) )
                    filter.selectFirstMatch( sel );
            }
            filter.simplify();

            setFilter( filter );
        }
        return computedFilter;
    }

    public void setFilter( Filter filter ) {
        super.setFilter( filter );
        computedFilter = filter;
    }

    public IModel getChoices() {
        return choices;
    }

    public void setChoices( IModel choices ) {
        detachModels();
        this.choices = choices;
    }

    public IModel getSelections() {
        return selections;
    }

    public void setSelections( IModel selections ) {
        detachModels();
        this.selections = selections;
    }

    public List<Ref> getNewSelections() {
        List<Ref> results = new ArrayList<Ref>();
        Filter filter = getFilter();
        for ( Ref ref : new FilteredContainer( filter.getContainer(), filter, false ) )
            results.add( ref );

        return results;
    }

}
