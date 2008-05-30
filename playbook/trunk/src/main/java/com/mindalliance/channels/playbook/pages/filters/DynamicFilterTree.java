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

    public DynamicFilterTree(String id, IModel selections, IModel choices) {
        this(id, selections, choices, false);
    }

    public DynamicFilterTree(String id, IModel selections, IModel choices, boolean singleSelect) {
        super(id, createFilter(selections, choices), singleSelect);
        this.computedFilter = super.getFilter();
        this.selections = selections;
        this.choices = choices;
    }

    public void detachModels() {
        super.detachModels();
        choices.detach();
        selections.detach();
    }

    static public List<Ref> refs( IModel model ) {
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

    static public Filter createFilter(IModel selections, IModel choices) {
        List<Ref> choiceList = refs( choices );
        List<Ref> selectionList = refs( selections );

        Filter filter = new RootFilter(new RefContainer(choiceList), false);
        filter.setShowingLeaves(true);

        // Set selections
        for (Ref sel : selectionList) {
            if (choiceList.contains(sel))
                filter.selectFirstMatch(sel);
        }
        filter.simplify();
        filter.setExpanded(true);
        return filter;
    }

    public synchronized Filter getFilter() {
        if (computedFilter == null) {
            setFilter(createFilter(selections, choices));
        }

        return computedFilter;
    }

    public synchronized void setFilter(Filter filter) {
        computedFilter = filter;
        super.setFilter(filter);
    }

    public IModel getChoices() {
        return choices;
    }

    public void setChoices(IModel choices) {
        detachModels();
        this.choices = choices;
        setFilter(createFilter(selections, choices));
        super.invalidateAll();
    }

    public IModel getSelections() {
        return selections;
    }

    public void setSelections(IModel selections) {
        this.selections = selections;
//        updateTree();
    }

    public List<Ref> getNewSelections() {
        List<Ref> results = new ArrayList<Ref>();
        RefContainer data = new RefContainer( refs( choices ) );
        for (Ref ref : new FilteredContainer(data, getFilter(), false))
            results.add(ref);

        return results;
    }

    public Ref getNewSelection() {
        Ref result = null;
        if (this.isSingleSelect()) {
            List<Ref> results = getNewSelections();
            if (results.size() > 0) {
                result = results.get(0);
            }
        } else {
            throw new RuntimeException("Not in single selection mode");
        }
        return result;
    }

}
