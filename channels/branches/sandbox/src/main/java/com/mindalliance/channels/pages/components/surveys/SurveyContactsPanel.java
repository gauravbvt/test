package com.mindalliance.channels.pages.components.surveys;

import com.mindalliance.channels.query.QueryService;
import com.mindalliance.channels.surveys.SurveyService;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.surveys.Contact;
import com.mindalliance.channels.surveys.Survey;
import com.mindalliance.channels.surveys.SurveyException;
import com.mindalliance.channels.util.Matcher;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.collections.TransformerUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Survey contacts panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 29, 2009
 * Time: 4:25:27 PM
 */
public class SurveyContactsPanel extends AbstractUpdatablePanel implements Filterable {

    private IModel<Survey> surveyModel;
    @SpringBean
    private QueryService queryService;
    @SpringBean
    private SurveyService surveyService;
    /**
     * Maximum number of rows of contacts to show at a time.
     */
    private static final int MAX_ROWS = 10;
    /**
     * Filters mapped by property.
     */
    private Map<String, ModelObject> filters = new HashMap<String, ModelObject>();
    private static final String USERS_SURVEYED = "Users surveyed";
    private static final String USERS_NOT_SURVEYED = "Users not surveyed";
    private static final String ALL = "All";
    private static String[] UsersScopes = {USERS_SURVEYED, USERS_NOT_SURVEYED, ALL};
    private String usersScope = USERS_SURVEYED;
    /**
     * Survey contacts table.
     */
    private SurveyContactsTable surveyContactsTable;

    public SurveyContactsPanel( String id, IModel<Survey> surveyModel ) {
        super( id );
        this.surveyModel = surveyModel;
        init();
    }

    private void init() {
        addUsersChoices();
        addContactsTable();
        addUpdateContactsButton();
    }

    private void addUsersChoices() {
        DropDownChoice<String> usersChoice = new DropDownChoice<String>(
                "usersScope",
                new PropertyModel<String>( this, "usersScope" ),
                Arrays.asList( UsersScopes )
        );
        usersChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addContactsTable();
                target.addComponent( surveyContactsTable );
            }
        } );
        add( usersChoice );
    }

    public String getUsersScope() {
        return usersScope;
    }

    public void setUsersScope( String usersScope ) {
        this.usersScope = usersScope;
    }

    private void addContactsTable() {
        surveyContactsTable = new SurveyContactsTable(
                "contacts",
                getContactDescriptors()
        );
        surveyContactsTable.setOutputMarkupId( true );
        addOrReplace( surveyContactsTable );
    }

    private void addUpdateContactsButton() {
        AjaxFallbackLink addContactsButton = new AjaxFallbackLink( "updateContacts" ) {
            public void onClick( AjaxRequestTarget target ) {
                boolean changed = updateContacts( target );
                if ( changed ) update( target, new Change( Change.Type.Updated, getSurvey() ) );
            }
        };
        addContactsButton.setVisible( !getSurvey().isClosed() );
        add( addContactsButton );
    }

    @SuppressWarnings( "unchecked" )
    boolean updateContacts( AjaxRequestTarget target ) {
        boolean changed = updateSurveyWithContacts();
        List<ContactDescriptor> toBeContacted =
                (List<ContactDescriptor>) CollectionUtils.select(
                        surveyContactsTable.getContactDescriptors(),
                        PredicateUtils.invokerPredicate( "isToBeContacted" ) );

        List<String> usernames = (List<String>) CollectionUtils.collect(
                toBeContacted,
                TransformerUtils.invokerTransformer( "getUsername" )
        );
        try {
            if ( getSurvey().isLaunched() ) {
                surveyService.inviteContacts( getSurvey(), usernames );
            }
        } catch ( SurveyException e ) {
            e.printStackTrace();
            target.prependJavascript( "alert(\"Failed to add new contacts.\")" );
        }
        return changed;
    }

    private boolean updateSurveyWithContacts() {
        boolean changed = false;
        for ( ContactDescriptor contactDescriptor : surveyContactsTable.getContactDescriptors() ) {
            Contact.Status newStatus = contactDescriptor.getNewStatus();
            if ( newStatus != null ) {
                getSurvey().updateContact( contactDescriptor.getUsername(), newStatus );
                changed = true;
            }
        }
        return changed;
    }

    /**
     * {@inheritDoc}
     */
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        if ( isFiltered( identifiable, property ) ) {
            filters.remove( property );
        } else {
            filters.put( property, (ModelObject) identifiable );
        }
        addContactsTable();
        target.addComponent( surveyContactsTable );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered( Identifiable identifiable, String property ) {
        ModelObject filtered = filters.get( property );
        return filtered != null && filtered.equals( identifiable );
    }

    @SuppressWarnings( "unchecked" )
    private List<ContactDescriptor> getContactDescriptors() {
        List<ContactDescriptor> contactDescriptors = new ArrayList<ContactDescriptor>();
        for ( Contact contact : getAllContacts() ) {
            String username = contact.getUsername();
            Actor actor = queryService.findOrCreate( Participation.class, username ).getActor();
            if ( actor == null ) {
                contactDescriptors.add( new ContactDescriptor( contact, null ) );
            } else {
                // TODO - Does this make sense?
                List<ResourceSpec> specs = queryService
                        .findAllResourcesNarrowingOrEqualTo( ResourceSpec.with( actor ) );
                for ( ResourceSpec spec : specs ) {
                    contactDescriptors.add( new ContactDescriptor( contact, spec ) );
                }
            }
        }
        return (List<ContactDescriptor>) CollectionUtils.select(
                contactDescriptors,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return !isFilteredOut( (ContactDescriptor) obj );
                    }
                } );
    }

    @SuppressWarnings( "unchecked" )
    private List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<Contact>();
        contacts.addAll( getSurvey().getContacts() );
        if ( !getSurvey().isClosed() ) {
            List<String> surveyed = (List<String>) CollectionUtils.collect(
                    contacts,
                    TransformerUtils.invokerTransformer( "getUsername" ) );
            List<String> others = (List<String>) CollectionUtils.subtract(
                    queryService.getUserService().getUsernames( getPlan().getUri() ),
                    surveyed );
            for ( String other : others ) {
                contacts.add( new Contact( other ) );
            }
        }
        return contacts;
    }

    private boolean isFilteredOut( ContactDescriptor contactDescriptor ) {
        if ( !usersScope.equals( ALL ) ) {
            final Contact contact = contactDescriptor.getContact();
            boolean isSurveyed = CollectionUtils.exists(
                    getSurvey().getContacts(),
                    PredicateUtils.equalPredicate( contact )
            );
            if ( usersScope.equals( USERS_SURVEYED ) && !isSurveyed ) return true;
            if ( usersScope.equals( USERS_NOT_SURVEYED ) && isSurveyed ) return true;
        }
        Organization org = (Organization) filters.get( "organization" );
        if ( org != null && !org.equals( contactDescriptor.getOrganization() ) )
            return true;
        Role role = (Role) filters.get( "role" );
        if ( role != null && !role.equals( contactDescriptor.getRole() ) )
            return true;
        Place jurisdiction = (Place) filters.get( "jurisdiction" );
        return jurisdiction != null
                && !Matcher.within( contactDescriptor.getJurisdiction(), jurisdiction );
    }

    public Survey getSurvey() {
        return surveyModel.getObject();
    }

    public class SurveyContactsTable extends AbstractTablePanel {

        private List<ContactDescriptor> contacts;
        private String[] statuses = {
                Contact.Status.None.name(),
                Contact.Status.To_be_contacted.name(),
                Contact.Status.Contacted.name()
        };

        public SurveyContactsTable( String id, List<ContactDescriptor> contacts ) {
            super( id, null, MAX_ROWS, null );
            this.contacts = contacts;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeTernaryCheckBoxColumn(
                    "",
                    "status",
                    statuses
            ) );
            columns.add( makeColumn(
                    "Name",
                    "fullName",
                    EMPTY ) );
            columns.add( makeColumn(
                    "Participation",
                    "userRole",
                    EMPTY ) );
            columns.add( makeFilterableLinkColumn(
                    "Organization",
                    "organization",
                    "organization.name",
                    EMPTY,
                    SurveyContactsPanel.this ) );
            columns.add( makeFilterableLinkColumn(
                    "Role",
                    "role",
                    "role.name",
                    EMPTY,
                    SurveyContactsPanel.this ) );
            columns.add( makeFilterableLinkColumn(
                    "Jurisdiction",
                    "jurisdiction",
                    "jurisdiction.name",
                    EMPTY,
                    SurveyContactsPanel.this ) );
            // provider and table
            add( new AjaxFallbackDefaultDataTable(
                    "contacts",
                    columns,
                    new SortableBeanProvider<ContactDescriptor>(
                            contacts,
                            "fullName" ),
                    getPageSize() ) );
        }

        public List<ContactDescriptor> getContactDescriptors() {
            return contacts;
        }
    }

    public class ContactDescriptor implements Serializable {
        private Contact contact;
        private String fullName;
        private ResourceSpec resourceSpec;
        private String userRole;
        private Contact.Status newStatus = null;

        public ContactDescriptor( Contact contact, ResourceSpec resourceSpec ) {
            this.contact = contact;
            this.resourceSpec = resourceSpec;
        }

        public String getUsername() {
            return contact.getUsername();
        }

        public String getFullName() {
            if ( fullName == null )
                fullName = queryService.findUserNormalizedFullName( getUsername() );
            return fullName;
        }

        public ResourceSpec getResourceSpec() {
            return resourceSpec;
        }

        public String getUserRole() {
            if ( userRole == null ) {
                if ( queryService.findAllPlanners().contains( getUsername() ) ) {
                    userRole = "planner";
                } else {
                    userRole = "responder";
                }
            }
            return userRole;
        }

        public Organization getOrganization() {
            if ( resourceSpec == null )
                return null;
            else
                return resourceSpec.getOrganization();
        }

        public Role getRole() {
            if ( resourceSpec == null )
                return null;
            else
                return resourceSpec.getRole();
        }

        public Place getJurisdiction() {
            if ( resourceSpec == null )
                return null;
            else
                return resourceSpec.getJurisdiction();
        }

        public boolean isContacted() {
            return getStatus().equals( Contact.Status.Contacted.name() );
        }

        public void setStatus( String status ) {
            newStatus = Contact.Status.valueOf( status );
        }

        public String getStatus() {
            if ( newStatus == null )
                return contact.getStatus().name();
            else
                return newStatus.name();
        }

        public boolean isToBeContacted() {
            return getStatus().equals( Contact.Status.To_be_contacted.name() );
        }

        public Contact.Status getNewStatus() {
            return newStatus;
        }

        public Contact getContact() {
            return contact;
        }
    }
}
