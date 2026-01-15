function searchByName() {
    let input = document.getElementById('searchByName').value.toLowerCase();
    let table = document.getElementById('tableBody');
    let rows = table.getElementsByTagName('tr');

    for (let i = 0; i < rows.length; i++) {
        let firstCell = rows[i].getElementsByTagName('td')[0]; 
        let secondCell = rows[i].getElementsByTagName('td')[1]; 

        if (firstCell || secondCell) {
            let firstText = firstCell ? (firstCell.textContent || firstCell.innerText).toLowerCase() : '';
            let secondText = secondCell ? (secondCell.textContent || secondCell.innerText).toLowerCase() : '';

            if (firstText.indexOf(input) > -1 || secondText.indexOf(input) > -1) {
                rows[i].style.display = ''; 
            } else {
                rows[i].style.display = 'none'; 
            }
        }
    }
}
