// This script handles all interactions on the accounts.jsp page.
document.addEventListener('DOMContentLoaded', function() {
    
    // --- Card Actions Menu (Three-dots menu) Functionality ---

    // Get all the action buttons on the page
    const actionButtons = document.querySelectorAll('.action-btn');

    actionButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            // Stop the click from bubbling up to the window listener immediately
            event.stopPropagation();
            
            // Find the menu associated with this specific button
            // In our HTML, the menu is the next sibling of the button
            const menu = this.nextElementSibling;

            // Close all other open menus first for a clean UI
            closeAllMenus(menu);
            
            // Toggle the 'show' class for the clicked menu
            if (menu) {
                menu.classList.toggle('show');
            }
        });
    });

    /**
     * Helper function to close all action menus.
     * @param {Element} exceptThisOne - An optional menu element to exclude from closing.
     */
    function closeAllMenus(exceptThisOne = null) {
        const allMenus = document.querySelectorAll('.action-menu');
        allMenus.forEach(menu => {
            if (menu !== exceptThisOne) {
                menu.classList.remove('show');
            }
        });
    }

    // Add a click listener to the whole window to close menus when clicking elsewhere
    window.addEventListener('click', function() {
        closeAllMenus();
    });

});
