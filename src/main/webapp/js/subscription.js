function submitRequest() {

    var subscriberNumber = document.getElementsByName('subscriberNumber')[0].value;
    var queryParam = "subscriberNumber=" + subscriberNumber + "&inputMessage=" + document.getElementById('smsText').value;
    var url = 'subscription/enroll?' + queryParam;
    http(url, updatePage);
    document.getElementById('smsText').value = "";
    return false;
}

function submitEventRequest() {
    return false;
}

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

function updatePage(inputString) {
    document.getElementById('requestResponse').innerHTML = document.getElementById('requestResponse').innerHTML + "<br/>" + inputString;
}
