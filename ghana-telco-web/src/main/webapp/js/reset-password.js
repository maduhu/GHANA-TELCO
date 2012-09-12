function validateForm() {
    var newPassword=document.forms["passwordReset"]["newPassword"].value;
    var retypedPassword=document.forms["passwordReset"]["retypedPassword"].value;
    if (newPassword != retypedPassword)
    {
        alert("New password and Retyped password do not match");
        return false;
    }
}