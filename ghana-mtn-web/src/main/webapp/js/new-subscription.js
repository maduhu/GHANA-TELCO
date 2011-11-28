$.Enrollment = function() {
    var hitServer = function() {
        var queryParam = "subscriberNumber=" + $('#subNo').val() + "&inputMessage=" + $('#smsText').val();
        var url = 'subscription/handle?' + queryParam;
        $.ajax({
            url:url,
            dataType:'html',
            success:clearInputs
        });
        return false;
    };

    var clearInputs = function(response) {
        $('#smsText').val("");
        hitAudit();
    };

    var hitAudit = function() {
        var url = 'audits/' + $('#audit_options').val();
        $.ajax({
            url:url,
            dataType:'html',
            success:updateAuditsTable
        });
        return false;
    };

    var updateAuditsTable = function(response) {
        $('#audit_table').html(response);
    };


    var bootstrap = function() {
        $('#submit_enrollment').click(hitServer);
        hitAudit();
        $('#audit_options').change(hitAudit);
        $('#refresh_audit').click(hitAudit);
    };

    $(bootstrap);
};
