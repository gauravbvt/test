package com.mindalliance.channels.pages.playbook;

import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.User;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * A generic playbook page.
 */
public abstract class PlaybookPage extends WebPage {

    /** The name of the agent id parameter. */
    public static final String ACTOR_PARM = "0";

    /** The name of the part id parameter. */
    public static final String PART_PARM = "1";

    /** The current plan. */
    @SpringBean
    private Plan plan;

    /** The logged-in user. */
    @SpringBean
    private User user;

    /** Data access. */
    @SpringBean
    private QueryService queryService;

    /** The actor of this page. */
    private Actor actor;

    /** The part, if available. */
    private Part part;

    //----------------------------------------
    protected PlaybookPage() {
        setStatelessHint( true );
        actor = null;
        part = null;
    }

    protected PlaybookPage( PageParameters parameters ) {
        super( parameters );
        setStatelessHint( true );

        QueryService service = getQueryService();
        actor = getParm( service, parameters, ACTOR_PARM, Actor.class );
        part = getParm( service, parameters, PART_PARM, Part.class );
    }

    public final Plan getPlan() {
        return plan;
    }

    public final QueryService getQueryService() {
        return queryService;
    }

    public final User getUser() {
        return user;
    }

    private static <T extends ModelObject> T getParm(
            QueryService service, PageParameters parameters, String parm, Class<T> parmClass ) {

        T result = null;
        if ( parameters.containsKey( parm ) )
            try {
                result = service.find( parmClass, Long.valueOf( parameters.getString( parm ) ) );

            } catch ( NumberFormatException ignored ) {
                result = null;

            } catch ( NotFoundException ignored ) {
                result = null;
            }

        return result;
    }

    public Actor getActor() {
        return actor;
    }

    public Part getPart() {
        return part;
    }
}
