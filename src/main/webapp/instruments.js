function deleteInstrument(instrument) {
    if(instrument.lentTo) {
        bootbox.alert("Instrumentet kan ikke slettes da det er utlånt!")
    } else {
        bootbox.confirm("Er du sikker på at du vil slette " + instrument.type + ", " + instrument.make + ", "
                + instrument.productNo + " med serienr " + instrument.serialNo + "?", function(result) {    
            if(result) {
                $.ajax({
                    url: 'webresources/instruments/' + instrument.id,
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
        $(document).on("click", ("#delete" + i), {instrument: data[i]}, function(event) {
            deleteInstrument(event.data.instrument);
        });
        $(document).on("click", ("#edit" + i), {id: data[i].id}, function(event) {
            window.location.href = "instrument.html?id=" + event.data.id;
        });
    }       
}

$(document).ready(function() {
    var table;

    getLoggedOnUser(function(data) {
        $("#logout").html(data.email + ": logg ut");

        $.when(
            $.ajax({
                url: 'webresources/instruments',
                type: 'GET',
                dataType: 'json'
            }),  
            $.ajax({
                url: 'webresources/instruments/types',
                type: 'GET',
                dataType: 'json'               
            }),             
            $.ajax({
                url: 'webresources/instruments/makes',
                type: 'GET',
                dataType: 'json'               
            })
        ).then(function(instruments, types, makes) {
            table = $('#instruments').DataTable( {
                "paging": true,
                "lengthChange": true,
                "info": true,
                "searching": true,
                "order": [[ 1, "asc" ]]
            });
            
            initTable(table, instruments[0]);
            
            $.each(makes[0], function(key, value) {   
                $('#makes')
                    .append($("<option></option>")
                    .attr("value", value)); 
            });       
            
            $.each(types[0], function(key, value) {   
                $('#type')
                    .append($("<option></option>")
                    .attr("value", value.id)
                    .text(value.name)); 
            });                       
        }, function(xhr, status, error) {
            handleError(xhr, status, error);
        });
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
            success: function () {
                $('#newInstrumentModal').modal('hide');
                location.reload();
            },
            error: function (xhr, status, error) {
                handleError(xhr, status, error);
            }
        });    
    });
});