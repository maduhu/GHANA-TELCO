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

$.MTNUsers = function() {
    var hitServer = function() {
        $.ajax({
            url:'mock-mtn/users/all',
            dataType:'html',
            success:updateUsersTable
        });
        return false;
    };

    var addUser = function() {
        $.ajax({
            url:'mock-mtn/users/add',
            dataType:'html',
            success:hitServer,
            data:{mtnUserNumber:$('#mtn_user_no').val(),mtnUserBalance:$('#mtn_user_balance').val()},
            type: 'POST'
        });
        return false;
    };

    var updateUsersTable = function(response) {
        $('#mtn_table_box').html(response);
    };

    var bootstrap = function() {
        $('#tab2').click(hitServer);
        $('#update_mtn_user').click(addUser);

    };
    $(bootstrap);
};

$(document).ready(function() {
    $("#tabs").tabs();
    new $.Enrollment();
    new $.MTNUsers();
});
