(function () {
	let menuEl = null;
	let currentCustomerId = null;
	let tbodyContextHandler = null;

	function ensureMenu() {
		if (menuEl) {
			return menuEl;
		}
		menuEl = document.createElement('div');
		menuEl.id = 'customerRowContextMenu';
		menuEl.className = 'customer-row-context-menu shadow border bg-white rounded py-1';
		menuEl.style.cssText = 'display:none;position:fixed;z-index:20000;min-width:160px;';
		menuEl.innerHTML =
			'<button type="button" class="dropdown-item w-100 text-start border-0 bg-transparent py-2 px-3" id="ctxCustomerDetails">Detalhes</button>' +
			'<button type="button" class="dropdown-item w-100 text-start border-0 bg-transparent py-2 px-3" id="ctxCustomerEdit">Editar</button>';
		document.body.appendChild(menuEl);

		document.getElementById('ctxCustomerDetails').addEventListener('click', () => {
			const id = currentCustomerId;
			hide();
			openCustomerModal('customerProxyDetailsBtn', id);
		});
		document.getElementById('ctxCustomerEdit').addEventListener('click', () => {
			const id = currentCustomerId;
			hide();
			openCustomerModal('customerProxyEditBtn', id);
		});
		menuEl.addEventListener('click', (ev) => ev.stopPropagation());
		document.addEventListener('click', hide);
		document.addEventListener('scroll', hide, true);
		return menuEl;
	}

	function hide() {
		if (menuEl) {
			menuEl.style.display = 'none';
		}
		currentCustomerId = null;
	}

	function openCustomerModal(proxyBtnId, customerId) {
		const id =
			customerId != null && customerId !== ''
				? String(customerId)
				: currentCustomerId;
		if (!id) {
			return;
		}
		const proxy = document.getElementById(proxyBtnId);
		if (!proxy) {
			return;
		}
		proxy.setAttribute('data-customer-id', id);
		proxy.click();
	}

	function show(x, y) {
		const m = ensureMenu();
		m.style.display = 'block';
		m.style.left = Math.min(x, window.innerWidth - m.offsetWidth - 8) + 'px';
		m.style.top = Math.min(y, window.innerHeight - m.offsetHeight - 8) + 'px';
	}

	window.initCustomerRowContextMenu = function () {
		const tbody = document.getElementById('crudSearchTbody');
		if (!tbody) {
			return;
		}
		if (tbodyContextHandler) {
			tbody.removeEventListener('contextmenu', tbodyContextHandler);
		}
		tbodyContextHandler = onContextMenu;
		tbody.addEventListener('contextmenu', tbodyContextHandler);
	};

	function onContextMenu(ev) {
		const tbody = ev.currentTarget;
		const tr = ev.target.closest('tr.customer-data-row');
		if (!tr || !tbodyContains(tbody, tr)) {
			return;
		}
		ev.preventDefault();
		const id = tr.getAttribute('data-customer-id');
		if (!id) {
			return;
		}
		currentCustomerId = id;
		show(ev.clientX, ev.clientY);
	}

	function tbodyContains(tbody, tr) {
		return tr && tbody && tr.parentElement === tbody;
	}

	window.afterCrudTableSearch = function (kind) {
		if (kind === 'customers' && typeof window.initCustomerRowContextMenu === 'function') {
			window.initCustomerRowContextMenu();
		}
	};

	if (document.readyState === 'loading') {
		document.addEventListener('DOMContentLoaded', () => window.initCustomerRowContextMenu && window.initCustomerRowContextMenu());
	} else {
		window.initCustomerRowContextMenu();
	}
})();
