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
    };
    var bootstrap = function() {
        $('#submit_enrollment').click(hitServer);
    };
    $(bootstrap);
};

$.Audits = function() {
    var hitServer = function() {
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
        $('#submit_audit').click(hitServer);
    };
    $(bootstrap);
};

$.MTNUsers = function() {
    var hitServer = function() {
        var url = 'mock-mtn/users/';
        $.ajax({
            url:url,
            dataType:'html',
            success:updateUsersTable
        });
        return false;
    };
    var updateUsersTable = function(response) {
        $('#mtn_table').html(response);
    };
    var bootstrap = function() {
        $('#submit_mtn').click(hitServer);
    };
    $(bootstrap);
};

$(document).ready(function() {
     $("#tabs").tabs();
    new $.Enrollment();
    new $.Audits();
    new $.MTNUsers();
});
