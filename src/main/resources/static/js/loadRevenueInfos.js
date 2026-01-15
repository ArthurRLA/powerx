const revenueDetailsModal = document.getElementById('revenueDetailsModal');
const editRevenueModal = document.getElementById('editRevenueModal');
const deleteRevenueModal = document.getElementById('deleteRevenueModal');
const deleteRevenueResponseModal = document.getElementById('deleteRevenueResponseModal');

const revenueDetailsArea = document.getElementById('revenueDetailsArea');
const revenueEditDetailsArea = document.getElementById('revenueEditDetailsArea');
const revenueDeleteResponseArea = document.getElementById('revenueDeleteResponseArea');

const revenuesContentArea = document.getElementById('revenuesContentArea');

const loadingRevenueDetailsArea = document.getElementById('loadingRevenueDetailsArea');
const loadingRevenueEditDetailsArea = document.getElementById('loadingRevenueEditDetailsArea');
const loadingRevenueDeleteResponseArea = document.getElementById('loadingRevenueDeleteResponseArea');
const loadingRevenuesContentArea = document.getElementById('loadingRevenuesContentArea');

function debounce(fn, wait = 300) {
	let timer = null;
	return function(...args) {
		clearTimeout(timer);
		timer = setTimeout(() => fn.apply(this, args), wait);
	};
}

const debouncedLoadRevenueDetails = debounce(loadRevenueDetails, 400);
const debouncedLoadRevenueInfosToEdit = debounce(loadRevenueInfosToEdit, 400);

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

		if (modal) {
			const currentModal = bootstrap.Modal.getInstance(modal);
			currentModal.hide();
		}

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

revenueDetailsModal.addEventListener('shown.bs.modal', function(event) {
	const revenueId = event.relatedTarget.getAttribute('data-revenue-id');
	debouncedLoadRevenueDetails(revenueId);
})

async function loadRevenueDetails(revenueId) {
	startLoadingInfos(loadingRevenueDetailsArea, revenueDetailsArea);

	const url = `/api/revenues/details/${revenueId}`;

	try {
		const request = await fetch(url);

		await requestIsOk(request, revenueDetailsModal, loadingRevenueDetailsArea);

		const response = await request.json();

		fillRevenueDetails(response);
	} catch (error) {
		console.log(error);
	}
}

async function fillRevenueDetails(response) {
	endLoadingInfos(loadingRevenueDetailsArea, revenueDetailsArea);

	document.getElementById('invoinceNumberDetails').textContent = response.invoiceNumber || '-';
	document.getElementById('unysoftIdDetails').textContent = response.unysoftId || '-';
	document.getElementById('userDetails').textContent = response.user || '-';
	document.getElementById('customerDetails').textContent = response.customer.fantasyName + " (" + response.customer.cnpj + ")" || '-';
	document.getElementById('operationTypeDetails').textContent = response.operationType || '-';
	document.getElementById('balanceDetails').textContent = response.balance || '-';

	const rawDate = response.date;

	if (rawDate) {
		const date = new Date(rawDate);
		const formattedDate = new Intl.DateTimeFormat('pt-BR').format(date);
		document.getElementById('dateDetails').textContent = formattedDate;
	} else {
		document.getElementById('dateDetails').textContent = '-';
	}

	const tBody = document.getElementById('revenueItemsDetailsTableBody');
	tBody.innerHTML = '';

	response.items.forEach(item => {
		const tr = document.createElement('tr');

		const tdCod = document.createElement('td');
		tdCod.textContent = item.productCode;
		tr.appendChild(tdCod);

		const tdDesc = document.createElement('td');
		tdDesc.textContent = item.productName;
		tr.appendChild(tdDesc);

		const tdQtd = document.createElement('td');
		tdQtd.textContent = item.quantity;
		tr.appendChild(tdQtd);

		const tdPrice = document.createElement('td');
		tdPrice.textContent = "R$ " + item.price;
		tr.appendChild(tdPrice);

		const tdSubtotal = document.createElement('td');
		tdSubtotal.textContent = "R$ " + item.subTotal;
		tr.appendChild(tdSubtotal);

		tBody.appendChild(tr);
	});

	document.getElementById('totaRevenueDetails').textContent = "R$ " + response.balance;

	const deleteButton = document.getElementById('deleteRevenueButton');
	deleteButton.setAttribute('data-revenue-id', response.id);
}


editRevenueModal.addEventListener('shown.bs.modal', function(event) {
	const revenueId = event.relatedTarget.getAttribute('data-revenue-id');
	debouncedLoadRevenueInfosToEdit(revenueId);
});

async function loadRevenueInfosToEdit(revenueId) {
	startLoadingInfos(loadingRevenueEditDetailsArea, revenueEditDetailsArea);

	const url = `/api/revenues/edit/${revenueId}`;

	try {
		const request = await fetch(url);

		await requestIsOk(request, editRevenueModal, loadingRevenueEditDetailsArea);

		const response = await request.json();

		fillRevenueEditDetails(response);
	} catch (error) {
		console.log(error);
	}
}


async function fillRevenueEditDetails(response) {
	endLoadingInfos(loadingRevenueEditDetailsArea, revenueEditDetailsArea);

	document.getElementById('ediRevenueId').value = response.id ?? '';
	document.getElementById('invoiceNumberEdit').value = response.invoiceNumber ?? '';
	document.getElementById('unysoftIdEdit').value = response.unysoftId ?? '';

	const opSelect = document.getElementById('operationTypeSelectEdit');
	opSelect.innerHTML = '';
	opSelect.append(new Option('Selecione a Natureza', '', true, false));
	response.operationTypes.forEach(op => {
		const option = new Option(op, op, false, op === response.operationType);
		opSelect.append(option);
	});
	$(opSelect).trigger('change');

	const custSelect = $('#customerSelectEdit');
	custSelect.empty().append('<option></option>');
	response.customers.forEach(c => {
		custSelect.append(
			`<option value="${c.id}" ${c.id === response.customer ? 'selected' : ''}>` +
			`${c.cnpj} ${c.fantasyName}` +
			`</option>`
		);
	});
	custSelect.select2({
		placeholder: "Selecione um Cliente",
		allowClear: true,
		closeOnSelect: false,
		width: '100%',
		dropdownParent: $('#editRevenueModal')
	}).trigger('change');

	const editDateInput = document.getElementById('editDate');
	if (editDateInput._flatpickr) editDateInput._flatpickr.destroy();
	flatpickr(editDateInput, {
		dateFormat: 'Y-m-d',
		altInput: true,
		altFormat: 'd/m/Y',
		defaultDate: response.date,
	});

	const productOptionsHTML = response.products.map(p =>
		`<option value="${p.id}">${p.productCode} ${p.productName}</option>`
	).join('');

	const tbody = document.getElementById('revenueItemsEditTableBody');
	tbody.innerHTML = '';

	response.items.forEach(item => {
		const $row = $(`
      <tr>
        <td>
          <select class="form-control select2 product-select">
            <option></option>
            ${productOptionsHTML}
          </select>
        </td>
        <td><input type="number" class="form-control qty-input" /></td>
        <td>
          <button type="button" class="btn btn-danger remove-btn">Remover</button>
        </td>
      </tr>
    `);
		$('#revenueItemsEditTableBody').append($row);

		const prodSelect = $row.find('.product-select');
		prodSelect.val(item.productId);
		prodSelect.select2({
			placeholder: "Selecione um Produto",
			allowClear: true,
			closeOnSelect: false,
			width: '100%',
			dropdownParent: $('#editRevenueModal')
		}).on('select2:open', () => {
			$('.select2-dropdown').css('z-index', 1060);
		});

		$row.find('.qty-input').val(item.quantity);
	});

	$('#revenueItemsEditTableBody')
		.off('select2:select').on('select2:select', '.product-select', function(e) {
			const selId = e.params.data.id;
			$('select.product-select').not(this).each(function() {
				$(this).find(`option[value="${selId}"]`).remove();
			});
		})
		.off('select2:unselect').on('select2:unselect', '.product-select', function(e) {
			const unId = e.params.data.id, unText = e.params.data.text;
			$('select.product-select').each(function() {
				if (!$(this).find(`option[value="${unId}"]`).length) {
					$(this).append(`<option value="${unId}">${unText}</option>`);
				}
			});
		});
}

document.getElementById('editRevenueBtn')
	.addEventListener('click', async function onSaveClick(event) {
		event.preventDefault();

		try {
			const saveBtn = this;
			saveBtn.disabled = true;
			saveBtn.textContent = 'Salvando...';

			const revenueId = document
				.getElementById('ediRevenueId')
				.value;

			const invoiceNumber = document
				.getElementById('invoiceNumberEdit')
				.value.trim();

			const unysoftId = document
				.getElementById('unysoftIdEdit')
				.value.trim();

			const operationType = document
				.getElementById('operationTypeSelectEdit')
				.value;

			const customerId = $('#customerSelectEdit').val();

			const date = document
				.getElementById('editDate')
				._flatpickr
				.input.value;

			const items = [];
			$('#revenueItemsEditTableBody tr').each(function() {
				const $row = $(this);
				const productId = $row.find('.product-select').val();
				const quantity = Number($row.find('.qty-input').val());

				if (productId && quantity > 0) {
					items.push({ productId, quantity });
				}
			});

			const payload = {
				invoiceNumber,
				unysoftId,
				operationType,
				customerId,
				date,
				items
			};

			const response = await fetch(`/api/revenues/update/${revenueId}`, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
					[csrfHeader]: csrfToken
				},
				body: JSON.stringify(payload)
			});

			if (!response.ok) {
				const responseError = await response.text();

				const editRevenueModal = bootstrap.Modal.getInstance(document.getElementById('editRevenueModal'));

				editRevenueModal.hide();

				const errorModal = new bootstrap.Modal(document.getElementById('errorModal'));
				const errorMessage = document.getElementById('errorMessage');
				errorMessage.textContent = responseError;

				errorModal.show();

				throw new Error(responseError || 'Erro Desconhecido');
			}

			alert('Faturamento salvo com sucesso!');
			window.location.href = '/revenues';

		} catch (err) {
			console.error(err);
		}
	});


const deleteRevenueButton = document.getElementById('deleteRevenueButton');

deleteRevenueButton.addEventListener('click', async () => {
	const revenueId = document.getElementById('deleteRevenueButton').getAttribute('data-revenue-id');

	const url = `/api/revenues/delete/${revenueId}`;

	try {
		const request = await fetch(url, {
			method: 'DELETE',
			headers: {
				'Content-Type': 'application/json',
				[csrfHeader]: csrfToken
			}
		});

		await requestIsOk(request, deleteRevenueModal, null);

		const response = await request.json();

		fillDeleteResponse(response);

		//window.location.href = "/revenues";
	} catch (error) {
		console.log(error);
	}

	console.log(revenueId);
});

async function fillDeleteResponse(response) {
	let total = 0;

	const modal = new bootstrap.Modal(deleteRevenueResponseModal);
	modal.show();

	const confirmDeleteModal = bootstrap.Modal.getInstance(deleteRevenueModal);
	confirmDeleteModal.hide();

	startLoadingInfos(loadingRevenueDeleteResponseArea, revenueDeleteResponseArea);

	document.getElementById('deleteMessageResponse').textContent = response.message ?? '-';

	document.getElementById('customerIdRevenueDeleted').textContent = response.customer.id ?? '-';
	document.getElementById('customerCnpjRevenueDeleted').textContent = response.customer.cnpj ?? '-';
	document.getElementById('customerFantasyNameRevenueDeleted').textContent = response.customer.fantasyName ?? '-';

	const tBody = document.getElementById('itemsRevenueDeletedTbody');
	tBody.innerHTML = '';

	response.items.forEach(item => {
		const tr = document.createElement('tr');

		const tdCod = document.createElement('td');
		tdCod.textContent = item.productCode ?? '-';
		tr.appendChild(tdCod);

		const tdDesc = document.createElement('td');
		tdDesc.textContent = item.productName ?? '-';
		tr.appendChild(tdDesc);

		const tdQty = document.createElement('td');
		tdQty.textContent = item.quantity ?? '-';
		tr.appendChild(tdQty);

		const tdPrice = document.createElement('td');
		tdPrice.textContent = "R$ " + item.price ?? '-';
		tr.appendChild(tdPrice);

		const tdSubTotal = document.createElement('td');
		tdSubTotal.textContent = "R$ " + item.subTotal ?? '-';
		tr.appendChild(tdSubTotal);

		tBody.appendChild(tr);

		total += item.subTotal;
	});

	document.getElementById('totalReveneuDeleted').textContent = total ?? '-';

	endLoadingInfos(loadingRevenueDeleteResponseArea, revenueDeleteResponseArea);
}

function reloadPage() {
	window.location.href = '/revenues';
}


async function searchByInvoiceNumber() {
	startLoadingInfos(loadingRevenuesContentArea, revenuesContentArea);

	const invoiceNumber = document.getElementById('searchByInvoice').value;

	if (!invoiceNumber || invoiceNumber <= 0) {
		alert('Digite um N° de documento válido');
		return;
	}

	const url = `/revenues/by-invoice-number/${invoiceNumber}`;

	try {
		const request = await fetch(url);

		await requestIsOk(request, null, loadingRevenuesContentArea);

		const response = await request.text();

		fillRevenuesContentAreaFiltered(response);

	} catch (error) {
		console.log(error);
	}
}

async function fillRevenuesContentAreaFiltered(response) {
	revenuesContentArea.innerHTML = '';
	revenuesContentArea.innerHTML = response;

	const clearSearchBtn = document.getElementById('clearSearchBtn');
	clearSearchBtn.style.display = 'block';
	endLoadingInfos(loadingRevenuesContentArea, revenuesContentArea);
}

async function fillRevenuesContentAreaAllRevenues(response) {
	revenuesContentArea.innerHTML = '';
	revenuesContentArea.innerHTML = response;
	endLoadingInfos(loadingRevenuesContentArea, revenuesContentArea);
}

async function clearInvoiceSearch() {
	startLoadingInfos(loadingRevenuesContentArea, revenuesContentArea);

	const url = '/revenues/clearFilters';

	try {
		const request = await fetch(url);

		await requestIsOk(request, null, loadingRevenuesContentArea);

		const response = await request.text();

		fillRevenuesContentAreaAllRevenues(response);

	} catch (error) {
		console.log(error);
	}
}

const filterButton = document.getElementById('filterButton');

filterButton.addEventListener('click', async () => {
	startLoadingInfos(loadingRevenuesContentArea, revenuesContentArea);

	const filterModal = bootstrap.Modal.getInstance(document.getElementById('filterModal'));
	filterModal.hide();

	const baseUrl = '/revenues/filter';
	const url = new URL(baseUrl, window.location.origin);

	const start = document.getElementById('startDate').value;
	const end = document.getElementById('endDate').value;
	const userIds = Array.from(document.getElementById('filterUser').selectedOptions).map(opt => opt.value);
	const customerIds = Array.from(document.getElementById('filterCustomer').selectedOptions).map(opt => opt.value);
	const groupIds = Array.from(document.getElementById('filterGroup').selectedOptions).map(opt => opt.value);
	const OperationTypes = Array.from(document.getElementById('filterOperationType').selectedOptions).map(opt => opt.value);


	url.searchParams.set("start", start);
	url.searchParams.set("end", end);
	url.searchParams.set("userIds", userIds);
	url.searchParams.set("customerIds", customerIds);
	url.searchParams.set("groupIds", groupIds);
	url.searchParams.set("OperationTypes", OperationTypes);

	console.log(url.toString());

	try {
		const request = await fetch(url);

		await requestIsOk(request, revenuesContentArea, loadingRevenuesContentArea);

		const response = await request.text();

		const content = document.getElementById('revenuesContentArea');
		content.innerHTML = '';
		content.innerHTML = response;

		const filterModal = bootstrap.Modal.getInstance(document.getElementById('filterModal'));
		filterModal.hide();

		endLoadingInfos(loadingRevenuesContentArea, revenuesContentArea);

		const clearSearchBtn = document.getElementById('clearSearchBtn');
		clearSearchBtn.style.display = 'block';
	}
	catch (error) {
		console.log(error);
	}

});

document.addEventListener('DOMContentLoaded', () => {
	const container = document.getElementById('revenuesContentArea');

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
























