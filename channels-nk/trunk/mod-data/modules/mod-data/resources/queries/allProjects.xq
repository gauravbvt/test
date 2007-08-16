(: VARIABLES 
    part : a string that matches part of a project's name
:)
<list>
   { collection('__MODEL__')/project[contains(name,$part)] }
</list>
