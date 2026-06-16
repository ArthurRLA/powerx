const CRUD_SEARCH_DEBOUNCE_MS = 1500;

let crudSearchTimer = null;

function searchByName() {
    const input = document.getElementById('searchByName');
    if (!input) {
        return;
    }
    const url = input.getAttribute('data-search-url');
    const kind = input.getAttribute('data-search-kind');
    const tbodySelector = input.getAttribute('data-tbody-selector') || '#crudSearchTbody';
    const paginationSelector = input.getAttribute('data-pagination-selector');

    clearTimeout(crudSearchTimer);

    if (!url || !kind) {
        crudSearchTimer = setTimeout(() => legacyClientFilterSecondColumn(tbodySelector), CRUD_SEARCH_DEBOUNCE_MS);
        return;
    }

    crudSearchTimer = setTimeout(() => {
        runServerCrudSearch(input, url, kind, tbodySelector, paginationSelector);
    }, CRUD_SEARCH_DEBOUNCE_MS);
}

function legacyClientFilterSecondColumn(tbodySelector) {
    const input = document.getElementById('searchByName');
    const term = (input && input.value ? input.value : '').toLowerCase().trim();
    const tbody = document.querySelector(tbodySelector);
    if (!tbody) {
        return;
    }
    const rows = tbody.getElementsByTagName('tr');
    for (let i = 0; i < rows.length; i++) {
        const cells = rows[i].getElementsByTagName('td');
        if (cells.length < 2) {
            continue;
        }
        const second = (cells[1].textContent || cells[1].innerText || '').toLowerCase();
        rows[i].style.display = !term || second.indexOf(term) > -1 ? '' : 'none';
    }
}

function runServerCrudSearch(input, url, kind, tbodySelector, paginationSelector) {
    const raw = (input.value || '').trim();
    const tbody = document.querySelector(tbodySelector);
    if (!tbody) {
        return;
    }
    const pagination = paginationSelector ? document.querySelector(paginationSelector) : null;

    if (!raw) {
        window.location.assign(window.location.pathname);
        return;
    }

    const sep = url.indexOf('?') >= 0 ? '&' : '?';
    fetch(url + sep + 'q=' + encodeURIComponent(raw), {
        headers: { 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }
    })
        .then(r => {
            if (!r.ok) {
                throw new Error('Falha na busca');
            }
            return r.json();
        })
        .then(data => {
            tbody.innerHTML = '';
            const renderer = ROW_RENDERERS[kind];
            if (!renderer) {
                return;
            }
            renderer(tbody, data);
            if (pagination) {
                pagination.classList.add('d-none');
            }
            if (typeof window.afterCrudTableSearch === 'function') {
                window.afterCrudTableSearch(kind);
            }
        })
        .catch(err => console.error(err));
}

function appendCells(tr, texts) {
    texts.forEach(t => {
        const td = document.createElement('td');
        td.textContent = t == null ? '' : String(t);
        tr.appendChild(td);
    });
}

function appendActionButtons(tr, actions) {
    const td = document.createElement('td');
    td.className = actions.tdClass || '';
    actions.buttons.forEach(b => {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.className = b.className;
        btn.textContent = b.label;
        Object.keys(b.dataset || {}).forEach(k => btn.setAttribute('data-' + k, b.dataset[k]));
        if (b.toggleModal) {
            btn.setAttribute('data-bs-toggle', 'modal');
            btn.setAttribute('data-bs-target', b.toggleModal);
        }
        td.appendChild(btn);
    });
    tr.appendChild(td);
}

const ROW_RENDERERS = {
    users(tbody, rows) {
        rows.forEach(r => {
            const tr = document.createElement('tr');
            appendCells(tr, [r.id, r.name]);
            appendActionButtons(tr, {
                buttons: [
                    { className: 'btn btn-warning', label: 'Editar', dataset: { 'user-id': r.id }, toggleModal: '#editUserModal' },
                    { className: 'btn btn-info', label: 'Detalhes', dataset: { 'user-id': r.id }, toggleModal: '#detailsUserModal' }
                ]
            });
            tbody.appendChild(tr);
        });
    },
    customers(tbody, rows) {
        rows.forEach(r => {
            const tr = document.createElement('tr');
            tr.classList.add('customer-data-row');
            tr.setAttribute('data-customer-id', r.id);
            const ativo = r.active ? 'SIM' : 'NÃO';
            appendCells(tr, [
                r.id,
                r.fantasyName || '',
                r.unysoftCode || '',
                r.cnpj || '',
                r.registeredName || '',
                r.address || '',
                ativo,
                r.userName || '',
                r.groupName || '',
                r.mechanicApurationName || '',
                r.industryName || '',
                r.flagName || ''
            ]);
            tbody.appendChild(tr);
        });
    },
    products(tbody, rows) {
        rows.forEach(r => {
            const tr = document.createElement('tr');
            appendCells(tr, [r.productCode, r.productName]);
            const td = document.createElement('td');
            const edit = document.createElement('button');
            edit.type = 'button';
            edit.className = 'btn btn-warning';
            edit.setAttribute('data-bs-toggle', 'modal');
            edit.setAttribute('data-bs-target', '#editProductModal' + r.id);
            edit.textContent = 'Editar';
            const det = document.createElement('button');
            det.type = 'button';
            det.className = 'btn btn-info';
            det.setAttribute('data-bs-toggle', 'modal');
            det.setAttribute('data-bs-target', '#detailsProductModal' + r.id);
            det.textContent = 'Detalhes';
            td.appendChild(edit);
            td.appendChild(det);
            tr.appendChild(td);
            tbody.appendChild(tr);
        });
    },
    productIncentiveDistribution(tbody, rows) {
        rows.forEach(r => {
            const tr = document.createElement('tr');
            appendCells(tr, [r.productCode, r.productName]);
            const td = document.createElement('td');
            const inc = document.createElement('button');
            inc.type = 'button';
            inc.className = 'btn btn-secondary';
            inc.setAttribute('data-bs-toggle', 'modal');
            inc.setAttribute('data-bs-target', '#editIncentiveValuesModal' + r.id);
            inc.textContent = 'Atualizar incentivos';
            td.appendChild(inc);
            tr.appendChild(td);
            tbody.appendChild(tr);
        });
    },
    employees(tbody, rows) {
        rows.forEach(r => {
            const tr = document.createElement('tr');
            appendCells(tr, [r.id, r.name]);
            appendActionButtons(tr, {
                buttons: [
                    { className: 'btn btn-warning', label: 'Editar', dataset: { 'employee-id': r.id }, toggleModal: '#editEmployeeModal' },
                    { className: 'btn btn-info', label: 'Detalhes', dataset: { 'employee-id': r.id }, toggleModal: '#detailsEmployeeModal' }
                ]
            });
            tbody.appendChild(tr);
        });
    },
    groups(tbody, rows) {
        rows.forEach(r => {
            const tr = document.createElement('tr');
            appendCells(tr, [r.id, r.name]);
            const td = document.createElement('td');
            const edit = document.createElement('button');
            edit.type = 'button';
            edit.className = 'btn btn-warning';
            edit.setAttribute('data-bs-toggle', 'modal');
            edit.setAttribute('data-bs-target', '#editGroupModal' + r.id);
            edit.textContent = 'Editar';
            const det = document.createElement('button');
            det.type = 'button';
            det.className = 'btn btn-info';
            det.setAttribute('data-bs-toggle', 'modal');
            det.setAttribute('data-bs-target', '#detailsGroupModal' + r.id);
            det.textContent = 'Detalhes';
            td.appendChild(edit);
            td.appendChild(det);
            tr.appendChild(td);
            tbody.appendChild(tr);
        });
    },
    payments(tbody, rows) {
        rows.forEach(r => {
            const tr = document.createElement('tr');
            appendCells(tr, [r.id, r.name]);
            const td = document.createElement('td');
            const edit = document.createElement('button');
            edit.type = 'button';
            edit.className = 'btn btn-warning';
            edit.setAttribute('data-bs-toggle', 'modal');
            edit.setAttribute('data-bs-target', '#editPaymentModal' + r.id);
            edit.textContent = 'Editar';
            td.appendChild(edit);
            tr.appendChild(td);
            tbody.appendChild(tr);
        });
    },
    industries(tbody, rows) {
        rows.forEach(r => {
            const tr = document.createElement('tr');
            appendCells(tr, [r.id, r.name]);
            const td = document.createElement('td');
            const edit = document.createElement('button');
            edit.type = 'button';
            edit.className = 'btn btn-warning';
            edit.setAttribute('data-bs-toggle', 'modal');
            edit.setAttribute('data-bs-target', '#editIndustryModal' + r.id);
            edit.textContent = 'Editar';
            td.appendChild(edit);
            tr.appendChild(td);
            tbody.appendChild(tr);
        });
    },
    flags(tbody, rows) {
        rows.forEach(r => {
            const tr = document.createElement('tr');
            appendCells(tr, [r.id, r.name]);
            const td = document.createElement('td');
            const edit = document.createElement('button');
            edit.type = 'button';
            edit.className = 'btn btn-warning';
            edit.setAttribute('data-bs-toggle', 'modal');
            edit.setAttribute('data-bs-target', '#editFlagModal' + r.id);
            edit.textContent = 'Editar';
            td.appendChild(edit);
            tr.appendChild(td);
            tbody.appendChild(tr);
        });
    },
    'table-prices'(tbody, rows) {
        rows.forEach(r => {
            const tr = document.createElement('tr');
            appendCells(tr, [r.customerFantasyName, r.productName, r.price]);
            const td = document.createElement('td');
            td.className = 'd-flex gap-2';
            const view = document.createElement('button');
            view.type = 'button';
            view.className = 'btn btn-info';
            view.setAttribute('data-table-price-id', r.id);
            view.setAttribute('data-bs-toggle', 'modal');
            view.setAttribute('data-bs-target', '#viewTablePriceModal');
            view.textContent = 'Visualizar';
            const edit = document.createElement('button');
            edit.type = 'button';
            edit.className = 'btn btn-warning';
            edit.setAttribute('data-table-price-id', r.id);
            edit.setAttribute('data-bs-toggle', 'modal');
            edit.setAttribute('data-bs-target', '#editTablePriceModal');
            edit.textContent = 'Editar';
            td.appendChild(view);
            td.appendChild(edit);
            tr.appendChild(td);
            tbody.appendChild(tr);
        });
    }
};
