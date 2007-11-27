(: All projects in the model :)
<list>
	{
		 for $e in collection('__DB__')/project
		 order by $e/name
		 return
		 	<project>
		 		<id>{$e/id/text()}</id>
		 		<name>{$e/name/text()}</name>
		 	</project>
	}
</list>