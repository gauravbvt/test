package com.mindalliance.channels.c10n.accessors
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.c10n.util.Continuation
import com.mindalliance.channels.c10n.aspects.ContinuationAspect
import com.mindalliance.channels.c10n.aspects.IAspectContinuation
import com.mindalliance.channels.c10n.util.IContinuation
import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.nk.accessors.AbstractDataAccessor

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Feb 11, 2008
* Time: 4:22:14 PM
* To change this template use File | Settings | File Templates.
*/
class C10nAccessor extends AbstractDataAccessor {

    // Creates a continuation and adds it to a session
    // session: session
    // id: the id of the previous continuation (optional) -- the state is copied from previous to new
    // returns an IAspectContinuation
    void create(Context context) {
        use(NetKernelCategory) {
            def session = context.session
            String guid = context.makeGUID()
            Continuation c10n = new Continuation(guid)
            if (context.'id?') {
                String previousId = context.sourceString("this:param:id")
                IAspectContinuation iac = (IAspectContinuation)context.sourceAspect("active:c10n", [id:string(previousId), session:session.sessionURI], IAspectContinuation.class)
                IContinuation previous = iac.getContinuation()
                // Update previous with new followup
                previous.addFollowUp(c10n.id)
                session.storeToken(previous.id,  new ContinuationAspect(previous))
                c10n.previous = previous.id
                c10n.state = previous.state
            }
            IAspectContinuation ca = new ContinuationAspect(c10n)
            session.storeToken(guid, ca)
            context.respond(ca)
        }

    }

    // Gets an existing continuation stored in a session
    // session: session
    // id: continuation id
    // returns an IAspectContinuation  (exception if fails)
    void source(Context context) {
        use(NetKernelCategory) {
            String id = context.sourceString("this:param:id")
            def session = context.session
            IAspectContinuation iac = (IAspectContinuation) session.recallToken(id, IAspectContinuation.class)
            context.respond(iac)
        }
    }

    // Deletes a continuation from a session
    // session: session
    // id: continuation id
    // returns true (exception if fails)
    void delete(Context context) {
        use(NetKernelCategory) {
            def session = context.session
            String id = context.sourceString("this:param:id")
            deleteContinuation(id, session, context)
            context.respond(bool(true))
        }
    }

    private void deleteContinuation(String id, def session, Context context) {
        IContinuation c10n = ((IAspectContinuation) session.recallToken(id, IAspectContinuation.class)).getContinuation()
        if (c10n.followUps) {
            throw new Exception("Continuation has follow-ups. Delete them first")
        }
        if (c10n.previous) {
          IContinuation previous = ((IAspectContinuation) session.recallToken(c10n.previous, IAspectContinuation.class)).getContinuation()
          previous.removeFollowUp(c10n.id)
          if (previous.followUps) {  // update previous with one less follow up
              session.storeToken(previous.id, new ContinuationAspect(previous))
          }
          else {  // delete previous since it has no more followups
              deleteContinuation(previous.id, session, context)
          }
        }
        session.deleteToken(id)
    }

    // Updates a continuation stored in a session
    // session: session
    // continuation: an IAspectContinuation
    // returns true (exception if fails)
    void sink(Context context) {
        use(NetKernelCategory) {
            IAspectContinuation iac = (IAspectContinuation) context.sourceAspect("this:param:continuation", IAspectContinuation.class)
            IContinuation continuation = iac.getContinuation()
            def session = context.session
            session.storeToken(continuation.id, iac)
            context.respond(bool(true))
        }
    }

    // Does an identified continuation exist in a session?
    // session: session
    // id: continuation id
    // returns whether a continuation exists
    void exists(Context context) {
        use(NetKernelCategory) {
            String id = context.sourceString("this:param:id")
            def session = context.session
            boolean exists = session.tokenExists(id)
            context.respond(bool(exists))
        }
    }

}