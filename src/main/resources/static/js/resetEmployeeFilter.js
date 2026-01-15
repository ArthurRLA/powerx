function clearEmployeeFilters() {

	fetch('/employees/clearFilters', {
		method: 'GET'
	})
		.then(response => response.text())
		.then(html => {
			const div = document.getElementById('employee-table');
			div.innerHTML = '';
			div.innerHTML = html;

			const filterModal = new bootstrap.Modal(document.getElementById('filterModal'));
			filterModal.hide();

		})
		.catch(error => {
			console.error('Erro ao limpar filtros:', error);
		});
}
