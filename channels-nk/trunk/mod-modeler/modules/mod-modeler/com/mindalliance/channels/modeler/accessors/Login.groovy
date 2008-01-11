package com.mindalliance.channels.modeler.accessors

import com.mindalliance.channels.nk.NetKernelCategory
import com.mindalliance.channels.nk.accessors.AbstractAccessor
import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper as Context

/**
*
*/
class Login extends AbstractAccessor {
    static final SESSION_COOKIE_NAME = "NETKERNELSESSION";
    static final AUTHENTICATE_QUERY_URI = "ffcpl:/resources/xqueries/authenticate.xq";
    static final PROJECTS_CONFIG_URI = "ffcpl:/etc/projects.xml";
    static final VIEWLINKS_URI = "ffcpl:/etc/viewLinks.xml";
    static final INVALID_LOGIN_URI = "ffcpl:/analyst/view/invalidLogin";

    void source(Context context) {
        use(NetKernelCategory) {
            def authentication = context.sourceXML("this:param:param")
            String userid = authentication.userid;
            String project = authentication.project;
            String password = authentication.password;
            if (userid && password && project
                    && authenticate(userid, password, project, context)) {
                if (context.'cookie?') {
                    def cookie = context.getCookie(SESSION_COOKIE_NAME);
                    println cookie;
                }
                // Store session credentials
                def session = context.session;
                session.credentials = userid;
                // Store session project
                session.project = project;
                // Issue HTTP Redirect
                String url = authentication.url;
                context.subrequest("active:HTTPRedirect",
                        ["operator": string("<url>${url}</url>"),
                                "mimeType": "text/xml",
                                "expired": true])
            }
            else {
                context.subrequest("active:HTTPResponseCode", [
                                    param: string("<HTTPResponseCode><code>401</code></HTTPResponseCode>"),
                                    mimeType: "text/html",
                                    expired: true
                                    ])
            }
        }
    }

    def authenticate(String userid, String password, String project, Context ctx) throws Exception {
        use(NetKernelCategory) {
            def acl = ctx.sourceXML(PROJECTS_CONFIG_URI);
            return ( acl.project.any { it.name == project && it.admin == userid }
                     && acl.user.any { it.id == userid && it.password == password } )
        }

    }

}