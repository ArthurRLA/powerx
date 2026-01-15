const viewTablePriceModal = document.getElementById('viewTablePriceModal');
const editTablePriceModal = document.getElementById('editTablePriceModal');

const tablePriceViewDetails = document.getElementById('tablePriceViewDetails');
const tablePriceInfosToEdit = document.getElementById('tablePriceInfosToEdit');

const loadingTablePriceViewDetails = document.getElementById('loadingTablePriceViewDetails');
const loadingTablePriceInfosToEdit = document.getElementById('loadingTablePriceInfosToEdit');

function debounce(fn, wait = 300) {
	let timer = null;
	return function(...args) {
		clearTimeout(timer);
		timer = setTimeout(() => fn.apply(this, args), wait);
	};
}

const debouncedLoadTablePriceViewDetails = debounce(loadTablePriceViewDetails, 400);
const debouncedLoadTablePriceInfosToEdit = debounce(loadTablePriceInfosToEdit, 400);

async function startLoadingInfos(loading, details) {
	loading.style.display = 'flex';
	details.style.display = 'none';
}

async function endLoadingInfos(loading, details) {
	loading.style.display = 'none';

	if (details) {
		details.style.display = 'block';
	}
}

async function requestIsOk(request, modal, loading) {
	if (!request.ok) {
		const responseError = await request.text();

		endLoadingInfos(loading, null);

		const currentModal = bootstrap.Modal.getInstance(modal);
		currentModal.hide();
		openErrorModal(responseError);
		throw new Error(responseError || 'Erro Desconhecido');
	}
}

function openErrorModal(errorText) {
	const errorModal = new bootstrap.Modal(document.getElementById('errorModal'));
	const errorMessage = document.getElementById('errorMessage');
	errorMessage.textContent = errorText;

	errorModal.show();
}


viewTablePriceModal.addEventListener('shown.bs.modal', function(event) {
	const tablePriceId = event.relatedTarget.getAttribute('data-table-price-id');
	debouncedLoadTablePriceViewDetails(tablePriceId);
});

async function loadTablePriceViewDetails(tablePriceId) {
	startLoadingInfos(loadingTablePriceViewDetails, tablePriceViewDetails);

	const url = `/api/table-prices/details/${tablePriceId}`;

	try {
		const request = await fetch(url);

		await requestIsOk(request, viewTablePriceModal, loadingTablePriceViewDetails);

		const response = await request.json();

		fillTablePriceViewDetails(response);
	}
	catch (error) {
		console.log(error);
	}
}

async function fillTablePriceViewDetails(response) {
	await endLoadingInfos(loadingTablePriceViewDetails, tablePriceViewDetails);

	document.getElementById('table-price-id').textContent = response.id || '-';
	document.getElementById('table-price-customer').textContent = response.customer || '-';
	document.getElementById('table-price-product').textContent = response.product || '-';
	document.getElementById('table-price-price').textContent = response.price || '-';


}

editTablePriceModal.addEventListener('shown.bs.modal', function(event) {
	const tablePriceId = event.relatedTarget.getAttribute('data-table-price-id');
	debouncedLoadTablePriceInfosToEdit(tablePriceId);
});


async function loadTablePriceInfosToEdit(tablePriceId) {
	startLoadingInfos(loadingTablePriceInfosToEdit, tablePriceInfosToEdit);

	const url = `/api/table-prices/edit/${tablePriceId}`;

	try {
		const request = await fetch(url);

		await requestIsOk(request, tablePriceInfosToEdit, loadingTablePriceInfosToEdit);

		const response = await request.json();

		fillTablePriceInfosToEdit(response);
	}
	catch (error) {
		console.log(error);
	}
}

async function fillTablePriceInfosToEdit(response) {
	await endLoadingInfos(loadingTablePriceInfosToEdit, tablePriceInfosToEdit);

	document.getElementById('edit-id').value = response.id;
	document.getElementById('edit-price-input').value = response.price;

	const fillSimpleSelect = (selectId, list, valueKey, textKey, selectedItem) => {
		const select = document.getElementById(selectId);
		select.innerHTML = '';
		list.forEach(item => {
			const opt = document.createElement('option');
			opt.value = item[valueKey];
			opt.text = item[textKey];
			if (String(item[valueKey]) === String(selectedItem))
				opt.selected = true;
			select.appendChild(opt);
		});
	};

	fillSimpleSelect('edit-customer-select', response.customers, 'id', 'name', response.customer);
	fillSimpleSelect('edit-product-select', response.products, 'id', 'code', response.product);

	document.getElementById('edit-table-price-form')
		.setAttribute('action', `/table-prices/update/${response.id}`);

}


$(document).ready(function() {
	$('#filterModal').on('shown.bs.modal', function() {
		const $modal = $(this);
		['#filterCustomer'].forEach(selector => {
			$modal.find(selector).select2({
				placeholder: $(selector).attr('placeholder') || '',
				dropdownParent: $modal,
				width: '100%'
			}).on('select2:open', function() {
				$('.select2-dropdown').css('z-index', 1060);
			});
		});
	});
});

function applyTablePriceFilters() {
	const customers = $('#filterCustomer').val() || [];

	const params = new URLSearchParams();
	customers.forEach(c => params.append('customers', c))

	fetch(`/table-prices/filter?${params.toString()}`, {
		method: 'GET',
		headers: { 'X-Requested-With': 'XMLHttpRequest' }
	})
		.then(resp => resp.text())
		.then(html => {
			document.getElementById('table-prices-table').innerHTML = html;
			bootstrap.Modal.getInstance(document.getElementById('filterModal')).hide();
		})
		.catch(err => console.error('Erro ao aplicar filtros:', err));
}

document.addEventListener('DOMContentLoaded', () => {
	const container = document.getElementById('table-prices-table');

	async function loadFilteredPage(url) {
		try {
			const res = await fetch(url);
			if (!res.ok) throw new Error(res.statusText);
			const html = await res.text();
			container.innerHTML = html;
		} catch (e) {
			alert('Erro ao paginar: ' + e.message);
		}
	}

	container.addEventListener('click', e => {
		const a = e.target.closest('a.page-link[data-url]');
		if (!a) return;
		e.preventDefault();
		const url = a.getAttribute('data-url');
		loadFilteredPage(url);
	});
});

function clearTablePricesFilter() {
	fetch('/table-prices/clearFilters', {
		method: 'GET'
	})
		.then(response => response.text())
		.then(html => {
			const div = document.getElementById('table-prices-table');
			div.innerHTML = '';
			div.innerHTML = html;

			const filterModal = new bootstrap.Modal(document.getElementById('filterModal'));
			filterModal.hide();

		})
		.catch(error => {
			console.error('Erro ao limpar filtros:', error);
		});
}





















