const detailsCustomerModal = document.getElementById('detailsCustomerModal');
const showEmployeesModal = document.getElementById('showEmployeesModal');
const showTablePriceModal = document.getElementById('showTablePriceModal');
const showIncentiveValuesModal = document.getElementById('showIncentiveValuesModal');
const findIncentiveValueByCustomerIdAndApurationTypeButton = document.getElementById('findIncentiveValueByCustomerIdAndApurationTypeButton');
const showProductStockModal = document.getElementById('showProductStockModal');
const showCurrentAccontModal = document.getElementById('showCurrentAccontModal');
const showCurrentAccountBalanceInfoModal = document.getElementById('showCurrentAccountBalanceInfoModal');
const setCurrentAccountBalanceModal = document.getElementById('setCurrentAccountBalanceModal');
const editCustomerModal = document.getElementById('editCustomerModal');

function debounce(fn, wait = 300) {
	let timer = null;
	return function(...args) {
		clearTimeout(timer);
		timer = setTimeout(() => fn.apply(this, args), wait);
	};
}

const debouncedLoadEdit = debounce(loadEditCustomerInfos, 400);
const debouncedLoadCustomerBasicInfos = debounce(loadCustomerBasicInfos, 400);
const debouncedLoadCustomerEmployeeInfos = debounce(loadCustomerEmployeeInfos, 400);
const debouncedLoadCustomerTablePriceInfos = debounce(loadCustomerTablePriceInfos, 400);
const debouncedLoadCustomerIncentiveValue = debounce(loadCustomerIncentiveValue, 400);
const debouncedLoadCustomerProductStock = debounce(loadCustomerProductStock, 400);
const debouncedLoadCustomerCurrentAccountInfos = debounce(loadCustomerCurrentAccountInfos, 400);
const debouncedLoadCustomerSetCurrentAccount = debounce(loadCustomerSetCurrentAccount, 400);


detailsCustomerModal.addEventListener('shown.bs.modal', function(event) {
	const button = event.relatedTarget;

	const customerId = button.getAttribute('data-customer-id');

	debouncedLoadCustomerBasicInfos(customerId);
});

async function loadCustomerBasicInfos(customerId) {
	const url = `/api/customers/details/${customerId}`;

	const loading = document.getElementById('detailsCustomerLoading');
	const contentArea = document.getElementById('detailsCustomerContainer');

	loading.style.display = 'flex';
	contentArea.style.display = 'none';

	const request = await fetch(url);

	try {
		await requestIsOk(request, detailsCustomerModal, loading);

		const response = await request.json();

		fillCustomerBasicInfos(response, customerId);
	}
	catch (error) {
		console.error("Erro ao carregar dados do cliente:", error);
	}
}


function fillCustomerBasicInfos(response, customerId) {
	document.getElementById('detailsCustomerLoading').style.display = 'none';
	document.getElementById('detailsCustomerContainer').style.display = 'block';

	const viewActive = document.getElementById('viewActive');
	const viewGroupName = document.getElementById('viewGroupName');
	const viewUnysoftCode = document.getElementById('viewUnysoftCode');
	const viewCnpj = document.getElementById('viewCnpj');
	const viewRegisteredName = document.getElementById('viewRegisteredName');
	const viewFantasyName = document.getElementById('viewFantasyName');
	const viewCurrentAccountBalance = document.getElementById('viewCurrentAccountBalance');
	const viewAddress = document.getElementById('viewAddress');
	const viewUserName = document.getElementById('viewUserName');
	const viewIndustryName = document.getElementById('viewIndustryName');
	const viewFlagName = document.getElementById('viewFlagName');
	const viewMechanicApurationName = document.getElementById('viewMechanicApurationName');

	viewActive.textContent = response.active == true ? 'Sim' : 'Não';
	viewGroupName.textContent = response.groupName || '-';
	viewUnysoftCode.textContent = response.unysoftCode || '-';
	viewCnpj.textContent = response.cnpj || '-';
	viewRegisteredName.textContent = response.registeredName || '-';
	viewFantasyName.textContent = response.fantasyName || '-';
	viewCurrentAccountBalance.textContent = response.currentAccountBalance || '-';
	viewAddress.textContent = response.address || '-';
	viewUserName.textContent = response.userName || '-';
	viewIndustryName.textContent = response.industryName || '-';
	viewFlagName.textContent = response.flagName || '-';
	viewMechanicApurationName.textContent = response.mechanicApurationName || '-';

	const showEmployeesModalButton = document.getElementById('showEmployeesModalButton');
	showEmployeesModalButton.setAttribute('data-customer-id', customerId);

	const showTablePriceModalButton = document.getElementById('showTablePriceModalButton');
	showTablePriceModalButton.setAttribute('data-customer-id', customerId);

	const findIncentiveValueByCustomerIdAndApurationTypeButton = document.getElementById('findIncentiveValueByCustomerIdAndApurationTypeButton');
	findIncentiveValueByCustomerIdAndApurationTypeButton.setAttribute('data-customer-id', customerId);

	const showProductStockModalButton = document.getElementById('showProductStockModalButton');
	showProductStockModalButton.setAttribute('data-customer-id', customerId);

	const showCurrentAccountBalanceInfoModalButton = document.getElementById('showCurrentAccountBalanceInfoModalButton');
	showCurrentAccountBalanceInfoModalButton.setAttribute('data-customer-id', customerId);

	const setCurrentAccountBalanceModalButton = document.getElementById('setCurrentAccountBalanceModalButton');
	setCurrentAccountBalanceModalButton.setAttribute('data-customer-id', customerId);

	const backToDetailsButtons = document.querySelectorAll('.back-to-details');
	backToDetailsButtons.forEach(button => {
		button.setAttribute('data-customer-id', customerId);
	});
}

async function requestIsOk(request, modal, loading) {
	if (!request.ok) {
		const responseError = await request.text();

		loading.style.display = 'none';

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


showEmployeesModal.addEventListener('shown.bs.modal', function(event) {
	const button = event.relatedTarget;

	const customerId = button.getAttribute('data-customer-id');

	debouncedLoadCustomerEmployeeInfos(customerId);
});

async function loadCustomerEmployeeInfos(customerId) {
	const url = `/api/customers/employees/${customerId}`;


	const loading = document.getElementById('employeeLoading');
	const contentArea = document.getElementById('employeeContainer');

	loading.style.display = 'flex';
	contentArea.style.display = 'none';

	const request = await fetch(url);

	try {
		await requestIsOk(request, showEmployeesModal, loading)

		const response = await request.json();

		fillCustomerEmployeeInfos(response);
	}
	catch (error) {
		console.error("Erro ao carregar funcionários:", error);
	}
}

function fillCustomerEmployeeInfos(response) {
	document.getElementById('employeeLoading').style.display = 'none';
	document.getElementById('employeeContainer').style.display = 'block';


	const tableBody = document.getElementById('employeesTableBody');
	tableBody.innerHTML = '';

	response.forEach(dto => {
		const tr = document.createElement('tr');

		const tdCpf = document.createElement('td');
		tdCpf.textContent = dto.cpf;
		tr.appendChild(tdCpf);

		const tdName = document.createElement('td');
		tdName.textContent = dto.name;
		tr.appendChild(tdName);

		const tdFunctions = document.createElement('td');
		tdFunctions.textContent = dto.functions.join(", ");
		tr.appendChild(tdFunctions);

		const tdApurationTypes = document.createElement('td');
		tdApurationTypes.textContent = dto.apurationTypes.join(", ");
		tr.appendChild(tdApurationTypes);

		tableBody.appendChild(tr);
	});

}

showTablePriceModal.addEventListener('shown.bs.modal', function(event) {
	const button = event.relatedTarget;

	const customerId = button.getAttribute('data-customer-id');

	debouncedLoadCustomerTablePriceInfos(customerId);
});

async function loadCustomerTablePriceInfos(customerId) {
	const url = `/api/customers/table-price/${customerId}`;

	const loading = document.getElementById('tablePriceLoading');
	const contentArea = document.getElementById('tablePriceContainer');

	loading.style.display = 'flex';
	contentArea.style.display = 'none';

	const request = await fetch(url);

	try {
		await requestIsOk(request, showTablePriceModal, loading)

		const response = await request.json();

		fillCustomerTablePriceInfos(response);
	}
	catch (error) {
		console.error("Erro ao carregar funcionários:", error);
	}
}

async function fillCustomerTablePriceInfos(response) {
	document.getElementById('tablePriceLoading').style.display = 'none';
	document.getElementById('tablePriceContainer').style.display = 'block';

	const tableBody = document.getElementById('tablePriceTableBody');
	tableBody.innerHTML = '';

	response.forEach(dto => {
		const tr = document.createElement('tr');

		const tdCode = document.createElement('td');
		tdCode.textContent = dto.productCode;
		tr.appendChild(tdCode);

		const tdName = document.createElement('td');
		tdName.textContent = dto.productName;
		tr.appendChild(tdName);

		const tdPrice = document.createElement('td');
		tdPrice.textContent = dto.price;
		tr.appendChild(tdPrice);

		tableBody.appendChild(tr);
	});
}


findIncentiveValueByCustomerIdAndApurationTypeButton.addEventListener('click', function(event) {
	const button = event.target;

	const customerId = button.getAttribute('data-customer-id');

	debouncedLoadCustomerIncentiveValue(customerId);
});

async function loadCustomerIncentiveValue(customerId) {
	const apurationType = document.getElementById('apurationTypeSelect').value;

	if (!apurationType || apurationType === 'default') {
		alert('Nenhuma Apuração Selecionada! \nPor favor selecione um tipo de apuração para buscar');
		return;
	}

	const url = `/incentive-value/a/${customerId}/${apurationType}`;

	const loading = document.getElementById('incentiveValuesLoading');
	const contentArea = document.getElementById('incentiveValueArea');

	loading.style.display = 'flex';
	contentArea.style.display = 'none';

	const request = await fetch(url);

	try {
		await requestIsOk(request, showIncentiveValuesModal, loading);

		const response = await request.text();

		fillCustomerIncentiveValues(response);
	}
	catch (error) {
		console.error("Erro ao carregar funcionários:", error);
	}
}

async function fillCustomerIncentiveValues(response) {
	document.getElementById('incentiveValuesLoading').style.display = 'none';
	document.getElementById('incentiveValueArea').style.display = 'block';

	const incentiveValueArea = document.getElementById('incentiveValueArea');
	incentiveValueArea.innerHTML = response;
}



showProductStockModal.addEventListener('shown.bs.modal', function(event) {
	const button = event.relatedTarget;

	const customerId = button.getAttribute('data-customer-id');

	debouncedLoadCustomerProductStock(customerId);
});

async function loadCustomerProductStock(customerId) {
	const url = `/api/customers/product-stock/${customerId}`;

	const loading = document.getElementById('productStockLoading');
	const contentArea = document.getElementById('productStockContainer');

	loading.style.display = 'flex';
	contentArea.style.display = 'none';

	const request = await fetch(url);

	try {
		await requestIsOk(request, showProductStockModal, loading)

		const response = await request.json();

		fillCustomerProductStock(response);
	}
	catch (error) {
		console.error("Erro ao carregar funcionários:", error);
	}
}

async function fillCustomerProductStock(response) {
	document.getElementById('productStockLoading').style.display = 'none';
	document.getElementById('productStockContainer').style.display = 'block';

	const tableBody = document.getElementById('productStockTableBody');
	tableBody.innerHTML = '';

	response.forEach(dto => {
		const tr = document.createElement('tr');

		const tdCode = document.createElement('td');
		tdCode.textContent = dto.productCode;
		tr.appendChild(tdCode);

		const tdName = document.createElement('td');
		tdName.textContent = dto.productName;
		tr.appendChild(tdName);

		const tdQtd = document.createElement('td');
		tdQtd.textContent = dto.quantity;
		tr.appendChild(tdQtd);

		tableBody.appendChild(tr);
	});
}




showCurrentAccountBalanceInfoModal.addEventListener('shown.bs.modal', function(event) {
	const button = event.relatedTarget;

	const customerId = button.getAttribute('data-customer-id');

	debouncedLoadCustomerCurrentAccountInfos(customerId);
});

async function loadCustomerCurrentAccountInfos(customerId) {
	const url = `/api/customers/current-account/${customerId}`;

	const loading = document.getElementById('currentAccountBalanceLoading');
	const contentArea = document.getElementById('currentAccountBalanceContainer');

	loading.style.display = 'flex';
	contentArea.style.display = 'none';

	const request = await fetch(url);

	try {
		await requestIsOk(request, showCurrentAccountBalanceInfoModal, loading)

		const response = await request.json();

		fillCustomerCurrentAccountInfos(response);
	}
	catch (error) {
		console.error("Erro ao carregar funcionários:", error);
	}
}

async function fillCustomerCurrentAccountInfos(response) {
	document.getElementById('currentAccountBalanceLoading').style.display = 'none';
	document.getElementById('currentAccountBalanceContainer').style.display = 'block';


	const currentAccountCustomerFantasyName = document.getElementById('currentAccountCustomerFantasyName');
	const currentAccountCustomerCnpj = document.getElementById('currentAccountCustomerCnpj');
	const currentAccountBalance = document.getElementById('currentAccountBalance');

	const tableBody = document.getElementById('currentAccountCustomerProductStock');
	tableBody.innerHTML = '';

	currentAccountCustomerFantasyName.textContent = response.fantasyName || '';
	currentAccountCustomerCnpj.textContent = response.cnpj || '';
	currentAccountBalance.textContent = response.balance || '';

	response.customerProductStockItems.forEach(dto => {
		const tr = document.createElement('tr');

		const tdCode = document.createElement('td');
		tdCode.textContent = dto.productCode;
		tr.appendChild(tdCode);

		const tdName = document.createElement('td');
		tdName.textContent = dto.productName;
		tr.appendChild(tdName);

		const tdQtd = document.createElement('td');
		tdQtd.textContent = dto.quantity;
		tr.appendChild(tdQtd);

		const tdCcValue = document.createElement('td');
		tdCcValue.textContent = dto.ccValue;
		tr.appendChild(tdCcValue);

		const tdBalance = document.createElement('td');
		tdBalance.textContent = dto.balance;
		tr.appendChild(tdBalance);

		tableBody.appendChild(tr);
	})

	const tr = document.createElement('tr');

	const tdTotalText = document.createElement('td');
	tdTotalText.colSpan = 4;
	tdTotalText.innerHTML = '<strong>Total</strong>';

	const tdTotalValue = document.createElement('td');
	tdTotalValue.textContent = response.balance || '';

	tr.appendChild(tdTotalText);
	tr.appendChild(tdTotalValue);

	tableBody.appendChild(tr);
}


setCurrentAccountBalanceModal.addEventListener('shown.bs.modal', function(event) {
	const button = event.relatedTarget;

	const customerId = button.getAttribute('data-customer-id');

	debouncedLoadCustomerSetCurrentAccount(customerId);
});


async function loadCustomerSetCurrentAccount(customerId) {
	const url = `/api/customers/set-current-account/${customerId}`;

	const loading = document.getElementById('setCurrentAccountBalanceLoading');
	const contentArea = document.getElementById('setCurrentAccountBalanceContainer');

	loading.style.display = 'flex';
	contentArea.style.display = 'none';

	const request = await fetch(url);

	try {
		await requestIsOk(request, setCurrentAccountBalanceModal, loading);

		const response = await request.json();

		fillCustomerSetCurrentAccount(response, customerId);
	}
	catch (error) {
		console.error("Erro ao carregar funcionários:", error);
	}
}

async function fillCustomerSetCurrentAccount(response, customerId) {
	document.getElementById('setCurrentAccountBalanceLoading').style.display = 'none';
	document.getElementById('setCurrentAccountBalanceContainer').style.display = 'block';

	const setCurrentAccountCustomerFantasyName = document.getElementById('setCurrentAccountCustomerFantasyName');
	const setCurrentAccountCustomerCnpj = document.getElementById('setCurrentAccountCustomerCnpj');

	const tableBody = document.getElementById('setCurrentAccountProductStock');
	tableBody.innerHTML = '';

	setCurrentAccountCustomerFantasyName.textContent = response.fantasyName || '';
	setCurrentAccountCustomerCnpj.textContent = response.cnpj || '';

	response.customerProductStockItems.forEach(dto => {
		const tr = document.createElement('tr');
		tr.setAttribute('data-product-id', dto.id);

		const tdCode = document.createElement('td');
		tdCode.textContent = dto.productCode;
		tr.appendChild(tdCode);

		const tdName = document.createElement('td');
		tdName.textContent = dto.productName;
		tr.appendChild(tdName);

		const tdInput = document.createElement('td');

		const input = document.createElement('input');
		input.type = 'number';
		input.className = 'form-control';
		input.id = `quantity${dto.id}`;
		input.name = `quantity${dto.id}`;
		input.setAttribute('data-quantity-product-id', dto.id);
		tdInput.appendChild(input);
		tr.appendChild(tdInput);

		tableBody.appendChild(tr);
	});

	const setStockButton = document.getElementById('setStockButton');
	setStockButton.onclick = null;
	setStockButton.onclick = () => setStock(customerId);

}


editCustomerModal.addEventListener('shown.bs.modal', function(event) {
	const button = event.relatedTarget;

	const customerId = button.getAttribute('data-customer-id');

	debouncedLoadEdit(customerId);
});

async function loadEditCustomerInfos(customerId) {
	const url = `/api/customers/edit-data/${customerId}`;

	const loading = document.getElementById('editCustomerLoading');
	const contentArea = document.getElementById('editCustomerFormContainer');

	loading.style.display = 'flex';
	contentArea.style.display = 'none';

	const request = await fetch(url);

	try {
		await requestIsOk(request, editCustomerModal, loading)

		const response = await request.json();

		fillEditCustomerInfos(response);
	}
	catch (error) {
		console.error("Erro ao carregar funcionários:", error);
	}
}

async function fillEditCustomerInfos(response) {
	document.getElementById('editCustomerLoading').style.display = 'none';
	document.getElementById('editCustomerFormContainer').style.display = 'block';

	document.getElementById('input-id').value = response.id;
	document.getElementById('input-unysoftCode').value = response.unysoftCode;
	document.getElementById('input-cnpj').value = response.cnpj;
	document.getElementById('input-registeredName').value = response.registeredName;
	document.getElementById('input-fantasyName').value = response.fantasyName;
	document.getElementById('input-address').value = response.address;


	const fillSelect = (selId, list, valueKey, textKey, selectedValue) => {
		const sel = document.getElementById(selId);
		sel.innerHTML = '';
		list.forEach(item => {
			const opt = document.createElement('option');
			opt.value = item[valueKey];
			opt.text = item[textKey];
			if (String(item[valueKey]) === String(selectedValue)) opt.selected = true;
			sel.appendChild(opt);
		});
	};

	fillSelect('select-group', response.groups, 'id', 'name', response.groupId);
	fillSelect('select-industry', response.industries, 'id', 'name', response.industryId);
	fillSelect('select-flag', response.flags, 'id', 'name', response.flagId);
	fillSelect('select-user', response.users, 'id', 'name', response.userId);
	fillSelect('select-mechanicApuration', response.mechanicApurations, 'id', 'name', response.mechanicApurationId);

	const fillSelectActive = (selId, list, selectedValue) => {
		const sel = document.getElementById(selId);
		sel.innerHTML = '';
		list.forEach(item => {
			const opt = document.createElement('option');
			opt.value = item;
			opt.text = item == true ? 'Sim' : 'Não';
			if (item == selectedValue) opt.selected = true;
			sel.appendChild(opt);
		});
	};

	fillSelectActive('selectActive', [true, false], response.active);


	const empSel = document.getElementById('select-employees');
	empSel.innerHTML = '';
	response.employees.forEach(emp => {
		const opt = document.createElement('option');
		opt.value = emp.id;
		opt.text = emp.name;
		if (response.employeeIds.includes(emp.id)) opt.selected = true;
		empSel.appendChild(opt);
	});

	document.getElementById('editCustomerForm')
		.setAttribute('action', `/customers/update/${response.id}`);

}

document.addEventListener('DOMContentLoaded', () => {
	const container = document.getElementById('customer-table');

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




