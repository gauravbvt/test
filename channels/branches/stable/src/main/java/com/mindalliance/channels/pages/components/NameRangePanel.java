package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.util.NameRange;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 27, 2009
 * Time: 3:51:04 PM
 */
public class NameRangePanel extends Panel {
    /**
     * Desired maximum size for a name range.
     */
    private int maxSize;
    /**
     * Who to inform of a range selection.
     */
    private final NameRangeable rangeable;
    /**
     * Model for a list of strings.
     */
    private IModel<List<String>> domainModel;
    /**
     * The ranges that partition the list of strings.
     */
    private List<NameRange> ranges;
    /**
     * What to call the empty range that "contains" the whole list.
     */
    private String fullRangeLabel;
    /**
     * Currently selected range, if any.
     */
    private NameRange selectedRange;
    /**
     * Container for the list of ranges.
     */
    private WebMarkupContainer rangesDiv;

    public NameRangePanel(
            String id,
            IModel<List<String>> model,
            int maxSize,
            NameRangeable rangeable,
            String fullRangeLabel ) {
        super( id, model );
        domainModel = model;
        this.maxSize = Math.max( 1, maxSize );
        this.rangeable = rangeable;
        this.fullRangeLabel = fullRangeLabel;
        selectedRange = getNameRanges().get( 0 );
        init();
    }

    private void init() {
        rangesDiv = new WebMarkupContainer( "rangesDiv" );
        rangesDiv.setOutputMarkupId( true );
        add( rangesDiv );
        addRangeList();
    }

    private void addRangeList() {
        rangesDiv.addOrReplace( new ListView<NameRange>(
                "ranges",
                new PropertyModel<List<NameRange>>( this, "nameRanges" ) ) {
            protected void populateItem( ListItem<NameRange> item ) {
                final NameRange range = item.getModelObject();
                AjaxFallbackLink link = new AjaxFallbackLink( "link" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        rangeable.setNameRange( target, range );
                    }
                };
                item.add( link );
                String rangeLabel = range.getLabel();
                if ( range == selectedRange || ( range.isEmpty() && selectedRange == null ) ) {
                    link.add( new AttributeModifier( "class", true, new Model<String>( "selected" ) ) );
                }
                link.add( new Label(
                        "label",
                        new Model<String>( rangeLabel == null ? fullRangeLabel : rangeLabel ) ) );
            }
        } );

    }

    /**
     * Get the name ranges.
     * @return a list of name ranges
     */
    @SuppressWarnings( "unchecked" )
    public List<NameRange> getNameRanges() {
        if ( ranges == null ) {
            List<String> domain = (List<String>) CollectionUtils.collect(
                    (List<String>) CollectionUtils.select(
                            domainModel.getObject(),
                            new Predicate() {
                                public boolean evaluate( Object obj ) {
                                    return !( (String) obj ).trim().isEmpty();
                                }
                            } ),
                    new Transformer() {
                        public Object transform( Object obj ) {
                            return ( (String) obj ).trim().toLowerCase();
                        }
                    } );
            Collections.sort( domain );
            ranges = new ArrayList<NameRange>();
            ranges.add( new NameRange() );
            NameRange priorRange = null;
            int index = 0;
            while ( index < domain.size() ) {
                int[] bounds = getNextRangeBounds( index, domain );
                NameRange range = new NameRange(
                        domain.get( bounds[0] ),
                        domain.get( bounds[1] ) );
                if ( priorRange != null ) {
                    range.setPrior( priorRange.getUpper() );
                    priorRange.setPosterior( range.getLower() );
                }
                ranges.add( range );
                priorRange = range;
                index = bounds[1] + 1;
            }
        }
        return ranges;
    }

    /**
     * Find bounds for the next range.
     *
     * @param start  starting index
     * @param domain list of names
     * @return an interge array with low and high bound (inclusive)
     */
    private int[] getNextRangeBounds( int start, List<String> domain ) {
        int size = domain.size();
        assert start < size;
        if ( start > size - maxSize ) {
            return new int[]{start, size - 1};
        }
        int cut = Math.min( start + maxSize - 1, size - 1 );
        int lowCut = cut;
        int highCut = cut;
        while ( lowCut > start
                && lowCut < size - 1
                && domain.get( lowCut ).equals( domain.get( lowCut + 1 ) ) )
        {
            lowCut--;
        }
        while ( highCut < size - 1 && domain.get( highCut ).equals( domain.get( highCut + 1 ) ) ) {
            highCut++;
        }
        if ( lowCut == start || ( highCut - cut ) <= ( cut - lowCut ) ) {
            return new int[]{start, highCut};
        } else {
            return new int[]{start, lowCut};
        }
    }

    /**
     * Find the range that contains a name.
     *
     * @param name a string
     * @return a name range
     */
    public NameRange getRangeFor( String name ) {
        for ( NameRange range : getNameRanges() ) {
            if ( !range.isEmpty() && range.contains( name ) ) return range;
        }
        return null;
    }

    /**
     * Set selected name range.
     *
     * @param target an ajax request target
     * @param range  a name range
     */
    public void setSelected( AjaxRequestTarget target, NameRange range ) {
        selectedRange = range;
        addRangeList();
        target.add( rangesDiv );
    }
}
