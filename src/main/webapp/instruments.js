function initTable(table) {
    $.ajax({
        url: 'webresources/instruments',
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            table.clear();
            for(i=0; i<data.length; i++) {
                table.row.add([
                    getEditIcon("edit" + i) + getDeleteIcon("delete" + i),
                    data[i].type,
                    data[i].make + (data[i].productNo?", " + data[i].productNo:""),
                    data[i].serialNo,
                    data[i].lentTo,
                    data[i].status
                ]);
            }
            table.draw();
            
            // Register callbacks
            for(i=0; i<data.length; i++) {
                $("#delete" + i).click({instrument: data[i]}, function(event) {
                    if(event.data.instrument.lentTo) {
                        bootbox.alert("Instrumentet kan ikke slettes da det er utlånt!")
                    } else {
                        bootbox.confirm("Er du sikker på at du vil slette instrument " + event.data.instrument.make + " " + event.data.instrument.type + "?", function(result) {    
                            if(result) {
                                $.ajax({
                                    url: 'webresources/instruments/' + event.data.instrument.id,
                                    type: 'DELETE',
                                    success: function(data) {
                                        initTable(table);
                                    },
                                    error: function() {
                                        window.location.href = "error.html";
                                    }                   
                                });
                            }
                        });                  
                    }
                });
                $("#edit" + i).click({id: data[i].id}, function(event) {
                    window.location.href = "instrument.html?id=" + event.data.id;
                });
            }
        },
        error: function() {
            window.location.href = "error.html";
        }
    });
}

$(document).ready(function() {
    var table = $('#instruments').DataTable( {
        "paging": true,
        "lengthChange": false,
        "info": false,
        "searching": true,
        "order": [[ 1, "asc" ]]
    });
    
    $.getJSON('webresources/instruments/types', function(data) {
        $.each(data, function(key, value) {   
            $('#type')
                .append($("<option></option>")
                .attr("value", value.id)
                .text(value.name)); 
        });        
    });
    
    $.getJSON('webresources/instruments/makes', function(data) {
        $.each(data, function(key, value) {   
            $('#makes')
                .append($("<option></option>")
                .attr("value", value)); 
        });        
    });
    
    getLoggedOnUser(function(data) {
        $("#logout").html(data.email + ": logg ut");
        initTable(table);
    });
    
    $("#newInstrumentForm").submit(function(event) {
        // Stop form from submitting normally
        event.preventDefault();
        
        // Save new user
        $.ajax({
            type: "POST",
            url: "webresources/instruments",
            data: JSON.stringify({
                type: $("#type").val(), 
                make: $("#make").val(),
                productNo: $("#productNo").val(),
                serialNo: $("#serialNo").val(),
                description: $("#description").val(),
                status: $("#status").val()
            }),
            contentType: "application/json",
            success: function (data, textStatus, jqXHR) {
                initTable(table);
                $('#newInstrumentModal').modal('hide');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                window.location.href = "error.html";
            }
        });    
    });
});