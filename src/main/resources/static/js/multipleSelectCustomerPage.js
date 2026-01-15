$(document).ready(function() {
	$('#newCustomerModal').on('shown.bs.modal', function() {
		$('#multiSelectNew').select2({
			placeholder: "Selecione os Premiados",
			allowClear: true,
			closeOnSelect: false,
			dropdownParent: $('#newCustomerModal')
		});
	});

	$(document).on('shown.bs.modal', '.modal[id^="editCustomerModal"]', function() {
		const modal = $(this);
		const select = modal.find('#select-employees');

		select.select2({
			placeholder: "Selecione os Premiados",
			allowClear: true,
			closeOnSelect: false,
			dropdownParent: modal
		});
	});
	$('#filterModal').on('shown.bs.modal', function() {
		const $modal = $(this);
		['#filterUser', '#filterGroup', '#filterIndustry', '#filterFlag'].forEach(selector => {
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
