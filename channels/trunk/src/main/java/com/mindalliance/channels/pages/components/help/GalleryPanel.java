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
        // Visualizations in Channels Planner
        List<GalleryGroup> planner_groups = new ArrayList<GalleryGroup>();
        planner_groups.add( new GalleryGroup( "Domain collaboration plans" )
                .add( "segmentMap", "Segment map", "The segment map shows all segments that compose a collaboration plan. A segment is a plan module dealing with one scenario. The segment map shows how segments are inter-connected." )
                .add( "flowMap", "Info flow map", "An information flow map shows how tasks in the segment of a plan are connected by information flows, and how they relate to events and goals (risk to be mitigated and gains to be made)." )
                .add( "checklistFlow", "Checklist process flow", "The steps by which a task is to be executed - its checklist- can be organized as a process, with prerequisite steps and preconditions. The checklist process flow diagram illustrates the process logic." )
                .add( "checklistMap", "Checklist flow map", "A checklist flow map displays tasks assigned to an agent or organization in focus, and it shows how these assigned tasks are connected via information sharing commitments with tasks assigned to others." )
                .add( "taxonomy", "Taxonomy", "Taxonomy diagrams show how concepts used in the definition of a plan (roles, organization types etc.) are used to categorize one another." )
                .add( "failureImpact", "Failure impacts", "A failure impact diagram shows how the failure of a particular task or information flow can cause a cascade of failures in dependent tasks, ultimately jeopardizing the planned achievement of goals." )
                .add( "dissemination", "Info dissemination", "An info dissemination diagram shows how essential elements of information would be propagated from or to a given task according to plan. Untimely propagation is highlighted." )
                .add( "network", "Collaboration network", "A collaboration network summarizes how organizations, roles or agents would, according to a plan, be inter-connected by info sharing commitments." )
                .add( "issuesReport", "Issues report", "The issues report summarizes, categorizes and prioritizes all the issues in a plan that were detected by Channels or were reported by planners.")
        );
        planner_groups.add( new GalleryGroup( "Plan communities" )
                .add( "requirements", "Collaboration requirements", "Plan communities can define requirements for how member organizations ought to share information. The diagram shows to what extent these  requirements would be satisfied should organizations follow the plan and carry out their checklists." )
                .add( "checklistsReport", "Checklists", "Each participant in a plan community has access to checklists generated from the plan on the basis of the participant's current roles and responsibilities in member organizations. A checklist describes what triggers a task assignable to the participant, the steps, including the sharing of information, by which the task is to be carried out, and it gives the contact information of all involved with this task.")
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
