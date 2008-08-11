package com.mindalliance.channels.playbook.pages.filters;

import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.FilteredContainer;
import com.mindalliance.channels.playbook.support.models.RefContainer;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;
import java.io.ObjectInputStream;
import java.io.IOException;

/** A filter tree that computes itself given two collections of refs. */
public class DynamicFilterTree extends FilterTree {

    private IModel<?> choices;
    private IModel<?> selections;
    private transient Filter computedFilter;
    private static final long serialVersionUID = -6368582533192176269L;

    //--------------------------
    public DynamicFilterTree(
            String id, IModel<?> selections, IModel<?> choices ) {
        this( id, selections, choices, false );
    }

    public DynamicFilterTree(
            String id, IModel<?> selections, IModel<?> choices,
            boolean singleSelect ) {
        super(
                id, createFilter( selections, choices, singleSelect ),
                singleSelect );
        computedFilter = super.getFilter();
        this.selections = selections;
        this.choices = choices;
    }

    private void readObject( ObjectInputStream in )
            throws IOException, ClassNotFoundException {
        computedFilter = null;
        in.defaultReadObject();
    }


    //--------------------------
    @Override
    public synchronized void detachModels() {
        super.detachModels();
        choices.detach();
        selections.detach();
    }

    @SuppressWarnings( { "unchecked" } )
    private static List<Ref> refs( IModel<?> model ) {
        Object object = model.getObject();
        if ( object instanceof List )
            return (List<Ref>) object;
        else {
            List<Ref> result = new ArrayList<Ref>();
            if ( object != null )
                result.add( (Ref) object );
            return result;
        }
    }

    @SuppressWarnings( { "unchecked" } )
    private static Filter createFilter(
            IModel<?> selections, IModel<?> choices, boolean singleSelect ) {
        List<Ref> choiceList = refs( choices );
        List<Ref> selectionList = refs( selections );

        // Set selection model list to new list (otherwise interface won't sync)
        if ( selections.getObject() instanceof List )
            ( (IModel<Object>) selections ).setObject( selectionList );

        Filter filter = new RootFilter( new RefContainer( choiceList ), false );
        filter.setShowingLeaves( true );

        // Set selections
        for ( Ref sel : selectionList )
            if ( choiceList.contains( sel ) )
                filter.hasSelected( sel );

        filter.setExpanded( true );
        filter.setSingleSelect( singleSelect );
        filter.simplify();
        return filter;
    }

    @Override
    public synchronized Filter getFilter() {
        if ( computedFilter == null )
            setFilter( createFilter( selections, choices, isSingleSelect() ) );

        return computedFilter;
    }

    @Override
    public synchronized void setFilter( Filter filter ) {
        computedFilter = filter;
        if ( filter != null )
            filter.setSingleSelect( isSingleSelect() );
        super.setFilter( filter );
    }

    public synchronized IModel<?> getChoices() {
        return choices;
    }

    public synchronized void setChoices( IModel<?> choices ) {
        detachModels();
        this.choices = choices;
        setFilter( createFilter( selections, choices, isSingleSelect() ) );
        super.invalidateAll();
    }

    public synchronized IModel<?> getSelections() {
        return selections;
    }

    public synchronized void setSelections( IModel<?> selections ) {
        this.selections = selections;
    }

    public List<Ref> getNewSelections() {
        List<Ref> results = new ArrayList<Ref>();
        RefContainer data = new RefContainer( refs( getChoices() ) );
        for ( Ref ref : new FilteredContainer( data, getFilter(), false ) )
            results.add( ref );

        return results;
    }

    public Ref getNewSelection() {
        Ref result = null;
        if ( isSingleSelect() ) {
            List<Ref> results = getNewSelections();
            if ( !results.isEmpty() )
                result = results.get( 0 );

        } else
            throw new IllegalStateException( "Not in single selection mode" );

        return result;
    }
}
