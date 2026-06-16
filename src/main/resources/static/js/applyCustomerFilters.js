function applyCustomerFilters() {
  const users      = $('#filterUser').val()      || [];
  const groups     = $('#filterGroup').val()     || [];
  const industries = $('#filterIndustry').val()  || [];
  const flags      = $('#filterFlag').val()      || [];
  const active      = $('#filterActive').val()      || [];

  const params = new URLSearchParams();
  users.forEach(u => params.append('users', u));
  groups.forEach(g => params.append('groups', g));
  industries.forEach(i => params.append('industries', i));
  flags.forEach(f => params.append('flags', f));
  params.append('active', active);
  params.append('page', 0);
  params.append('size', 50);

  fetch(`/customers/filter?${params.toString()}`, {
    method: 'GET',
    headers: { 'X-Requested-With': 'XMLHttpRequest' }
  })
  .then(resp => resp.text())
  .then(html => {
    document.getElementById('customer-table').innerHTML = html;
    bootstrap.Modal.getInstance(document.getElementById('filterModal')).hide();
    if (typeof window.initCustomerRowContextMenu === 'function') {
      window.initCustomerRowContextMenu();
    }
  })
  .catch(err => console.error('Erro ao aplicar filtros:', err));
}
