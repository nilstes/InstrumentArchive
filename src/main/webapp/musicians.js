function createInstrumentLinkList(instruments) {
    var html = "";
    if(instruments) {
        for(j=0; j<instruments.length; j++) {
            html += createInstrumentLink(instruments[j]);
            if(j<instruments.length-1) {
                html += ", ";
            }
        }
    }
    return html;
}

function createInstrumentLink(instrument) {
    var display = instrument.make + " " + instrument.type;
    var link = "instrument.html?id=" + instrument.id;
    return "<a href='" + link + "'>" + display + "</a>";
}

function deleteMusician(musician) {
    if(musician.instruments && musician.instruments.length > 0) {
        bootbox.alert("Musikanten har instrument(er) til utlån!")
    } else {
        bootbox.confirm("Er du sikker på at du vil slette musikant " + musician.firstName + " " + musician.lastName + "?", function(result) {    
            if(result) {                     
                $.ajax({
                    url: 'webresources/musicians/' + musician.id,
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
}

function initTable(table, data) {
    for(i=0; i<data.length; i++) {
        table.row.add([
            getEditIcon("edit" + i) + getDeleteIcon("delete" + i),
            data[i].firstName,
            data[i].lastName,
            createInstrumentLinkList(data[i].instruments)
        ]);
    }
    table.draw();

    // Register callbacks
    for(i=0; i<data.length; i++) {
        $(document).on("click", ("#delete" + i), {musician: data[i]}, function(event) {
            deleteMusician(event.data.musician);
        });
        $(document).on("click", ("#edit" + i), {i: i}, function(event) {
            $('#editMusicianId').val(data[event.data.i].id);
            $('#editMusicianFirstName').val(data[event.data.i].firstName);
            $('#editMusicianLastName').val(data[event.data.i].lastName);
            $('#editMusicianModal').modal('show');           
        });
    }
}

$(document).ready(function() {
    var table;
            
    getLoggedOnUser(function(data) {
        $.ajax({
            url: 'webresources/musicians',
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                $("#logout").html(data.email + ": logg ut");
                
                table = $('#musicians').DataTable( {
                    "paging": true,
                    "lengthChange": false,
                    "info": false,
                    "searching": true,
                    "order": [[ 2, "asc" ]]
                });

                initTable(table, data);
            },
            error: function (xhr, status, error) {
                handleError(xhr, status, error);
            }
        });
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
            success: function () {
                $('#newMusicianModal').modal('hide');
                location.reload();
            },
            error: function (xhr, status, error) {
                handleError(xhr, status, error);
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
            success: function () {
                $('#editMusicianModal').modal('hide');
                location.reload();
            },
            error: function (xhr, status, error) {
                handleError(xhr, status, error);
            }
        });    
    });
});