package com.mindalliance.channels.pages.procedures;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.IndicatorAwareWebContainer;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * The plan's procedures from a map.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/14/11
 * Time: 9:51 AM
 */
public class ProcedureMapPage extends AbstractChannelsWebPage {

    private WebMarkupContainer indicatorAware;
    /**
     * Ajax activity spinner.
     */
    private WebMarkupContainer spinner;

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
        addIndicatorAware();
        addSelector();
        addHeader();
        addSpinner();
        addSelected();
/*
        indicatorAware.add( new Label( "year", "" + Calendar.getInstance().get( Calendar.YEAR ) ) );
*/
        indicatorAware.add( new Label( "client", selector.getPlan().getClient() ) );
    }

    private void addIndicatorAware() {
        indicatorAware = new IndicatorAwareWebContainer( "indicatorAware", "spinner" );
        add( indicatorAware );
    }

    private void addSpinner() {
        spinner = new WebMarkupContainer( "spinner" );
        spinner.setOutputMarkupId( true );
        spinner.add( new AttributeModifier( "id", true, new Model<String>( "spinner" ) ) );
        spinner.add( new AttributeModifier( "style", true, new Model<String>( "display:none" ) ) );
        addOrReplace( spinner );
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
        addChannelsLogo();
        indicatorAware.addOrReplace( header );
    }

    private void addChannelsLogo() {
          WebMarkupContainer channels_logo = new WebMarkupContainer( "channelsHome");
          channels_logo.add( new AjaxEventBehavior( "onclick") {
              @Override
              protected void onEvent( AjaxRequestTarget target ) {
                  String homeUrl =  AbstractChannelsWebPage.redirectUrl( "home", getPlan() );
                  RedirectPage page =  new RedirectPage( homeUrl );
                  setResponsePage( page );
              }
          });
          header.add( channels_logo );
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
        indicatorAware.addOrReplace( selector );
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
        indicatorAware.addOrReplace( selected );
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

}
