// This script handles the show/hide functionality of the "Add Record" modal.
document.addEventListener('DOMContentLoaded', function() {
    
    const modal = document.getElementById('addRecordModal');
    const openBtn = document.getElementById('openModalBtn');
    const closeBtn = document.getElementById('closeModalBtn');

    // Function to open the modal
    function openModal() {
        if (modal) {
            modal.style.display = 'flex';
        }
    }

    // Function to close the modal
    function closeModal() {
        if (modal) {
            modal.style.display = 'none';
        }
    }

    // Event listeners
    if (openBtn) {
        openBtn.addEventListener('click', openModal);
    }

    if (closeBtn) {
        closeBtn.addEventListener('click', closeModal);
    }

    // Also close the modal if the user clicks on the backdrop
    if (modal) {
        modal.addEventListener('click', function(event) {
            if (event.target === modal) {
                closeModal();
            }
        });
    }
});

