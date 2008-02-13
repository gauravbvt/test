package com.mindalliance.channels.c10n.accessors
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context
import com.mindalliance.channels.c10n.util.Continuation
import com.mindalliance.channels.nk.SessionHelper
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
            String id = context.sourceString("this:param:id")
            def session = context.session
            session.deleteToken(id)
            context.respond(bool(true))
        }
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