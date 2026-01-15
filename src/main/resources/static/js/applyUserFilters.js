function applyUserFilters() {
	const selectedPositions = Array.from(document.getElementById('filterPosition').selectedOptions).map(option => option.value);
	const selectedStates = Array.from(document.getElementById('filterState').selectedOptions).map(option => option.value);
	const selectedStatus = document.getElementById('filterStatus').value;

	const params = new URLSearchParams();
	selectedPositions.forEach(p => params.append('positions', p));
	selectedStates.forEach(s => params.append('states', s));
	params.append('active', selectedStatus);
	params.append('page', 0);
	params.append('size', 50);

	fetch(`/users/filter?${params.toString()}`, {
		method: 'GET',
		headers: { 'X-Requested-With': 'XMLHttpRequest' }
	})
		.then(resp => resp.text())
		.then(html => {
			document.getElementById('user-table').innerHTML = html;
			bootstrap.Modal.getInstance(document.getElementById('filterModal')).hide();
		})
		.catch(error => console.error('Erro ao aplicar filtros:', error));

}
