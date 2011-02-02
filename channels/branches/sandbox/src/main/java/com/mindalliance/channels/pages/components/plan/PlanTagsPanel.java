package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Tag;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
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
public class PlanTagsPanel extends AbstractCommandablePanel {

    private Tag selectedTag;
    private WebMarkupContainer tagIndexContainer;

    public PlanTagsPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addTagDomainPanel();
        addTaggedIndex();
    }

    private void addTagDomainPanel() {
        TagDomainPanel tagDomainPanel = new TagDomainPanel( "tags" );
        add( tagDomainPanel );
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
            target.addComponent( tagIndexContainer );
        } else {
            super.updateWith( target, change, updated );
        }
    }

}
