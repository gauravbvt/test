package com.mindalliance.channels.view.flowmap.delegates {
	import com.mindalliance.channels.view.flowmap.FlowMapLayoutHelper;
	import com.mindalliance.channels.view.flowmap.GraphHelper;
	import com.mindalliance.channels.view.flowmap.data.GraphDataMapper;
	import com.mindalliance.channels.view.flowmap.data.LabelData;
	import com.mindalliance.channels.view.flowmap.data.PortType;
	import com.yworks.graph.model.IGraph;
	import com.yworks.graph.model.ILabel;
	import com.yworks.graph.model.INode;
	import com.yworks.support.Iterator;
	
	
	public class RoleDelegate extends BaseDelegate {
		
		public function RoleDelegate(mapper:GraphDataMapper, helper:GraphHelper, graph:IGraph) {
			super(mapper, helper, graph) ;
		}

		public function renameRole(roleID:String, newText:String):void {
			var labelIter:Iterator = graph.nodeLabels.iterator() ;
			while (labelIter.hasNext()) {
				var label:ILabel = labelIter.next() as ILabel ;
				var ld:LabelData = mapper.labelDataMapper.lookupValue(label) as LabelData ;
				if (ld.type == LabelData.LABEL_TYPE_ROLE && ld.id == roleID) {
					graph.setLabelText(ld.label, newText) ;
					// Adjust node size to fit label
					FlowMapLayoutHelper.updateNodeBounds(graph, label.owner as INode) ;
				}
			}
			
			dispatchFlowMapChanged() ;
		}
		
		public function removeRole(roleID:String):void {
			helper.removeLabelsByID(roleID) ;
			helper.removePorts(roleID, PortType.PORT_TYPE_ROLE_INCOMING) ;
			// Deal with edges.
			dispatchFlowMapChanged() ;
		}
	}
}