function initTable(table) {
    $.ajax({
        url: 'webresources/musicians',
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            for(i=0; i<data.length; i++) {
                var instruments = data[i].instruments.join();
                table.row.add([
                    getEditIcon("edit" + i) + getDeleteIcon("delete" + i),
                    data[i].firstName,
                    data[i].lastName,
                    instruments
                ]);
            }
            table.draw();

            // Register callbacks
            for(i=0; i<data.length; i++) {
                $("#delete" + i).click({musician: data[i]}, function(event) {
                    bootbox.confirm("Er du sikker på at du vil slette musikant " + event.data.musician.firstName + " " + event.data.musician.lastName + "?", function(result) {    
                        if(result) {
                            $.ajax({
                                url: 'webresources/musicians/' + event.data.musician.id,
                                type: 'DELETE',
                                success: function(data) {
                                    table.clear();
                                    initTable(table);
                                },
                                error: function() {
                                    window.location.href = "error.html";
                                }                   
                            });
                        }
                    });                  
                });
                $("#edit" + i).click({i: i}, function(event) {
                    $('#editMusicianId').val(data[event.data.i].id);
                    $('#editMusicianFirstName').val(data[event.data.i].firstName);
                    $('#editMusicianLastName').val(data[event.data.i].lastName);
                    $('#editMusicianModal').modal('show');           
                });
            }
        },
        error: function() {
            window.location.href = "error.html";
        }
    });
}

$(document).ready(function() {
    var t = $('#musicians').DataTable( {
        "paging": true,
        "lengthChange": false,
        "info": false,
        "searching": true,
        "order": [[ 2, "asc" ]]
    });
            
    getLoggedOnUser(function(data) {
        $("#logout").html(data.email + ": logg ut");
        initTable(t);
    });
    
    $("#newMusicianForm").submit(function(event) {
        // Stop form from submitting normally
        event.preventDefault();
        
        // Save new musician
        $.ajax({
            type: "POST",
            url: "webresources/musicians",
            data: JSON.stringify({
                firstName: $("#newMusicianFirstName").val(), 
                lastName: $("#newMusicianLastName").val()
            }),
            contentType: "application/json",
            success: function (data, textStatus, jqXHR) {
                t.clear();
                initTable(t);
                $('#newMusicianModal').modal('hide');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                window.location.href = "error.html";
            }
        });    
    });
    
    $("#editMusicianForm").submit(function(event) {
        // Stop form from submitting normally
        event.preventDefault();
        
        // Save new musician
        $.ajax({
            type: "PUT",
            url: "webresources/musicians",
            data: JSON.stringify({
                id: $("#editMusicianId").val(), 
                firstName: $("#editMusicianFirstName").val(), 
                lastName: $("#editMusicianLastName").val()
            }),
            contentType: "application/json",
            success: function (data, textStatus, jqXHR) {
                t.clear();
                initTable(t);
                $('#editMusicianModal').modal('hide');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                window.location.href = "error.html";
            }
        });    
    });
});