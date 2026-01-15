const detailsEmployeeModal = document.getElementById('detailsEmployeeModal');
const editEmployeeModal = document.getElementById('editEmployeeModal');

const employeeDetails = document.getElementById('employeeDetails');
const employeeEdit = document.getElementById('employeeEdit');

const loadingEmployeeDetails = document.getElementById('loadingEmployeeDetails');
const loadingEmployeeEdit = document.getElementById('loadingEmployeeEdit');

function debounce(fn, wait = 300) {
	let timer = null;
	return function (...args) {
		clearTimeout(timer);
		timer = setTimeout(() => fn.apply(this, args), wait);
	};
}

const debouncedLoadEmployeeDetails = debounce(loadEmployeeDetails, 400);
const debouncedLoadEmployeeEdit = debounce(loadEmployeeEdit, 400);

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

detailsEmployeeModal.addEventListener('shown.bs.modal', function (event) {
	const employeeId = event.relatedTarget.getAttribute('data-employee-id');
	debouncedLoadEmployeeDetails(employeeId);
});

async function loadEmployeeDetails(employeeId) {
	await startLoadingInfos(loadingEmployeeDetails, employeeDetails);

	const url = `/api/employees/details/${employeeId}`;

	try {
		const request = await fetch(url);

		await requestIsOk(request, detailsEmployeeModal, loadingEmployeeDetails);

		const response = await request.json();

		fillDetailsEmployeeModal(response);
	}
	catch (error) {
		console.log(error);
	}
}

async function fillDetailsEmployeeModal(response) {
	await endLoadingInfos(loadingEmployeeDetails, employeeDetails);

	const idText = document.getElementById('idText');
	const cpfText = document.getElementById('cpfText');
	const nameText = document.getElementById('nameText');
	const emailText = document.getElementById('emailText');
	const phoneText = document.getElementById('phoneText');
	const birthDateText = document.getElementById('birthDateText');
	const functionsText = document.getElementById('functionsText');
	const customersText = document.getElementById('customersText');
	const apurationTypesText = document.getElementById('apurationTypesText');
	const paymentMethodText = document.getElementById('paymentMethodText');
	const activeText = document.getElementById('activeText');

	activeText.textContent = response.active == true ? 'Ativo' : 'Inativo';
	idText.textContent = response.id || '-';
	cpfText.textContent = response.cpf || '-';
	nameText.textContent = response.name || '-';
	emailText.textContent = response.email || '-';
	phoneText.textContent = response.phone || '-';
	birthDateText.textContent = response.birthDate || '-';
	functionsText.textContent = response.functions.join(", ") || '-';
	customersText.textContent = response.customers.join(", ") || '-';
	apurationTypesText.textContent = response.apurationTypes.join(", ") || '-';
	paymentMethodText.textContent = response.paymentMethod || '-';

}

editEmployeeModal.addEventListener('shown.bs.modal', function (event) {
	const employeeId = event.relatedTarget.getAttribute('data-employee-id');
	debouncedLoadEmployeeEdit(employeeId);
});

async function loadEmployeeEdit(employeeId) {
	await startLoadingInfos(loadingEmployeeEdit, employeeEdit);

	const url = `/api/employees/edit-data/${employeeId}`;

	try {
		const request = await fetch(url);

		await requestIsOk(request, editEmployeeModal, editEmployeeModal);

		const response = await request.json();

		fillEmployeeEdit(response);
	}
	catch (error) {
		console.log(error)
	}
}

async function fillEmployeeEdit(response) {
	await endLoadingInfos(loadingEmployeeEdit, employeeEdit);

	document.getElementById('input-id').value = response.id;
	document.getElementById('editCpf').value = response.cpf;
	document.getElementById('editName').value = response.name;
	document.getElementById('editEmail').value = response.email;
	document.getElementById('editPhone').value = response.phone;
	document.getElementById('editBirthdate').value = response.birthDate;

	const fillMultipleSelect = (selectId, fullList, id, name, selectedItems) => {
		const select = document.getElementById(selectId);
		select.innerHTML = '';
		fullList.forEach(item => {
			const option = document.createElement('option');
			option.value = item[id];
			option.text = item[name];
			if (selectedItems.includes(item[id])) option.selected = true;
			select.appendChild(option);
		});
	}

	const fillSimpleSelect = (selId, list, valueKey, textKey, selectedValue) => {
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

	fillMultipleSelect('multiSelectFunctionsEdit', response.functions, 'id', 'name', response.selectedFunctions);
	fillMultipleSelect('multiSelectCustomersEdit', response.customers, 'id', 'name', response.selectedCustomers);
	fillMultipleSelect('multiSelectApurationTypesEdit', response.apurationTypes, 'id', 'name', response.selectedApurationTypes);
	fillSimpleSelect('editPaymentMethod', response.paymentMethods, 'id', 'name', response.selectedPaymentMethod);

	const fillSelectActive = (selId, list, selectedValue) => {
		const sel = document.getElementById(selId);
		sel.innerHTML = '';
		list.forEach(item => {
			const opt = document.createElement('option');
			opt.value = item;
			opt.text = item == true ? 'Ativo' : 'Inativo';
			if (item == selectedValue) opt.selected = true;
			sel.appendChild(opt);
		});
	};

	fillSelectActive('editStatus', [true, false], response.active);

	document.getElementById('editEmployeeForm')
		.setAttribute('action', `/employees/update/${response.id}`);
}

document.addEventListener('DOMContentLoaded', () => {
	const filterButton = document.getElementById('filterButton');
	const container = document.getElementById('employee-table');

	filterButton.addEventListener('click', async () => {
	});

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