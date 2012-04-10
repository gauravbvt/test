package com.mindalliance.channels.pages.components.social.rfi;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.social.model.rfi.Questionnaire;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
import com.mindalliance.channels.social.services.RFIService;
import com.mindalliance.channels.social.services.RFISurveyService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RFI surveys panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/6/12
 * Time: 2:36 PM
 */
public class RFISurveysPanel extends AbstractUpdatablePanel implements Filterable {

    private static final String ANYTHING = "anything";

    @SpringBean
    private RFISurveyService rfiSurveyService;
    
    @SpringBean
    private RFIService rfiService;

    private String aboutTypeName = ANYTHING;
    private boolean onlyActive = true;
    private RFISurvey selectedRFISurvey;
    /**
     * Filters mapped by property.
     */
    private Map<String, ModelObject> filters = new HashMap<String, ModelObject>();
    private WebMarkupContainer rfiSurveyContainer;
    private AjaxCheckBox activeCheckBox;
    private static final int MAX_ROWS = 10;
    private RFISurveyTable rfiSurveyTable;


    public RFISurveysPanel( String id, IModel<RFISurvey> rfiSurveyModel ) {
        super( id, rfiSurveyModel );
        init();
    }

    private void init() {
        addFilters();
        addRFISurveyTable();
        addRFISurveyContainer();
    }

    private void addFilters() {
        activeCheckBox = new AjaxCheckBox(
                "active",
                new PropertyModel<Boolean>( this, "onlyActive" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addRFISurveyTable();
                target.add( rfiSurveyTable );
            }
        };
        add( activeCheckBox );
        DropDownChoice<String> aboutChoice = new DropDownChoice<String>(
                "aboutChoice",
                new PropertyModel<String>( this, "aboutTypeName" ),
                getAboutChoices() );
        aboutChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addRFISurveyTable();
                target.add( rfiSurveyTable );
            }
        } );
        add( aboutChoice );
    }

    private List<String> getAboutChoices() {
        List<String> choices = new ArrayList<String>();
        choices.add( ANYTHING );
        choices.addAll( ModelObject.TYPE_LABELS );
        return choices;
    }


    private void addRFISurveyTable() {
        rfiSurveyTable = new RFISurveyTable(
                "rfiSurveys",
                new PropertyModel<List<RFISurveyWrapper>>( this, "rfiSurveyWrappers" )
        );
        rfiSurveyTable.setOutputMarkupId( true );
        addOrReplace( rfiSurveyTable );
    }

    public List<RFISurveyWrapper> getRfiSurveyWrappers() {
        List<RFISurveyWrapper> wrappers = new ArrayList<RFISurveyWrapper>();
        List<RFISurvey> rfiSurveys = rfiSurveyService.select(
                getPlan(),
                onlyActive, // restrict to open surveys
                aboutTypeName );
        for ( RFISurvey rfiSurvey : rfiSurveys ) {
            if ( !isFilteredOut( rfiSurvey ) )
                if ( onlyActive && rfiSurvey.isActive( getQueryService())) {
                    rfiSurveyService.refresh( rfiSurvey );
                    wrappers.add( new RFISurveyWrapper( rfiSurvey ) );
                }
        }
        return wrappers;
    }

    private void addRFISurveyContainer() {
        rfiSurveyContainer = new WebMarkupContainer( "rfiSurveyContainer" );
        rfiSurveyContainer.setOutputMarkupId( true );
        makeVisible( rfiSurveyContainer, selectedRFISurvey != null );
        rfiSurveyContainer.add( new Label(
                "rfiSurveyLabel",
                new PropertyModel<String>( this, "rfiSurveyLabel" ) ) );
        rfiSurveyContainer.add( selectedRFISurvey == null
                ? new Label( "rfiSurvey", "" )
                : new RFISurveyPanel(
                "rfiSurvey",
                new PropertyModel<RFISurvey>( this, "selectedSurveyRFI" ) ) );
        addOrReplace( rfiSurveyContainer );
    }

    public String getRfiSurveyLabel() {
        return selectedRFISurvey == null
                ? ""
                : selectedRFISurvey.getLabel( getQueryService() );
    }

    public RFISurvey getSelectedRFISurvey() {
        return selectedRFISurvey;
    }

    public void setSelectedRFISurvey( RFISurvey rfiSurvey ) {
        this.selectedRFISurvey = rfiSurvey.isUnknown() ? null : rfiSurvey;
    }

    @Override
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        if ( isFiltered( identifiable, property ) ) {
            filters.remove( property );
        } else {
            filters.put( property, (ModelObject) identifiable );
        }
        addRFISurveyContainer();
        target.add( rfiSurveyContainer );
    }

    @Override
    public boolean isFiltered( Identifiable identifiable, String property ) {
        ModelObject filtered = filters.get( property );
        return filtered != null && filtered.equals( identifiable );
    }

    private boolean isFilteredOut( RFISurvey rfiSurvey ) {
        Segment segment = (Segment) filters.get( "segment" );
        return segment != null && !segment.equals( rfiSurvey.getSegment( getQueryService() ) );
    }

    public boolean isOnlyActive() {
        return onlyActive;
    }

    public void setOnlyActive( boolean onlyActive ) {
        this.onlyActive = onlyActive;
    }

    public String getAboutTypeName() {
        return ( aboutTypeName == null || aboutTypeName.isEmpty() ) ? ANYTHING : aboutTypeName;
    }

    public void setAboutTypeName( String val ) {
        this.aboutTypeName = val.equals( ANYTHING )
                ? null
                : val;
    }

    @Override
    public void changed( Change change ) {
        if ( change.isForInstanceOf( RFISurveyWrapper.class ) && change.isExpanded() ) {
            RFISurveyWrapper fw = (RFISurveyWrapper) change.getSubject( getQueryService() );
            setSelectedRFISurvey( fw.getRfiSurvey() );
        } else {
            super.changed( change );
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForInstanceOf( RFISurveyWrapper.class ) && change.isExpanded() ) {
            addRFISurveyContainer();
            target.add( rfiSurveyContainer );
        } else
            super.updateWith( target, change, updated );
    }



    public class RFISurveyWrapper implements Identifiable {

        private RFISurvey rfiSurvey;

        private RFISurveyWrapper( RFISurvey rfiSurvey ) {
            this.rfiSurvey = rfiSurvey;
        }

        public RFISurvey getRfiSurvey() {
            return rfiSurvey;
        }

        @Override
        public long getId() {
            return rfiSurvey.getId();
        }

        @Override
        public String getDescription() {
            return rfiSurvey.getDescription();
        }

        @Override
        public String getTypeName() {
            return rfiSurvey.getTypeName();
        }

        @Override
        public boolean isModifiableInProduction() {
            return rfiSurvey.isModifiableInProduction();
        }

        @Override
        public String getName() {
            return rfiSurvey.getName();
        }

        public Questionnaire getQuestionnaire() {
            return rfiSurvey.getQuestionnaire();
        }

        public Segment getSegment() {
            return rfiSurvey.getSegment( getQueryService() );
        }

        public String getStatusLabel() {
            return rfiSurvey.getStatusLabel( getQueryService() );
        }

        public int getSentToCount() {
            return rfiSurvey.getRfis().size();
        }

        public String getResponseMetrics() {
            return rfiSurveyService.findResponseMetrics( rfiSurvey, rfiService );
        }

    }

    public class RFISurveyTable extends AbstractTablePanel<RFISurveyWrapper> {

        private IModel<List<RFISurveyWrapper>> rfiSurveysModel;

        public RFISurveyTable( String id, IModel<List<RFISurveyWrapper>> rfiSurveysModel ) {
            super( id );
            this.rfiSurveysModel = rfiSurveysModel;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // Columns
            columns.add( makeColumn( "Questionnaire", "questionnaire.name", EMPTY ) );
            columns.add( makeColumn( "About", "questionnaire.about", EMPTY ) );
            columns.add( makeFilterableLinkColumn( "Segment", "segment", "segment.name", EMPTY, RFISurveysPanel.this ) );
            columns.add( makeColumn( "Status", "statusLabel", EMPTY ) );
            columns.add( makeColumn( "Sent to", "sentToCount", EMPTY ) );
            columns.add( makeColumn( "Responses", "responseMetrics", EMPTY ) );
            columns.add( makeExpandLinkColumn( "", "", "more..." ) );
            // Provider and table
            add( new AjaxFallbackDefaultDataTable( "rfiSurveys",
                    columns,
                    new SortableBeanProvider<RFISurveyWrapper>( rfiSurveysModel.getObject(),
                            "created" ),
                    MAX_ROWS ) );

        }
    }

}
