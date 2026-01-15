async function findIncentiveValue(productId, customerId) {
    if (!customerId) {
        alert("Selecione um cliente antes de buscar os valores de incentivos.");
        return;
    }

    const url = `/incentive-value/${productId}/${customerId}`;

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Erro: ${response.status} - ${response.statusText}`);
        }

        const data = await response.text();

        const incentiveValueArea = document.getElementById(`incentiveValueArea-${productId}`);
        
        if (incentiveValueArea) {
            incentiveValueArea.innerHTML = data;
        } else {
            console.error(`Elemento incentiveValueArea-${productId} não encontrado.`);
        }

    } catch (error) {
        alert(`Não foi possível encontrar os valores de incentivos: ${error.message}`);
    }
}

async function findIncentiveValueByCustomerIdAndApurationType(customerId, apurationType) {
    if (!apurationType) {
        alert("Selecione um cliente antes de buscar os valores de incentivos.");
        return;
    }

    const url = `/incentive-value/a/${customerId}/${apurationType}`;

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Erro: ${response.status} - ${response.statusText}`);
        }

        const data = await response.text();

        const incentiveValueArea = document.getElementById(`IncentiveValueArea-${customerId}`);
        
        if (incentiveValueArea) {
            incentiveValueArea.innerHTML = data;
        } else {
            console.error(`Elemento IncentiveValueArea-${customerId} não encontrado.`);
        }

    } catch (error) {
        alert(`Não foi possível encontrar os valores de incentivos: ${error.message}`);
    }
}
