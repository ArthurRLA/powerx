$(document).ready(function () {
    $('#newUserModal').on('shown.bs.modal', function () {
        $('#multiSelectNew').select2({
            placeholder: "Selecione os Clientes",
            allowClear: true,
            closeOnSelect: false,
            dropdownParent: $('#newUserModal')
        });
    });

    $(document).on('shown.bs.modal', '.modal[id^="editUserModal"]', function () {
        const modal = $(this);
        const select = modal.find('.select2');

        select.select2({
            placeholder: "Selecione os Clientes",
            allowClear: true,
            closeOnSelect: false,
            dropdownParent: modal 
        });
    });
	
	$('#filterModal').on('shown.bs.modal', function() {
			const $modal = $(this);
			['#filterPosition', '#filterState'].forEach(selector => {
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
