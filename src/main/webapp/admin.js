function initTable(table) {
    // Get user list
    $.ajax({
        url: 'webresources/users',
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            // Draw table
            for(i=0; i<data.length; i++) {
                table.row.add([
                    getEditIcon("edit" + i) + getDeleteIcon("delete" + i),
                    data[i].email
                ]);
            }
            table.draw();

            // Register callbacks
            for(i=0; i<data.length; i++) {
                $("#delete" + i).click({email: data[i].email}, function(event) {
                    bootbox.confirm("Er du sikker på at du vil slette bruker " + event.data.email + "?", function(result) {    
                        $.ajax({
                            url: 'webresources/users/' + event.data.email,
                            type: 'DELETE',
                            success: function(data) {
                                table.clear();
                                initTable(table);
                            },
                            error: function() {
                                window.location.href = "error.html";
                            }                   
                        });
                    });                  
                });
                $("#edit" + i).click({i: i}, function(event) {
                    $('#editUserEmail').val(data[event.data.i].email);
                    $('#editUserPwd').val(data[event.data.i].password);
                    $('#editUserModal').modal('show');           
                });
            }
        },
        error: function() {
            window.location.href = "error.html";
        }
    });
}

$(document).ready(function() {
    var t = $('#users').DataTable( {
        "paging": false,
        "lengthChange": false,
        "info": false,
        "searching": false,
        "order": [[ 1, "asc" ]]
    });
            
    getLoggedOnUser(function(data) {
        $("#logout").html(data.email + ": logg ut");
        initTable(t);
    });

    $("#newUserForm").submit(function(event) {
        // Stop form from submitting normally
        event.preventDefault();
        
        // Save new user
        $.ajax({
            type: "POST",
            url: "webresources/users",
            data: JSON.stringify({email: $("#newUserEmail").val(), password: $("#newUserPwd").val()}),
            contentType: "application/json",
            success: function (data, textStatus, jqXHR) {
                t.clear();
                initTable(t);
                $('#newUserModal').modal('hide');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                window.location.href = "error.html";
            }
        });    
    });

    $("#editUserForm").submit(function(event) {
        // Stop form from submitting normally
        event.preventDefault();
        
        // Save new user
        $.ajax({
            type: "PUT",
            url: "webresources/users/" + $("#editUserEmail").val(),
            data: JSON.stringify({email: $("#editUserEmail").val(), password: $("#editUserPwd").val()}),
            contentType: "application/json",
            success: function (data, textStatus, jqXHR) {
                t.clear();
                initTable(t);
                $('#editUserModal').modal('hide');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                window.location.href = "error.html";
            }
        });    
    });
});