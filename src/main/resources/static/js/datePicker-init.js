$(document).ready(function() {
	
	flatpickr('[data-report-field="startDate"]', {
		dateFormat: 'Y-m-d',
		altInput: true,
		altFormat: 'd/m/Y',
		allowInput: false,
		clickOpens: true,
		wrap: false
	});
	
	flatpickr('[data-report-field="endDate"]', {
		dateFormat: 'Y-m-d',
		altInput: true,
		altFormat: 'd/m/Y',
		allowInput: false,
		clickOpens: true,
		wrap: false
	});
	
});