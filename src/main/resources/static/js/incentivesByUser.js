const incentivesModalEl = document.getElementById('showIncentivesModal');
const incentivesBackBtn = document.getElementById('incentivesBackButton');
const documentModalEl   = document.getElementById('documentModal');
const documentBackBtn   = document.getElementById('documentBackButton');

incentivesModalEl.addEventListener('show.bs.modal', async event => {
  let userId;
  if (event.relatedTarget) {
    userId = event.relatedTarget.getAttribute('data-user-id');
    incentivesModalEl.dataset.userId = userId;
  } else {
    userId = incentivesModalEl.dataset.userId;
  }

  try {
    const res = await fetch(`/users/incentives/${userId}`);
    if (!res.ok) throw new Error(res.statusText);
    document.getElementById('incentivesByUserArea')
            .innerHTML = await res.text();
  } catch (e) {
    console.error("Erro ao carregar incentivos:", e);
    document.getElementById('incentivesByUserArea')
            .innerHTML = '<p class="text-danger">Erro ao carregar incentivos.</p>';
  }

  incentivesBackBtn.onclick = () => {
    bootstrap.Modal.getInstance(incentivesModalEl).hide();
    incentivesModalEl.addEventListener('hidden.bs.modal', function handler() {
      new bootstrap.Modal(
        document.getElementById(`detailsUserModal${userId}`)
      ).show();
      incentivesModalEl.removeEventListener('hidden.bs.modal', handler);
    });
  };
});

incentivesModalEl.addEventListener('hidden.bs.modal', () => {
  document.querySelectorAll('.modal-backdrop').forEach(el => el.remove());
});

documentModalEl.addEventListener('show.bs.modal', async event => {
  let documentNumber;
  if (event.relatedTarget) {
    documentNumber = event.relatedTarget.getAttribute('data-document-number');
    documentModalEl.dataset.documentNumber = documentNumber;
  } else {
    documentNumber = documentModalEl.dataset.documentNumber;
  }

  try {
    const res = await fetch(`/incentives/${documentNumber}`);
    if (!res.ok) throw new Error(res.statusText);
    const data = await res.json();
    populateModal(
      data.sales, data.incentives,
      data.fantasyName, data.cnpj,
      data.method, data.date,
      data.state
    );
    document.getElementById('downloadReport')
            .onclick = () => downloadReport(documentNumber);
  } catch (e) {
    console.error("Erro ao buscar detalhes do documento:", e);
  }

  documentBackBtn.onclick = () => {
    bootstrap.Modal.getInstance(documentModalEl).hide();
    documentModalEl.addEventListener('hidden.bs.modal', function handler() {
      new bootstrap.Modal(incentivesModalEl).show();
      documentModalEl.removeEventListener('hidden.bs.modal', handler);
    });
  };
});

documentModalEl.addEventListener('hidden.bs.modal', () => {
  document.querySelectorAll('.modal-backdrop').forEach(el => el.remove());
});

function populateModal(sales, incentives, fantasyName, cnpj, method, date, state) {
  const headerInfos    = document.getElementById('header-infos');
  const incentivesBody = document.getElementById('incentivesTableBody');
  const salesContainer = document.getElementById('salesContainer');

  headerInfos.innerHTML    = '';
  incentivesBody.innerHTML = '';
  salesContainer.innerHTML = '';

  headerInfos.innerHTML = `
    <div class="row">
      <div class="col-md-3">
        <div><strong>Nome Fantasia:</strong> ${fantasyName||''}</div>
        <div><strong>CNPJ:</strong> ${cnpj||''}</div>
        <div><strong>Região:</strong> ${state||''}</div>
      </div>
      <div class="col-md-3">
        <div><strong>Método de Pagamento:</strong> ${method||''}</div>
        <div><strong>Data:</strong> ${date?formatDate(date):''}</div>
      </div>
    </div>
  `;

  const grouped = sales.reduce((acc, s) => {
    (acc[s.employeeName] = acc[s.employeeName]||[]).push(s);
    return acc;
  }, {});
  Object.entries(grouped).forEach(([name, empSales]) => {
    const title = document.createElement('h5');
    title.className   = 'mt-4';
    title.textContent = `Funcionário: ${name}`;
    salesContainer.appendChild(title);

    const tbl = document.createElement('table');
    tbl.className = 'table table-bordered mt-2 table-striped';
    tbl.style.width = '60%';
    tbl.innerHTML = `
      <thead>
        <tr>
          <th>Código do Produto</th>
          <th>Descrição do Produto</th>
          <th>Quantidade</th>
        </tr>
      </thead>
      <tbody>
        ${empSales.map(s => `
          <tr>
            <td class="small">${s.productCode||''}</td>
            <td class="small">${s.productName||''}</td>
            <td class="small">${s.quantity||''}</td>
          </tr>
        `).join('')}
      </tbody>
    `;
    salesContainer.appendChild(tbl);
  });

  const cc  = incentives.filter(i => i.apurationType?.toLowerCase().trim()==="conta corrente");
  const nfs = incentives.filter(i => i.apurationType?.toLowerCase().trim()==="nf serviço");
  cc.concat(nfs).forEach(i => {
    incentivesBody.innerHTML += `
      <tr>
        <td class="small">${i.apurationType||''}</td>
        <td class="small">${i.cpf||''}</td>
        <td class="small">${i.employeeName||''}</td>
        <td class="small">${Number(i.incentiveValue||0).toFixed(2)}</td>
        <td class="small">${i.functionName||''}</td>
      </tr>
    `;
  });
}

function formatDate(dateString) {
  if (!dateString) return 'N/A';
  const d = new Date(dateString);
  if (isNaN(d)) return 'Data Inválida';
  return `${String(d.getMonth()+1).padStart(2,'0')}-${d.getFullYear()}`;
}

function downloadReport(documentNumber) {
  fetch(`/reports/sales/${encodeURIComponent(documentNumber)}`, {
    headers: { 'Accept': 'application/pdf' }
  })
  .then(r => {
    if (!r.ok) throw new Error(r.statusText);
    return r.blob();
  })
  .then(blob => {
    const url = URL.createObjectURL(blob);
    const a   = document.createElement('a');
    a.href    = url;
    a.download = `relatorio_${documentNumber}.pdf`;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
  })
  .catch(e => {
    console.error('Erro ao baixar o relatório:', e);
    alert('Erro ao baixar o relatório. Tente novamente!');
  });
}
