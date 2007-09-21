(: All agents of a task. 							:)

(: Variables:															  :)
(: 			taskId -- the id of a task	:)

<list>
	{
		 let $r := collection('__MODEL__')/role
		 for 
		 	$a in collection('__MODEL__')/agent
		 where 
		 	$a/taskId = $taskId and
		 	$r/id = $a/roleId
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