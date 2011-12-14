
function http(url, fn) {
    var xmlHttpReq = false;
    var self = this;
    // Mozilla/Safari
    if (window.XMLHttpRequest) {
        self.xmlHttpReq = new XMLHttpRequest();
    }
    // IE
    else if (window.ActiveXObject) {
        self.xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
    }
    self.xmlHttpReq.open('GET', url, true);
    self.xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    self.xmlHttpReq.onreadystatechange = function() {
        if (self.xmlHttpReq.readyState == 4) {
            fn(self.xmlHttpReq.responseText);
        }
    }
    self.xmlHttpReq.send();
    return false;
}


function submitRequest() {
    var subscriberNumber = document.getElementsByName('subscriberNumber')[0].value;
    var queryParam = "subscriberNumber=" + subscriberNumber + "&inputMessage=" + document.getElementById('smsText').value;
    var url = 'subscription/enroll?' + queryParam;
    http(url, updateEnrollmentResponse);
    document.getElementById('smsText').value = "";
    return false;
}

function updateEnrollmentResponse(inputString) {
    document.getElementById('requestResponse').innerHTML = document.getElementById('requestResponse').innerHTML + "<br/>" + inputString;
}

function submitEventRequest() {
    return false;
}

function refreshAudit(){
    var selected_option = document.getElementById('audit_options').value
    http('audits/' + selected_option, updateAuditResponse);
    return false;
}

function updateAuditResponse(response){
    document.getElementById('audit_table').innerHTML = response;
}
