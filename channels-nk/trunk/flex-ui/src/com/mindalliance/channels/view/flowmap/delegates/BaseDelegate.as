package com.mindalliance.channels.view.flowmap.delegates
{
	import com.mindalliance.channels.view.flowmap.GraphMapperHelper;
	import com.yworks.graph.model.ILabel;
	import com.yworks.graph.model.INode;
	import com.yworks.support.IMapper;
	
	public class BaseDelegate
	{
		
		protected var mapper:GraphMapperHelper ;
		
		public function BaseDelegate(mapperHelper:GraphMapperHelper) {
			mapper = mapperHelper ;
		}
		
		internal function getLabel(node:INode, labelType:String):DefaultLabel {
			var iter:Iterator = node.labels.iterator() ;
			while (iter.hasNext()) {
				var label:ILabel = iter.next() as ILabel ;
				var ld:LabelData = _mapperHelper.labelDataMapper.lookupValue(label) as LabelData ;
				if (ld.type == labelType)
					return label ;
			}
			return null ;
		}
		
	}
}