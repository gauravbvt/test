(: All artifacts for a task. 							:)

(: Variables:															  :)
(: 			taskId -- the id of a task	:)

<list>
	{
		 for $e in collection('__MODEL__')/artifact
		 where $e/product/taskId = $taskId
		 order by $e/name
		 return
		 	<artifact>
		 		<id>{$e/id/text()}</id>
		 		<name>{$e/name/text()}</name>
		 	</artifact>
	}
</list>