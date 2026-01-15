$(document).ready(function () {
    $('#newEmployeeModal').on('shown.bs.modal', function () {
        $('#multiSelectFunctionsNew, #multiSelectCustomersNew, #multiSelectApurationTypesNew').select2({
            placeholder: "Selecione uma opção",
            allowClear: true,
            closeOnSelect: false,
            dropdownParent: $('#newEmployeeModal')
        });
    });

    $('div[id^="editEmployeeModal"]').on('shown.bs.modal', function () {
        const modalId = $(this).attr('id');
        $(`#${modalId} select[id^="multiSelectFunctionsEdit"], 
           #${modalId} select[id^="multiSelectCustomersEdit"],
           #${modalId} select[id^="multiSelectApurationTypesEdit"]`).select2({
            placeholder: "Selecione uma opção",
            allowClear: true,
            closeOnSelect: false,
            dropdownParent: $(`#${modalId}`)
        });
    });
	$('#filterModal').on('shown.bs.modal', function() {
			const $modal = $(this);
			['#filterCustomer', '#filterFunction'].forEach(selector => {
				$modal.find(selector).select2({
					placeholder: $(selector).attr('placeholder') || '',
					dropdownParent: $modal,
					width: '100%'
				}).on('select2:open', function() {
					$('.select2-dropdown').css('z-index', 1060);
				});
			});
		});
});
