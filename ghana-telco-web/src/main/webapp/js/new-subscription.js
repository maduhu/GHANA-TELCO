$.Enrollment = function () {
    var hitServer = function () {
        var queryParam = "subscriberNumber=" + $('#subNo').val() + "&inputMessage=" + $('#smsText').val();
        var url = 'subscription/handle?' + queryParam;
        $.ajax({
            url:url,
            dataType:'html',
            success:clearInputs
        });
        return false;
    };

    var submitEnrollment = function () {
        if ($('#subNo').val() == '') {
            alert("Subscriber Number cannot be empty.");
            return false;
        }
        var program = $('#program').val();
        var startTimeId = '#startTime' + program;
        var queryParam = "subscriberNumber=" + $('#subNo').val() + "&inputMessage=" + $('#program').val() + ' ' + $(startTimeId).val();
        var url = 'subscription/handle?' + queryParam;
        $.ajax({
            url:url,
            dataType:'html',
            success:clearInputs
        });
        return false;
    };

    var searchEnrollment = function () {
        $('#sms_table').html('<br>');
        var programNames = '';
        $('#programName :checked').each(function () {
            programNames += '/' + $(this).val();
        });

        var status = '';
        $('#status :checked').each(function () {
            status += '/' + $(this).val();
        });
        var queryParam = "subscriberNumber=" + $('#searchSubNo').val() + "&programName=" + programNames + "&status=" + status;
        var url = 'subscription/search?' + queryParam;
        $.ajax({
            url:url,
            dataType:'html',
            success:showResults
        });
        return false;
    };

    this.rollover = function (subscriberNumber) {
        var url = 'subscription/rollover/' + subscriberNumber;
        if (confirm('Do you want to Rollover ' + subscriberNumber + ' to Child Care program?')) {
            $.ajax({
                url:url,
                dataType:'html',
                success:searchEnrollment
            });
        }
        return false;
    };

    this.unRegister = function (subscriberNumber, programType) {
        var url = 'subscription/unregister/' + subscriberNumber + '/' + programType;
        if (confirm('Do you want to Unregister ' + subscriberNumber + ' from ' + programType + ' ?')) {
            $.ajax({
                url:url,
                dataType:'html',
                success:searchEnrollment
            });
        }
        return false;
    };

    var showResults = function (response) {
        $('#result_table').html(response);
    }

    var clearInputs = function (response) {
        $('#smsText').val("");
        hitAudit();
    };

    var hitAudit = function () {
        var url = 'audits/' + $('#audit_options').val();
        $.ajax({
            url:url,
            dataType:'html',
            success:updateAuditsTable
        });
        return false;
    };

    var checkStartTime = function () {
        $('#startTimeP').hide();
        $('#startTimeC').hide();
        var selected = '#startTime' + $('#program').val();
        $(selected).prop('selectedIndex', 0);
        $(selected).show();
    }

    this.getAuditForSubscriber = function (phoneNumber) {
        var url = 'filter/' + $('#audit_options').val() + '/for/' + phoneNumber;
        $.ajax({
            url:url,
            dataType:'html',
            success:updateAuditsTable
        });
        return false;
    };

    this.getAllAuditSmsForSubscriber = function (phoneNumber) {
        var url = 'filter/sms/all/for/' + phoneNumber;
        $.ajax({
            url:url,
            dataType:'html',
            success:updateSmsTable
        });
        return false;
    };


    var updateAuditsTable = function (response) {
        $('#audit_table').html(response);
    };

    var updateSmsTable = function (response) {
        $('#sms_table').html(response);
    };

    var bootstrap = function () {
        $('#submit_enrollment').click(submitEnrollment);
        $('#search_enrollment').click(searchEnrollment);
        $('#startTimeC').hide();
        hitAudit();
        $('#audit_options').change(hitAudit);
        $('#program').change(checkStartTime);
        $('#refresh_audit').click(hitAudit);
        $('input[name = "programName"]').click(function () {
            searchEnrollment();
        });
        $('input[name = "status"]').click(function () {
            searchEnrollment();
        });
    };

    $(bootstrap);
};
var enrollment;
$(document).ready(function () {
    $("#tabs").tabs();
    enrollment = new $.Enrollment();
});