(: All agents of a task. 							:)

(: Variables:															  :)
(: 			taskId -- the id of a task	:)

<list>
	{
		 for 
		 	$a in collection('__MODEL__')/agent,
		 	$r in collection('__MODEL__')/role
		 where 
		 	$a/taskId = $taskId and
		 	$r/id = $a/roleId
		 order by $a/name
		 return
		 	<agent>
		 		<id>{$a/id/text()}</id>
		 		<name>{$a/name/text()}</name>
		        <taskId>{$taskId}</taskId>
		        <roleId>{$r/id/text()}</roleId>
		 	</agent>
	}
</list>