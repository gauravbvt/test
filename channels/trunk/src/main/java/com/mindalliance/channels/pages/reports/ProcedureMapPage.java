package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.Updatable;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
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
    private Label reportTitle;
    private AjaxLink goBackLink = null;

    public ProcedureMapPage( PageParameters parameters ) {
        super( parameters );
        setDefaultModel( new CompoundPropertyModel<Object>( this ) );
        init();
    }

    public void renderHead( HtmlHeaderContainer container ) {
        container.getHeaderResponse().renderJavascript( PlanPage.IE7CompatibilityScript, null );
        super.renderHead( container );
    }

    private void init() {
        add( new Label( "pageTitle" ) );
        addSelector();
        addHeader();
        addSelected();
        add( new Label( "year", "" + Calendar.getInstance().get( Calendar.YEAR ) ) );
        add( new Label( "client", selector.getPlan().getClient() ) );
    }

    private void addHeader() {
        header = new WebMarkupContainer( "header" );
        header.setOutputMarkupId( true );
        addReportTitle();
        AjaxLink showLink = new AjaxLink( "show" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                selector.setGoingForward();
                makeVisible( goBackLink, selector.canGoBack() );
                target.addComponent( goBackLink );
                showingMap = !showingMap;
                if ( showingMap ) {
                    selector.resetSelected();
                }
                addReportTitle();
                target.addComponent( reportTitle );
                showMapOrReport( target );
                target.addComponent( showLabel );
            }
        };
        // makeVisible( showLink, ( !showingMap || selector.hasProcedures() ) );
        header.add( showLink );
        showLabel = new Label( "show-what", new PropertyModel<String>( this, "showString" ) );
        showLabel.setOutputMarkupId( true );
        showLink.add( showLabel );
        goBackLink = new AjaxLink( "back" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                Change change = selector.goBack();
                if ( change != null ) {
                    changed( change );
                    makeVisible( goBackLink, selector.canGoBack() );
                    target.addComponent( goBackLink );
                    updateWith( target, change, new ArrayList<Updatable>() );
                }
            }
        };
        makeVisible( goBackLink, selector.canGoBack() );
        header.add( goBackLink );
        addOrReplace( header );
    }

    private void addReportTitle() {
        reportTitle = new Label( "reportTitle" );
        reportTitle.setOutputMarkupId( true );
        header.addOrReplace( reportTitle );
    }

    private void showMapOrReport( AjaxRequestTarget target ) {
        addSelected();
        makeVisible( selector, showingMap );
        makeVisible( selected, !showingMap );
        target.addComponent( selector );
        target.addComponent( selected );
    }

    private void addSelector() {
        selector = new ProcedureMapSelectorPanel( "selector" );
        selector.setOutputMarkupId( true );
        makeVisible( selector, showingMap );
        addOrReplace( selector );
    }

    private void addSelected() {
        if ( !showingMap ) {
            if ( selector.getAssignment() != null ) {
                selected = new AssignmentReportPanel(
                        "selected", new DefaultReportHelper( selector, this, selector.getAssignment() ) );
            } else if ( selector.getFlow() != null && selector.getPart() != null /*&& selector.getActor() != null*/ ) {
                selected = new CommitmentReportPanel(
                        "selected",
                        new DefaultReportHelper(
                                selector,
                                this,
                                selector.getFlow(),
                                selector.getPart() ) );
            } else {
                selected = new AssignmentsReportPanel(
                        "selected",
                        selector,
                        new DefaultReportHelper( selector, this ) );
            }
        } else {
            selected = new Label( "selected", "" );
        }
        selected.setOutputMarkupId( true );
        makeVisible( selected, !showingMap );
        addOrReplace( selected );
    }

    public String getShowString() {
        return showingMap
                ? "Show report"
                : "Show map";
    }

    public String getReportTitle() {
        return selector.getTitle();
    }

    public String getPageTitle() {
        return "Channels - " + getReportTitle();
    }


    @Override
    public void changed( Change change ) {
        selector.changed( change );
    }


    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isSelected() ) {
            showingMap = !change.isForProperty( "showReport" )
                    && ( change.isForInstanceOf( Segment.class ) || change.isForInstanceOf( Plan.class ) );
            addHeader();
            addSelected();
            addReportTitle();
            target.addComponent( header );
            showMapOrReport( target );
        }
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

    private static void makeVisible( Component component, boolean visible ) {
        component.add( new AttributeModifier( "style", true, new Model<String>(
                visible ? "" : "display:none" ) ) );
    }

}
