package com.mindalliance.channels.pages.components.plan.floating;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.GeoLocatable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractFloatingMultiAspectPanel;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.plan.ModelBibliographyPanel;
import com.mindalliance.channels.pages.components.plan.ModelIndexPanel;
import com.mindalliance.channels.pages.components.plan.ModelTagsPanel;
import com.mindalliance.channels.pages.components.plan.ModelTypologiesPanel;
import com.mindalliance.channels.pages.components.plan.ModelWhosWhoPanel;
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
public class ModelSearchingFloatingPanel extends AbstractFloatingMultiAspectPanel {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ModelSearchingFloatingPanel.class );


    public static final String INDEX = "Index";
    public static final String TAGS = "Tags";
    public static final String TAXONOMIES = "Taxonomies";
    public static final String WHOSWHO = "Who's who";
    public static final String ATTACHMENTS = "All attachments";

    private ModelIndexPanel modelIndexPanel;
    private ModelTagsPanel modelTagsPanel;
    private ModelTypologiesPanel modelTypologiesPanel;
    private ModelBibliographyPanel modelBibliographyPanel;
    private ModelWhosWhoPanel modelWhosWhoPanel;

    /**
     * Aspects.
     */
    private static final String[] ASPECTS = {INDEX, TAGS, TAXONOMIES, WHOSWHO, ATTACHMENTS};


    public ModelSearchingFloatingPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
    }

    public ModelSearchingFloatingPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions, String aspect ) {
        super( id, model, expansions, aspect );
    }

    public ModelSearchingFloatingPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions, String aspect, Change change ) {
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
        return Channels.MODEL_SEARCHING;
    }

    @Override
    protected Component makeAspectPanel( String aspect, Change change ) {
        if ( aspect.equals( INDEX ) ) {
            return getModelIndexPanel();
        } else if ( aspect.equals( TAGS ))  {
            return getModelTagsPanel();
        } else if ( aspect.equals( TAXONOMIES ) ) {
            return getModelTypologiesPanel();
        } else if ( aspect.equals( WHOSWHO ) ) {
            return getModelWhosWhoPanel();
        } else if ( aspect.equals( ATTACHMENTS ) ) {
            return getModelBibliographyPanel();
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
        Change change = new Change( Change.Type.Collapsed, Channels.MODEL_SEARCHING );
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

    private ModelBibliographyPanel getModelBibliographyPanel() {
        if ( modelBibliographyPanel == null ) {
            modelBibliographyPanel = new ModelBibliographyPanel( "aspect" );
        }
        return modelBibliographyPanel;
    }

    private ModelIndexPanel getModelIndexPanel() {
        if ( modelIndexPanel == null ) {
            modelIndexPanel = new ModelIndexPanel( "aspect", getExpansions()  );
        }
        return modelIndexPanel;
    }

    private ModelTagsPanel getModelTagsPanel() {
        if ( modelTagsPanel == null ) {
            modelTagsPanel = new ModelTagsPanel( "aspect" );
        }
        return modelTagsPanel;
    }

    private ModelTypologiesPanel getModelTypologiesPanel() {
        if ( modelTypologiesPanel == null ) {
            modelTypologiesPanel = new ModelTypologiesPanel( "aspect" );
        }
        return modelTypologiesPanel;
    }

    private ModelWhosWhoPanel getModelWhosWhoPanel() {
        if ( modelWhosWhoPanel == null ) {
           modelWhosWhoPanel = new ModelWhosWhoPanel( "aspect" );
        }
        return modelWhosWhoPanel;
    }
}
