function clearCustomerFilters() {
        document.getElementById('filterUser').selectedIndex = -1;
        document.getElementById('filterGroup').selectedIndex = -1;

        fetch('/customers/clearFilters', {
            method: 'GET'
        })
        .then(response => response.text())
        .then(html => {
            const div = document.getElementById('customer-table');
			div.innerHTML = '';
			div.innerHTML = html;

            const filterModal = new bootstrap.Modal(document.getElementById('filterModal'));
            filterModal.hide();
            if (typeof window.initCustomerRowContextMenu === 'function') {
                window.initCustomerRowContextMenu();
            }
        })
        .catch(error => {
            console.error('Erro ao limpar filtros:', error);
        });
    }
