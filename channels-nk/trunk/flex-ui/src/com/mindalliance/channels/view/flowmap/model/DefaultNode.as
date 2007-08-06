package com.mindalliance.graph.model
{
    import com.yworks.canvas.geom.IRectangle;
    import com.yworks.graph.model.DefaultNode;
    import com.yworks.graph.model.ILabelCollection;
    import com.yworks.graph.model.IPortCollection;

    public class DefaultNode extends DefaultNode
    {
        public function DefaultNode(labelCollection:ILabelCollection=null, layout:IRectangle=null, ports:IPortCollection=null)
        {
            super(labelCollection, layout, ports);
        }
        
    }
}