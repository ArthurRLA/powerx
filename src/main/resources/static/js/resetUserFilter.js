function clearUserFilters() {
	document.getElementById('filterPosition').selectedIndex = -1;
	document.getElementById('filterState').selectedIndex = -1;
	const statusSel = document.getElementById('filterStatus');
	if (statusSel) {
		statusSel.value = 'ACTIVE';
	}

	fetch('/users/clearFilters?page=0&size=50', {
		method: 'GET'
	})
		.then(response => response.text())
		.then(html => {
			const div = document.getElementById('user-table');
			div.innerHTML = '';
			div.innerHTML = html;

			const filterModal = new bootstrap.Modal(document.getElementById('filterModal'));
			filterModal.hide();

		})
		.catch(error => {
			console.error('Erro ao limpar filtros:', error);
		});
}
