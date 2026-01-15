document.addEventListener('DOMContentLoaded', function() {
    initializeSelects();
});

async function initializeSelects() {
    const metas = document.querySelectorAll('meta[name^="url-"]');

    metas.forEach(async (meta) => {
        const nameAttr = meta.getAttribute("name");
        const key = nameAttr.replace("url-", "");
        const url = meta.getAttribute("content");

        const select = document.querySelector(`[data-report-field="${key}"]`);
        if (!select) {
            alert(`Elemento <select> com id="${key}" não encontrado.`);
            return;
        }

        try {
            const request = await fetch(url);

            await requestIsOk(request);

            const response = await request.json();

            fillSelect(key, response, 'id', 'name');

        } catch (error) {
            console.log(error);
        }
    });

    $('.select2').select2({
        placeholder: "Selecione as opções",
        allowClear: true,
        closeOnSelect: false,
    })
}

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
    const sel = document.querySelector(`[data-report-field="${selId}"]`);
    sel.innerHTML = '';

    const defaultOption = document.createElement('option');
    defaultOption.value = '';
    defaultOption.text = 'Selecione uma opção';
    sel.appendChild(defaultOption);

    list.forEach(item => {
        const opt = document.createElement('option');
        opt.value = item[valueKey];
        opt.text = item[textKey];
        sel.appendChild(opt);
    });
};

const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
const generateButton = document.getElementById('generateButton');

function collectReportData() {
    const fields = document.querySelectorAll('[data-report-field]');
    const data = {};

    fields.forEach(field => {
        const name = field.getAttribute('data-report-field');
        let value;

        if (field.type === 'checkbox') {
            value = field.checked;
        } else if (field.tagName === 'SELECT') {
            if (field.multiple) {
                value = Array.from(field.selectedOptions).map(opt => opt.value);
            } else {
                value = field.value;
            }
        } else {
            value = field.value;
        }

        data[name] = value;
    });

    return data;
}

function validateReportFields() {
    const requiredFields = document.querySelectorAll('[data-report-field][data-required="true"]');

    for (let field of requiredFields) {
        const value = field.value;

        if (!value || (field.multiple && field.selectedOptions.length === 0)) {
            alert(`O campo "${field.getAttribute('data-report-field')}" é obrigatório.`);
            field.focus();
            return false;
        }
    }
    return true;
}

generateButton.addEventListener('click', async () => {
    if (!validateReportFields()) return;

    const reportInstructions = collectReportData();
    console.log(reportInstructions);

    try {
        generateButton.textContent = 'Gerando Relatório...';
        generateButton.disabled = true;

        const url = document.querySelector('meta[name="endpoint"]').getAttribute('content');

        const request = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(reportInstructions)
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

        if (blob.size > 0) {
            const objectUrl = URL.createObjectURL(blob);

            if (filename.toLowerCase().endsWith('.pdf')) {
                window.open(objectUrl, '_blank');
            } else if (filename.toLowerCase().endsWith('.xlsx')) {
                const link = document.createElement('a');
                link.href = objectUrl;
                link.download = filename;
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
            } else {
                throw new Error('Formato de arquivo não suportado: ' + filename);
            }

            URL.revokeObjectURL(objectUrl);
        } else {
            throw new Error('Arquivo inválido ou vazio.');
        }

    } catch (error) {
        console.error(error);
        openErrorModal(error.message);
    } finally {
        generateButton.textContent = 'Gerar Relatório';
        generateButton.disabled = false;
    }
});