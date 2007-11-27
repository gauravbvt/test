$(document).ready(
        function() {
          $('#mainMenu').Fisheye (
            {
              maxWidth: 50,
              items: 'a',
              itemsText: 'span',
              container: '.fisheyeContainer',
              itemWidth: 40,
              proximity: 90,
              halign : 'center',
              valign: 'center'
            }
          );
         }
      );