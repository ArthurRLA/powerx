$(document).ready(function() {

	$('#newCustomer').select2({
		placeholder: "Selecione um Cliente",
		allowClear: true,
		closeOnSelect: false,
		width: '50%',
		dropdownParent: $('#newRevenueModal')
	}).on('select2:open', () => {
		$('.select2-dropdown').css('z-index', 1060);
	});

	$('#editCustomer').select2({
		placeholder: "Selecione um Cliente",
		allowClear: true,
		closeOnSelect: false,
		width: '50%',
		dropdownParent: $('#newRevenueModal')
	}).on('select2:open', () => {
		$('.select2-dropdown').css('z-index', 1060);
	});

	$('.product-select').select2({
		placeholder: "Selecione um Produto",
		allowClear: true,
		closeOnSelect: false,
		width: '100%',
		dropdownParent: $('#newRevenueModal')
	}).on('select2:open', () => {
		$('.select2-dropdown').css('z-index', 1060);
	});

	flatpickr('#newDate', {
		dateFormat: 'Y-m-d',
		altInput: true,
		altFormat: 'd/m/Y',
		allowInput: false,
		clickOpens: true,
		wrap: false
	});

	flatpickr('#editDate', {
		dateFormat: 'Y-m-d',
		altInput: true,
		altFormat: 'd/m/Y',
		allowInput: false,
		clickOpens: true,
		wrap: false
	});

	flatpickr('#startDate', {
		dateFormat: 'Y-m-d',
		altInput: true,
		altFormat: 'd/m/Y',
		allowInput: false,
		clickOpens: true,
		wrap: false
	});

	flatpickr('#endDate', {
		dateFormat: 'Y-m-d',
		altInput: true,
		altFormat: 'd/m/Y',
		allowInput: false,
		clickOpens: true,
		wrap: false
	});

	$('#filterModal').on('shown.bs.modal', function() {
		const $modal = $(this);
		['#filterCustomer', '#filterGroup', '#filterUser', '#filterOperationType'].forEach(selector => {
			$modal.find(selector).select2({
				placeholder: $(selector).attr('placeholder') || '',
				dropdownParent: $modal,
				width: '100%'
			}).on('select2:open', function() {
				$('.select2-dropdown').css('z-index', 1060);
			});
		});
	});

	const productOptionsHTML = $('.product-select').first().html();

	$('#addRowBtn').on('click', function() {
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

		$('#revenueItemsBody').append($row);

		$row.find('.product-select').select2({
			placeholder: "Selecione um Produto",
			allowClear: true,
			closeOnSelect: false,
			width: '100%',
			dropdownParent: $('#newRevenueModal')
		}).on('select2:open', () => {
			$('.select2-dropdown').css('z-index', 1060);
		});
	});

	$('#revenueItemsBody').on('click', '.remove-btn', function() {
		const $select = $(this).closest('tr').find('.product-select');
		const removedVal = $select.val();
		if (removedVal) {
			$('select.product-select').each(function() {
				if (!$(this).find(`option[value="${removedVal}"]`).length) {
					$(this).append(`<option value="${removedVal}">${$select.find('option:selected').text()}</option>`);
				}
			});
		}
		$(this).closest('tr').remove();
	});

	$('#revenueItemsBody').on('select2:select', '.product-select', function(e) {
		const selectedId = e.params.data.id;
		$('select.product-select').not(this).each(function() {
			$(this).find(`option[value="${selectedId}"]`).remove();
		});
	});

	$('#revenueItemsBody').on('select2:unselect', '.product-select', function(e) {
		const unselectedId = e.params.data.id;
		const unselectedText = e.params.data.text;
		$('select.product-select').each(function() {
			if (!$(this).find(`option[value="${unselectedId}"]`).length) {
				$(this).append(`<option value="${unselectedId}">${unselectedText}</option>`);
			}
		});
	});

	$('#addRowBtnEdit').on('click', function() {
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

		$row.find('.product-select').select2({
			placeholder: "Selecione um Produto",
			allowClear: true,
			closeOnSelect: false,
			width: '100%',
			dropdownParent: $('#editRevenueModal')
		}).on('select2:open', () => {
			$('.select2-dropdown').css('z-index', 1060);
		});
	});

	$('#revenueItemsEditTableBody').on('select2:select', '.product-select', function(e) {
		const selectedId = e.params.data.id;

		$('select.product-select').not(this).each(function() {
			$(this).find(`option[value="${selectedId}"]`).remove();
		});
	});

	$('#revenueItemsEditTableBody').on('select2:unselect', '.product-select', function(e) {
		const unselectedId = e.params.data.id;
		const unselectedText = e.params.data.text;

		$('select.product-select').each(function() {
			if (!$(this).find(`option[value="${unselectedId}"]`).length) {
				$(this).append(`<option value="${unselectedId}">${unselectedText}</option>`);
			}
		});
	});

	$('#revenueItemsEditTableBody').on('click', '.remove-btn', function() {
		const $select = $(this).closest('tr').find('.product-select');
		const removedVal = $select.val();

		if (removedVal) {
			$('select.product-select').each(function() {
				if (!$(this).find(`option[value="${removedVal}"]`).length) {
					const removedText = $select.find('option:selected').text();
					$(this).append(`<option value="${removedVal}">${removedText}</option>`);
				}
			});
		}

		$(this).closest('tr').remove();
	});

});


const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

async function saveRevenue() {
	const invoiceNumber = document.getElementById('newInvoiceNumber').value;
	const date = document.getElementById('newDate').value;
	const operationType = document.getElementById('newOperationType').value;
	const customerId = document.getElementById('newCustomer').value;

	if (!invoiceNumber) {
		alert('Por favor informe o Nº da Nota Fiscal.');
		return;
	}
	if (!date) {
		alert('Por favor selecione a data do faturamento.');
		return;
	}
	if (!operationType) {
		alert('Por favor selecione a natureza da operação.');
		return;
	}
	if (!customerId) {
		alert('Por favor selecione um cliente.');
		return;
	}


	const items = [];
	const rows = document.querySelectorAll('#revenueItemsBody tr');

	rows.forEach(row => {
		if (row.style.display === "none") return;

		const productSelect = row.querySelector('.product-select');
		const quantityInput = row.querySelector('.qty-input');

		const productId = productSelect.value;
		const quantity = quantityInput.value;

		if (!productId) {
			alert('Selecione um produto na linha: ' + (Array.from(rows).indexOf(row) + 1));
			return;
		}
		if (!quantity || isNaN(quantity) || Number(quantity) <= 0) {
			alert('Informe uma quantidade válida na linha: ' + (Array.from(rows).indexOf(row)));
			return;
		}

		if (productId && quantity) {
			items.push({
				productId: productId,
				quantity: quantity
			});
		}
	});

	const dto = {
		invoiceNumber: invoiceNumber,
		date: date,
		operationType: operationType,
		customerId: customerId,
		items: items
	};

	const url = "/revenues/save";
	const newRevenueModal = bootstrap.Modal.getInstance(document.getElementById('newRevenueModal'));

	try {
		const request = await fetch(url, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				[csrfHeader]: csrfToken
			},
			body: JSON.stringify(dto)
		});

		if (!request.ok) {
			const responseError = await request.text();

			newRevenueModal.hide();

			const errorModal = new bootstrap.Modal(document.getElementById('errorModal'));
			const errorMessage = document.getElementById('errorMessage');
			errorMessage.textContent = responseError;

			errorModal.show();

			throw new Error(responseError || 'Erro Desconhecido');
		}

		alert('Faturamento Salvo com sucesso');

		newRevenueModal.hide();

		window.location.href = '/revenues';
	} catch (error) {
		console.log("Erro ao salvar faturamento", error);
	}


}



















