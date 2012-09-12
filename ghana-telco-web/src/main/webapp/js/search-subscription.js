//$('#selected').click(submitEnrollment);
//$('#rollover_enrollments').change(hitAudit);
//$('#unregister_enrollments').click(hitAudit);
$('input[name = "checkbox_C"]').click(function(){
    var checked = $("input:checkbox[name=checkbox_C]:checked").length;
    if(checked > 0) {
        $('#rollover_enrollments').attr('disabled', 'disabled');
    } else {
        $('#rollover_enrollments').removeAttr('disabled');
    }
});