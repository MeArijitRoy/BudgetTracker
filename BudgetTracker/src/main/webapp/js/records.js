// This script handles all interactions on the records.jsp page.
document.addEventListener('DOMContentLoaded', function() {
    
    // --- "Add Record" Modal Functionality ---

    const modal = document.getElementById('addRecordModal');
    const openBtn = document.getElementById('openModalBtn');
    const closeBtn = document.getElementById('closeModalBtn');

    function openModal() {
        if (modal) modal.style.display = 'flex';
    }

    function closeModal() {
        if (modal) modal.style.display = 'none';
    }

    if (openBtn) openBtn.addEventListener('click', openModal);
    if (closeBtn) closeBtn.addEventListener('click', closeModal);

    if (modal) {
        modal.addEventListener('click', function(event) {
            if (event.target === modal) {
                closeModal();
            }
        });
    }

    // --- NEW: Card Actions Menu (Three-dots menu) Functionality ---

    // Get all the action buttons on the page
    const actionButtons = document.querySelectorAll('.action-btn');

    actionButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            // Stop the click from bubbling up to the window listener immediately
            event.stopPropagation();
            
            // Find the menu associated with this specific button
            const menu = this.nextElementSibling;

            // Close all other open menus first
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

