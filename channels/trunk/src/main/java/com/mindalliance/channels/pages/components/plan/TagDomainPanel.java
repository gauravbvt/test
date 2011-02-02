package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Tag;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.NameRangePanel;
import com.mindalliance.channels.pages.components.NameRangeable;
import com.mindalliance.channels.util.NameRange;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tag domain panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/1/11
 * Time: 11:14 AM
 */
public class TagDomainPanel extends AbstractUpdatablePanel implements NameRangeable {

    /**
     * Maximum number of rows shown in table at a time.
     */
    private static final int MAX_INDEX_ROWS = 23;
    /**
     * Length at which a name is abbreviated.
     */
    private static final int MAX_NAME_LENGTH = 30;

    /**
     * Name index panel.
     */
    private WebMarkupContainer tagsContainer;
    /**
     * Selected name range.
     */
    private NameRange nameRange = new NameRange();
    /**
     * Name index panel.
     */
    private NameRangePanel nameRangePanel;

    private List<Tag> filteredTags;


    public TagDomainPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addNameRangePanel();
        addTags();
    }

    private void addNameRangePanel() {
        nameRangePanel = new NameRangePanel(
                "nameRanges",
                new PropertyModel<List<String>>( this, "tagDomainNames" ),
                MAX_INDEX_ROWS * 3,
                this,
                "All"
        );
        nameRangePanel.setOutputMarkupId( true );
        addOrReplace( nameRangePanel );
    }

    @SuppressWarnings( "unchecked" )
    public List<String> getTagDomainNames() {
        return (List<String>) CollectionUtils.collect(
                getTagDomain(),
                TransformerUtils.invokerTransformer( "getName" ) );
    }

    private List<Tag> getTagDomain() {
        return getQueryService().findTagDomain();
    }

    private void addTags() {
        tagsContainer = new WebMarkupContainer( "tags-container" );
        tagsContainer.setOutputMarkupId( true );
        addOrReplace( tagsContainer );
        ListView<Tag> indices1 = new ListView<Tag>(
                "tags1",
                getTags1()
        ) {
            protected void populateItem( ListItem<Tag> item ) {
                Tag tag = item.getModelObject();
                item.add( new IndexedTagPanel( "tag", new Model<Tag>( tag ) ) );
            }
        };
        tagsContainer.addOrReplace( indices1 );
        ListView<Tag> indices2 = new ListView<Tag>(
                "tags2",
                getTags2()
        ) {
            protected void populateItem( ListItem<Tag> item ) {
                Tag tag = item.getModelObject();
                item.add( new IndexedTagPanel( "tag", new Model<Tag>( tag ) ) );
            }
        };
        tagsContainer.addOrReplace( indices2 );
        ListView<Tag> indices3 = new ListView<Tag>(
                "tags3",
                getTags3()
        ) {
            protected void populateItem( ListItem<Tag> item ) {
                Tag tag = item.getModelObject();
                item.add( new IndexedTagPanel( "tag", new Model<Tag>( tag ) ) );
            }
        };
        tagsContainer.addOrReplace( indices3 );
    }

    public List<Tag> getTags1() {
        List<Tag> allTags = getFilteredTags();
        int fromIndex = 0;
        int toIndex = getRowCounts()[0];
        return ( toIndex > 0 )
                ? allTags.subList( fromIndex, toIndex )
                : new ArrayList<Tag>();
    }

    public List<Tag> getTags2() {
        List<Tag> allTags = getFilteredTags();
        int fromIndex = getRowCounts()[0];
        int toIndex = getRowCounts()[0] + getRowCounts()[1];
        return ( toIndex > 0 )
                ? allTags.subList( fromIndex, toIndex )
                : new ArrayList<Tag>();
    }

    public List<Tag> getTags3() {
        List<Tag> allTags = getFilteredTags();
        int fromIndex = getRowCounts()[0] + getRowCounts()[1];
        int toIndex = allTags.size();
        return ( toIndex > 0 )
                ? allTags.subList( fromIndex, toIndex )
                : new ArrayList<Tag>();
    }

    @SuppressWarnings( "unchecked" )
    private List<Tag> getFilteredTags() {
        if ( filteredTags == null ) {
            filteredTags = (List<Tag>) CollectionUtils.select(
                    getTagDomain(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            Tag tag = (Tag) object;
                            return nameRange.contains( tag.getName() );
                        }
                    } );
        }
        Collections.sort( filteredTags );
        return filteredTags;
    }

    private int[] getRowCounts() {
        int count = getFilteredTags().size();
        int split = count / 3;
        int[] rowCounts = new int[]{split, split, split};
        count = count % 3;
        if ( count-- > 0 ) rowCounts[0]++;
        if ( count > 0 ) rowCounts[1]++;
        return rowCounts;
    }


    /**
     * {@inheritDoc}
     */
    public void setNameRange( AjaxRequestTarget target, NameRange range ) {
        filteredTags = null;
        nameRange = range;
        nameRangePanel.setSelected( target, range );
        addTags();
        target.addComponent( tagsContainer );
    }

    private class IndexedTagPanel extends AbstractUpdatablePanel {

        private IModel<Tag> tagModel;

        public IndexedTagPanel( String id, IModel<Tag> tagModel ) {
            super( id );
            this.tagModel = tagModel;
            addTagLink();
        }

        private void addTagLink() {
            String name = getAbbreviatedTagName();
            AjaxLink<String> tagLink = new AjaxLink<String>(
                    "tagLink",
                    new Model<String>( name ) ) {
                @Override
                public void onClick( AjaxRequestTarget target ) {
                    Change change = new Change( Change.Type.Selected );
                    change.addQualifier( "tag", getTag() );
                    update( target, change );
                }
            };
            add( tagLink );
            Label tagNameLabel = new Label( "name", name );
            if ( !name.equals( getTag().getName() ) ) {
                tagNameLabel.add( new AttributeModifier(
                        "title",
                        true,
                        new Model<String>( getTag().getName() ) ) );
            }
            tagLink.add( tagNameLabel );
        }

        private String getAbbreviatedTagName() {
            return StringUtils.abbreviate( getTag().getName(), MAX_NAME_LENGTH );
        }

        private Tag getTag() {
            return tagModel.getObject();
        }

    }

}
