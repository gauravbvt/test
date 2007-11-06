package com.mindalliance.channels.flowmap.view.delegates {
	import com.mindalliance.channels.flowmap.view.FlowMapLayoutHelper;
	import com.mindalliance.channels.flowmap.view.GraphHelper;
	import com.mindalliance.channels.flowmap.view.data.GraphDataMapper;
	import com.mindalliance.channels.flowmap.view.data.LabelData;
	import com.mindalliance.channels.flowmap.view.data.NodeData;
	import com.mindalliance.channels.flowmap.view.data.PortType;
	import com.mindalliance.channels.flowmap.view.data.RepositoryNodeData;
	import com.mindalliance.channels.flowmap.view.visualelements.FlowMapStyles;
	import com.yworks.canvas.geom.IOrientedRectangle;
	import com.yworks.graph.model.IGraph;
	import com.yworks.graph.model.ILabel;
	import com.yworks.graph.model.INode;
	import com.yworks.graph.model.IPort;
	import com.yworks.support.DictionaryMapper;
	import com.yworks.support.Iterator;
	
	import mx.collections.ArrayCollection;
	
	public class RepositoryDelegate extends BaseDelegate {
		
		public function RepositoryDelegate(mapper:GraphDataMapper, helper:GraphHelper, graph:IGraph) {
			super(mapper, helper, graph) ;
		}

		public function removeRepository(reposID:String, dispatchEvent:Boolean=true):void {
			helper.removeNode(reposID) ;
			if (dispatchEvent)
				dispatchFlowMapChanged() ;
		}
		
		public function removeRepositoryOwner(reposOwnerID:String):void {
			helper.removeLabelsByID(reposOwnerID) ;
			dispatchFlowMapChanged() ;
		}
				
		public function removeAllRepositories():void {
			var iter:Iterator = (mapper.nodeDataMapper as DictionaryMapper).values() ;
			var itemsToRemove:ArrayCollection = new ArrayCollection() ;
			while (iter.hasNext()) {
				var nd:NodeData = iter.next() as NodeData ;
				if (nd.type == NodeData.NODE_TYPE_REPOSITORY)
					itemsToRemove.addItem(nd.id) ;
			}
			for each (var id:String in itemsToRemove)
				removeRepository(id, false) ;
			
			dispatchFlowMapChanged() ;
		}
		
		public function renameRepository(reposID:String, newText:String):void {
			var nd:RepositoryNodeData = mapper.nodeDataMapper.lookupValue(reposID) as RepositoryNodeData;
			var labelIter:Iterator = nd.node.labels.iterator() ;
			while (labelIter.hasNext()) {
				var label:ILabel = labelIter.next() as ILabel ;
				var ld:LabelData = mapper.labelDataMapper.lookupValue(label) as LabelData ;
				if (ld.type != LabelData.LABEL_TYPE_REPOSITORY)
					continue ;
				graph.setLabelText(ld.label, newText) ;
				// Adjust node size to fit label
				FlowMapLayoutHelper.updateNodeBounds(graph, label.owner as INode) ;
				break ;
			}
			dispatchFlowMapChanged() ;
		}
		
		public function renameRepositoryOwner(reposOwnerID:String, newText:String):void {
			var labelIter:Iterator = graph.nodeLabels.iterator() ;
			while (labelIter.hasNext()) {
				var label:ILabel = ILabel(labelIter.next()) ;
				var ld:LabelData = mapper.labelDataMapper.lookupValue(label) as LabelData ;
				if (ld.type == LabelData.LABEL_TYPE_REPOSITORY_OWNER && ld.id == reposOwnerID) {
					graph.setLabelText(ld.label, newText) ;
				}
			}
			dispatchFlowMapChanged() ;
		}
		
		public function addRepository(phaseID:String, reposID:String, reposLabel:String):void {
	
			// Rename if already present
			var rnd:RepositoryNodeData = mapper.nodeDataMapper.lookupValue(reposID) as RepositoryNodeData ;
			if (rnd) {
				renameRepository(reposID, reposLabel) ;
			}
			
			// Find a place to add the event node
			var node:INode = helper.addNewNode(FlowMapStyles.repositoryNodeStyle,
												reposID) ;
			
			//Setup mappings
			rnd = new RepositoryNodeData(node, reposID) ;
			mapper.nodeDataMapper.mapValue(reposID, rnd) ;
			mapper.idMapper.mapValue(node, reposID) ;
			
			// Add repository name label
			var label:ILabel = helper.addNewNodeLabel(node, reposLabel, 
													FlowMapStyles.repositoryLabelModelParameter, 
													FlowMapStyles.repositoryLabelStyle,
													reposID, LabelData.LABEL_TYPE_REPOSITORY) ;
			
			var rect:IOrientedRectangle = label.layout ;
			var port:IPort = helper.addNewPort(node, rect.anchorX - 5, rect.anchorY - rect.height/2, PortType.PORT_TYPE_REPOSITORY_INCOMING, reposID) ;
		}
	
		public function setRepositoryOwner(reposID:String, reposOwnerID:String, reposOwnerLabel:String):void {
			// Get the node in which this reposOwner is to be added
			var nd:RepositoryNodeData = mapper.nodeDataMapper.lookupValue(reposID) as RepositoryNodeData ;
			var label:ILabel = helper.getLabelByType(nd.node, LabelData.LABEL_TYPE_REPOSITORY_OWNER) ;
			if (label == null)
				addRepositoryOwner(nd.node, reposOwnerID, reposOwnerLabel) ;
			else {
				var ld:LabelData = mapper.labelDataMapper.lookupValue(label) as LabelData ;
				if (ld.id != reposOwnerID)
					replaceRepositoryOwner(nd.node, reposOwnerID, reposOwnerLabel) ;
				else
					renameRepositoryOwner(reposOwnerID, reposOwnerLabel) ;
			}
			dispatchFlowMapChanged() ;
		}
		
		private function addRepositoryOwner(node:INode, reposOwnerID:String, reposOwnerLabel:String):void {
			var label:ILabel = graph.addLabel(node, reposOwnerLabel, FlowMapStyles.repositoryOwnerLabelModelParameter, FlowMapStyles.repositoryOwnerLabelStyle) as ILabel ;
			var ld:LabelData = new LabelData(label, reposOwnerID, LabelData.LABEL_TYPE_REPOSITORY_OWNER) ;
			mapper.labelDataMapper.mapValue(label, ld) ;
			mapper.idMapper.mapValue(label, reposOwnerID) ;
			
			dispatchFlowMapChanged() ;
		}
		
		private function replaceRepositoryOwner(node:INode, newReposOwnerID:String, newReposOwnerLabel:String):void {
			var label:ILabel = helper.getLabelByType(node, LabelData.LABEL_TYPE_REPOSITORY_OWNER) as ILabel ;
			var ld:LabelData = mapper.labelDataMapper.lookupValue(label) as LabelData ;
			graph.setLabelText(label, newReposOwnerLabel) ;
			ld.id = newReposOwnerID ;
			ld.label = label ;
			mapper.idMapper.mapValue(label, newReposOwnerID) ;
			mapper.labelDataMapper.mapValue(label, ld) ;
			
			dispatchFlowMapChanged() ;
		}
	}
}