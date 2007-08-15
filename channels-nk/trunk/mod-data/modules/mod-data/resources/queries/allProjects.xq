(: VARIABLES 
    part : a string that matches part of a project's name
:)
<results>
   { collection('__MODEL__')/project[contains(name,$part)] }
</results>
