(: All acquirements in a scenario. 							:)
(: Variables:															  :)
(: 			scenarioId -- the id of a scenario	:)

<list>
	{
		 for 
		 	$t in collection('__MODEL__')/task,
		 	$a in collection('__MODEL__')/acquirement
		 where 
		 	$t/scenarioId = $scenarioId and
		 	$a/product/taskId = $t/id
		 order by $a/name
		 return
		 	<acquirement>
		 		<id>{$a/id/text()}</id>
		 		<name>{$a/name/text()}</name>
		 	</acquirement>
	}
</list>