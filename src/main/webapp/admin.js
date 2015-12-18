function deleteUser(email) {
    bootbox.confirm("Er du sikker på at du vil slette bruker " + email + "?", function(result) {    
        if(result) {
            $.ajax({
                url: 'webresources/users/' + email,
                type: 'DELETE',
                success: function() {
                    location.reload();
                },
                error: function (xhr, status, error) {
                    handleError(xhr, status, error);
                }
            });
        }
    });                  
}
            
function initTable(table, data) {
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
        $(document).on("click", ("#delete" + i), {email: data[i].email}, function(event) {
            deleteUser(event.data.email);
        });
        $(document).on("click", ("#edit" + i), {i: i}, function(event) {
            $('#editUserEmail').val(data[event.data.i].email);
            $('#editUserPwd').val(data[event.data.i].password);
            $('#editUserModal').modal('show');           
        });
    }
}

$(document).ready(function() {
    var table;
                        
    getLoggedOnUser(function() {
        $.ajax({
            url: 'webresources/users',
            type: 'GET',
            dataType: 'json',
            success: function(data) {        
                $("#logout").html(data.email + ": logg ut");

                table = $('#users').DataTable( {
                    "paging": false,
                    "lengthChange": false,
                    "info": false,
                    "searching": false,
                    "order": [[ 1, "asc" ]]
                });
             
                initTable(table, data);
            },
            error: function (xhr, status, error) {
                handleError(xhr, status, error);
            }
        });      
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
            success: function () {
                $('#newUserModal').modal('hide');
                location.reload();
            },
            error: function (xhr, status, error) {
                handleError(xhr, status, error);
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
            success: function () {
                $('#editUserModal').modal('hide');
                location.reload();
            },
            error: function (xhr, status, error) {
                handleError(xhr, status, error);
            }
        });    
    });
});