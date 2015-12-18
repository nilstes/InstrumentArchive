$(document).ready(function() {
    // Log out
    $.ajax({
        url: 'webresources/session',
        type: 'DELETE'
    });

    // Log in
    $("#loginForm").submit(function(event) {
        event.preventDefault();
        $.ajax({
            url: 'webresources/session',
            type: 'POST',
            data: JSON.stringify({
                email: $("#inputEmail").val(),
                password: $("#inputPassword").val()
            }),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            complete: function(xhr) {
                switch (xhr.status) {
                    case 200:
                        document.cookie = "testcookie=sensitive data";
                        window.location.href="instruments.html";
                        break;
                    case 401:
                        $("#wrongPasswordAlert").removeClass("hide");
                        break;
                    default:
                        window.location.href = "loggedout.html";
                        break;
                }
            }
        });
    });
});