const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

document.addEventListener('DOMContentLoaded', function () {
    initializeDatePickers();
    initializeSelect2();
});

function initializeDatePickers() {
    flatpickr("#passageDate", {
        dateFormat: "Y-m-d",
        defaultDate: new Date()
    });

    flatpickr("#editPassageDate", {
        dateFormat: "Y-m-d"
    });

    flatpickr("#filterStartDate", {
        dateFormat: "Y-m-d"
    });

    flatpickr("#filterEndDate", {
        dateFormat: "Y-m-d"
    });
}

function initializeSelect2() {
    $('#passageCustomer').select2({
        placeholder: "Selecione um cliente",
        dropdownParent: $('#newPassageModal'),
        width: '100%'
    });

    $('#editPassageCustomer').select2({
        placeholder: "Selecione um cliente",
        dropdownParent: $('#editPassageModal'),
        width: '100%'
    });

    $('#filterCustomer').select2({
        placeholder: "Todos",
        dropdownParent: $('#filterModal'),
        width: '100%'
    });
}

// Nova Passagem
document.getElementById('newPassageBtn').addEventListener('click', function () {
    document.getElementById('passageDate').value = new Date().toISOString().split('T')[0];
    document.getElementById('passageCustomer').value = '';
    $('#passageCustomer').trigger('change');
    document.getElementById('employeesTableContainer').style.display = 'none';
    document.getElementById('employeesTableBody').innerHTML = '';

    const modal = new bootstrap.Modal(document.getElementById('newPassageModal'));
    modal.show();
});

// Carregar funcionários automaticamente ao trocar cliente
$('#passageCustomer').on('change', async function() {
    const customerId = $(this).val();
    
    if (!customerId) {
        document.getElementById('employeesTableContainer').style.display = 'none';
        document.getElementById('employeesTableBody').innerHTML = '';
        return;
    }

    try {
        const response = await fetch(`/api/customers/${customerId}/employees-for-passage`);
        if (!response.ok) throw new Error('Erro ao carregar funcionários');

        const employees = await response.json();
        displayEmployees(employees, 'employeesTableBody');
        document.getElementById('employeesTableContainer').style.display = 'block';
    } catch (error) {
        alert('Erro ao carregar funcionários: ' + error.message);
        document.getElementById('employeesTableContainer').style.display = 'none';
        document.getElementById('employeesTableBody').innerHTML = '';
    }
});

function displayEmployees(employees, tableBodyId) {
    const tbody = document.getElementById(tableBodyId);
    tbody.innerHTML = '';

    employees.forEach(employee => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${employee.name}</td>
            <td>
                <input type="number" class="form-control form-control-sm" 
                       data-employee-id="${employee.id}" 
                       min="0" value="0">
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// Salvar Passagem
document.getElementById('savePassageBtn').addEventListener('click', async function () {
    const date = document.getElementById('passageDate').value;
    const customerId = document.getElementById('passageCustomer').value;

    if (!date || !customerId) {
        alert('Preencha todos os campos obrigatórios');
        return;
    }

    const items = [];
    const inputs = document.querySelectorAll('#employeesTableBody input[type="number"]');

    inputs.forEach(input => {
        const quantity = parseInt(input.value) || 0;
        if (quantity > 0) {
            items.push({
                employeeId: parseInt(input.getAttribute('data-employee-id')),
                quantity: quantity
            });
        }
    });

    if (items.length === 0) {
        alert('Adicione pelo menos um funcionário com quantidade maior que zero');
        return;
    }

    const passageDTO = {
        referenceDate: date,
        customerId: parseInt(customerId),
        items: items
    };

    try {
        const response = await fetch('/passages', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(passageDTO)
        });

        if (!response.ok) throw new Error('Erro ao criar passagem');

        alert('Passagem criada com sucesso!');
        location.reload();
    } catch (error) {
        alert('Erro ao criar passagem: ' + error.message);
    }
});

// Visualizar Passagem
async function viewPassage(passageNumber) {
    try {
        const response = await fetch(`/passages/${passageNumber}`);
        if (!response.ok) throw new Error('Erro ao carregar passagem');

        const passage = await response.json();

        document.getElementById('viewPassageNumber').textContent = passage.passageNumber;
        document.getElementById('viewPassageDate').textContent = new Date(passage.referenceDate).toLocaleDateString('pt-BR');
        document.getElementById('viewPassageCustomer').textContent = passage.customerName;
        document.getElementById('viewPassageTotal').textContent = passage.totalQuantity;

        const tbody = document.getElementById('viewEmployeesTableBody');
        tbody.innerHTML = '';

        passage.employees.forEach(emp => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${emp.employeeName}</td>
                <td>${emp.employeeCpf}</td>
                <td>${emp.quantity}</td>
            `;
            tbody.appendChild(tr);
        });

        const modal = new bootstrap.Modal(document.getElementById('viewPassageModal'));
        modal.show();
    } catch (error) {
        alert('Erro ao visualizar passagem: ' + error.message);
    }
}

// Editar Passagem
async function editPassage(passageNumber) {
    try {
        const response = await fetch(`/passages/${passageNumber}`);
        if (!response.ok) throw new Error('Erro ao carregar passagem');

        const passage = await response.json();

        document.getElementById('editPassageNumber').value = passage.passageNumber;
        document.getElementById('editPassageDate').value = passage.referenceDate;
        document.getElementById('editPassageCustomer').value = passage.customerId;
        $('#editPassageCustomer').trigger('change');

        // Carregar funcionários do cliente
        const empResponse = await fetch(`/api/customers/${passage.customerId}/employees-for-passage`);
        if (!empResponse.ok) throw new Error('Erro ao carregar funcionários');

        const employees = await empResponse.json();
        const tbody = document.getElementById('editEmployeesTableBody');
        tbody.innerHTML = '';

        employees.forEach(employee => {
            const passageEmp = passage.employees.find(e => e.employeeId === employee.id);
            const quantity = passageEmp ? passageEmp.quantity : 0;

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${employee.name}</td>
                <td>
                    <input type="number" class="form-control form-control-sm" 
                           data-employee-id="${employee.id}" 
                           min="0" value="${quantity}">
                </td>
            `;
            tbody.appendChild(tr);
        });

        const modal = new bootstrap.Modal(document.getElementById('editPassageModal'));
        modal.show();
    } catch (error) {
        alert('Erro ao editar passagem: ' + error.message);
    }
}

// Recarregar Funcionários (Edição) - agora também automático
$('#editPassageCustomer').on('change', async function() {
    const customerId = $(this).val();

    if (!customerId) {
        document.getElementById('editEmployeesTableBody').innerHTML = '';
        return;
    }

    try {
        const response = await fetch(`/api/customers/${customerId}/employees-for-passage`);
        if (!response.ok) throw new Error('Erro ao carregar funcionários');

        const employees = await response.json();
        displayEmployees(employees, 'editEmployeesTableBody');
    } catch (error) {
        alert('Erro ao carregar funcionários: ' + error.message);
    }
});

// Atualizar Passagem
document.getElementById('updatePassageBtn').addEventListener('click', async function () {
    const passageNumber = document.getElementById('editPassageNumber').value;
    const date = document.getElementById('editPassageDate').value;
    const customerId = document.getElementById('editPassageCustomer').value;

    if (!date || !customerId) {
        alert('Preencha todos os campos obrigatórios');
        return;
    }

    const items = [];
    const inputs = document.querySelectorAll('#editEmployeesTableBody input[type="number"]');

    inputs.forEach(input => {
        const quantity = parseInt(input.value) || 0;
        if (quantity > 0) {
            items.push({
                employeeId: parseInt(input.getAttribute('data-employee-id')),
                quantity: quantity
            });
        }
    });

    if (items.length === 0) {
        alert('Adicione pelo menos um funcionário com quantidade maior que zero');
        return;
    }

    const passageDTO = {
        referenceDate: date,
        customerId: parseInt(customerId),
        items: items
    };

    try {
        const response = await fetch(`/passages/${passageNumber}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(passageDTO)
        });

        if (!response.ok) throw new Error('Erro ao atualizar passagem');

        alert('Passagem atualizada com sucesso!');
        location.reload();
    } catch (error) {
        alert('Erro ao atualizar passagem: ' + error.message);
    }
});

// Deletar Passagem
let passageToDelete = null;

function confirmDeletePassage(passageNumber) {
    passageToDelete = passageNumber;
    const modal = new bootstrap.Modal(document.getElementById('confirmDeleteModal'));
    modal.show();
}

document.getElementById('confirmDeleteBtn').addEventListener('click', async function () {
    if (!passageToDelete) return;

    try {
        const response = await fetch(`/passages/${passageToDelete}`, {
            method: 'DELETE',
            headers: {
                [csrfHeader]: csrfToken
            }
        });

        if (!response.ok) throw new Error('Erro ao deletar passagem');

        alert('Passagem deletada com sucesso!');
        location.reload();
    } catch (error) {
        alert('Erro ao deletar passagem: ' + error.message);
    }
});

// Filtros
document.getElementById('openFilterModal').addEventListener('click', function () {
    const modal = new bootstrap.Modal(document.getElementById('filterModal'));
    modal.show();
});

document.getElementById('applyFiltersBtn').addEventListener('click', async function () {
    const start = document.getElementById('filterStartDate').value;
    const end = document.getElementById('filterEndDate').value;
    const customerId = document.getElementById('filterCustomer').value;

    const params = new URLSearchParams();
    if (start) params.append('start', start);
    if (end) params.append('end', end);
    if (customerId) params.append('customerId', customerId);

    try {
        const response = await fetch(`/passages/filter?${params.toString()}`);
        if (!response.ok) throw new Error('Erro ao filtrar passagens');

        const passages = await response.json();
        updatePassagesTable(passages);

        const modal = bootstrap.Modal.getInstance(document.getElementById('filterModal'));
        modal.hide();
    } catch (error) {
        alert('Erro ao filtrar passagens: ' + error.message);
    }
});

document.getElementById('clearFiltersBtn').addEventListener('click', function () {
    location.reload();
});

function updatePassagesTable(passages) {
    const tbody = document.querySelector('#passagesTable tbody');
    tbody.innerHTML = '';

    if (Object.keys(passages).length === 0) {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td colspan="5" class="text-center text-muted">
                <i class="bi bi-inbox"></i> Nenhuma passagem encontrada com os filtros aplicados
            </td>
        `;
        tbody.appendChild(tr);
        return;
    }

    Object.entries(passages).forEach(([passageNumber, passage]) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${passageNumber}</td>
            <td>${new Date(passage.referenceDate).toLocaleDateString('pt-BR')}</td>
            <td>${passage.customerName}</td>
            <td>${passage.totalQuantity}</td>
            <td>
                <button type="button" class="btn btn-secondary btn-sm"
                    onclick="viewPassage(${passageNumber})" title="Visualizar">
                    <i class="bi bi-eye"></i>
                </button>
                <button type="button" class="btn btn-warning btn-sm"
                    onclick="editPassage(${passageNumber})" title="Editar">
                    <i class="bi bi-pencil"></i>
                </button>
                <button type="button" class="btn btn-danger btn-sm"
                    onclick="confirmDeletePassage(${passageNumber})" title="Deletar">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}
