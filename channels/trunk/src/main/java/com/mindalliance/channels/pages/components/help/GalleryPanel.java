package com.mindalliance.channels.pages.components.help;

import com.google.code.jqwicket.ui.fancybox.FancyBoxOptions;
import com.google.code.jqwicket.ui.fancybox.FancyBoxWebMarkupContainer;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.resource.CssResourceReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Channels gallery panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/15/13
 * Time: 10:54 AM
 */
public class GalleryPanel extends AbstractUpdatablePanel {

    private static String VIZ_PATH = "viz/";
    private static String THUMBNAIL_SUFFIX = "_s.png";
    private static String FULL_SIZE_SUFFIX = "_b.png";

    public static final CssResourceReference GALLERY_CSS_RESOURCE = new CssResourceReference(
            GalleryPanel.class, "res/gallery.css" );


    private static Map<String,List<GalleryGroup>> GROUPS;

    // todo - get gallery content from XML file
    static {
        GROUPS = new HashMap<String, List<GalleryGroup>>();
        // Visualizations for planners
        List<GalleryGroup> planner_groups = new ArrayList<GalleryGroup>();
        planner_groups.add( new GalleryGroup( "Planning visualizations" )
                .add( "segmentMap", "Segment map", "All segments making up a collaboration plan and how they are related" )
                .add( "flowMap", "Info flow map", "The tasks in the segment of a plan and how they are connected by information flows" )
                .add( "checklistFlow", "Checklist process flow", "The process flow for the execution of a task, as defined by its checklist" )
                .add( "checklistMap", "Checklist flow map", "How assigned tasks are inter-connected by information flows with other assigned tasks" )
                .add( "taxonomy", "Taxonomy", "How a plan's vocabulary (roles, organization types etc.) form taxonomies" )
        );
        planner_groups.add( new GalleryGroup( "Analytical visualizations" )
                .add( "failureImpact", "Failure impacts", "The cascade of failures from a hypothetically failing task or information flow" )
                .add( "dissemination", "Info dissemination", "Planned dissemination of essential elements of information to a task or from a task" )
                .add( "network", "Collaboration network", "Information sharing commitments connecting organizations, roles or agents" )
                // .add( "requirements", "Collaboration requirements", "How collaboration requirements are satisfied by organizations in a community" )

        );
        GROUPS.put( "planner", planner_groups );
        // Visualizations for others
        // TBD
    }

    static List<GalleryGroup> getGalleryGroups( String name ) {
        return GROUPS.get( name );
    }

    private List<GalleryGroup> galleryGroups;

    public GalleryPanel( String id, String name ) {
        super( id );
        this.galleryGroups = GROUPS.get( name );
        init();
    }

    private void init() {
        addGalleryGroups();
    }

    private void addGalleryGroups() {
        ListView<GalleryGroup> groupListView = new ListView<GalleryGroup>(
                "groups",
                galleryGroups
        ) {
            @Override
            protected void populateItem( ListItem<GalleryGroup> item ) {
                GalleryGroup galleryGroup = item.getModelObject();
                item.add( new Label( "groupName", galleryGroup.getName() ) );
                ListView<GalleryItem> visualizationListView = new ListView<GalleryItem>(
                        "groupItems",
                        galleryGroup.getGalleryItems()
                ) {
                    @Override
                    protected void populateItem( ListItem<GalleryItem> item ) {
                        GalleryItem viz = item.getModelObject();
                        FancyBoxWebMarkupContainer box = new FancyBoxWebMarkupContainer(
                                "groupItem",
                                new FancyBoxOptions().overlayShow( false )
                                        .transitionIn( "elastic" )
                                        .transitionOut( "elastic" )
                                        .titlePosition( "inside" )
                                        .setCssResourceReferences( GALLERY_CSS_RESOURCE )
                        );
                        box.add( new AttributeModifier( "href", VIZ_PATH + viz.getImage() + FULL_SIZE_SUFFIX ) );
                        box.add( new AttributeModifier( "title", viz.getTitle() ) );
                        WebMarkupContainer img = new WebMarkupContainer( "thumbnail" );
                        img.add( new AttributeModifier( "src", VIZ_PATH + viz.getImage() + THUMBNAIL_SUFFIX ) );
                        img.add( new AttributeModifier( "alt", viz.getImage() ) );
                        box.add( img );
                        box.add( new Label( "caption", viz.getCaption() ) );
                        item.add( box );
                    }
                };
                item.add( visualizationListView );
            }
        };
        add( groupListView );
    }

}
