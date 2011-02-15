package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.pages.Updatable;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.Calendar;
import java.util.List;

/**
 * The plan's procedures from a map.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/14/11
 * Time: 9:51 AM
 */
public class ProcedureMapPage extends WebPage implements Updatable {

    private ProcedureMapSelectorPanel selector;
    private WebMarkupContainer header;
    private Component selected;
    private Label showLabel;
    private boolean showingMap = true;

    public ProcedureMapPage( PageParameters parameters ) {
        super( parameters );
        setDefaultModel( new CompoundPropertyModel<Object>( this ) );
        init();
    }

    private void init() {
        add( new Label( "pageTitle" ) );
        addHeader();
        addSelector();
        addSelected();
        add( new Label( "year", "" + Calendar.getInstance().get( Calendar.YEAR ) ) );
        add( new Label( "client", selector.getPlan().getClient() ) );
    }

    private void addHeader() {
        header = new WebMarkupContainer( "header" );
        header.setOutputMarkupId( true );
        Label reportTitle = new Label( "reportTitle" );
        header.add( reportTitle );
        AjaxLink showLink = new AjaxLink( "show" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                showingMap = !showingMap;
                showMapOrReport();
                target.addComponent( showLabel );
                target.addComponent( selected );
            }
        };
        header.add( showLink );
        showLabel = new Label( "show-what", new PropertyModel<String>( this, "showString" ) );
        showLabel.setOutputMarkupId( true );
        showLink.add( showLabel );
        addOrReplace( header );
    }

    private void showMapOrReport() {
        selector.add( new AttributeModifier(
                "class",
                true,
                new Model<String>( showingMap ? "expanded" : "collapsed") ));
        selected.add( new AttributeModifier(
                "class",
                true,
                new Model<String>( showingMap ? "collapsed" : "expanded") ));
    }

    private void addSelector() {
        selector = new ProcedureMapSelectorPanel( "selector" );
        selector.setOutputMarkupId( true );
        addOrReplace( selector );
    }

    private void addSelected() {
        selected = new SelectedAssignmentsPanel(
                "selected",
                selector );
        selected.setOutputMarkupId( true );
        addOrReplace( selected );
    }

    public String getShowString() {
        return showingMap
                ? "Show the report"
                : "Show the map";
    }

    public String getReportTitle() {
        return "Procedures - " + selector.getSelection().toString();
    }

    public String getPageTitle() {
        return "Channels - " + getReportTitle();
    }


    @Override
    public void changed( Change change ) {
        // Do nothing.
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        addHeader();
        addSelector();
        addSelected();
        target.addComponent( header );
        target.addComponent( selected );
    }

    @Override
    public void update( AjaxRequestTarget target, Object object, String action ) {
        // Do nothing
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change, List<Updatable> updated, String aspect ) {
        // Do nothing
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        // Do nothing
    }

    @Override
    public void refresh( AjaxRequestTarget target, Change change ) {
        // Do nothing
    }
}
