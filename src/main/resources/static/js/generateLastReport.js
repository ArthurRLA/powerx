document.getElementById('generateLastReport').addEventListener('click', () => {
	const url = '/reports/last';

	fetch(url, {
		method: 'GET',
		headers: {
			'Accept': 'application/pdf'
		}
	})
		.then(response => {
			if (!response.ok) {
				throw new Error(`Erro ao gerar relatório: ${response.statusText}`);
			}

			return response.blob();
		})
		.then(blob => {
			const url = window.URL.createObjectURL(blob);

			const a = document.createElement('a');
			a.href = url;
			a.download = `relatorio.pdf`;
			document.body.appendChild(a);
			a.click();

			a.remove();
			window.URL.revokeObjectURL(url);
		})
		.catch(error => {
			alert('Erro ao baixar o relatório. Tente novamente!, Erro: ' + error);
		})
})