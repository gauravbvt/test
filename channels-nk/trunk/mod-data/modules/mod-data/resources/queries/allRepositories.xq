(: All repositories in the model :)
<list>
	{
		 for $e in collection('__MODEL__')/repository
		 order by $e/name
		 return
		 	<repository>
		 		<id>{$e/id/text()}</id>
		 		<name>{$e/name/text()}</name>
		 	</repository>
	}
</list>
