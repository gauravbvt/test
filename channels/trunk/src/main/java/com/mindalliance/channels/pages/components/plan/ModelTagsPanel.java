package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Tag;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/1/11
 * Time: 10:39 AM
 */
public class ModelTagsPanel extends AbstractCommandablePanel implements Guidable {

    private Tag selectedTag;
    private WebMarkupContainer tagIndexContainer;

    public ModelTagsPanel( String id ) {
        super( id );
        init();
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }

    @Override
    public String getHelpSectionId() {
        return "searching";
    }

    @Override
    public String getHelpTopicId() {
        return "tags";
    }


    private void init() {
        addTagDomainPanel();
        addTaggedIndex();
    }

    private void addTagDomainPanel() {
        TagDomainPanel tagDomainPanel = new TagDomainPanel( "tags" );
        addOrReplace( tagDomainPanel );
    }

    private void addTaggedIndex() {
        tagIndexContainer = new WebMarkupContainer( "taggedContainer" );
        tagIndexContainer.setOutputMarkupId( true );
        makeVisible( tagIndexContainer, selectedTag != null && !selectedTag.isEmpty() );
        addOrReplace( tagIndexContainer );
        Label tagLabel = new Label(
                "taggedWith",
                selectedTag == null
                        ? ""
                        : ("Tagged with " + selectedTag.getName() ) );
        tagIndexContainer.add( tagLabel );
        TagIndexPanel tagIndexPanel = new TagIndexPanel(
                "tagged",
                new PropertyModel<Tag>( this, "selectedTag" ) );
        tagIndexContainer.add( tagIndexPanel );
    }

    public Tag getSelectedTag() {
        return selectedTag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isSelected() && change.getQualifier( "tag" ) != null ) {
            selectedTag =(Tag)change.getQualifier( "tag" );
            addTaggedIndex();
            target.add( tagIndexContainer );
        } else {
            super.updateWith( target, change, updated );
        }
    }

}
