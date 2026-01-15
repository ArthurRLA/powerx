$(document).ready(function () {
    $('#newGroupModal').on('shown.bs.modal', function () {
        $('#multiSelectNewProduct').select2({
            placeholder: "Selecione os Produtos",
            allowClear: true,
            closeOnSelect: false,
            dropdownParent: $('#newGroupModal')
        });

        $('#multiSelectNewCustomer').select2({
            placeholder: "Selecione os Clientes",
            allowClear: true,
            closeOnSelect: false,
            dropdownParent: $('#newGroupModal')
        });
    });

    $(document).on('shown.bs.modal', '.modal[id^="editGroupModal"]', function () {
        const modal = $(this);

        modal.find('.multiSelectEditProduct').select2({
            placeholder: "Selecione os Produtos",
            allowClear: true,
            closeOnSelect: false,
            dropdownParent: modal
        });

        modal.find('.multiSelectEditCustomer').select2({
            placeholder: "Selecione os Clientes",
            allowClear: true,
            closeOnSelect: false,
            dropdownParent: modal
        });
    });
});
