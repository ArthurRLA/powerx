const detailsUserModal = document.getElementById('detailsUserModal');
const showCustomersModal = document.getElementById('showCustomersModal');
const editUserModal = document.getElementById('editUserModal');

const userDetails = document.getElementById('userDetails');
const userCustomersDetails = document.getElementById('userCustomersDetails');
const editUserDetailsArea = document.getElementById('editUserDetailsArea');

const loadingUserInfos = document.getElementById('loadingUserInfos');
const loadingUserCustomerInfos = document.getElementById('loadingUserCustomerInfos');
const loadingUserEdit = document.getElementById('loadingUserEdit');

function debounce(fn, wait = 300) {
	let timer = null;
	return function(...args) {
		clearTimeout(timer);
		timer = setTimeout(() => fn.apply(this, args), wait);
	};
}

const debouncedLoadUserDetails = debounce(loadUserDetails, 400);
const debouncedLoadUserCustomersDetails = debounce(loadUserCustomersDetails, 400);
const debouncedLoadUserEdit = debounce(loadUserEdit, 400);

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

detailsUserModal.addEventListener('shown.bs.modal', function(event) {
	const userId = event.relatedTarget.getAttribute('data-user-id');
	debouncedLoadUserDetails(userId);
});

async function loadUserDetails(userId) {
	await startLoadingInfos(loadingUserInfos, userDetails);

	const url = `/api/users/details/${userId}`;

	try {
		const request = await fetch(url);

		await requestIsOk(request, detailsUserModal, loadingUserInfos);

		const response = await request.json();

		fillDetailsUserModal(response);
	}
	catch (error) {
		console.log(error);
	}
}

async function fillDetailsUserModal(response) {
	await endLoadingInfos(loadingUserInfos, userDetails);

	const userIdText = document.getElementById('userIdText');
	const userUnysoftCodeText = document.getElementById('userUnysoftCodeText');
	const userNameText = document.getElementById('userNameText');
	const userBirthDateText = document.getElementById('userBirthDateText');
	const userCpfText = document.getElementById('userCpfText');
	const userAddressText = document.getElementById('userAddressText');
	const userEmailText = document.getElementById('userEmailText');
	const userPositionText = document.getElementById('userPositionText');
	const userStateText = document.getElementById('userStateText');
	const userPhoneText = document.getElementById('userPhoneText');
	const userCreationDateText = document.getElementById('userCreationDateText');
	const userStartOfActivitiesText = document.getElementById('userStartOfActivitiesText');
	const userLastUpdateText = document.getElementById('userLastUpdateText');
	const userActiveText = document.getElementById('userActiveText');
	const userRoleText = document.getElementById('userRoleText');

	userIdText.textContent = response.id || '-';
	userUnysoftCodeText.textContent = response.unysoftCode || '-';
	userNameText.textContent = response.name || '-';
	userBirthDateText.textContent = response.birthDate || '-';
	userCpfText.textContent = response.cpf || '-';
	userAddressText.textContent = response.address || '-';
	userEmailText.textContent = response.email || '-';
	userPositionText.textContent = response.position || '-';
	userStateText.textContent = response.state || '-';
	userPhoneText.textContent = response.phone || '-';
	userCreationDateText.textContent = response.creationDate || '-';
	userStartOfActivitiesText.textContent = response.startOfActivities || '-';
	userLastUpdateText.textContent = response.lastUpdate || '-';
	userActiveText.textContent = response.active || '-';
	userRoleText.textContent = response.role || '-';

	const userCustomersBtn = document.getElementById('userCustomersBtn');
	userCustomersBtn.setAttribute('data-user-id', response.id);

	const backToDetailsButtons = document.querySelectorAll('.back-to-details');
	backToDetailsButtons.forEach(button => {
		button.setAttribute('data-user-id', response.id);
	});

}

showCustomersModal.addEventListener('shown.bs.modal', function(event) {
	const userId = event.relatedTarget.getAttribute('data-user-id');
	debouncedLoadUserCustomersDetails(userId);
});


async function loadUserCustomersDetails(userId) {
	await startLoadingInfos(loadingUserCustomerInfos, userCustomersDetails);

	const url = `/api/customers/by-user/${userId}`;

	try {

		const request = await fetch(url);

		await requestIsOk(request, showCustomersModal, loadingUserCustomerInfos);

		const response = await request.json();

		fillDetailsUserCustomersModal(response);

	} catch (error) {
		console.log(error);
	}
}

async function fillDetailsUserCustomersModal(response) {
	await endLoadingInfos(loadingUserCustomerInfos, userCustomersDetails);

	const customers = response.customers;

	const tableBody = document.getElementById('userCustomersTableBody');
	tableBody.innerHTML = '';

	customers.forEach(customer => {
		const tr = document.createElement('tr');

		const tdId = document.createElement('td');
		tdId.textContent = customer.id;
		tr.appendChild(tdId);

		const tdCnpj = document.createElement('td');
		tdCnpj.textContent = customer.cnpj;
		tr.appendChild(tdCnpj);

		const tdFantasyName = document.createElement('td');
		tdFantasyName.textContent = customer.fantasyName;
		tr.appendChild(tdFantasyName);

		tableBody.appendChild(tr);
	});

}



editUserModal.addEventListener('shown.bs.modal', function(event) {
	const userId = event.relatedTarget.getAttribute('data-user-id');
	debouncedLoadUserEdit(userId);
});

async function loadUserEdit(userId) {
	await startLoadingInfos(loadingUserEdit, editUserDetailsArea);

	const url = `/api/users/edit-data/${userId}`;

	try {
		const request = await fetch(url);

		await requestIsOk(request, editUserModal, loadingUserEdit);

		const response = await request.json();

		fillUserEdit(response, userId);
	}
	catch (error) {
		console.log(error)
	}
}

async function fillUserEdit(response, userId) {
	await endLoadingInfos(loadingUserEdit, editUserDetailsArea);

	document.getElementById('editUnysoftCode').value = response.unysoftCode;
	document.getElementById('editUsername').value = response.name;
	document.getElementById('editCpf').value = response.cpf;
	document.getElementById('editBirthday').value = response.birthDate;
	document.getElementById('editAddress').value = response.address;
	document.getElementById('editPhone').value = response.phone;
	document.getElementById('editEmail').value = response.email;


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

	fillMultipleSelect('editCustomersSelect', response.allAvailableCustomers, 'id', 'name', response.currentCustomers);
	fillSimpleSelect('editPositionSelect', response.allPositions, 'id', 'name', response.currentPosition);
	fillSimpleSelect('editStateSelect', response.allStates, 'id', 'name', response.currentState);
	fillSimpleSelect('editRoleSelect', response.allRoles, 'id', 'name', response.currentRole);

	const fillActiveSelect = (selId, list, selectedValue) => {
		const sel = document.getElementById(selId);
		sel.innerHTML = '';
		list.forEach(item => {
			const opt = document.createElement('option');
			opt.value = item;
			opt.text = item == true ? 'Sim' : 'Não' ;
			if (item == selectedValue) opt.selected = true;
			sel.appendChild(opt);
		});
	};

	fillActiveSelect('editActiveSelect', [true, false], response.active);

	document.getElementById('userUpdateForm')
		.setAttribute('action', `/users/update/${userId}`);
}

document.addEventListener('DOMContentLoaded', () => {

	const filterButton = document.getElementById('filterButton');
	const container = document.getElementById('user-table');

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