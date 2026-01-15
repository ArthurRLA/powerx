async function setStock(customerId) {
  const rows = document.querySelectorAll('#setCurrentAccountBalanceModal tbody tr');

  const productStockItems = Array.from(rows).map(row => {
    const productId = row.getAttribute('data-product-id');
    const quantityValue = document.getElementById(`quantity${productId}`).value;
    const quantity = parseInt(quantityValue, 10) || 0;
    return { product: productId, quantity };
  });

  const productStock = {
    customer: customerId,
    productStockItems
  };

  const url = `/customers/${customerId}/save-stock`;
  const csrfToken  = document.querySelector('meta[name="_csrf"]').getAttribute('content');
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        [csrfHeader]: csrfToken
      },
      body: JSON.stringify(productStock)
    });

    if (!response.ok) {
      throw new Error(`Não foi possível salvar estoque: ${response.statusText}`);
    }

    alert('Estoque redefinido com sucesso!');

    const modalEl = document.getElementById('setCurrentAccountBalanceModal');
    const modal   = bootstrap.Modal.getInstance(modalEl);
    modal.hide();

    window.location.href = '/customers';
  }
  catch (error) {
    console.error(error);
    alert('Não foi possível salvar estoque');
  }
}
