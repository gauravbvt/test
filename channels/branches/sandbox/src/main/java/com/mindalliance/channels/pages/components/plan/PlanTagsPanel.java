package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.InfoStandard;
import com.mindalliance.channels.model.Tag;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
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
    private WebMarkupContainer infoStandardContainer;
    private WebMarkupContainer tagIndexContainer;

    public PlanTagsPanel( String id ) {
        super( id );
        init();
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }


    private void init() {
        addTagDomainPanel();
        addInfoStandard();
        addTaggedIndex();
    }

    private void addTagDomainPanel() {
        TagDomainPanel tagDomainPanel = new TagDomainPanel( "tags" );
        addOrReplace( tagDomainPanel );
    }

    private void addInfoStandard() {
        infoStandardContainer = new WebMarkupContainer( "infoStandard" );
        infoStandardContainer.setOutputMarkupId( true );
        makeVisible( infoStandardContainer, selectedTag != null && selectedTag.isInfoStandard() );
        addOrReplace( infoStandardContainer );
        Label infoLabelStandard = new Label(
                "infoStandardLabel",
                selectedTag == null
                        ? ""
                        : (selectedTag.getName() + " info standard"));
        infoStandardContainer.add( infoLabelStandard );
        ListView<String> eoiList = new ListView<String>(
                "eois",
                getStandardEois()
        ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                item.add( new Label("eoi", item.getModelObject()));
            }
        };
        infoStandardContainer.add( eoiList );
    }

    private List<String> getStandardEois() {
        List<String> eois = new ArrayList<String>();
        if ( selectedTag != null && selectedTag.isInfoStandard() ) {
            InfoStandard infoStandard = (InfoStandard)selectedTag;
            for ( String eoiName : infoStandard.getEoiNames() ) {
                StringBuilder sb = new StringBuilder(  );
                sb.append( eoiName );
                String desc = infoStandard.getEoiDescription( eoiName );
                if ( !desc.isEmpty() ) {
                    sb.append( " (");
                    sb.append( desc );
                    sb.append( ")");
                }
                eois.add( sb.toString() );
            }
        }
        return eois;
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
            addInfoStandard();
            target.addComponent( infoStandardContainer );
            addTaggedIndex();
            target.addComponent( tagIndexContainer );
        } else {
            super.updateWith( target, change, updated );
        }
    }

}
