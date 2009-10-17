package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Plan;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Top-level playbook browsing. Page with people index.
 */
public class MainPage extends PlaybookPage {

    //----------------------------------------------
    public MainPage() {
        init( getQueryService(), getPlan() );
    }

    private void init( QueryService service, Plan plan ) {

        List<Actor> actors = new ArrayList<Actor>( service.listActualEntities( Actor.class ) );
        Collections.sort( actors );

        add( new Label( "title", plan.getName() ),
             new Label( "plan-name", plan.getName() ),
             new Label( "description", plan.getDescription() ),

             new ListView<Actor>( "participants", actors ) {
                @Override
                protected void populateItem( ListItem<Actor> item ) {
                    Actor actor = item.getModelObject();
                    String desc = actor.getDescription();
                    desc = desc == null || desc.trim().isEmpty() ? "" : ", " + desc;
                    item.add( new BookmarkablePageLink<ActorPlaybook>( "participant",
                                                                       TaskPlaybook.class )
                            .setParameter( ACTOR_PARM, actor.getId() )
                            .add( new Label( "name", actor.getName() ).setRenderBodyOnly( true ) ),
                              new Label( "actor-desc", desc ).setRenderBodyOnly( true ) );
                }
            } );
    }
}
