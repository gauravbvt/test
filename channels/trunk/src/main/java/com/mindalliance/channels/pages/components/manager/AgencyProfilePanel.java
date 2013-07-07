package com.mindalliance.channels.pages.components.manager;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.community.Agency;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Channelable;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.db.services.communities.RegisteredOrganizationService;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.ChannelListPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Agency profile panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/13/12
 * Time: 10:54 AM
 */
public class AgencyProfilePanel extends AbstractUpdatablePanel {

    @SpringBean
    private RegisteredOrganizationService registeredOrganizationService;


    private Agency agency;

    private Agency tempAgency;

    private List<String> errors = new ArrayList<String>();

    private WebMarkupContainer errorsContainer;
    private AjaxLink<String> applyButton;
    private List<String> parentNameChoices;

    public AgencyProfilePanel( String id, IModel<Agency> registeredOrgModel ) {
        super( id );
        init( registeredOrgModel.getObject() );
    }

    private void init( Agency a ) {
        resetAgency( a );
        addNameField();
        addDescriptionField();
        addMissionField();
        addAddressField();
        addParentField();
        addContactInfoPanel();
        addErrorsList();
        addCancelAndApplyButtons();
    }

    private void resetAgency( Agency a ) {
        agency = a;
        tempAgency = new Agency( agency );
        tempAgency.setEditable( canBeEdited() );
        tempAgency.initChannels( registeredOrganizationService, getCommunityService() );
    }

    private void addNameField() {
        final List<String> choices = findNewOrganizationNameChoices();
        AutoCompleteTextField<String> nameField = new AutoCompleteTextField<String>(
                "name",
                new PropertyModel<String>( this, "name" ),
                getAutoCompleteSettings()
        ) {
            @Override
            protected Iterator<String> getChoices( String input ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( Matcher.matches( input, choice ) )
                        candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateFields( target );
            }
        } );
        nameField.setVisible( canBeEdited() );
        add( nameField );
        //
        Label nameLabel = new Label( "nameLabel", getName() );
        nameLabel.setVisible( !canBeEdited() );
        add( nameLabel );
    }

    private void addDescriptionField() {
        TextArea<String> descriptionField = new TextArea<String>(
                "description",
                new PropertyModel<String>( this, "description" )
        );
        descriptionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                updateApplyButton( target );
            }
        } );
        descriptionField.setVisible( canBeEdited() );
        add( descriptionField );
        //
        Label descriptionLabel = new Label( "descriptionLabel", getDescription() );
        descriptionLabel.setVisible( !canBeEdited() );
        add( descriptionLabel );
    }

    private void addMissionField() {
        TextArea<String> missionField = new TextArea<String>(
                "mission",
                new PropertyModel<String>( this, "mission" )
        );
        missionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                updateApplyButton( target );
            }
        } );
        missionField.setVisible( canBeEdited() );
        add( missionField );
        //
        Label missionLabel = new Label( "missionLabel", getMission() );
        missionLabel.setVisible( !canBeEdited() );
        add( missionLabel );
    }

    private void addAddressField() {
        TextArea<String> addressField = new TextArea<String>(
                "address",
                new PropertyModel<String>( this, "address" )
        );
        addressField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                updateApplyButton( target );
            }
        } );
        addressField.setVisible( canBeEdited() );
        add( addressField );
        //
        Label addressLabel = new Label( "addressLabel", getAddress() );
        addressLabel.setVisible( !canBeEdited() );
        add( addressLabel );
    }


    private void addParentField() {
        final List<String> choices = findParentNameChoices();
        AutoCompleteTextField<String> parentNameField = new AutoCompleteTextField<String>(
                "parent",
                new PropertyModel<String>( this, "parentName" ),
                getAutoCompleteSettings()
        ) {
            @Override
            protected Iterator<String> getChoices( String input ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( Matcher.matches( input, choice ) )
                        candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        parentNameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateFields( target );
            }
        } );
        parentNameField.setVisible( canBeEdited() );
        add( parentNameField );
        //
        Label parentLabel = new Label( "parentLabel", getParentName() );
        parentLabel.setVisible( !canBeEdited() );
        add( parentLabel );

    }

    private void updateFields( AjaxRequestTarget target ) {
        updateErrors();
        addErrorsList();
        target.add( errorsContainer );
        parentNameChoices = null;
        updateApplyButton( target );
    }

    private void updateApplyButton( AjaxRequestTarget target ) {
        applyButton.setEnabled( errors.isEmpty() );
        target.add( applyButton );
    }

    private void addContactInfoPanel() {
        add( new ChannelListPanel(
                "orgContact",
                new Model<Channelable>( tempAgency ),
                false ) );
    }

    private void addErrorsList() {
        errorsContainer = new WebMarkupContainer( "errorsContainer" );
        errorsContainer.setOutputMarkupId( true );
        ListView<String> errorsListView = new ListView<String>(
                "errors",
                errors
        ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                item.add( new Label( "error", item.getModelObject() ) );
            }
        };
        errorsContainer.add( errorsListView );
        makeVisible( errorsContainer, !errors.isEmpty() );
        addOrReplace( errorsContainer );
    }

    private void addCancelAndApplyButtons() {
        // cancel
        AjaxLink<String> cancelButton = new AjaxLink<String>( "cancel" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Collapsed, agency ) );
            }
        };
        add( cancelButton );
        // apply
        applyButton = new AjaxLink<String>( "apply" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                boolean success = registeredOrganizationService.updateWith(
                        getUser(),
                        agency.getName(),
                        tempAgency,
                        getCommunityService() );
                // resetAgency( tempAgency );
                Change change = new Change( Change.Type.Collapsed, agency );
                change.setMessage( success
                        ? "Organization profile updated"
                        : "Failed to update organization profile" );
                update( target, change );
            }
        };
        applyButton.setOutputMarkupId( true );
        applyButton.setVisible( !agency.isFixedOrganization() );
        add( applyButton );
    }


    private List<String> findNewOrganizationNameChoices() {
        List<String> newNames = new ArrayList<String>();
        for ( String orgName : findAllOrganizationNames( getCommunityService() ) ) {
            newNames.add( orgName + " 2" ); // todo - make a wee bit smarter
        }
        return newNames;
    }

    private List<String> findAllOrganizationNames( CommunityService communityService ) {
        Set<String> allOrgNames = new HashSet<String>();
        for ( Organization org : communityService.getPlanService().listActualEntities( Organization.class, true ) ) {
            if ( !org.isPlaceHolder() ) {
                allOrgNames.add( org.getName() );
            }
        }
        allOrgNames.addAll( registeredOrganizationService.getAllRegisteredNames( communityService ) );
        List<String> sortedNames = new ArrayList<String>( allOrgNames );
        Collections.sort( sortedNames );
        return sortedNames;
    }

    @SuppressWarnings( "unchecked" )
    private List<String> findParentNameChoices() {
        if ( parentNameChoices == null ) {
            parentNameChoices = (List<String>) CollectionUtils.select(
                    findAllOrganizationNames( getCommunityService() ),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            return getCommunityService().canHaveParentAgency(
                                    agency.getName(),
                                    (String) object );
                        }
                    }
            );
        }
        return parentNameChoices;
    }

    private void updateErrors() {
        errors = new ArrayList<String>();
        if ( canBeEdited() ) {
            // name
            if ( !tempAgency.getName().equals( agency.getName() ) ) {
                if ( findAllOrganizationNames( getCommunityService() ).contains( tempAgency.getName() ) ) {
                    errors.add( "The name \"" + tempAgency.getName() + "\" is already taken by another organization" );
                }
            }
            // parent
            String tempParentName = tempAgency.getParentName();
            if ( tempParentName != null ) {
                if ( !getCommunityService().canHaveParentAgency(
                        agency.getName(),
                        tempParentName ) ) {
                    errors.add( "The organization can not have \"" + tempParentName + "\" as parent" );
                }
            }
        }
    }


    private boolean canBeEdited() {
        CommunityService communityService = getCommunityService();
        return !isFixedOrganization()
                && getPlanCommunity().isOrganizationLead( getUser(), agency.getRegistration( ), communityService );
    }

    public String getName() {
        return tempAgency.getName();
    }

    public void setName( String val ) {
        if ( val != null && !val.isEmpty() ) {
            tempAgency.setName( val );
        }
    }

    public String getDescription() {
        return tempAgency.getDescription();
    }

    public void setDescription( String val ) {
        if ( val != null && !val.isEmpty() ) {
            tempAgency.setDescription( val );
        }
    }

    public String getMission() {
        return tempAgency.getMission();
    }

    public void setMission( String val ) {
        if ( val != null && !val.isEmpty() ) {
            tempAgency.setMission( val );
        }
    }

    public String getAddress() {
        return tempAgency.getAddress();
    }

    public void setAddress( String val ) {
        if ( val != null && !val.isEmpty() ) {
            tempAgency.setAddress( val );
        }
    }


    public String getParentName() {
        return tempAgency.getParentName();
    }

    public void setParentName( String val ) {
        tempAgency.setParentName( val );
    }

    private Organization getFixedOrganization() {
        return tempAgency.getFixedOrganization();
    }

    private boolean isFixedOrganization() {
        return tempAgency.isFixedOrganization();
    }

    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updatables ) {
        if ( change.isForInstanceOf( Channelable.class ) ) {
            updateApplyButton( target );
        }
        if ( !change.isForProperty( "modifiableChannels" ) ) {
            super.updateWith( target, change, updatables );
        }
    }
}
