// Stolen from PingPong application

package com.mindalliance.channels.data.accessors;

import org.ten60.netkernel.layer1.representation.*;
import org.ten60.netkernel.layer1.meta.*;
import org.ten60.netkernel.layer1.nkf.*;
import org.ten60.netkernel.layer1.nkf.impl.NKFAccessorImpl;
import com.ten60.netkernel.urii.*;
import java.util.*;

/**
 * Stores state between invocations
 * @author  tab
 */
public class Transient extends NKFAccessorImpl
{
    private final static Map<String,Holder> mDocuments = new HashMap<String,Holder>();
    
    public Transient()
    {   super(4,false,INKFRequestReadOnly.RQT_SOURCE|INKFRequestReadOnly.RQT_SINK|INKFRequestReadOnly.RQT_DELETE);
    }
    
 public void processRequest(INKFConvenienceHelper context) throws Exception
    {   switch (context.getThisRequest().getRequestType())
        {   case INKFRequestReadOnly.RQT_SOURCE:
                source(context);
                break;
            case INKFRequestReadOnly.RQT_SINK:
                sink(context);
                break;
            case INKFRequestReadOnly.RQT_DELETE:
                delete(context);
                break;
        }       
    }
    
    public void source(INKFConvenienceHelper context) throws Exception
    {   // use the URI as the key into the map
        String path = context.getThisRequest().getURIWithoutFragment();
        Holder h = (Holder)mDocuments.get(path);
        if (h==null)
        {  throw new Exception("Resource not found");
        }
        else
        {   INKFResponse resp = context.createResponseFrom(h);
            context.setResponse(resp);
        }
    }
    public void delete(INKFConvenienceHelper context) throws Exception
    {
        String path = context.getThisRequest().getURIWithoutFragment();
        Object o = mDocuments.remove(path);
        INKFResponse resp = context.createResponseFrom(new BooleanAspect(o!=null));
        context.setResponse(resp);
    }
    
    public void sink(INKFConvenienceHelper context) throws Exception
    {
        // sink requests pass representation to sink with the URI_SYSTEM uri
        IURRepresentation rep = context.source(INKFRequestReadOnly.URI_SYSTEM);
        // get the first aspect (assume it is the one and only)
        IURAspect aspect = (IURAspect)rep.getAspects().iterator().next();
        String path = context.getThisRequest().getURIWithoutFragment();
        Holder h = new Holder(aspect);
        
        Holder old = (Holder)mDocuments.put(path,h);
        if (old!=null)
        {   old.setExpired();
        }
        // if we set no response then a void result is assumed
    }   
    
    
    private class Holder implements IURRepresentation
    {
        private boolean mExpired;
        private IURAspect mAspect;
        private IURMeta mMeta;
    
        public Holder(IURAspect aAspect)
        {   mAspect = aAspect;
            mMeta = createMeta();
            mExpired=false;
        }
        
        private IURMeta createMeta()
        {   return new MetaImpl("context/unknown", 0, 0)
            {   public boolean isExpired()
                {   return mExpired;
                }
                public String toString() { return "Holder meta!";};
            };
        }
        
        public void setExpired()
        {   mExpired=true;
        }
        
        public IURAspect getAspect(Class aAspectClass)
        {   if (mAspect.getClass().isAssignableFrom(aAspectClass))
            {   return mAspect;
            }
            return null;
        }
        
        public Collection getAspects()
        {   return Collections.singletonList(mAspect);
        }
        
        public IURMeta getMeta()
        {   return mMeta;
        }
        
        public boolean hasAspect(Class aAspectClass)
        {   return mAspect.getClass().isAssignableFrom(aAspectClass);
        }
    }       
}