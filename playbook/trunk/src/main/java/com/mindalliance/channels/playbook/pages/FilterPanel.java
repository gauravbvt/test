package com.mindalliance.channels.playbook.pages;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.IDataProvider;

/**
 * ...
 */
public class FilterPanel extends Panel {

    private IDataProvider filteredData;
    private IDataProvider rawData;

    public FilterPanel( String id, IDataProvider rawData ) {
        super( id );
        setRawData( rawData );
        final Form form = new Form( "filter-form" );
        add( form );
        form.add( new Label( "filter-tree", "[Filter]" ) );
    }

    public IDataProvider getFilteredData() {
        return filteredData;
    }

    public void setFilteredData( IDataProvider filteredData ) {
        this.filteredData = filteredData;
    }

    public IDataProvider getRawData() {
        return rawData;
    }

    public void setRawData( IDataProvider rawData ) {
        this.rawData = rawData;
    }
}
