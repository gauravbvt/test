package com.mindalliance.channels.pages.components.community;

import com.mindalliance.channels.core.community.CommunityServiceFactory;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.db.services.communities.UserParticipationService;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * All communities panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/11/13
 * Time: 12:39 PM
 */
public class AllCollaborationCommunitiesPanel extends AbstractCommandablePanel {

    private static final int MAX_ROWS = 10;

    @SpringBean
    private PlanCommunityManager planCommunityManager;
    @SpringBean
    private UserParticipationService userParticipationManager;
    @SpringBean
    private CommunityServiceFactory communityServiceFactory;

    private AllCollaborationCommunitiesTable collaborationCommunitiesTable;
    private CollaborationModel selectedProductionModel;
    private AjaxLink<String> newCollaborationCommunityButton;

    public AllCollaborationCommunitiesPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addCollaborationCommunitiesTable();
        addStartCollaborationPlan();
    }

    private void addStartCollaborationPlan() {
        addProductionModelChoice();
        addCreateButton();
    }

    private void addProductionModelChoice() {
        DropDownChoice<CollaborationModel> modelsChoice = new DropDownChoice<CollaborationModel>(
                "productionModels",
                new PropertyModel<CollaborationModel>( this, "selectedProductionModel" ),
                new PropertyModel<List<CollaborationModel>>( this, "productionModels" ),
                new IChoiceRenderer<CollaborationModel>() {
                    @Override
                    public Object getDisplayValue( CollaborationModel plan ) {
                        return plan.getVersionedName();
                    }

                    @Override
                    public String getIdValue( CollaborationModel plan, int index ) {
                        return Integer.toString( index );
                    }
                }
        );
        modelsChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                newCollaborationCommunityButton.setEnabled( selectedProductionModel != null );
                target.add( newCollaborationCommunityButton );
            }
        } );
        modelsChoice.setOutputMarkupId( true );
        addOrReplace( modelsChoice );
    }

    public List<CollaborationModel> getProductionModels() {
        List<CollaborationModel> productionCollaborationModels = getModelManager().getProductionModels();
        Collections.sort( productionCollaborationModels, new Comparator<CollaborationModel>() {
            @Override
            public int compare( CollaborationModel p1, CollaborationModel p2 ) {
                return p1.getName().compareTo( p2.getName() );
            }
        } );
        return productionCollaborationModels;
    }

    public CollaborationModel getSelectedProductionModel() {
        return selectedProductionModel;
    }

    public void setSelectedProductionModel( CollaborationModel selectedProductionModel ) {
        this.selectedProductionModel = selectedProductionModel;
    }

    private void addCreateButton() {
        newCollaborationCommunityButton = new AjaxLink<String>( "newCommunity" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                startNewCollaborationPlan();
                addCollaborationCommunitiesTable();
                target.add( collaborationCommunitiesTable );
            }
        };
        newCollaborationCommunityButton.setOutputMarkupId( true );
        add( newCollaborationCommunityButton );
        newCollaborationCommunityButton.setEnabled( selectedProductionModel != null );
    }

    private void startNewCollaborationPlan() {
        planCommunityManager.createNewCommunityFor( selectedProductionModel, getUser(), getCommunityService() );
    }

    public void updateContent( AjaxRequestTarget target ) {
        addCollaborationCommunitiesTable();
        target.add( collaborationCommunitiesTable );
    }

    private void addCollaborationCommunitiesTable() {
        collaborationCommunitiesTable = new AllCollaborationCommunitiesTable(
                "allCollaborationCommunitiesTable",
                new PropertyModel<List<CollaborationCommunityWrapper>>( this, "collaborationCommunityWrappers" ) );
        addOrReplace( collaborationCommunitiesTable );
    }

    public List<CollaborationCommunityWrapper> getCollaborationCommunityWrappers() {
        List<CollaborationCommunityWrapper> wrappers = new ArrayList<CollaborationCommunityWrapper>();
        for ( PlanCommunity planCommunity : planCommunityManager.getPlanCommunities() ) {
            if ( !planCommunity.isModelCommunity()
                    && ( !planCommunity.isClosed() || isCollaborationPlanner( getUser(), planCommunity ) ) ) {
                wrappers.add( new CollaborationCommunityWrapper( planCommunity ) );
            }
        }
        Collections.sort( wrappers );
        return wrappers;
    }

    private boolean isCollaborationPlanner( ChannelsUser user, PlanCommunity planCommunity ) {
        return user.isCommunityPlanner( planCommunity.getUri() );
    }

    public class CollaborationCommunityWrapper implements Identifiable, Comparable<CollaborationCommunityWrapper> {

        private PlanCommunity collaborationCommunity;

        public CollaborationCommunityWrapper( PlanCommunity collaborationCommunity ) {
            this.collaborationCommunity = collaborationCommunity;
        }

        public PlanCommunity getCollaborationCommunity() {
            return collaborationCommunity;
        }

        public Place getLocale() {
            return collaborationCommunity.getLocale( getCommunityService() );
        }

        public CollaborationModel getModel() {
            return getModelManager().getModel( collaborationCommunity.getModelUri(), collaborationCommunity.getModelVersion() );
        }

        public String getUserParticipates(  ) {
            return getCommunityService().getParticipationManager()
                    .userHasJoinedCommunity( getUser(), communityServiceFactory.getService( collaborationCommunity ) )
                    ? "Yes"
                    : "No";
        }

        public String getCollaborationCommunityUrl() {
            return makeCommunityPageUrl( collaborationCommunity );
        }

        public String getStatus() {
            return collaborationCommunity.isClosed()
                    ? "Closed"
                    : "Open";
        }

        @Override
        public String getClassLabel() {
            return getClass().getSimpleName();
        }

        @Override
        public String getKindLabel() {
            return getTypeName();
        }

        @Override
        public String getUid() {
            return Long.toString( getId() );
        }

        @Override
        public long getId() {
            return collaborationCommunity.getId();
        }

        @Override
        public String getDescription() {
            return collaborationCommunity.getDescription();
        }

        @Override
        public String getTypeName() {
            return collaborationCommunity.getTypeName();
        }

        @Override
        public boolean isModifiableInProduction() {
            return collaborationCommunity.isModifiableInProduction();
        }

        @Override
        public String getName() {
            return collaborationCommunity.getName();
        }

        @Override
        public int compareTo( CollaborationCommunityWrapper other ) {
            return getName().compareTo( other.getName() );
        }
    }

    public class AllCollaborationCommunitiesTable extends AbstractTablePanel<CollaborationCommunityWrapper> {
        private IModel<List<CollaborationCommunityWrapper>> collaborationPlansModel;

        public AllCollaborationCommunitiesTable( String s, IModel<List<CollaborationCommunityWrapper>> collaborationPlansModel ) {
            super( s );
            this.collaborationPlansModel = collaborationPlansModel;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // Columns
            columns.add( makeColumn( "Name", "name", null, EMPTY, "description" ) );
            columns.add( makeColumn( "Locale", "locale.name", null, EMPTY, "locale.description" ) );
            columns.add( makeColumn( "Collaboration model", "model.name", null, EMPTY, "model.description" ) );
            columns.add( makeColumn( "Model URI", "model.versionUri", null, EMPTY, "model.versionUri" ) );
            columns.add( makeColumn( "I participate", "userParticipates", EMPTY ) );
            columns.add( makeColumn( "Status", "status", EMPTY ) );
            columns.add( makeExternalLinkColumn(
                    "",
                    "collaborationCommunityUrl",
                    null,
                    "Go",
                    false   // stay on same page
            ) );
            // Provider and table
            add( new AjaxFallbackDefaultDataTable( "allCollaborationCommunities",
                    columns,
                    new SortableBeanProvider<CollaborationCommunityWrapper>( collaborationPlansModel.getObject(),
                            "name" ),
                    MAX_ROWS ) );
        }
    }
}
