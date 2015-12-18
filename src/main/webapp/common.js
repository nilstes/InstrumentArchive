function getLoggedOnUser(success) {
    $.ajax({
        url: 'webresources/session',
        type: 'GET',
        dataType: 'json',
        success: function(session) {
            $.ajax({
                url: 'webresources/users/' + session.email,
                type: 'GET',
                dataType: 'json',
                success: function(user) {   
                    success(user);
                },
                error: function (xhr, status, error) {
                    handleError(xhr, status, error);
                }                   
            }); 
        },
        error: function (xhr, status, error) {
            if(xhr.status === 404) { // No session
                window.location.href = "loggedout.html";
            } else {
                handleError(xhr, status, error);
            }
        }                   
    });
};

function getQueryParam(param) {
    location.search.substr(1)
        .split("&")
        .some(function(item) { // returns first occurence and stops
            return item.split("=")[0] == param && (param = item.split("=")[1])
        })
    return param
}

function getDeleteIcon(id) {
    return getGlyphIcon("glyphicon glyphicon-trash", id);
}

function getEditIcon(id) {
    return getGlyphIcon("glyphicon glyphicon-pencil", id);
}

function getGlyphIcon(icon, id) {
    return "<div class='btn-group'><button id='" + id + "' type='button' class='btn btn-default btn-sm' aria-label='Left Align'><span class='" + icon + "' aria-hidden='true'></span></button></div>";
}

function handleError(xhr, status, error) {
    if(xhr.status === 401) {
        window.location.href = "loggedout.html";
    } else {
        window.location.href = "error.html?status=" + xhr.status;
    }
}