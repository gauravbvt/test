(: All projects in the model :)
<list>
	{
		 for $e in collection('__MODEL__')/project
		 order by $e/name
		 return
		 	<project>
		 		<id>{$e/id}</id>
		 		<name>{$e/name}</name>
		 	</project>
	}
</list>
