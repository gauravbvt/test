// Build the fisheye menu
$(document).ready(
        function() {
          $('#mainMenu').Fisheye (
            {
              maxWidth: 48,
              items: 'a',
              itemsText: 'span',
              container: '.fisheyeContainer',
              itemWidth: 30,
              proximity: 90,
              halign : 'center',
              valign: 'center'
            }
          );
         }
      );
