(: All acquirements for a task. 							:)

(: Variables:															  :)
(: 			taskId -- the id of a task	:)

<list>
	{
		 for 
		 	$e in collection('__MODEL__')/acquirement
		 where 
		 	$e/product/taskId = $taskId
		 order by $e/name
		 return
		 	<acquirement>
		 		<id>{$e/id/text()}</id>
		 		<name>{$e/name/text()}</name>
		 	</acquirement>
	}
</list>