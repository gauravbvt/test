package com.mindalliance.channels.pages.components.entities.analytics;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.util.NameRange;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.pages.components.NameRangePanel;
import com.mindalliance.channels.pages.components.NameRangeable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Role analytics panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/6/12
 * Time: 11:34 AM
 */
public class RoleAnalyticsPanel extends AbstractUpdatablePanel implements NameRangeable, Filterable {

    /**
     * Indexing choice.
     */
    private static final String ACTORS = "Agents";
    /**
     * Indexing choice.
     */
    private static final String ORGANIZATIONS = "Organizations";
    /**
     * Indexing choice.
     */
    private static final String LOCATIONS = "Locations";
    /**
     * Indexing choice.
     */
    private static final String ROLES = "Roles";
    /**
     * Maximum number of rows shown in table at a time.
     */
    private static final int MAX_ROWS = 13;
    /**
     * What "column" to index names on.
     */
    private String indexedOn;
    /**
     * Name index panel.
     */
    private NameRangePanel nameRangePanel;
    /**
     * Selected name range.
     */
    private NameRange nameRange;
    /**
     * /**
     * Role employment table.
     */
    private RoleEmploymentTable roleEmploymentTable;
    /**
     * Model objects filtered on (show only where so and so is the actor etc.)
     */
    private List<Identifiable> filters;
    private WebMarkupContainer performersContainer;

    public RoleAnalyticsPanel(
            String id,
            IModel<? extends ModelEntity> model,
            Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    /**
     * {@inheritDoc}
     */
    protected void init() {
        indexedOn = getIndexingChoices().get( 0 );
        nameRange = new NameRange();
        filters = new ArrayList<Identifiable>();
        addRolePerformers();
    }


    private void addRolePerformers() {
        performersContainer = new WebMarkupContainer( "performersContainer" );
        performersContainer.setOutputMarkupId( true );
        addOrReplace( performersContainer );
        addIndexedOnChoice();
        addNameRangePanel();
        addRoleEmploymentTable();
    }


    private void addIndexedOnChoice() {
        DropDownChoice<String> indexedOnChoices = new DropDownChoice<String>(
                "indexed",
                new PropertyModel<String>( this, "indexedOn" ),
                getIndexingChoices() );
        indexedOnChoices.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                nameRange = new NameRange();
                addNameRangePanel();
                addRoleEmploymentTable();
                target.add( nameRangePanel );
                target.add( roleEmploymentTable );
            }
        } );
        performersContainer.add( indexedOnChoices );
    }

    private List<String> getIndexingChoices() {
        if ( getRole().isActual() ) {
            String[] choices = {ACTORS, ORGANIZATIONS, LOCATIONS};
            return Arrays.asList( choices );
        } else {
            String[] choices = {ACTORS, ORGANIZATIONS, ROLES, LOCATIONS};
            return Arrays.asList( choices );
        }
    }

    private void addNameRangePanel() {
        nameRangePanel = new NameRangePanel(
                "nameRanges",
                new PropertyModel<List<String>>( this, "indexedNames" ),
                MAX_ROWS,
                this,
                "All names"
        );
        nameRangePanel.setOutputMarkupId( true );
        performersContainer.addOrReplace( nameRangePanel );
    }

    private void addRoleEmploymentTable() {
        roleEmploymentTable = new RoleEmploymentTable(
                "roleEmployments",
                new PropertyModel<List<Employment>>( this, "employments" ),
                MAX_ROWS
        );
        roleEmploymentTable.setOutputMarkupId( true );
        performersContainer.addOrReplace( roleEmploymentTable );
    }

    public String getIndexedOn() {
        return indexedOn;
    }

    public void setIndexedOn( String val ) {
        indexedOn = val;
    }

    /**
     * {@inheritDoc}
     */
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        // Property ignored since no two properties filtered are ambiguous on type.
        if ( isFiltered( identifiable, property ) ) {
            filters.remove( identifiable );
        } else {
            filters.add( identifiable );
        }
        addRoleEmploymentTable();
        target.add( roleEmploymentTable );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered( Identifiable identifiable, String property ) {
        return filters.contains( identifiable );
    }

    /**
     * Change the selected name range.
     *
     * @param target an ajax request target
     * @param range  a name range
     */
    public void setNameRange( AjaxRequestTarget target, NameRange range ) {
        nameRange = range;
        nameRangePanel.setSelected( target, range );
        addRoleEmploymentTable();
        target.add( roleEmploymentTable );
    }

    /**
     * Find all names to be indexed.
     *
     * @return a list of strings
     */
    @SuppressWarnings( "unchecked" )
    public List<String> getIndexedNames() {
        List<Employment> employments = getQueryService().findAllEmploymentsForRole( getRole() );
        if ( indexedOn.equals( ACTORS ) ) {
            return (List<String>) CollectionUtils.collect(
                    employments,
                    new Transformer() {
                        public Object transform( Object obj ) {
                            return ( (Employment) obj ).getActor().getName();
                        }
                    } );
        } else if ( indexedOn.equals( ORGANIZATIONS ) ) {
            return (List<String>) CollectionUtils.collect(
                    CollectionUtils.select(
                            employments,
                            new Predicate() {
                                public boolean evaluate( Object obj ) {
                                    return ( (Employment) obj ).getOrganization() != null;
                                }
                            } ),
                    new Transformer() {
                        public Object transform( Object obj ) {
                            return ( (Employment) obj ).getOrganization().getName();
                        }

                    } );
        } else if ( indexedOn.equals( ROLES ) ) {
            return (List<String>) CollectionUtils.collect(
                    CollectionUtils.select(
                            employments,
                            new Predicate() {
                                public boolean evaluate( Object obj ) {
                                    return ( (Employment) obj ).getRole() != null;
                                }
                            } ),
                    new Transformer() {
                        public Object transform( Object obj ) {
                            return ( (Employment) obj ).getRole().getName();
                        }

                    } );
        } else if ( indexedOn.equals( LOCATIONS ) ) {
            return (List<String>) CollectionUtils.collect(
                    CollectionUtils.select(
                            employments,
                            new Predicate() {
                                public boolean evaluate( Object obj ) {
                                    return ( (Employment) obj ).getLocation() != null;
                                }
                            } ),
                    new Transformer() {
                        public Object transform( Object obj ) {
                            return ( (Employment) obj ).getLocation().getName();
                        }

                    } );
        } else {
            throw new IllegalStateException( "Can't index on " + indexedOn );
        }
    }

    /**
     * Find all employments in the plan that are not filtered out and are within selected name range.
     *
     * @return a list of employments.
     */
    @SuppressWarnings( "unchecked" )
    public List<Employment> getEmployments() {
        return (List<Employment>) CollectionUtils.select(
                getQueryService().findAllEmploymentsForRole( getRole() ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return !isFilteredOut( (Employment) obj ) && isInNameRange( (Employment) obj );
                    }
                } );
    }

    private boolean isFilteredOut( Employment employment ) {
        boolean filteredOut = false;
        for ( Identifiable filter : filters ) {
            filteredOut = filteredOut ||
                    ( filter instanceof Actor && employment.getActor() != filter )
                    || ( filter instanceof Organization && employment.getOrganization() != filter )
                    || ( filter instanceof Role && employment.getRole() != filter )
                    || ( filter instanceof Place && employment.getLocation() != filter );
        }
        return filteredOut;
    }

    private boolean isInNameRange( Employment employment ) {
        if ( indexedOn.equals( ACTORS ) ) {
            return nameRange.contains( employment.getActor().getName() );
        } else if ( indexedOn.equals( ORGANIZATIONS ) ) {
            return employment.getOrganization() != null
                    && nameRange.contains( employment.getOrganization().getName() );
        } else if ( indexedOn.equals( ROLES ) ) {
            return employment.getRole() != null
                    && nameRange.contains( employment.getRole().getName() );
        } else if ( indexedOn.equals( LOCATIONS ) ) {
            return ( employment.getLocation() != null
                    && nameRange.contains( employment.getLocation().getName() ) );
        } else {
            throw new IllegalStateException( "Can't index on " + indexedOn );
        }
    }

    private Role getRole() {
        return (Role) getModel().getObject();
    }

    /**
     * Role employment table panel.
     */
    public class RoleEmploymentTable extends AbstractTablePanel<Employment> {

        /**
         * Employment model.
         */
        private IModel<List<Employment>> employmentModel;

        public RoleEmploymentTable(
                String id,
                IModel<List<Employment>> employmentModel,
                int pageSize ) {
            super( id, null, pageSize, null );
            this.employmentModel = employmentModel;
            init();
        }

        @SuppressWarnings( "unchecked" )
        private void init() {
            final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeFilterableLinkColumn(
                    "Agent",
                    "actor",
                    "actor.normalizedName",
                    EMPTY,
                    RoleAnalyticsPanel.this ) );
            columns.add( makeFilterableLinkColumn(
                    "Organization",
                    "organization",
                    "organization.name",
                    EMPTY,
                    RoleAnalyticsPanel.this ) );
            if ( getRole().isType() ) {
                columns.add( makeFilterableLinkColumn(
                        "Role",
                        "role",
                        "role.name",
                        EMPTY,
                        RoleAnalyticsPanel.this ) );
            }
            columns.add( makeFilterableLinkColumn(
                    "Location",
                    "organization.location",
                    "organization.location.name",
                    EMPTY,
                    RoleAnalyticsPanel.this ) );
            // provider and table
            add( new AjaxFallbackDefaultDataTable(
                    "employments",
                    columns,
                    new SortableBeanProvider<Employment>(
                            employmentModel.getObject(),
                            "actor.lastName" ),
                    getPageSize() ) );
        }
    }

}
