package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.command.commands.UpdateSegmentObject;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.model.Tag;
import com.mindalliance.channels.model.Taggable;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Tags panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/27/11
 * Time: 2:41 PM
 */
public class TagsPanel extends AbstractCommandablePanel {

    private AutoCompleteTextField<String> tagsField;

    public TagsPanel( String id, IModel<? extends Taggable> iModel ) {
        super( id, iModel );
        init();
    }

    private void init() {
        tagsField = new AutoCompleteTextField<String>(
                "tags",
                new PropertyModel<String>( this, "tagsString" ) ) {
            @Override
            protected Iterator<String> getChoices( String input ) {
                return computeTagStringChoices( input );
            }
        };
        tagsField.setOutputMarkupId( true );
        tagsField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                target.addComponent( tagsField );
                update( target, new Change( Change.Type.Updated, getModel().getObject(), "tags" ) );
            }
        } );
        tagsField.setEnabled( isLockedByUser( getTaggable() ) );
        add( tagsField );
    }

    private Iterator<String> computeTagStringChoices( String input ) {
        List<String> choices = new ArrayList<String>();
        List<Tag> tags = Tag.tagsFromString( input );
        if ( !tags.isEmpty() ) {
            Tag lastTag = tags.get( tags.size() - 1 );
            Set<Tag> lastTagMatches = new HashSet<Tag>();
            QueryService queryService = getQueryService();
            List<Tag> domain =  getQueryService().findTagDomain();
            for ( Tag t : domain ) {
                if ( !tags.contains( t ) ) {
                    if ( queryService.likelyRelated( lastTag, t ) )
                        lastTagMatches.add( t );
                }
            }
            List<Tag> tagsButLast = tags.size() <= 1
                    ? new ArrayList<Tag>()
                    : tags.subList( 0, tags.size() - 1 );
            for ( Tag match : lastTagMatches ) {
                String butLastString = Tag.tagsToString( tagsButLast );
                String choice = butLastString
                        + ( butLastString.isEmpty() ? "" : ( Tag.SEPARATOR + " " ) )
                        + match.getName();
                choices.add( choice );
            }
            Collections.sort( choices );
        }
        return choices.iterator();
    }

    public String getTagsString() {
        return Tag.tagsToString( getTaggable().getTags() );
    }

    public void setTagsString( String s ) {
        String val = s == null ? "" : s;
        Identifiable mo = getModel().getObject();
        Command updateCommand = mo instanceof SegmentObject
                ? new UpdateSegmentObject( mo, "tags", Tag.tagsFromString( val ) )
                : new UpdatePlanObject( mo, "tags", Tag.tagsFromString( val ) );
        doCommand( updateCommand );
    }

    private Taggable getTaggable() {
        return (Taggable) getModel().getObject();
    }


}
