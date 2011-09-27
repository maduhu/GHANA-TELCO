function submitRequest() {
    updatePage(document.getElementById('inputText').value);
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
    self.xmlHttpReq.open('GET', 'subscription/enroll?' + getQueryString(), true);
    self.xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    self.xmlHttpReq.onreadystatechange = function() {
        if (self.xmlHttpReq.readyState == 4) {
            updatePage(self.xmlHttpReq.responseText);
        }
    }
    self.xmlHttpReq.send();
    document.getElementById('inputText').value = "";
    return false;
}

function getQueryString() {
    return "subscriberNumber=1234567890&inputMessage=" + document.getElementById('inputText').value;
}

function updatePage(inputString) {
    document.getElementById('requestResponse').innerHTML = document.getElementById('requestResponse').innerHTML + "<br/>" + inputString;
}
