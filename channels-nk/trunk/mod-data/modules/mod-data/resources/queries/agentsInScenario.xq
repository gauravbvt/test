(: All agents in a scenario. 							:)
(: Variables:															  :)
(: 			scenarioId -- the id of a scenario	:)

<list>
	{
		 let $t := collection('__MODEL__')/task
		 for 
		 	$a in collection('__MODEL__')/agent
		 where 
		 	$t/scenarioId = $scenarioId and
		 	$a/taskId = $t/id
		 order by $a/name
		 return
		 	<agent>
	 			<id>{$a/id/text()}</id>
	 			<name>{$a/name/text()}</name>
	        	<taskId>{$a/taskId/text()}</taskId>
	        	<roleId>{$a/roleId/text()}</roleId>
		 	</agent>
	}
</list>