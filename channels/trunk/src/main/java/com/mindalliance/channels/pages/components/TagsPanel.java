package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.Command;
import com.mindalliance.channels.core.command.commands.UpdateModelObject;
import com.mindalliance.channels.core.command.commands.UpdateSegmentObject;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.model.Tag;
import com.mindalliance.channels.core.model.Taggable;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;
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
public class TagsPanel extends AbstractCommandablePanel implements TabIndexable {

    private AutoCompleteTextField<String> tagsField;
    private String tagsProperty;
    private TabIndexer tabIndexer;


    public TagsPanel( String id, IModel<? extends Taggable> iModel, String tagsProperty ) {
        super( id, iModel );
        this.tagsProperty = tagsProperty;
        init();

    }

    public TagsPanel( String id, IModel<? extends Taggable> iModel ) {
        this( id, iModel, "rawTags" );
    }

    public void initTabIndexing( TabIndexer tabIndexer ) {
        this.tabIndexer = tabIndexer;
        if ( tabIndexer != null )
            tabIndexer.giveTabIndexTo( tagsField );
    }

    private void init() {
        tagsField = new AutoCompleteTextField<String>(
                "tags",
                new PropertyModel<String>( this, "tagsString" ),
                getAutoCompleteSettings() ) {
            @Override
            protected Iterator<String> getChoices( String input ) {
                return computeTagStringChoices( input );
            }
        };
        tagsField.setOutputMarkupId( true );
        tagsField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                target.add( tagsField );
                update( target, new Change( Change.Type.Updated, getModel().getObject(), tagsProperty ) );
            }
        } );
        tagsField.setEnabled( isLockedByUser( getTaggable() ) );
        addInputHint( tagsField, "Comma-separated tags" );
        addTipTitle( tagsField, "Compose tags with ':' and separate them with ','. Example: 'esf:transportation, law enforcement*, practiced'. Tags ending in '*' will be displayed." );
        add( tagsField );
    }

    private Iterator<String> computeTagStringChoices( String input ) {
        List<String> choices = new ArrayList<String>();
        List<Tag> tags = Tag.tagsFromString( input );
        if ( !tags.isEmpty() ) {
            Tag lastTag = tags.get( tags.size() - 1 );
            Set<Tag> lastTagMatches = new HashSet<Tag>();
            QueryService queryService = getQueryService();
            List<Tag> domain = getQueryService().findTagDomain();
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

    @SuppressWarnings("unchecked")
    public String getTagsString() {
        return Tag.tagsToString( (List<Tag>) ChannelsUtils.getProperty( getTaggable(), tagsProperty, null ) );
    }

    public void setTagsString( String s ) {
        String val = s == null ? "" : s;
        Identifiable mo = getModel().getObject();
        Command updateCommand = mo instanceof SegmentObject
                ? new UpdateSegmentObject( getUser().getUsername(), mo, tagsProperty, Tag.tagsFromString( val ) )
                : new UpdateModelObject( getUser().getUsername(), mo, tagsProperty, Tag.tagsFromString( val ) );
        doCommand( updateCommand );
    }

    private Taggable getTaggable() {
        return (Taggable) getModel().getObject();
    }


}
