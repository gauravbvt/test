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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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


    private static List<Group> groups;

    static {
        groups = new ArrayList<Group>();
        groups.add( new Group( "Planning visualizations" )
                .add( "segmentMap", "All segments making up a collaboration plan and how they are related" )
                .add( "flowMap", "The tasks in the segment of a plan and how they are connected by information flows" )
                .add( "checklistFlow", "The process flow for the execution of a task, as defined by its checklist" )
                .add( "checklistMap", "How assigned tasks are inter-connected by information flows with other assigned tasks" )
                .add( "taxonomy", "How a plan's vocabulary (roles, organization types etc.) form taxonomies" )
        );
        groups.add( new Group( "Analytical visualizations" )
                .add( "failureImpact", "The cascade of failures from a hypothetically failing task or information flow" )
                .add( "dissemination", "Planned dissemination of essential elements of information to a task or from a task" )
                .add( "network", "Information sharing commitments connecting organizations, roles or agents" )
                .add( "requirements", "How collaboration requirements are satisfied by organizations in a community" )
        );
    }

    public GalleryPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addVisualizations();
    }

    private void addVisualizations() {
        ListView<Group> groupListView = new ListView<Group>(
                "groups",
                groups
        ) {
            @Override
            protected void populateItem( ListItem<Group> item ) {
                Group group = item.getModelObject();
                item.add( new Label( "groupName", group.getName() ) );
                ListView<Visualization> visualizationListView = new ListView<Visualization>(
                        "visualizations",
                        group.getVisualizations()
                ) {
                    @Override
                    protected void populateItem( ListItem<Visualization> item ) {
                        Visualization viz = item.getModelObject();
                        FancyBoxWebMarkupContainer box = new FancyBoxWebMarkupContainer(
                                "visualization",
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
                        item.add( box );
                    }
                };
                item.add( visualizationListView );
            }
        };
        add( groupListView );
    }

    private static class Group implements Serializable {

        private String name;
        private List<Visualization> visualizations;

        private Group( String name ) {
            this.name = name;
            visualizations = new ArrayList<Visualization>();
        }

        private String getName() {
            return name;
        }

        private List<Visualization> getVisualizations() {
            return visualizations;
        }

        private Group add( String image, String title ) {
            visualizations.add( new Visualization( image, title ) );
            return this;
        }
    }

    private static class Visualization implements Serializable {

        private String title;
        private String image;

        private Visualization( String image, String title ) {
            this.image = image;
            this.title = title;
        }

        private String getImage() {
            return image;
        }

        private String getTitle() {
            return title;
        }

    }
}
