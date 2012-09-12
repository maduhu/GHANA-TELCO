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

    var submitEnrollment = function() {
        var queryParam = "subscriberNumber=" + $('#subNo').val() + "&inputMessage=" + $('#smsText').val();
        var url = 'subscription/handle?' + queryParam;
        $.ajax({
            url:url,
            dataType:'html',
            success:clearInputs
        });
        return false;
    };

    var searchEnrollment = function() {
        var queryParam = "subscriberNumber=" + $('#subNo').val() + "&programName=" + $("#programName") + "&status=" + $("#status");
        var url = 'subscription/search?' + queryParam;
        $.ajax({
            url:url,
            dataType:'html',
            success:showResults
        });
        return false;
    };

    var showResults = function(response) {
        $('#result_table').html(response);
    }

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
        $('#submit_enrollment').click(submitEnrollment);
        $('#search_enrollment').click(searchEnrollment);
        hitAudit();
        $('#audit_options').change(hitAudit);
        $('#refresh_audit').click(hitAudit);
    };


    this.rollover = function (subscriberNumber) {
        var url = 'subscription/rollover/' + subscriberNumber;
        if(confirm('Do you want to Rollover '+subscriberNumber+' to Child Care program?')) {
            $.ajax({
                url:url,
                dataType:'html',
                success: searchEnrollment
            });
        }
        return false;
    };

    this.unRegister = function (subscriberNumber, programType) {
        var url = 'subscription/unregister/' + subscriberNumber + '/' + programType;
        if(confirm('Do you want to Unregister '+subscriberNumber+' from '+ programType +' ?')) {
            $.ajax({
                url:url,
                dataType:'html',
                success: searchEnrollment
            });
        }
        return false;
    };

    $(bootstrap);
};

$(document).ready(function() {
    $("#tabs").tabs();
    new $.Enrollment();
});