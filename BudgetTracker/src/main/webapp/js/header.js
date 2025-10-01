document.addEventListener('DOMContentLoaded', function() {

    const profileIcon = document.getElementById('profileIcon');
    const profileDropdown = document.getElementById('profileDropdown');

    if (profileIcon && profileDropdown) {
        // Event listener to toggle the dropdown when the icon is clicked
        profileIcon.addEventListener('click', function(event) {
            // Stop the click from immediately propagating to the window
            event.stopPropagation(); 
            profileDropdown.classList.toggle('show-dropdown');
        });

        // Event listener to close the dropdown if the user clicks anywhere else
        window.addEventListener('click', function(event) {
            if (profileDropdown.classList.contains('show-dropdown')) {
                profileDropdown.classList.remove('show-dropdown');
            }
        });
    }

});
