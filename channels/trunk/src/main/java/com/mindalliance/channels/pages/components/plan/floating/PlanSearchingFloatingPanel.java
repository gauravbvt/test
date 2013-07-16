package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.GeoLocatable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractFloatingMultiAspectPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.plan.PlanBibliographyPanel;
import com.mindalliance.channels.pages.components.plan.PlanIndexPanel;
import com.mindalliance.channels.pages.components.plan.PlanTagsPanel;
import com.mindalliance.channels.pages.components.plan.PlanTypologiesPanel;
import com.mindalliance.channels.pages.components.plan.PlanWhosWhoPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Plan Index Floating Panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 7:17 PM
 */
public class PlanSearchingFloatingPanel extends AbstractFloatingMultiAspectPanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( PlanSearchingFloatingPanel.class );


    public static final String INDEX = "Index";
    public static final String TAGS = "Tags";
    public static final String TAXONOMIES = "Taxonomies";
    public static final String WHOSWHO = "Who's who";
    public static final String ATTACHMENTS = "All attachments";

    private PlanIndexPanel planIndexPanel;
    private PlanTagsPanel planTagsPanel;
    private PlanTypologiesPanel planTypologiesPanel;
    private PlanBibliographyPanel planBibliographyPanel;
    private PlanWhosWhoPanel planWhosWhoPanel;

    /**
     * Aspects.
     */
    private static final String[] ASPECTS = {INDEX, TAGS, TAXONOMIES, WHOSWHO, ATTACHMENTS};


    public PlanSearchingFloatingPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }

    public PlanSearchingFloatingPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions, String aspect ) {
        super( id, model, expansions, aspect );
    }

    public PlanSearchingFloatingPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions, String aspect, Change change ) {
        super( id, model, expansions, aspect, change );
    }

    @Override
    protected List<String> getActionableAspects() {
        return new ArrayList<String>(  );
    }

    @Override
    protected String getCssClass() {
        return "searching";
    }

    @Override
    protected MenuPanel makeActionMenu( String menuId ) {
        return null;
    }

    @Override

    protected long getTabIdentifiableId() {
        return Channels.PLAN_SEARCHING;
    }

    @Override
    protected Component makeAspectPanel( String aspect, Change change ) {
        if ( aspect.equals( INDEX ) ) {
            return getPlanIndexPanel();
        } else if ( aspect.equals( TAGS ))  {
            return getPlanTagsPanel();
        } else if ( aspect.equals( TAXONOMIES ) ) {
            return getPlanTypologiesPanel();
        } else if ( aspect.equals( WHOSWHO ) ) {
            return getPlanWhosWhoPanel();
        } else if ( aspect.equals( ATTACHMENTS ) ) {
            return getPlanBibliographyPanel();
        } else {
            throw new RuntimeException( "Unknown searching aspect " + aspect );
        }
    }

    @Override
    protected List<String> getAllAspects() {
        return Arrays.asList( ASPECTS );
    }

    @Override
    protected String getMapTitle() {
        return "";
    }

    @Override
    protected List<? extends GeoLocatable> getGeoLocatables() {
        return new ArrayList<GeoLocatable>(  );
    }

    @Override
    protected void doClose( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.Collapsed, Channels.PLAN_SEARCHING );
        update( target, change );
    }

    @Override
    protected String getTitle() {
        return "Searching";
    }

    @Override
    protected PathIcon getSurveysPathIcon( String id ) {
        return null;
    }

    @Override
    protected PathIcon getIssuesPathIcon( String id ) {
        return null;
    }

    private PlanBibliographyPanel getPlanBibliographyPanel() {
        if ( planBibliographyPanel == null ) {
            planBibliographyPanel = new PlanBibliographyPanel( "aspect" );
        }
        return planBibliographyPanel;
    }

    private PlanIndexPanel getPlanIndexPanel() {
        if ( planIndexPanel == null ) {
            planIndexPanel = new PlanIndexPanel( "aspect", getExpansions()  );
        }
        return planIndexPanel;
    }

    private PlanTagsPanel getPlanTagsPanel() {
        if ( planTagsPanel == null ) {
            planTagsPanel = new PlanTagsPanel( "aspect" );
        }
        return planTagsPanel;
    }

    private PlanTypologiesPanel getPlanTypologiesPanel() {
        if ( planTypologiesPanel == null ) {
            planTypologiesPanel = new PlanTypologiesPanel( "aspect" );
        }
        return planTypologiesPanel;
    }

    private PlanWhosWhoPanel getPlanWhosWhoPanel() {
        if ( planWhosWhoPanel == null ) {
           planWhosWhoPanel = new PlanWhosWhoPanel( "aspect" );
        }
        return planWhosWhoPanel;
    }
}
