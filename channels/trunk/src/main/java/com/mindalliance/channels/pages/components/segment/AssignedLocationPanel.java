package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateSegmentObject;
import com.mindalliance.channels.core.model.AssignedLocation;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.Subject;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.TabIndexable;
import com.mindalliance.channels.pages.components.TabIndexer;
import com.mindalliance.channels.pages.components.entities.EntityReferencePanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 12/19/11
 * Time: 1:59 PM
 */
public class AssignedLocationPanel extends AbstractCommandablePanel implements TabIndexable {

    private WebMarkupContainer subjectContainer;
    private Component placeReferencePanel;
    private WebMarkupContainer eoiContainer;
    private TabIndexer tabIndexer;
    private DropDownChoice<AssignedLocation.Kind> kindChoice;

    public AssignedLocationPanel( String id, PropertyModel<Part> partModel ) {
        super( id, partModel );
        init();
    }


    public void initTabIndexing( TabIndexer tabIndexer ) {
        this.tabIndexer = tabIndexer;
        if ( tabIndexer != null ) {
            tabIndexer.giveTabIndexTo( kindChoice );
            initTabIndices( placeReferencePanel, tabIndexer ); // do it right away to at least reserve the index for the component by id
        }
    }

    private void init() {
        addKindChoice();
        addNamedPlace();
        addSubject();
        adjustFields();
    }

    private void adjustFields() {
        placeReferencePanel.setEnabled( isLockedByUser( getPart() ) );
        makeVisible( placeReferencePanel, getKind() == AssignedLocation.Kind.NamedPlace );
        makeVisible( subjectContainer, getKind() == AssignedLocation.Kind.CommunicatedPlace );
        makeVisible( eoiContainer, getKind() == AssignedLocation.Kind.CommunicatedPlace
                && getAssignedLocation().hasInfo() );
    }

    private void addKindChoice() {
        kindChoice = new DropDownChoice<AssignedLocation.Kind>(
                "kind",
                new PropertyModel<AssignedLocation.Kind>( this, "kind" ),
                Arrays.asList( AssignedLocation.Kind.values() ),
                new IChoiceRenderer<AssignedLocation.Kind>() {
                    @Override
                    public Object getDisplayValue( AssignedLocation.Kind kind ) {
                        return kind.getLabel();
                    }

                    @Override
                    public String getIdValue( AssignedLocation.Kind object, int index ) {
                        return Integer.toString( index );
                    }
                }
        );
        kindChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addNamedPlace();
                adjustFields( target );
                update( target,
                        new Change(
                                Change.Type.Updated,
                                getPart(),
                                "location"
                        )
                );
            }
        } );
        kindChoice.setEnabled( isLockedByUser( getPart() ) );
        add( kindChoice );
        applyTabIndexTo( kindChoice, tabIndexer );
    }

    private void addNamedPlace() {
        if ( getAssignedLocation().getKind() == AssignedLocation.Kind.NamedPlace ) {
            final List<String> choices = getQueryService().findAllEntityNames( Place.class );
            placeReferencePanel = new EntityReferencePanel<Place>(
                    "named",
                    new Model<Part>( getPart() ),
                    choices,
                    "location.namedPlace",
                    Place.class
            );
            initTabIndices( placeReferencePanel, tabIndexer ); // generate the tabIndex dom attribute if possible

        } else {
            placeReferencePanel = new Label( "named", "" );
            placeReferencePanel.setOutputMarkupId( true );
        }
        addOrReplace( placeReferencePanel );
    }

    private void addSubject() {
        subjectContainer = new WebMarkupContainer( "subject" );
        subjectContainer.setOutputMarkupId( true );
        addSubjectInfo();
        addSubjectEoi();
        addOrReplace( subjectContainer );
    }

    private void addSubjectInfo() {
        final List<String> infoChoices = findInfoChoices();
        AutoCompleteTextField<String> infoField = new AutoCompleteTextField<String>(
                "info",
                new PropertyModel<String>( this, "subjectInfo" ),
                getAutoCompleteSettings()
        ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : infoChoices ) {
                    if ( getQueryService().likelyRelated( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        infoField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target,
                        new Change(
                                Change.Type.Updated,
                                getPart(),
                                "location"
                        )
                );
                addSubjectEoi();
                adjustFields( target );
            }
        } );
        subjectContainer.add( infoField );
    }

    private void addSubjectEoi() {
        eoiContainer = new WebMarkupContainer( "eoiContainer" );
        eoiContainer.setOutputMarkupId( true );
        subjectContainer.addOrReplace( eoiContainer );
        final List<String> eoiChoices = findEoiChoices();
        AutoCompleteTextField<String> eoiField = new AutoCompleteTextField<String>(
                "eoi",
                new PropertyModel<String>( this, "subjectContent" ),
                getAutoCompleteSettings()
        ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : eoiChoices ) {
                    if ( getQueryService().likelyRelated( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        eoiField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target,
                        new Change(
                                Change.Type.Updated,
                                getPart(),
                                "location"
                        )
                );
            }
        } );
        eoiContainer.add( eoiField );
    }

    private List<String> findInfoChoices() {
        Set<String> choices = new HashSet<String>();
        String eoi = getEoi();
        for ( Flow flow : getPart().getAllSharingReceives() ) {
            if ( eoi.isEmpty() || flow.hasEoiNamed( eoi ) )
                choices.add( flow.getName() );
        }
        for ( Flow flow : getPart().getNeeds() ) {
            if ( eoi.isEmpty() || flow.hasEoiNamed( eoi ) )
                choices.add( flow.getName() );
        }
        List<String> results = new ArrayList<String>( choices );
        Collections.sort( results );
        return results;
    }

    private List<String> findEoiChoices() {
        Set<String> choices = new HashSet<String>();
        String info = getInfo();
        if ( !info.isEmpty() ) {
            for ( Flow flow : getPart().getAllSharingReceives() ) {
                if ( Matcher.same( info, flow.getName() ) )
                    for ( ElementOfInformation eoi : flow.getEffectiveEois() ) {
                        choices.add( eoi.getContent() );
                    }
            }
            for ( Flow flow : getPart().getNeeds() ) {
                if ( Matcher.same( info, flow.getName() ) )
                    for ( ElementOfInformation eoi : flow.getEffectiveEois() ) {
                        choices.add( eoi.getContent() );
                    }
            }
        }
        List<String> results = new ArrayList<String>( choices );
        Collections.sort( results );
        return results;
    }

    private String getInfo() {
        return getAssignedLocation().isCommunicated()
                ? getAssignedLocation().getSubject().getInfo()
                : "";
    }

    private String getEoi() {
        return getAssignedLocation().isCommunicated()
                ? getAssignedLocation().getSubject().getContent()
                : "";
    }


    private void adjustFields( AjaxRequestTarget target ) {
        adjustFields();
        target.add( placeReferencePanel );
        target.add( subjectContainer );
        target.add( eoiContainer );
    }

    public AssignedLocation.Kind getKind() {
        return getAssignedLocation().getKind();
    }

    public void setKind( AssignedLocation.Kind kind ) {
        Place oldPlace = getPart().getKnownLocation();
        if ( getAssignedLocation().isNamed() ) {
            doCommand( new UpdateSegmentObject( getUsername(), getPart(), "location.namedPlace", null ) );
        }
        if ( getAssignedLocation().isCommunicated() ) {
            doCommand( new UpdateSegmentObject( getUsername(), getPart(), "location.subject", null ) );
        }
        doCommand( new UpdateSegmentObject( getUsername(), getPart(), "location.kind", kind ) );
        if ( oldPlace != null )
            getCommander().cleanup( Place.class, oldPlace.getName() );
    }

    public String getSubjectInfo() {
        Subject subject = getAssignedLocation().getSubject();
        return subject != null
                ? subject.getInfo()
                : "";
    }

    public void setSubjectInfo( String val ) {
        if ( val != null && !val.isEmpty() ) {
            doCommand( new UpdateSegmentObject( getUsername(), getPart(), "location.subject.info", val ) );
        }
    }

    public String getSubjectContent() {
        Subject subject = getAssignedLocation().getSubject();
        return subject != null
                ? subject.getContent()
                : "";
    }

    public void setSubjectContent( String val ) {
        if ( val != null && !val.isEmpty() ) {
            doCommand( new UpdateSegmentObject( getUsername(), getPart(), "location.subject.content", val ) );
        }
    }

    private AssignedLocation getAssignedLocation() {
        return getPart().getLocation();
    }

    private Part getPart() {
        return (Part) getModel().getObject();
    }
}
