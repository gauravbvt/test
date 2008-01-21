package com.mindalliance.channels.nk;

import org.ten60.netkernel.layer1.nkf.INKFConvenienceHelper;
import com.ten60.netkernel.urii.IURAspect;
import com.ten60.netkernel.urii.IURRepresentation;

/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Jan 16, 2008
 * Time: 3:31:50 PM
 */
public interface IStoreAdaptor {

    public boolean open(String db, INKFConvenienceHelper context);

    public void load(String db, String contentUri, INKFConvenienceHelper context);

    public void close(String db, INKFConvenienceHelper context);

    public void persist(String db, String id, IURAspect aspect, INKFConvenienceHelper context);

    public IURRepresentation retrieve(String db, String id, INKFConvenienceHelper context);

    public void remove(String db, String id, INKFConvenienceHelper context);

    public boolean exists(String db, String id, INKFConvenienceHelper context);

}
