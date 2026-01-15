const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

const newIncentiveBtn = document.getElementById('newIncentiveBtn');

const newIncentivePage1 = document.getElementById('page1');
const customerSelect = document.getElementById('newIncentiveCustomerSelect');
const newIncentiveGoToPage2Btn = document.getElementById('go-to-page-2');

const newIncentivePage2 = document.getElementById('page2');
const consultantSelect = document.getElementById('newIncentiveConsultantSelect');
const consultantSalesTableBody = document.getElementById('consultantSalesTableBody');
const consultantResumeSalesTableBody = document.getElementById('consultantResumeSalesTableBody');

const noConsultantsAvailable = document.getElementById('noConsultantsAvailable');
const consultantModalBody = document.getElementById('consultantModalBody');
const noMechanicsAvailable = document.getElementById('noMechanicsAvailable');
const mechanicsModalBody = document.getElementById('mechanicsModalBody');

const consultantResumeSalesModal = document.getElementById('consultantResumeSalesModal');
const consultantResumeSalesModalTableBody = document.getElementById('consultantResumeSalesModalTableBody');

const saveConsultantSaleBtn = document.getElementById('saveConsultantSale');

const consultantConfirmUpdateBtn = document.getElementById('consultantConfirmUpdateBtn');

const exitConfirmModal = document.getElementById('exitConfirm');
const exitConfirmBtn = document.getElementById('exitConfirmBtn');

const newIncentivePage3 = document.getElementById('page3');
const saveMechanicSaleBtn = document.getElementById('saveMechanicSale');
const newIncentiveMechanicSelect = document.getElementById('newIncentiveMechanicSelect');
const mechanicSalesTableBody = document.getElementById('mechanicSalesTableBody');
const mechanicResumeSalesTableBody = document.getElementById('mechanicResumeSalesTableBody');
const mechanicResumeSalesModalTableBody = document.getElementById('mechanicResumeSalesModalTableBody');
const mechanicConfirmUpdateBtn = document.getElementById('mechanicConfirmUpdateBtn');
const resumeMecName = document.getElementById('resumeMecName');
const mechanicResumeSalesModal = document.getElementById('mechanicResumeSalesModal');


const saveTinkerSaleBtn = document.getElementById('saveTinkerSale');

async function requestIsOk(request) {
    if (!request.ok) {
        const responseError = await request.text();
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

function fillSelect(selId, list, valueKey, textKey) {
    const sel = document.getElementById(selId);

    sel.innerHTML = '';

    const optDefault = document.createElement('option');
    optDefault.value = 'default';
    optDefault.text = 'Selecione uma Opção';
    sel.appendChild(optDefault);

    list.forEach(item => {
        const opt = document.createElement('option');
        opt.value = item[valueKey];
        opt.text = item[textKey];
        sel.appendChild(opt);
    });
};


newIncentiveBtn.addEventListener('click', function () {
    const page1ModalInstance = bootstrap.Modal.getOrCreateInstance(newIncentivePage1);
    page1ModalInstance.show();
});

newIncentiveGoToPage2Btn.addEventListener('click', async function () {
    newIncentiveGoToPage2Btn.disabled = true;
    newIncentiveGoToPage2Btn.textContent = 'avançando ...';

    const customerId = customerSelect.value;

    if (!customerId || customerId === 'default') {
        alert('Selecione um Cliente!');
        return;
    }

    await loadPage2Infos(customerId);
    const page2ModalInstance = bootstrap.Modal.getOrCreateInstance(newIncentivePage2);
    page2ModalInstance.show();

    newIncentiveGoToPage2Btn.disabled = false;
    newIncentiveGoToPage2Btn.textContent = 'avançar';
});

let sales = [];
let employees = [];
let products = [];
let mechanicCanBeGreater = false;

document.addEventListener('DOMContentLoaded', function () {
    initializeSelect2();
    setNewActionForCloseBtns();
    configureExitConfirmModal();

    saveConsultantSaleBtn.addEventListener('click', saveConsultantSale);
    saveMechanicSaleBtn.addEventListener('click', saveMechanicSale);
    saveTinkerSaleBtn.addEventListener('click', saveTinkerSale);

    const container = document.getElementById('incentivesTableArea');

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

function initializeSelect2() {

    $('#filterModal').on('shown.bs.modal', function () {
        const $modal = $(this);
        ['#customerFilterSelect', '#userSelect'].forEach(selector => {
            $modal.find(selector).select2({
                placeholder: $(selector).attr('placeholder') || '',
                dropdownParent: $modal,
                width: '100%'
            }).on('select2:open', function () {
                $('.select2-dropdown').css('z-index', 1060);
            });
        });

        if (flatpickr("#start").length > 0) {
            flatpickr("#start")[0].destroy();
        }
        if (flatpickr("#end").length > 0) {
            flatpickr("#end")[0].destroy();
        }

        flatpickr("#start", {
            dateFormat: "Y-m-d",
            appendTo: $modal[0],
            static: true,
            clickOpens: true
        });

        flatpickr("#end", {
            dateFormat: "Y-m-d",
            appendTo: $modal[0],
            static: true,
            clickOpens: true
        });
    });

    $("#page1").on('shown.bs.modal', function () {
        $('#newIncentiveCustomerSelect').select2({
            placeholder: "Selecione uma opção",
            allowClear: true,
            closeOnSelect: true,
            dropdownParent: $('#page1')
        });
    });
}

function setMessageDisplay(message, body, on) {
    if (on) {
        message.style.display = 'block';
        body.style.display = 'none';
    } else {
        message.style.display = 'none';
        body.style.display = 'block';
    }
}



function setNewActionForCloseBtns() {
    const closeBtns = document.querySelectorAll('.btn-close');
    closeBtns.forEach(btn => {
        btn.addEventListener('click', function () {
            const exitConfirmModalInstace = bootstrap.Modal.getOrCreateInstance(exitConfirmModal);
            exitConfirmModalInstace.show();
        });
    });
}

function configureExitConfirmModal() {
    exitConfirmBtn.addEventListener('click', function () {
        const pagesModal = document.querySelectorAll('.incentive-page-modal');

        pagesModal.forEach(modal => {
            if (modal) {
                const instance = bootstrap.Modal.getOrCreateInstance(modal);
                if (instance) {
                    instance.hide();
                }
            }
        });

        sales = [];
        employees = [];
        products = [];
        mechanicCanBeGreater = false;

        eraseConsultantInfos();
        eraseMechanicInfos();
        eraseTinkerInfos();

        const exitConfirmModalInstace = bootstrap.Modal.getOrCreateInstance(exitConfirmModal);
        exitConfirmModalInstace.hide();
    });
}

async function loadPage2Infos(customerId) {
    const url = `/api/customers/${customerId}/Consultor Técnico`;

    try {
        const request = await fetch(url);

        await requestIsOk(request);

        const response = await request.json();

        fillPage2(response);

    } catch (error) {
        console.log(error);
    }
}

function fillPage2(response) {
    const consultants = response.employees;
    const customerProducts = response.products;
    const mechanicAp = response.mechanicCanBeGreater;

    if (mechanicAp === true) {
        mechanicCanBeGreater = true;
    }
    else {
        mechanicCanBeGreater = false;
    }

    if (!consultants || consultants.length == 0) {
        setMessageDisplay(noConsultantsAvailable, consultantModalBody, true);
        return;
    }

    if (Array.isArray(consultants)) {
        employees = consultants;
    } else if (typeof consultants === 'object' && consultants !== null) {
        employees = Object.values(consultants);
    } else {
        employees = [];
        console.warn('response.employees não é array nem objeto válido!');
    }

    if (Array.isArray(customerProducts)) {
        products = customerProducts;
    } else if (typeof customerProducts === 'object' && customerProducts !== null) {
        products = Object.values(customerProducts);
    } else {
        products = [];
        console.warn('response.products não é array nem objeto válido!');
    }

    fillSelect('newIncentiveConsultantSelect', consultants, 'id', 'name');
    fillProductTable(customerProducts);
}

function fillProductTable(products) {
    consultantSalesTableBody.innerHTML = '';

    products.forEach(product => {
        const tr = document.createElement('tr');
        tr.setAttribute('data-product-id', product.id);

        const codTd = document.createElement('td');
        codTd.textContent = product.productCode;
        codTd.className = 'small';
        tr.appendChild(codTd);

        const nameTd = document.createElement('td');
        nameTd.textContent = product.productName;
        nameTd.className = 'small';
        tr.appendChild(nameTd);

        const qtdTd = document.createElement('td');
        const qtdInput = document.createElement('input');
        qtdInput.type = 'number';
        qtdInput.className = 'form-control  small';
        qtdTd.appendChild(qtdInput);
        tr.appendChild(qtdTd);

        const ivTd = document.createElement('td');
        ivTd.textContent = product.incentiveValue;
        ivTd.className = 'small';
        tr.appendChild(ivTd);

        const totalTd = document.createElement('td');
        totalTd.className = 'small';
        tr.appendChild(totalTd);

        qtdInput.addEventListener('change', function () {
            const iv = parseFloat(ivTd.textContent);;
            const qtd = parseFloat(qtdInput.value) || 0; const total = iv * qtd;
            totalTd.textContent = "R$ " + total.toFixed(2);
        });

        consultantSalesTableBody.appendChild(tr);
    });
}

function saveConsultantSale() {
    const customerId = customerSelect.value;
    const employeeId = consultantSelect.value;

    if (!employeeId || employeeId === 'default') {
        alert('selecione um Premiado');
        return;
    }

    let newSale = {
        customer: customerId,
        employee: employeeId,
        function: 'Consultor Técnico',
        products: []
    }

    const rows = consultantSalesTableBody.querySelectorAll('tr');
    rows.forEach(row => {
        const productId = row.getAttribute('data-product-id');
        const quantityInput = row.querySelector('input');
        const quantity = parseInt(quantityInput.value) || 0;

        if (quantity >= 0) {
            newSale.products.push({
                productId: productId,
                quantity: quantity
            });
        }

    });

    if (newSale.products.length > 0) {
        sales.push(newSale);
        console.log(newSale);
        alert("Venda salva com sucesso!");

        const optionToRemove = consultantSelect.querySelector(`option[value="${employeeId}"]`);
        if (optionToRemove) {
            optionToRemove.remove();
        }

        consultantSelect.value = "default";

        rows.forEach(row => {
            const quantityInput = row.querySelector('input');
            if (quantityInput) {
                quantityInput.value = "";
            }

            if (row) {
                const totalTd = row.children[4];
                totalTd.textContent = "";
            }
        });
    } else {
        alert("Preencha ao menos um produto com uma quantidade válida.");
    }

    fillResumeConsultantTable(newSale);
}

function fillResumeConsultantTable(newSale) {

    const emp = employees.find(e => e.id == newSale.employee);

    if (!emp) {
        console.error('Employee não encontrado para ID:', newSale.employee);
        alert('Erro: Consultor não encontrado!');
        return;
    }

    const empName = emp.name;
    const empId = emp.id;

    let total = 0;
    newSale.products.forEach(p => total += p.quantity);

    const tr = document.createElement('tr');
    tr.setAttribute('data-employee-id', empId);

    const empTd = document.createElement('td');
    empTd.textContent = empName;
    tr.appendChild(empTd);

    const totalTd = document.createElement('td');
    totalTd.textContent = total;
    tr.appendChild(totalTd);

    const actionTd = document.createElement('td');

    const editOrViewBtn = document.createElement('button');
    editOrViewBtn.textContent = 'Visualizar/Editar';
    editOrViewBtn.className = 'btn btn-warning';
    editOrViewBtn.setAttribute('data-employee-id', empId);
    editOrViewBtn.addEventListener('click', function () {
        fillResumeSaleModal(newSale, empName);
    })

    actionTd.appendChild(editOrViewBtn);
    tr.appendChild(actionTd);

    consultantResumeSalesTableBody.appendChild(tr);
}

function fillResumeSaleModal(sale, empName) {
    consultantResumeSalesModalTableBody.innerHTML = '';

    sale.products.forEach(product => {
        const productCode = products.find(p => p.id == product.productId).productCode;
        const tr = document.createElement('tr');
        tr.setAttribute('data-product-id', product.productId);

        const codTd = document.createElement('td');
        codTd.textContent = productCode;
        tr.appendChild(codTd);

        const qtdTd = document.createElement('td');
        const qtdInput = document.createElement('input');
        qtdInput.type = 'number';
        qtdInput.className = 'form-control';
        qtdInput.value = product.quantity;
        qtdTd.appendChild(qtdInput);
        tr.appendChild(qtdTd);

        consultantResumeSalesModalTableBody.appendChild(tr);
    });

    consultantConfirmUpdateBtn.onclick = function () {
        const updatedProducts = [];
        const rows = consultantResumeSalesModalTableBody.querySelectorAll('tr');
        rows.forEach(row => {
            const productId = row.getAttribute('data-product-id');
            const quantityInput = row.querySelector('input');
            const quantity = parseInt(quantityInput.value) || 0;
            if (quantity >= 0) {
                updatedProducts.push({ productId, quantity });
            }
        });
        updateConsultantSale(sale.employee, updatedProducts);
    };

    const resumeEmpName = document.getElementById('resumeEmpName');
    resumeEmpName.textContent = empName;

    const consultantResumeSalesInstance = bootstrap.Modal.getOrCreateInstance(consultantResumeSalesModal);
    consultantResumeSalesInstance.show();
}

function updateConsultantSale(empId, products) {
    const saleToUpdate = sales.find(s => s.employee === empId);
    if (saleToUpdate) {
        saleToUpdate.products = products;
        console.log('Venda atualizada:', saleToUpdate);
    } else {
        console.error('Venda não encontrada para atualização!');
    }

    const newTotal = products.reduce((sum, p) => sum + p.quantity, 0);

    const row = consultantResumeSalesTableBody
        .querySelector(`tr[data-employee-id="${empId}"]`);

    if (row) {
        const totalTd = row.children[1];
        totalTd.textContent = newTotal;
    }

    const consultantResumeSalesInstance = bootstrap.Modal.getOrCreateInstance(consultantResumeSalesModal);
    consultantResumeSalesInstance.hide();
}

function eraseConsultantInfos() {
    consultantResumeSalesModalTableBody.innerHTML = '';
    consultantResumeSalesTableBody.innerHTML = '';
    setMessageDisplay(noConsultantsAvailable, consultantModalBody, false);
}

const newIncentiveGoToPage3Btn = document.getElementById('go-to-page-3');

newIncentiveGoToPage3Btn.addEventListener('click', async function () {
    newIncentiveGoToPage3Btn.disabled = true;
    newIncentiveGoToPage3Btn.textContent = 'avançando ...';

    const customerId = customerSelect.value;

    await loadPage3Infos(customerId);

    setMessageDisplay(noTinkerAvailable, tinkerModalBody, false);

    const page3ModalInstance = bootstrap.Modal.getOrCreateInstance(newIncentivePage3);
    page3ModalInstance.show();

    newIncentiveGoToPage3Btn.disabled = false;
    newIncentiveGoToPage3Btn.textContent = 'avançar';
});

async function loadPage3Infos(customerId) {
    const url = `/api/customers/${customerId}/Mecânico`;

    try {
        const request = await fetch(url);

        await requestIsOk(request);

        const response = await request.json();

        fillPage3(response);

    } catch (error) {
        console.log(error);
    }
}

function fillPage3(response) {
    const mechanics = response.employees;
    const customerProducts = response.products;

    if (!mechanics || mechanics.length == 0) {
        setMessageDisplay(noMechanicsAvailable, mechanicsModalBody, true);
        return;
    }

    if (Array.isArray(mechanics)) {
        employees.push(...mechanics);
    } else if (typeof mechanics === 'object' && mechanics !== null) {
        employees.push(...Object.values(mechanics));
    } else {
        console.warn('response.employees não é array nem objeto válido!');
    }

    if (Array.isArray(customerProducts)) {
        products = customerProducts;
    } else if (typeof customerProducts === 'object' && customerProducts !== null) {
        products = Object.values(customerProducts);
    } else {
        products = [];
        console.warn('response.products não é array nem objeto válido!');
    }

    fillSelect('newIncentiveMechanicSelect', mechanics, 'id', 'name');
    fillMechanicProductTable(customerProducts);
}

function fillMechanicProductTable(products) {
    mechanicSalesTableBody.innerHTML = '';

    products.forEach(product => {
        const tr = document.createElement('tr');
        tr.setAttribute('data-product-id', product.id);

        const codTd = document.createElement('td');
        codTd.textContent = product.productCode;
        tr.appendChild(codTd);

        const nameTd = document.createElement('td');
        nameTd.textContent = product.productName;
        tr.appendChild(nameTd);

        const qtdTd = document.createElement('td');
        const qtdInput = document.createElement('input');
        qtdInput.type = 'number';
        qtdInput.className = 'form-control';
        qtdTd.appendChild(qtdInput);
        tr.appendChild(qtdTd);

        const ivTd = document.createElement('td');
        ivTd.textContent = product.incentiveValue;
        tr.appendChild(ivTd);

        const totalTd = document.createElement('td');
        tr.appendChild(totalTd);

        qtdInput.addEventListener('change', function () {
            const iv = parseFloat(ivTd.textContent);;
            const qtd = parseFloat(qtdInput.value) || 0; const total = iv * qtd;
            totalTd.textContent = "R$ " + total.toFixed(2);
        });

        mechanicSalesTableBody.appendChild(tr);
    });
}

function saveMechanicSale() {
    const customerId = customerSelect.value;
    const employeeId = newIncentiveMechanicSelect.value;

    if (!employeeId || employeeId === 'default') {
        alert('selecione um Premiado');
        return;
    }

    let newSale = {
        customer: customerId,
        employee: employeeId,
        function: 'Mecânico',
        products: []
    }

    const rows = mechanicSalesTableBody.querySelectorAll('tr');
    rows.forEach(row => {
        const productId = row.getAttribute('data-product-id');
        const quantityInput = row.querySelector('input');
        const quantity = parseInt(quantityInput.value) || 0;

        if (quantity >= 0) {
            newSale.products.push({
                productId: productId,
                quantity: quantity
            });
        }

    });

    if (newSale.products.length > 0) {
        sales.push(newSale);
        console.log(newSale);
        alert("Venda salva com sucesso!");

        const optionToRemove = newIncentiveMechanicSelect.querySelector(`option[value="${employeeId}"]`);
        if (optionToRemove) {
            optionToRemove.remove();
        }

        newIncentiveMechanicSelect.value = "default";

        rows.forEach(row => {
            const quantityInput = row.querySelector('input');
            if (quantityInput) {
                quantityInput.value = "";
            }

            if (row) {
                const totalTd = row.children[4];
                totalTd.textContent = "";
            }
        });
    } else {
        alert("Preencha ao menos um produto com uma quantidade válida.");
    }

    fillResumeMechanicTable(newSale);
}

function fillResumeMechanicTable(newSale) {

    const emp = employees.find(e => e.id == newSale.employee);

    if (!emp) {
        console.error('Employee não encontrado para ID:', newSale.employee);
        alert('Erro: Consultor não encontrado!');
        return;
    }

    const empName = emp.name;
    const empId = emp.id;

    let total = 0;
    newSale.products.forEach(p => total += p.quantity);

    const tr = document.createElement('tr');
    tr.setAttribute('data-employee-id', empId);

    const empTd = document.createElement('td');
    empTd.textContent = empName;
    tr.appendChild(empTd);

    const totalTd = document.createElement('td');
    totalTd.textContent = total;
    tr.appendChild(totalTd);

    const actionTd = document.createElement('td');

    const editOrViewBtn = document.createElement('button');
    editOrViewBtn.textContent = 'Visualizar/Editar';
    editOrViewBtn.className = 'btn btn-warning';
    editOrViewBtn.setAttribute('data-employee-id', empId);
    editOrViewBtn.addEventListener('click', function () {
        fillResumeMechanicSaleModal(newSale, empName);
    })

    actionTd.appendChild(editOrViewBtn);
    tr.appendChild(actionTd);

    mechanicResumeSalesTableBody.appendChild(tr);
}

function fillResumeMechanicSaleModal(sale, empName) {
    mechanicResumeSalesModalTableBody.innerHTML = '';

    sale.products.forEach(product => {
        const productCode = products.find(p => p.id == product.productId).productCode;
        const tr = document.createElement('tr');
        tr.setAttribute('data-product-id', product.productId);

        const codTd = document.createElement('td');
        codTd.textContent = productCode;
        tr.appendChild(codTd);

        const qtdTd = document.createElement('td');
        const qtdInput = document.createElement('input');
        qtdInput.type = 'number';
        qtdInput.className = 'form-control';
        qtdInput.value = product.quantity;
        qtdTd.appendChild(qtdInput);
        tr.appendChild(qtdTd);

        mechanicResumeSalesModalTableBody.appendChild(tr);
    });

    mechanicConfirmUpdateBtn.onclick = function () {
        const updatedProducts = [];
        const rows = mechanicResumeSalesModalTableBody.querySelectorAll('tr');
        rows.forEach(row => {
            const productId = row.getAttribute('data-product-id');
            const quantityInput = row.querySelector('input');
            const quantity = parseInt(quantityInput.value) || 0;
            if (quantity >= 0) {
                updatedProducts.push({ productId, quantity });
            }
        });
        updateMechanicSale(sale.employee, updatedProducts);
    };

    resumeMecName.textContent = empName;

    const mechanicResumeSalesInstance = bootstrap.Modal.getOrCreateInstance(mechanicResumeSalesModal);
    mechanicResumeSalesInstance.show();
}

function updateMechanicSale(empId, products) {
    const saleToUpdate = sales.find(s => s.employee === empId);
    if (saleToUpdate) {
        saleToUpdate.products = products;
        console.log('Venda atualizada:', saleToUpdate);
    } else {
        console.error('Venda não encontrada para atualização!');
    }

    const newTotal = products.reduce((sum, p) => sum + p.quantity, 0);

    const row = mechanicResumeSalesTableBody
        .querySelector(`tr[data-employee-id="${empId}"]`);

    if (row) {
        const totalTd = row.children[1];
        totalTd.textContent = newTotal;
    }

    const mechanicResumeSalesInstance = bootstrap.Modal.getOrCreateInstance(mechanicResumeSalesModal);
    mechanicResumeSalesInstance.hide();
}

function eraseMechanicInfos() {
    mechanicResumeSalesModalTableBody.innerHTML = '';
    mechanicResumeSalesTableBody.innerHTML = '';
    setMessageDisplay(noMechanicsAvailable, mechanicsModalBody, false);
}

const newIncentiveGoToPage4Btn = document.getElementById('go-to-page-4');
const resumeTotalPerFunctionTBody = document.getElementById('resumeTotalPerFunctionTBody');
const consultantFinalResumeSalesTableBody = document.getElementById('consultantFinalResumeSalesTableBody');
const mechanicFinalResumeSalesTableBody = document.getElementById('mechanicFinalResumeSalesTableBody');
const finalResumeSalesModal = document.getElementById('finalResumeSalesModal');
const newIncentivePage4 = document.getElementById('page4');
const finalResumeSalesModalTableBody = document.getElementById('finalResumeSalesModalTableBody');
const finalSalesConfirmUpdateBtn = document.getElementById('finalSalesConfirmUpdateBtn');
const finalResumeName = document.getElementById('finalResumeName');

const tinkerFinalResumeSalesTableBody = document.getElementById('tinkerFinalResumeSalesTableBody');


newIncentiveGoToPage4Btn.addEventListener('click', function () {

    newIncentiveGoToPage4Btn.disabled = true;
    newIncentiveGoToPage4Btn.textContent = 'avançando ...';

    loadPage4Infos();

    setMessageDisplay(noMechanicsAvailable, mechanicsModalBody, false);

    newIncentiveGoToPage4Btn.disabled = false;
    newIncentiveGoToPage4Btn.textContent = 'avançar';
});

function loadPage4Infos() {
    const consultantSales = sales.filter(s => s.function == 'Consultor Técnico');
    const tinkerSales = sales.filter(s => s.function == 'Consultor de Funilaria');
    const mechanicSales = sales.filter(s => s.function == 'Mecânico');

    const totalConsultantsParagraph = document.getElementById('totalConsultants');
    const totalTinkersParagraph = document.getElementById('totalTinkers');
    const totalMechanicsParagraph = document.getElementById('totalMechanics');
    const totalSalesFinalResumeParagraph = document.getElementById('totalSalesFinalResume');

    const totalConsultants = consultantSales.reduce((sum, sale) => sum + sale.products.reduce((qsum, p) => qsum + p.quantity, 0), 0);
    const totalTinkers = tinkerSales.reduce((sum, sale) => sum + sale.products.reduce((qsum, p) => qsum + p.quantity, 0), 0);
    const totalMechanics = mechanicSales.reduce((sum, sale) => sum + sale.products.reduce((qsum, p) => qsum + p.quantity, 0), 0);
    const totalResume = totalConsultants + totalTinkers + totalMechanics;

    totalConsultantsParagraph.textContent = totalConsultants;
    totalTinkersParagraph.textContent = totalTinkers;
    totalMechanicsParagraph.textContent = totalMechanics;
    totalSalesFinalResumeParagraph.textContent = totalResume;

    updateProductSummary();

    consultantFinalResumeSalesTableBody.innerHTML = '';

    consultantSales.forEach(cs => {
        const emp = employees.find(e => e.id == cs.employee);
        const total = cs.products.reduce((sum, p) => sum + p.quantity, 0);

        const tr = document.createElement('tr');
        tr.setAttribute('data-employee-id', emp.id);

        const nameTd = document.createElement('td');
        nameTd.textContent = emp.name;
        tr.appendChild(nameTd);

        const qtdTd = document.createElement('td');
        qtdTd.textContent = total;
        tr.appendChild(qtdTd);

        const actionTd = document.createElement('td');

        const editOrViewBtn = document.createElement('button');
        editOrViewBtn.textContent = 'Visualizar/Editar';
        editOrViewBtn.className = 'btn btn-warning';
        editOrViewBtn.setAttribute('data-employee-id', emp.id);
        editOrViewBtn.addEventListener('click', function () {
            fillFinalResumeSaleModal(cs, emp.name, consultantFinalResumeSalesTableBody);
        })

        actionTd.appendChild(editOrViewBtn);
        tr.appendChild(actionTd);

        consultantFinalResumeSalesTableBody.appendChild(tr);
    });

    tinkerFinalResumeSalesTableBody.innerHTML = '';

    tinkerSales.forEach(cs => {
        const emp = employees.find(e => e.id == cs.employee);
        const total = cs.products.reduce((sum, p) => sum + p.quantity, 0);

        const tr = document.createElement('tr');
        tr.setAttribute('data-employee-id', emp.id);

        const nameTd = document.createElement('td');
        nameTd.textContent = emp.name;
        tr.appendChild(nameTd);

        const qtdTd = document.createElement('td');
        qtdTd.textContent = total;
        tr.appendChild(qtdTd);

        const actionTd = document.createElement('td');

        const editOrViewBtn = document.createElement('button');
        editOrViewBtn.textContent = 'Visualizar/Editar';
        editOrViewBtn.className = 'btn btn-warning';
        editOrViewBtn.setAttribute('data-employee-id', emp.id);
        editOrViewBtn.addEventListener('click', function () {
            fillFinalResumeSaleModal(cs, emp.name, tinkerFinalResumeSalesTableBody);
        })

        actionTd.appendChild(editOrViewBtn);
        tr.appendChild(actionTd);

        tinkerFinalResumeSalesTableBody.appendChild(tr);
    });

    mechanicFinalResumeSalesTableBody.innerHTML = '';

    mechanicSales.forEach(cs => {
        const emp = employees.find(e => e.id == cs.employee);
        const total = cs.products.reduce((sum, p) => sum + p.quantity, 0);

        const tr = document.createElement('tr');
        tr.setAttribute('data-employee-id', emp.id);

        const nameTd = document.createElement('td');
        nameTd.textContent = emp.name;
        tr.appendChild(nameTd);

        const qtdTd = document.createElement('td');
        qtdTd.textContent = total;
        tr.appendChild(qtdTd);

        const actionTd = document.createElement('td');

        const editOrViewBtn = document.createElement('button');
        editOrViewBtn.textContent = 'Visualizar/Editar';
        editOrViewBtn.className = 'btn btn-warning';
        editOrViewBtn.setAttribute('data-employee-id', emp.id);
        editOrViewBtn.addEventListener('click', function () {
            fillFinalResumeSaleModal(cs, emp.name, mechanicFinalResumeSalesTableBody);
        })

        actionTd.appendChild(editOrViewBtn);
        tr.appendChild(actionTd);

        mechanicFinalResumeSalesTableBody.appendChild(tr);
    });

    const page4Instance = bootstrap.Modal.getOrCreateInstance(newIncentivePage4);
    page4Instance.show();
}

function fillFinalResumeSaleModal(sale, empName, tBody) {
    finalResumeSalesModalTableBody.innerHTML = '';

    sale.products.forEach(product => {
        const productCode = products.find(p => p.id == product.productId).productCode;
        const tr = document.createElement('tr');
        tr.setAttribute('data-product-id', product.productId);

        const codTd = document.createElement('td');
        codTd.textContent = productCode;
        tr.appendChild(codTd);

        const qtdTd = document.createElement('td');
        const qtdInput = document.createElement('input');
        qtdInput.type = 'number';
        qtdInput.className = 'form-control';
        qtdInput.value = product.quantity;
        qtdTd.appendChild(qtdInput);
        tr.appendChild(qtdTd);

        finalResumeSalesModalTableBody.appendChild(tr);
    });

    finalResumeName.textContent = empName;

    finalSalesConfirmUpdateBtn.onclick = function () {
        const updatedProducts = [];
        const rows = finalResumeSalesModalTableBody.querySelectorAll('tr');
        rows.forEach(row => {
            const productId = row.getAttribute('data-product-id');
            const quantityInput = row.querySelector('input');
            const quantity = parseInt(quantityInput.value) || 0;
            if (quantity >= 0) {
                updatedProducts.push({ productId, quantity });
            }
        });
        updateFinalSale(sale.employee, updatedProducts, tBody);
    };


    const finalResumeSalesModalInstance = bootstrap.Modal.getOrCreateInstance(finalResumeSalesModal);
    finalResumeSalesModalInstance.show();
}

function updateFinalSale(empId, products, tBody) {
    const saleToUpdate = sales.find(s => s.employee === empId);
    if (saleToUpdate) {
        saleToUpdate.products = products;
        console.log('Venda atualizada:', saleToUpdate);
    } else {
        console.error('Venda não encontrada para atualização!');
        return;
    }

    const newTotal = products.reduce((sum, p) => sum + p.quantity, 0);

    const row = tBody.querySelector(`tr[data-employee-id="${empId}"]`);
    if (row) {
        const totalTd = row.children[1];
        totalTd.textContent = newTotal;
    }

    updateProductSummary();
    updateTotals();

    const finalResumeSalesModalInstance = bootstrap.Modal.getOrCreateInstance(finalResumeSalesModal);
    finalResumeSalesModalInstance.hide();
}

function updateTotals() {
    const consultantSales = sales.filter(s => s.function === 'Consultor Técnico');
    const tinkerSales = sales.filter(s => s.function === 'Consultor de Funilaria');
    const mechanicSales = sales.filter(s => s.function === 'Mecânico');

    const totalConsultants = consultantSales.reduce((sum, sale) => sum + sale.products.reduce((qsum, p) => qsum + p.quantity, 0), 0);
    const totalTinker = tinkerSales.reduce((sum, sale) => sum + sale.products.reduce((qsum, p) => qsum + p.quantity, 0), 0);
    const totalMechanics = mechanicSales.reduce((sum, sale) => sum + sale.products.reduce((qsum, p) => qsum + p.quantity, 0), 0);
    const totalResume = totalConsultants + totalTinker + totalMechanics;

    document.getElementById('totalConsultants').textContent = totalConsultants;
    document.getElementById('totalTinkers').textContent = totalTinker;
    document.getElementById('totalMechanics').textContent = totalMechanics;
    document.getElementById('totalSalesFinalResume').textContent = totalResume;
}

function updateProductSummary() {
    const consultantSales = sales.filter(s => s.function === 'Consultor Técnico' || s.function === 'Consultor de Funilaria');
    const mechanicSales = sales.filter(s => s.function === 'Mecânico');

    resumeTotalPerFunctionTBody.innerHTML = '';

    products.forEach(p => {
        const consultantTotalForThisProduct = consultantSales.reduce((total, sale) => {
            const matchingProducts = sale.products.filter(sp => sp.productId == p.id);
            const quantitySum = matchingProducts.reduce((sum, sp) => sum + sp.quantity, 0);
            return total + quantitySum;
        }, 0);

        const mechanicTotalForThisProduct = mechanicSales.reduce((total, sale) => {
            const matchingProducts = sale.products.filter(sp => sp.productId == p.id);
            const quantitySum = matchingProducts.reduce((sum, sp) => sum + sp.quantity, 0);
            return total + quantitySum;
        }, 0);

        const tr = document.createElement('tr');

        const codTd = document.createElement('td');
        codTd.textContent = p.productCode;
        tr.appendChild(codTd);

        const descTd = document.createElement('td');
        descTd.textContent = p.productName;
        tr.appendChild(descTd);

        const consultantTotalTd = document.createElement('td');
        consultantTotalTd.textContent = consultantTotalForThisProduct;
        tr.appendChild(consultantTotalTd);

        const mechanicTotalTd = document.createElement('td');
        mechanicTotalTd.textContent = mechanicTotalForThisProduct;
        tr.appendChild(mechanicTotalTd);

        resumeTotalPerFunctionTBody.appendChild(tr);
    });
}

const newIncentiveGoToPage5Btn = document.getElementById('go-to-page-5');
const newIncentivePage5 = document.getElementById('page5');
const currentAccountIncentivesCalculatedTableBody = document.getElementById('currentAccountIncentivesCalculatedTableBody');
const nfsIncentivesCalculatedTableBody = document.getElementById('nfsIncentivesCalculatedTableBody');
const generateReportBtn = document.getElementById('generate-report');

newIncentiveGoToPage5Btn.addEventListener('click', async function () {
    const consultantSales = sales.filter(s => s.function === 'Consultor Técnico' || s.function === 'Consultor de Funilaria');
    const mechanicSales = sales.filter(s => s.function === 'Mecânico');

    let invalidProducts = [];

    products.forEach(p => {
        const consultantTotalForThisProduct = consultantSales.reduce((total, sale) => {
            const matchingProducts = sale.products.filter(sp => sp.productId == p.id);
            const quantitySum = matchingProducts.reduce((sum, sp) => sum + sp.quantity, 0);
            return total + quantitySum;
        }, 0);

        const mechanicTotalForThisProduct = mechanicSales.reduce((total, sale) => {
            const matchingProducts = sale.products.filter(sp => sp.productId == p.id);
            const quantitySum = matchingProducts.reduce((sum, sp) => sum + sp.quantity, 0);
            return total + quantitySum;
        }, 0);

        if (mechanicTotalForThisProduct > consultantTotalForThisProduct) {
            invalidProducts.push(p);
        }
    });

    if (invalidProducts.length > 0 && mechanicCanBeGreater === false) {
        let productNames = [];

        invalidProducts.forEach(p => productNames.push(p.productName));

        const namesToAlert = productNames.join(", ");

        alert('Aplicação dos mecânicos maior que venda dos consultores para os produtos: ' + namesToAlert);
        return;
    }

    newIncentiveGoToPage5Btn.disabled = true;
    newIncentiveGoToPage5Btn.textContent = 'avançando ...'

    const url = '/api/sales';

    const request = await fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(sales)
    });

    await requestIsOk(request);

    const response = await request.json();

    fillPage5(response);

    newIncentiveGoToPage5Btn.disabled = false;
    newIncentiveGoToPage5Btn.textContent = 'avançar';

});

let docNumToGen;

function fillPage5(response) {
    console.log(response);
    currentAccountIncentivesCalculatedTableBody.innerHTML = '';
    nfsIncentivesCalculatedTableBody.innerHTML = '';

    const incentiveReferenceDate = document.getElementById('incentiveReferenceDate');
    const incentiveState = document.getElementById('incentiveState');
    const incentiveCustomerName = document.getElementById('incentiveCustomerName');
    const incentiveCustomerCnpj = document.getElementById('incentiveCustomerCnpj');
    const incentivePaymentMethod = document.getElementById('incentivePaymentMethod');

    incentiveReferenceDate.textContent = response.referenceDate;
    incentiveState.textContent = response.state;
    incentiveCustomerName.textContent = response.customerName;
    incentiveCustomerCnpj.textContent = response.customerCnpj;
    incentivePaymentMethod.textContent = response.paymentMethod;

    response.ccIncentives.forEach(i => {
        const tr = document.createElement('tr');

        const cpfTd = document.createElement('td');
        cpfTd.className = 'small';
        cpfTd.textContent = i.cpf;
        tr.appendChild(cpfTd);

        const nameTd = document.createElement('td');
        nameTd.className = 'small';
        nameTd.textContent = i.employeeName;
        tr.appendChild(nameTd);

        const valueTd = document.createElement('td');
        valueTd.className = 'small';
        valueTd.textContent = 'R$ ' + i.incentiveValue.toFixed(2);
        tr.appendChild(valueTd);

        const funcTd = document.createElement('td');
        funcTd.className = 'small';
        funcTd.textContent = i.functionName;
        tr.appendChild(funcTd);

        currentAccountIncentivesCalculatedTableBody.appendChild(tr);
    });

    response.nfsIncentives.forEach(i => {
        const tr = document.createElement('tr');

        const cpfTd = document.createElement('td');
        cpfTd.className = 'small';
        cpfTd.textContent = i.cpf;
        tr.appendChild(cpfTd);

        const nameTd = document.createElement('td');
        nameTd.className = 'small';
        nameTd.textContent = i.employeeName;
        tr.appendChild(nameTd);

        const valueTd = document.createElement('td');
        valueTd.className = 'small';
        valueTd.textContent = 'R$ ' + i.incentiveValue.toFixed(2);
        tr.appendChild(valueTd);

        const funcTd = document.createElement('td');
        funcTd.className = 'small';
        funcTd.textContent = i.functionName;
        tr.appendChild(funcTd);

        nfsIncentivesCalculatedTableBody.appendChild(tr);
    });

    const docNum = response.saleDocumentNumber;
    docNumToGen = docNum;

    const documentInfosCcIncentiveTotal = document.getElementById('incentivesCalculatedCcIncentiveTotal');
    const documentInfosNfsIncentiveTotal = document.getElementById('incentivesCalculatedNfsIncentiveTotal');

    documentInfosCcIncentiveTotal.textContent = "R$ " + response.totalCc.toFixed(2);
    documentInfosNfsIncentiveTotal.textContent = "R$ " + response.totalNfs.toFixed(2);

    const page5Instance = bootstrap.Modal.getOrCreateInstance(newIncentivePage5);
    page5Instance.show();
}

generateReportBtn.addEventListener('click', async function () {
    if (!docNumToGen || docNumToGen == 0) {
        alert('N° de doc inválido');
        return;
    }

    try {
        generateReportBtn.textContent = 'Gerando Relatório...';
        generateReportBtn.disabled = true;

        const url = `/reports/by-docNum/${docNumToGen}`;

        const request = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(docNumToGen)
        });

        await requestIsOk(request);

        const contentDisposition = request.headers.get('content-disposition');
        const blob = await request.blob();

        let filename = 'relatorio.bin';
        if (contentDisposition) {
            const match = contentDisposition.match(/filename="?(.+)"?/);
            if (match && match.length > 1) {
                filename = match[1];
            }
        }

        if (blob.size > 0 && blob.type === 'application/pdf') {
            const objectUrl = URL.createObjectURL(blob);
            window.open(objectUrl, '_blank');
            URL.revokeObjectURL(objectUrl);
        } else {
            throw new Error('Arquivo inválido ou vazio.');
        }

    } catch (error) {
        console.error(error);
    } finally {
        generateReportBtn.textContent = 'Gerar Relatório';
        generateReportBtn.disabled = false;

        window.location.href = '/incentives'
    }

});

async function loadDocumentDetails(button) {
    const documentNumber = button.getAttribute('data-document-number');

    const url = `/incentives/${documentNumber}`;

    try {
        const request = await fetch(url);

        await requestIsOk(request);

        const data = await request.json();

        populateModal(data);

        const modal = new bootstrap.Modal(document.getElementById('documentModal'));
        modal.show();

        const downloadBtn = document.getElementById('downloadReport');
        downloadBtn.onclick = async () => downloadReport(documentNumber, downloadBtn);

    } catch (error) {
        console.error("Erro:", error);
        alert("Erro ao carregar os detalhes. Tente novamente.");
    }
}

function formatDate(dateString) {
    if (!dateString) return 'N/A';

    const date = new Date(dateString);
    if (isNaN(date)) return 'Data Inválida';

    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    return `${month}-${year}`;
}

const openConfirmDeleteModal = document.getElementById('openConfirmDeleteModal');

function populateModal(data) {
    const documentInfosDocumentNumber = document.getElementById('documentInfosDocumentNumber');
    const documentInfosDate = document.getElementById('documentInfosDate');
    const documentInfosPaymentMethod = document.getElementById('documentInfosPaymentMethod');
    const documentInfosState = document.getElementById('documentInfosState');
    const documentInfosCustomer = document.getElementById('documentInfosCustomer');
    const documentInfosCnpj = document.getElementById('documentInfosCnpj');
    const documentInfosSalesTotal = document.getElementById('documentInfosSalesTotal');
    const documentInfosTinkerTotal = document.getElementById('documentInfosTinkerTotal');
    const documentInfosAplicationsTotal = document.getElementById('documentInfosAplicationsTotal');

    documentInfosDocumentNumber.textContent = data.documentNumber;
    documentInfosDate.textContent = data.date;
    documentInfosPaymentMethod.textContent = data.paymentMethod;
    documentInfosState.textContent = data.state;
    documentInfosCustomer.textContent = data.customerName;
    documentInfosCnpj.textContent = data.customerCnpj;
    documentInfosSalesTotal.textContent = data.salesTotal;
    documentInfosTinkerTotal.textContent = data.tinkerTotal;
    documentInfosAplicationsTotal.textContent = data.aplicationsTotal;

    const documentInfosSaleArea = document.getElementById('documentInfosSaleArea');
    const documentInfosTinkerArea = document.getElementById('documentInfosTinkerArea');
    const documentInfosAplicationArea = document.getElementById('documentInfosAplicationArea');
    const documentInfosNFSIncentive = document.getElementById('documentInfosNFSIncentive');

    const documentInfosSaleAreaDetailment = document.getElementById('documentInfosSaleAreaDetailment');
    const documentInfosTinkerAreaDetailment = document.getElementById('documentInfosTinkerAreaDetailment');
    const documentInfosAplicationAreaDetailment = document.getElementById('documentInfosAplicationAreaDetailment');
    const documentInfosTotalPerFunctionTBody = document.getElementById('documentInfosTotalPerFunctionTBody');
    const documentInfosCCIncentivesTBody = document.getElementById('documentInfosCCIncentivesTBody');
    const documentInfosNFSIncentivesTBody = document.getElementById('documentInfosNFSIncentivesTBody');

    documentInfosSaleAreaDetailment.innerHTML = '';
    documentInfosTinkerAreaDetailment.innerHTML = '';
    documentInfosAplicationAreaDetailment.innerHTML = '';
    documentInfosTotalPerFunctionTBody.innerHTML = '';
    documentInfosCCIncentivesTBody.innerHTML = '';
    documentInfosNFSIncentivesTBody.innerHTML = '';

    const sales = data.consultantSales;
    const tinkerSales = data.tinkerSales;
    const aplications = data.mechanicSales;
    const productsResume = data.productsResume;
    const ccIncentives = data.ccIncentives;
    const nfsIncentives = data.nfsIncentives;

    if (!aplications.length > 0) {
        documentInfosAplicationArea.style.display = 'none';
    } else {
        documentInfosAplicationArea.style.display = 'block';
    }

    if (!sales.length > 0) {
        documentInfosSaleArea.style.display = 'none';
    } else {
        documentInfosSaleArea.style.display = 'block';
    }

    if (!tinkerSales.length > 0) {
        documentInfosTinkerArea.style.display = 'none';
    } else {
        documentInfosTinkerArea.style.display = 'block';
    }

    if (!nfsIncentives.length > 0) {
        documentInfosNFSIncentive.style.display = 'none';
    } else {
        documentInfosNFSIncentive.style.display = 'block';
    }

    if (sales.length > 0) {
        sales.forEach(s => {
            const title = document.createElement('h5');
            title.textContent = `${s.function}: ${s.employeeName}`;
            title.className = 'mt-4';

            const totalTitle = document.createElement('h6');
            totalTitle.textContent = `Total Vendido: ${s.totalQuantity}`;

            documentInfosSaleAreaDetailment.appendChild(title);
            documentInfosSaleAreaDetailment.appendChild(totalTitle);

            const table = document.createElement('table');
            table.className = 'table table-sm table-striped table-hover table-bordered border-dark w-75 mb-5';

            const thead = document.createElement('thead');

            const theadRow = document.createElement('tr');

            const thCod = document.createElement('th');
            thCod.textContent = 'CÓD';
            thCod.className = 'small';
            theadRow.appendChild(thCod);

            const thDesc = document.createElement('th');
            thDesc.textContent = 'Descrição';
            thDesc.className = 'small';
            theadRow.appendChild(thDesc);

            const thQtd = document.createElement('th');
            thQtd.textContent = 'Quantidade';
            thQtd.className = 'small';
            theadRow.appendChild(thQtd);

            thead.appendChild(theadRow);
            table.appendChild(thead);

            const tbody = document.createElement('tbody');

            s.products.forEach(p => {
                const tr = document.createElement('tr');

                const tdCod = document.createElement('td');
                tdCod.textContent = p.productCode;
                tdCod.className = 'small';
                tr.appendChild(tdCod);

                const tdDesc = document.createElement('td');
                tdDesc.textContent = p.productName;
                tdDesc.className = 'small';
                tr.appendChild(tdDesc);

                const tdQtd = document.createElement('td');
                tdQtd.textContent = p.quantity;
                tdQtd.className = 'small';
                tr.appendChild(tdQtd);

                tbody.appendChild(tr);
                table.appendChild(tbody);
            });

            documentInfosSaleAreaDetailment.appendChild(table);

        });
    }

    if (tinkerSales.length > 0) {
        tinkerSales.forEach(s => {
            const title = document.createElement('h5');
            title.textContent = `${s.function}: ${s.employeeName}`;
            title.className = 'mt-4';

            const totalTitle = document.createElement('h6');
            totalTitle.textContent = `Total Vendido: ${s.totalQuantity}`;

            documentInfosTinkerAreaDetailment.appendChild(title);
            documentInfosTinkerAreaDetailment.appendChild(totalTitle);

            const table = document.createElement('table');
            table.className = 'table table-sm table-striped table-hover table-bordered border-dark w-75 mb-5';

            const thead = document.createElement('thead');

            const theadRow = document.createElement('tr');

            const thCod = document.createElement('th');
            thCod.textContent = 'CÓD';
            thCod.className = 'small';
            theadRow.appendChild(thCod);

            const thDesc = document.createElement('th');
            thDesc.textContent = 'Descrição';
            thDesc.className = 'small';
            theadRow.appendChild(thDesc);

            const thQtd = document.createElement('th');
            thQtd.textContent = 'Quantidade';
            thQtd.className = 'small';
            theadRow.appendChild(thQtd);

            thead.appendChild(theadRow);
            table.appendChild(thead);

            const tbody = document.createElement('tbody');

            s.products.forEach(p => {
                const tr = document.createElement('tr');

                const tdCod = document.createElement('td');
                tdCod.textContent = p.productCode;
                tdCod.className = 'small';
                tr.appendChild(tdCod);

                const tdDesc = document.createElement('td');
                tdDesc.textContent = p.productName;
                tdDesc.className = 'small';
                tr.appendChild(tdDesc);

                const tdQtd = document.createElement('td');
                tdQtd.textContent = p.quantity;
                tdQtd.className = 'small';
                tr.appendChild(tdQtd);

                tbody.appendChild(tr);
                table.appendChild(tbody);
            });

            documentInfosTinkerAreaDetailment.appendChild(table);

        });
    }

    if (aplications.length > 0) {
        aplications.forEach(s => {
            const title = document.createElement('h5');
            title.textContent = `${s.function}: ${s.employeeName}`;
            title.className = 'mt-4';

            const totalTitle = document.createElement('h6');
            totalTitle.textContent = `Total Vendido: ${s.totalQuantity}`;

            documentInfosAplicationAreaDetailment.appendChild(title);
            documentInfosAplicationAreaDetailment.appendChild(totalTitle);

            const table = document.createElement('table');
            table.className = 'table table-sm table-striped table-hover table-bordered border-dark w-75 mb-5';

            const thead = document.createElement('thead');

            const theadRow = document.createElement('tr');

            const thCod = document.createElement('th');
            thCod.textContent = 'CÓD';
            thCod.className = 'small';
            theadRow.appendChild(thCod);

            const thDesc = document.createElement('th');
            thDesc.textContent = 'Descrição';
            thDesc.className = 'small';
            theadRow.appendChild(thDesc);

            const thQtd = document.createElement('th');
            thQtd.textContent = 'Quantidade';
            thQtd.className = 'small';
            theadRow.appendChild(thQtd);

            thead.appendChild(theadRow);
            table.appendChild(thead);

            const tbody = document.createElement('tbody');

            s.products.forEach(p => {
                const tr = document.createElement('tr');

                const tdCod = document.createElement('td');
                tdCod.textContent = p.productCode;
                tdCod.className = 'small';
                tr.appendChild(tdCod);

                const tdDesc = document.createElement('td');
                tdDesc.textContent = p.productName;
                tdDesc.className = 'small';
                tr.appendChild(tdDesc);

                const tdQtd = document.createElement('td');
                tdQtd.textContent = p.quantity;
                tdQtd.className = 'small';
                tr.appendChild(tdQtd);

                tbody.appendChild(tr);
                table.appendChild(tbody);
            });

            documentInfosAplicationAreaDetailment.appendChild(table);

        });
    }

    productsResume.forEach(p => {
        const tr = document.createElement('tr');

        const tdCod = document.createElement('td');
        tdCod.textContent = p.productCode;
        tdCod.className = 'small';
        tr.appendChild(tdCod);

        const tdDesc = document.createElement('td');
        tdDesc.textContent = p.productName;
        tdDesc.className = 'small';
        tr.appendChild(tdDesc);

        const tdQtdCt = document.createElement('td');
        tdQtdCt.textContent = p.consultantQuantity;
        tdQtdCt.className = 'small';
        tr.appendChild(tdQtdCt);

        const tdQtdMec = document.createElement('td');
        tdQtdMec.textContent = p.mechanicQuantity;
        tdQtdMec.className = 'small';
        tr.appendChild(tdQtdMec);


        documentInfosTotalPerFunctionTBody.appendChild(tr);
    });

    ccIncentives.forEach(ci => {
        const tr = document.createElement('tr');

        const tdCpf = document.createElement('td');
        tdCpf.textContent = ci.cpf;
        tdCpf.className = 'small';
        tr.appendChild(tdCpf);

        const tdName = document.createElement('td');
        tdName.textContent = ci.employeeName;
        tdName.className = 'small';
        tr.appendChild(tdName);

        const tdValue = document.createElement('td');
        tdValue.textContent = "R$ " + ci.incentiveValue.toFixed(2);
        tdValue.className = 'small';
        tr.appendChild(tdValue);

        const tdFunc = document.createElement('td');
        tdFunc.textContent = ci.functionName;
        tdFunc.className = 'small';
        tr.appendChild(tdFunc);

        documentInfosCCIncentivesTBody.appendChild(tr);
    });

    nfsIncentives.forEach(ci => {
        const tr = document.createElement('tr');

        const tdCpf = document.createElement('td');
        tdCpf.textContent = ci.cpf;
        tdCpf.className = 'small';
        tr.appendChild(tdCpf);

        const tdName = document.createElement('td');
        tdName.textContent = ci.employeeName;
        tdName.className = 'small';
        tr.appendChild(tdName);

        const tdValue = document.createElement('td');
        tdValue.textContent = "R$ " + ci.incentiveValue.toFixed(2);
        tdValue.className = 'small';
        tr.appendChild(tdValue);

        const tdFunc = document.createElement('td');
        tdFunc.textContent = ci.functionName;
        tdFunc.className = 'small';
        tr.appendChild(tdFunc);

        documentInfosNFSIncentivesTBody.appendChild(tr);
    });

    const documentInfosCcIncentiveTotal = document.getElementById('documentInfosCcIncentiveTotal');
    const documentInfosNfsIncentiveTotal = document.getElementById('documentInfosNfsIncentiveTotal');

    documentInfosCcIncentiveTotal.textContent = "R$ " + data.totalCc.toFixed(2);
    documentInfosNfsIncentiveTotal.textContent = "R$ " + data.totalNfs.toFixed(2);

    if (openConfirmDeleteModal) {
        openConfirmDeleteModal.setAttribute('data-document-number', data.documentNumber);
    }

}

const openFilterModal = document.getElementById('openFilterModal');
const filterModal = document.getElementById('filterModal');
const filterButton = document.getElementById('filterButton');


openFilterModal.addEventListener('click', () => {
    const filterModalInstance = bootstrap.Modal.getOrCreateInstance(filterModal);
    filterModalInstance.show();
})

filterButton.addEventListener('click', async () => {
    const baseUrl = '/incentives/filter';
    const url = new URL(baseUrl, window.location.origin);

    const start = document.getElementById('start').value;
    const end = document.getElementById('end').value;
    const userIds = Array.from(document.getElementById('userSelect').selectedOptions).map(opt => opt.value);
    const customerIds = Array.from(document.getElementById('customerFilterSelect').selectedOptions).map(opt => opt.value);


    url.searchParams.set("start", start);
    url.searchParams.set("end", end);
    url.searchParams.set("userIds", userIds);
    url.searchParams.set("customerIds", customerIds);

    console.log(url.toString());

    try {
        const request = await fetch(url);

        if (!request.ok) {
            throw new Error(request.statusText);
        }

        const response = await request.text();

        const tableBody = document.getElementById('incentivesTableArea');
        tableBody.innerHTML = '';
        tableBody.innerHTML = response;

        const filterModal = bootstrap.Modal.getInstance(document.getElementById('filterModal'));
        filterModal.hide();
    }
    catch (error) {
        alert(error);
    }
});

function clearIncentiveFilters() {
    fetch('/incentives/clearFilters', {
        method: 'GET'
    })
        .then(response => response.text())
        .then(html => {
            const div = document.getElementById('incentivesTableArea');
            div.innerHTML = '';
            div.innerHTML = html;

            const filterModal = new bootstrap.Modal(document.getElementById('filterModal'));
            filterModal.hide();
        })
        .catch(error => {
            console.error('Erro ao limpar filtros:', error);
        });
}

const updateIncentiveModal = document.getElementById('updateIncentiveModal');

const updateIncentiveConsultantSalesTableBody = document.getElementById('updateIncentiveConsultantSalesTableBody');
const updateIncentiveTinkerSalesTableBody = document.getElementById('updateIncentiveTinkerSalesTableBody');
const updateIncentiveMechanicSalesTableBody = document.getElementById('updateIncentiveMechanicSalesTableBody');

const updateIncentiveConsultantSales = document.getElementById('updateIncentiveConsultantSales');
const updateIncentiveTinkerSales = document.getElementById('updateIncentiveTinkerSales');
const updateIncentiveMechanicSales = document.getElementById('updateIncentiveMechanicSales');

const addBtnConsultant = document.getElementById('addBtnConsultant');
const addBtnTinker = document.getElementById('addBtnTinker');
const addBtnMechanic = document.getElementById('addBtnMechanic');

async function loadDocumentDetailsToEdit(button) {

    const updateIncentiveModalInstance = bootstrap.Modal.getOrCreateInstance(updateIncentiveModal);
    const documentNumber = button.getAttribute('data-document-number');

    const url = `/api/sales/details/${documentNumber}`;

    try {
        const request = await fetch(url);

        await requestIsOk(request);

        const response = await request.json();

        fillUpdateIncentiveModal(response);

        updateIncentiveModalInstance.show();
    } catch (error) {
        console.log(error);
    }

}

function fillUpdateIncentiveModal(response) {
    updateIncentiveConsultantSalesTableBody.innerHTML = '';
    updateIncentiveTinkerSalesTableBody.innerHTML = '';
    updateIncentiveMechanicSalesTableBody.innerHTML = '';

    addBtnConsultant.innerHTML = '';
    addBtnTinker.innerHTML = '';
    addBtnMechanic.innerHTML = '';

    const updateIncentiveModalDocumentNumber = document.getElementById('updateIncentiveModalDocumentNumber');
    const updateIncentiveDocumentNumber = document.getElementById('updateIncentiveDocumentNumber');
    const updateIncentiveCustomer = document.getElementById('updateIncentiveCustomer');
    const updateIncentiveDate = document.getElementById('updateIncentiveDate');

    updateIncentiveModalDocumentNumber.textContent = response.documentNumber;
    updateIncentiveDocumentNumber.textContent = response.documentNumber;
    updateIncentiveCustomer.textContent = response.customerName;
    updateIncentiveDate.textContent = response.date;

    const consultantSales = response.consultantSales;
    const tinkerSales = response.tinkerSales;
    const mechanicSales = response.mechanicSales;

    const consultants = response.consultants;
    const tinkers = response.tinkers;
    const mechanics = response.mechanics;
    const products = response.products;


    if (consultantSales) {
        consultantSales.forEach(cs => {
            const tr = document.createElement('tr');

            const empSelTd = document.createElement('td');
            const empSel = document.createElement('select');
            empSel.className = 'form-select uSelect2';
            const empOpt = document.createElement('option');
            empOpt.value = cs.employeeId;
            empOpt.text = cs.employeeName;
            empOpt.selected = true;
            empSel.appendChild(empOpt);

            if (consultants) {
                consultants.forEach(c => {
                    if (c.employeeId != cs.emplyeeId) {
                        const newOpt = document.createElement('option');
                        newOpt.value = c.employeeId;
                        newOpt.text = c.employeeName;
                        empSel.appendChild(newOpt);
                    }
                });
            }

            empSelTd.appendChild(empSel);
            tr.appendChild(empSelTd);


            const prodSelTd = document.createElement('td');
            const prodSel = document.createElement('select');
            prodSel.className = 'form-select uSelect2'
            const prodOpt = document.createElement('option');
            prodOpt.value = cs.productId;
            prodOpt.text = cs.productName;
            prodOpt.selected = true;
            prodSel.appendChild(prodOpt);

            if (products) {
                products.forEach(p => {
                    if (p.productId != cs.productId) {
                        const newOpt = document.createElement('option');
                        newOpt.value = p.productId;
                        newOpt.text = p.productName;
                        prodSel.appendChild(newOpt);
                    }
                });
            }

            prodSelTd.appendChild(prodSel);
            tr.appendChild(prodSelTd);

            const qtdSelTd = document.createElement('td');
            const qtdInput = document.createElement('input');
            qtdInput.className = 'form-control';
            qtdInput.type = 'number';
            qtdInput.value = cs.quantity;
            qtdSelTd.appendChild(qtdInput);
            tr.appendChild(qtdSelTd);

            const removeTd = document.createElement('td');
            const removeBtn = document.createElement('button');
            removeBtn.className = 'btn btn-danger';
            removeBtn.type = 'button';
            removeBtn.textContent = 'Remover';
            removeBtn.addEventListener('click', () => {
                tr.remove();
            });
            removeTd.appendChild(removeBtn);
            tr.appendChild(removeTd);

            updateIncentiveConsultantSalesTableBody.appendChild(tr);
        });
    }

    const addConsultantSaleBtn = document.createElement('button');
    addConsultantSaleBtn.className = 'btn btn-secondary';
    addConsultantSaleBtn.type = 'button'
    addConsultantSaleBtn.textContent = 'Adicionar Consultor';
    addConsultantSaleBtn.addEventListener('click', () => {
        if (consultants && products) {
            const tr = document.createElement('tr');

            const empSelTd = document.createElement('td');
            const empSel = document.createElement('select');
            empSel.className = 'form-select uSelect2';
            empSel.innerHTML = '<option value="">Selecione um Consultor</option>';
            consultants.forEach(c => {
                const newOpt = document.createElement('option');
                newOpt.value = c.id;
                newOpt.text = c.name;
                empSel.appendChild(newOpt);
            });
            empSelTd.appendChild(empSel);
            tr.appendChild(empSelTd);

            const prodSelTd = document.createElement('td');
            const prodSel = document.createElement('select');
            prodSel.className = 'form-select uSelect2';
            prodSel.innerHTML = '<option value="">Selecione um produto</option>';
            products.forEach(p => {
                const prodOpt = document.createElement('option');
                prodOpt.value = p.id;
                prodOpt.text = p.productCode || p.productName;
                prodSel.appendChild(prodOpt);
            });
            prodSelTd.appendChild(prodSel);
            tr.appendChild(prodSelTd);

            const qtdSelTd = document.createElement('td');
            const qtdInput = document.createElement('input');
            qtdInput.className = 'form-control';
            qtdInput.type = 'number';
            qtdInput.value = 0;
            qtdSelTd.appendChild(qtdInput);
            tr.appendChild(qtdSelTd);

            const removeTd = document.createElement('td');
            const removeBtn = document.createElement('button');
            removeBtn.className = 'btn btn-danger';
            removeBtn.type = 'button';
            removeBtn.textContent = 'Remover';
            removeBtn.addEventListener('click', () => {
                tr.remove();
            });
            removeTd.appendChild(removeBtn);
            tr.appendChild(removeTd);

            updateIncentiveConsultantSalesTableBody.appendChild(tr);
        }
    });

    addBtnConsultant.appendChild(addConsultantSaleBtn);

    if (tinkerSales) {
        tinkerSales.forEach(cs => {
            const tr = document.createElement('tr');

            const empSelTd = document.createElement('td');
            const empSel = document.createElement('select');
            empSel.className = 'form-select uSelect2';
            const empOpt = document.createElement('option');
            empOpt.value = cs.employeeId;
            empOpt.text = cs.employeeName;
            empOpt.selected = true;
            empSel.appendChild(empOpt);

            if (tinkers) {
                tinkers.forEach(c => {
                    if (c.employeeId != cs.emplyeeId) {
                        const newOpt = document.createElement('option');
                        newOpt.value = c.employeeId;
                        newOpt.text = c.employeeName;
                        empSel.appendChild(newOpt);
                    }
                });
            }

            empSelTd.appendChild(empSel);
            tr.appendChild(empSelTd);


            const prodSelTd = document.createElement('td');
            const prodSel = document.createElement('select');
            prodSel.className = 'form-select uSelect2'
            const prodOpt = document.createElement('option');
            prodOpt.value = cs.productId;
            prodOpt.text = cs.productName;
            prodOpt.selected = true;
            prodSel.appendChild(prodOpt);

            if (products) {
                products.forEach(p => {
                    if (p.productId != cs.productId) {
                        const newOpt = document.createElement('option');
                        newOpt.value = p.productId;
                        newOpt.text = p.productName;
                        prodSel.appendChild(newOpt);
                    }
                });
            }

            prodSelTd.appendChild(prodSel);
            tr.appendChild(prodSelTd);

            const qtdSelTd = document.createElement('td');
            const qtdInput = document.createElement('input');
            qtdInput.className = 'form-control';
            qtdInput.type = 'number';
            qtdInput.value = cs.quantity;
            qtdSelTd.appendChild(qtdInput);
            tr.appendChild(qtdSelTd);

            const removeTd = document.createElement('td');
            const removeBtn = document.createElement('button');
            removeBtn.className = 'btn btn-danger';
            removeBtn.type = 'button';
            removeBtn.textContent = 'Remover';
            removeBtn.addEventListener('click', () => {
                tr.remove();
            });
            removeTd.appendChild(removeBtn);
            tr.appendChild(removeTd);

            updateIncentiveTinkerSalesTableBody.appendChild(tr);
        });
    }

    const addTinkerSaleBtn = document.createElement('button');
    addTinkerSaleBtn.className = 'btn btn-secondary';
    addTinkerSaleBtn.type = 'button'
    addTinkerSaleBtn.textContent = 'Adicionar Consultor';
    addTinkerSaleBtn.addEventListener('click', () => {
        if (tinkers && products) {
            const tr = document.createElement('tr');

            const empSelTd = document.createElement('td');
            const empSel = document.createElement('select');
            empSel.className = 'form-select uSelect2';
            empSel.innerHTML = '<option value="">Selecione um Consultor</option>';
            tinkers.forEach(c => {
                const newOpt = document.createElement('option');
                newOpt.value = c.id;
                newOpt.text = c.name;
                empSel.appendChild(newOpt);
            });
            empSelTd.appendChild(empSel);
            tr.appendChild(empSelTd);

            const prodSelTd = document.createElement('td');
            const prodSel = document.createElement('select');
            prodSel.className = 'form-select uSelect2';
            prodSel.innerHTML = '<option value="">Selecione um produto</option>';
            products.forEach(p => {
                const prodOpt = document.createElement('option');
                prodOpt.value = p.id;
                prodOpt.text = p.productCode || p.productName;
                prodSel.appendChild(prodOpt);
            });
            prodSelTd.appendChild(prodSel);
            tr.appendChild(prodSelTd);

            const qtdSelTd = document.createElement('td');
            const qtdInput = document.createElement('input');
            qtdInput.className = 'form-control';
            qtdInput.type = 'number';
            qtdInput.value = 0;
            qtdSelTd.appendChild(qtdInput);
            tr.appendChild(qtdSelTd);

            const removeTd = document.createElement('td');
            const removeBtn = document.createElement('button');
            removeBtn.className = 'btn btn-danger';
            removeBtn.type = 'button';
            removeBtn.textContent = 'Remover';
            removeBtn.addEventListener('click', () => {
                tr.remove();
            });
            removeTd.appendChild(removeBtn);
            tr.appendChild(removeTd);

            updateIncentiveTinkerSalesTableBody.appendChild(tr);
        }
    });

    addBtnTinker.appendChild(addTinkerSaleBtn);

    if (mechanicSales) {
        mechanicSales.forEach(ms => {
            const tr = document.createElement('tr');

            const empSelTd = document.createElement('td');
            const empSel = document.createElement('select');
            empSel.className = 'form-select uSelect2';
            const empOpt = document.createElement('option');
            empOpt.value = ms.employeeId;
            empOpt.text = ms.employeeName;
            empOpt.selected = true;
            empSel.appendChild(empOpt);

            if (mechanics) {
                mechanics.forEach(c => {
                    if (c.employeeId != ms.emplyeeId) {
                        const newOpt = document.createElement('option');
                        newOpt.value = c.employeeId;
                        newOpt.text = c.employeeName;
                        empSel.appendChild(newOpt);
                    }
                });
            }

            empSelTd.appendChild(empSel);
            tr.appendChild(empSelTd);


            const prodSelTd = document.createElement('td');
            const prodSel = document.createElement('select');
            prodSel.className = 'form-select uSelect2'
            const prodOpt = document.createElement('option');
            prodOpt.value = ms.productId;
            prodOpt.text = ms.productName;
            prodOpt.selected = true;
            prodSel.appendChild(prodOpt);

            if (products) {
                products.forEach(p => {
                    if (p.productId != ms.productId) {
                        const newOpt = document.createElement('option');
                        newOpt.value = p.productId;
                        newOpt.text = p.productName;
                        prodSel.appendChild(newOpt);
                    }
                });
            }

            prodSelTd.appendChild(prodSel);
            tr.appendChild(prodSelTd);

            const qtdSelTd = document.createElement('td');
            const qtdInput = document.createElement('input');
            qtdInput.className = 'form-control';
            qtdInput.type = 'number';
            qtdInput.value = ms.quantity;
            qtdSelTd.appendChild(qtdInput);
            tr.appendChild(qtdSelTd);

            const removeTd = document.createElement('td');
            const removeBtn = document.createElement('button');
            removeBtn.className = 'btn btn-danger';
            removeBtn.type = 'button';
            removeBtn.textContent = 'Remover';
            removeBtn.addEventListener('click', () => {
                tr.remove();
            });
            removeTd.appendChild(removeBtn);
            tr.appendChild(removeTd);

            updateIncentiveMechanicSalesTableBody.appendChild(tr);
        });
    }

    const addMechanicSaleBtn = document.createElement('button');
    addMechanicSaleBtn.className = 'btn btn-secondary';
    addMechanicSaleBtn.type = 'button';
    addMechanicSaleBtn.textContent = 'Adicionar Mecânico';
    addMechanicSaleBtn.addEventListener('click', () => {
        if (mechanics && products) {
            const tr = document.createElement('tr');

            const empSelTd = document.createElement('td');
            const empSel = document.createElement('select');
            empSel.className = 'form-select uSelect2';
            empSel.innerHTML = '<option value="">Selecione um Mecânico</option>';
            mechanics.forEach(c => {
                const newOpt = document.createElement('option');
                newOpt.value = c.id;
                newOpt.text = c.name;
                empSel.appendChild(newOpt);
            });
            empSelTd.appendChild(empSel);
            tr.appendChild(empSelTd);

            const prodSelTd = document.createElement('td');
            const prodSel = document.createElement('select');
            prodSel.className = 'form-select uSelect2';
            prodSel.innerHTML = '<option value="">Selecione um produto</option>';
            products.forEach(p => {
                const prodOpt = document.createElement('option');
                prodOpt.value = p.id;
                prodOpt.text = p.productCode || p.productName;
                prodSel.appendChild(prodOpt);
            });
            prodSelTd.appendChild(prodSel);
            tr.appendChild(prodSelTd);

            const qtdSelTd = document.createElement('td');
            const qtdInput = document.createElement('input');
            qtdInput.className = 'form-control';
            qtdInput.type = 'number';
            qtdInput.value = 0;
            qtdSelTd.appendChild(qtdInput);
            tr.appendChild(qtdSelTd);

            const removeTd = document.createElement('td');
            const removeBtn = document.createElement('button');
            removeBtn.className = 'btn btn-danger';
            removeBtn.type = 'button';
            removeBtn.textContent = 'Remover';
            removeBtn.addEventListener('click', () => {
                tr.remove();
            });
            removeTd.appendChild(removeBtn);
            tr.appendChild(removeTd);

            updateIncentiveMechanicSalesTableBody.appendChild(tr);
        }
    });


    addBtnMechanic.appendChild(addMechanicSaleBtn);
}

const openConfirmUpdateModal = document.getElementById('openConfirmUpdateModal');

if (openConfirmUpdateModal) {
    openConfirmUpdateModal.addEventListener('click', () => {
        const confirmUpdateModal = bootstrap.Modal.getOrCreateInstance(document.getElementById('confirmUpdateModal'));
        confirmUpdateModal.show();
    });
}

async function sendIncentiveUpdates() {
    const consultantRows = document.querySelectorAll('#updateIncentiveConsultantSalesTableBody tr');
    const consultantSales = Array.from(consultantRows).map(tr => {
        const selects = tr.querySelectorAll('select');
        const inputs = tr.querySelectorAll('input');
        return {
            employeeId: selects[0].value,
            productId: selects[1].value,
            quantity: inputs[0].value
        };
    });

    const tinkerRows = document.querySelectorAll('#updateIncentiveTinkerSalesTableBody tr');
    const tinkerSales = Array.from(tinkerRows).map(tr => {
        const selects = tr.querySelectorAll('select');
        const inputs = tr.querySelectorAll('input');
        return {
            employeeId: selects[0].value,
            productId: selects[1].value,
            quantity: inputs[0].value
        };
    });

    const mechanicRows = document.querySelectorAll('#updateIncentiveMechanicSalesTableBody tr');
    const mechanicSales = Array.from(mechanicRows).map(tr => {
        const selects = tr.querySelectorAll('select');
        const inputs = tr.querySelectorAll('input');
        return {
            employeeId: selects[0].value,
            productId: selects[1].value,
            quantity: inputs[0].value
        };
    });

    function hasDuplicate(sales) {
        const set = new Set();
        for (const sale of sales) {
            const key = `${sale.employeeId}-${sale.productId}`;
            if (set.has(key)) {
                return true;
            }
            set.add(key);
        }
        return false;
    }

    if (hasDuplicate(consultantSales)) {
        alert('Não é permitido que o mesmo consultor venda o mesmo produto mais de uma vez.');
        return;
    }
    if (hasDuplicate(tinkerSales)) {
        alert('Não é permitido que o mesmo consultor venda o mesmo produto mais de uma vez.');
        return;
    }
    if (hasDuplicate(mechanicSales)) {
        alert('Não é permitido que o mesmo mecânico venda o mesmo produto mais de uma vez.');
        return;
    }

    const documentNumber = document.getElementById('updateIncentiveDocumentNumber').textContent;
    const payload = {
        documentNumber: documentNumber,
        consultantSales: consultantSales,
        tinkerSales: tinkerSales,
        mechanicSales: mechanicSales
    };

    try {
        const request = await fetch(`/api/sales/update/${documentNumber}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(payload)
        });

        await requestIsOk(request);

        alert('Vendas e incentivos atualizados com sucesso');

        const updateModalInstance = bootstrap.Modal.getOrCreateInstance(updateIncentiveModal);
        const confirmUpdateModalInstance = bootstrap.Modal.getOrCreateInstance(openConfirmUpdateModal);

        updateModalInstance.hide();
        confirmUpdateModalInstance.hide();

        window.location.href = '/incentives';

    } catch (error) {
        console.log(error);
    }
}

const confirmUpdateBtn = document.getElementById('confirmUpdateBtn');

if (confirmUpdateBtn) {
    confirmUpdateBtn.addEventListener('click', async () => {
        await sendIncentiveUpdates();
    });
}


async function downloadReport(num, btn) {
    try {
        btn.textContent = 'Gerando Relatório...';
        btn.disabled = true;

        const url = `/reports/by-docNum/${num}`;

        const request = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(num)
        });

        await requestIsOk(request);

        const contentDisposition = request.headers.get('content-disposition');
        const blob = await request.blob();

        let filename = 'relatorio.bin';
        if (contentDisposition) {
            const match = contentDisposition.match(/filename="?(.+)"?/);
            if (match && match.length > 1) {
                filename = match[1];
            }
        }

        if (blob.size > 0 && blob.type === 'application/pdf') {
            const objectUrl = URL.createObjectURL(blob);
            window.open(objectUrl, '_blank');
            URL.revokeObjectURL(objectUrl);
        } else {
            throw new Error('Arquivo inválido ou vazio.');
        }

    } catch (error) {
        console.error(error);
    } finally {
        generateReportBtn.textContent = 'Gerar Relatório';
        generateReportBtn.disabled = false;

        window.location.href = '/incentives'
    }
}

const confirmDeleteModal = document.getElementById('confirmDeleteModal');
const deleteIncentnveBtn = document.getElementById('deleteIncentnveBtn');

if (openConfirmDeleteModal) {
    openConfirmDeleteModal.addEventListener('click', (event) => {
        const documentNumber = event.target.getAttribute('data-document-number');
        deleteIncentnveBtn.setAttribute('data-document-number', documentNumber);

        const openConfirmDeleteModalInstance = bootstrap.Modal.getOrCreateInstance(confirmDeleteModal);
        openConfirmDeleteModalInstance.show();
    });
}

if (deleteIncentnveBtn) {
    deleteIncentnveBtn.addEventListener('click', async (event) => {
        const documentNumber = event.target.getAttribute('data-document-number');

        try {
            const url = `/api/sales/delete/${documentNumber}`;

            const request = await fetch(url, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                }
            });

            await requestIsOk(request);

            alert('Vendas e incentivos com número de documento: ' + documentNumber + ' deletado com sucesso');

            window.location.href = "/incentives";
        } catch (error) {
            console.log(error)
        }
    })
}


const page6Modal = document.getElementById('page6');
const tinkerSelect = document.getElementById('tinkerSelect');

const noTinkerAvailable = document.getElementById('noTinkerAvailable');
const tinkerModalBody = document.getElementById('tinkerModalBody');
const goToPage6 = document.getElementById('go-to-page-6');
const tinkerSalesTableBody = document.getElementById('tinkerSalesTableBody');
const tinkerResumeSalesTableBody = document.getElementById('tinkerResumeSalesTableBody');
const tinkerResumeSalesModalTableBody = document.getElementById('tinkerResumeSalesModalTableBody');
const tinkerConfirmUpdateBtn = document.getElementById('tinkerConfirmUpdateBtn');
const resumeTkName = document.getElementById('resumeTkName');
const tinkerResumeSalesModal = document.getElementById('tinkerResumeSalesModal');


goToPage6.addEventListener('click', async function () {
    goToPage6.disabled = true;
    goToPage6.textContent = 'avançando ...';

    const customerId = customerSelect.value;

    await loadPage6Infos(customerId);

    setMessageDisplay(noConsultantsAvailable, consultantModalBody, false);

    const page6ModalInstance = bootstrap.Modal.getOrCreateInstance(page6Modal);
    page6ModalInstance.show();

    goToPage6.disabled = false;
    goToPage6.textContent = 'avançar';
});

async function loadPage6Infos(customerId) {
    const url = `/api/customers/${customerId}/Consultor de Funilaria`;

    try {
        const request = await fetch(url);

        await requestIsOk(request);

        const response = await request.json();

        fillPage6(response);

    } catch (error) {
        console.log(error);
    }
}

function fillPage6(response) {
    const tinkers = response.employees;
    const customerProducts = response.products;

    if (!tinkers || tinkers.length == 0) {
        setMessageDisplay(noTinkerAvailable, tinkerModalBody, true);
        return;
    }

    if (Array.isArray(tinkers)) {
        employees.push(...tinkers);
    } else if (typeof tinkers === 'object' && tinkers !== null) {
        employees.push(...Object.values(tinkers));
    } else {
        console.warn('response.employees não é array nem objeto válido!');
    }

    if (Array.isArray(customerProducts)) {
        products = customerProducts;
    } else if (typeof customerProducts === 'object' && customerProducts !== null) {
        products = Object.values(customerProducts);
    } else {
        products = [];
        console.warn('response.products não é array nem objeto válido!');
    }

    fillSelect('tinkerSelect', tinkers, 'id', 'name');
    fillTinkerProductTable(customerProducts);
}

function fillTinkerProductTable(products) {
    tinkerSalesTableBody.innerHTML = '';

    products.forEach(product => {
        const tr = document.createElement('tr');
        tr.setAttribute('data-product-id', product.id);

        const codTd = document.createElement('td');
        codTd.textContent = product.productCode;
        tr.appendChild(codTd);

        const nameTd = document.createElement('td');
        nameTd.textContent = product.productName;
        tr.appendChild(nameTd);

        const qtdTd = document.createElement('td');
        const qtdInput = document.createElement('input');
        qtdInput.type = 'number';
        qtdInput.className = 'form-control';
        qtdTd.appendChild(qtdInput);
        tr.appendChild(qtdTd);

        const ivTd = document.createElement('td');
        ivTd.textContent = product.incentiveValue;
        tr.appendChild(ivTd);

        const totalTd = document.createElement('td');
        tr.appendChild(totalTd);

        qtdInput.addEventListener('change', function () {
            const iv = parseFloat(ivTd.textContent);;
            const qtd = parseFloat(qtdInput.value) || 0; const total = iv * qtd;
            totalTd.textContent = "R$ " + total.toFixed(2);
        });

        tinkerSalesTableBody.appendChild(tr);
    });
}

function saveTinkerSale() {
    const customerId = customerSelect.value;
    const employeeId = tinkerSelect.value;

    if (!employeeId || employeeId === 'default') {
        alert('selecione um Premiado');
        return;
    }

    let newSale = {
        customer: customerId,
        employee: employeeId,
        function: 'Consultor de Funilaria',
        products: []
    }

    const rows = tinkerSalesTableBody.querySelectorAll('tr');
    rows.forEach(row => {
        const productId = row.getAttribute('data-product-id');
        const quantityInput = row.querySelector('input');
        const quantity = parseInt(quantityInput.value) || 0;

        if (quantity >= 0) {
            newSale.products.push({
                productId: productId,
                quantity: quantity
            });
        }

    });

    if (newSale.products.length > 0) {
        sales.push(newSale);
        console.log(newSale);
        alert("Venda salva com sucesso!");

        const optionToRemove = tinkerSelect.querySelector(`option[value="${employeeId}"]`);
        if (optionToRemove) {
            optionToRemove.remove();
        }

        tinkerSelect.value = "default";

        rows.forEach(row => {
            const quantityInput = row.querySelector('input');
            if (quantityInput) {
                quantityInput.value = "";
            }

            if (row) {
                const totalTd = row.children[4];
                totalTd.textContent = "";
            }
        });
    } else {
        alert("Preencha ao menos um produto com uma quantidade válida.");
    }

    fillResumeTinkerTable(newSale);
}

function fillResumeTinkerTable(newSale) {

    const emp = employees.find(e => e.id == newSale.employee);

    if (!emp) {
        console.error('Premiado não encontrado para ID:', newSale.employee);
        alert('Erro: Consultor não encontrado!');
        return;
    }

    const empName = emp.name;
    const empId = emp.id;

    let total = 0;
    newSale.products.forEach(p => total += p.quantity);

    const tr = document.createElement('tr');
    tr.setAttribute('data-employee-id', empId);

    const empTd = document.createElement('td');
    empTd.textContent = empName;
    tr.appendChild(empTd);

    const totalTd = document.createElement('td');
    totalTd.textContent = total;
    tr.appendChild(totalTd);

    const actionTd = document.createElement('td');

    const editOrViewBtn = document.createElement('button');
    editOrViewBtn.textContent = 'Visualizar/Editar';
    editOrViewBtn.className = 'btn btn-warning';
    editOrViewBtn.setAttribute('data-employee-id', empId);
    editOrViewBtn.addEventListener('click', function () {
        fillResumeTinkerSaleModal(newSale, empName);
    })

    actionTd.appendChild(editOrViewBtn);
    tr.appendChild(actionTd);

    tinkerResumeSalesTableBody.appendChild(tr);
}

function fillResumeTinkerSaleModal(sale, empName) {
    tinkerResumeSalesModalTableBody.innerHTML = '';

    sale.products.forEach(product => {
        const productCode = products.find(p => p.id == product.productId).productCode;
        const tr = document.createElement('tr');
        tr.setAttribute('data-product-id', product.productId);

        const codTd = document.createElement('td');
        codTd.textContent = productCode;
        tr.appendChild(codTd);

        const qtdTd = document.createElement('td');
        const qtdInput = document.createElement('input');
        qtdInput.type = 'number';
        qtdInput.className = 'form-control';
        qtdInput.value = product.quantity;
        qtdTd.appendChild(qtdInput);
        tr.appendChild(qtdTd);

        tinkerResumeSalesModalTableBody.appendChild(tr);
    });

    tinkerConfirmUpdateBtn.onclick = function () {
        const updatedProducts = [];
        const rows = tinkerResumeSalesModalTableBody.querySelectorAll('tr');
        rows.forEach(row => {
            const productId = row.getAttribute('data-product-id');
            const quantityInput = row.querySelector('input');
            const quantity = parseInt(quantityInput.value) || 0;
            if (quantity >= 0) {
                updatedProducts.push({ productId, quantity });
            }
        });
        updateTinkerSale(sale.employee, updatedProducts);
    };

    resumeTkName.textContent = empName;

    const tinkerResumeSalesInstance = bootstrap.Modal.getOrCreateInstance(tinkerResumeSalesModal);
    tinkerResumeSalesInstance.show();
}

function updateTinkerSale(empId, products) {
    const saleToUpdate = sales.find(s => s.employee === empId);
    if (saleToUpdate) {
        saleToUpdate.products = products;
        console.log('Venda atualizada:', saleToUpdate);
    } else {
        console.error('Venda não encontrada para atualização!');
    }

    const newTotal = products.reduce((sum, p) => sum + p.quantity, 0);

    const row = tinkerResumeSalesTableBody
        .querySelector(`tr[data-employee-id="${empId}"]`);

    if (row) {
        const totalTd = row.children[1];
        totalTd.textContent = newTotal;
    }

    const tinkerResumeSalesInstance = bootstrap.Modal.getOrCreateInstance(tinkerResumeSalesModal);
    tinkerResumeSalesInstance.hide();
}

function eraseTinkerInfos() {
    tinkerResumeSalesModalTableBody.innerHTML = '';
    tinkerResumeSalesTableBody.innerHTML = '';
    setMessageDisplay(noTinkerAvailable, tinkerModalBody, false);
}