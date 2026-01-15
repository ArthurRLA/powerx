function applyEmployeeFilters() {
	const customer = $('#filterCustomer').val() || [];
	const functions = $('#filterFunction').val() || [];
	const active = $('#filterActive').val() || [];

	const params = new URLSearchParams();
	customer.forEach(c => params.append('customers', c));
	functions.forEach(f => params.append('functions', f));
	params.append('active', active);
	params.append('page', 0);
	params.append('size', 50);	

	fetch(`/employees/filter?${params.toString()}`, {
		method: 'GET',
		headers: { 'X-Requested-With': 'XMLHttpRequest' }
	})
		.then(resp => resp.text())
		.then(html => {
			document.getElementById('employee-table').innerHTML = html;
			bootstrap.Modal.getInstance(document.getElementById('filterModal')).hide();
		})
		.catch(err => console.error('Erro ao aplicar filtros:', err));
}


