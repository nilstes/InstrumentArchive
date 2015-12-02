function init(id, loansTable, statusesTable) {
    $.getJSON('webresources/instruments/' + id + '/loans', function(data) {
        loansTable.clear();
        for(i=0; i<data.length; i++) {
           loansTable.row.add([
                data[i].musicianName,
                data[i].outAt==null?"":new Date(data[i].outAt).toLocaleDateString(),
                data[i].outByUser,
                data[i].inAt==null?"":new Date(data[i].inAt).toLocaleDateString(),
                data[i].inByUser
            ]);
        }
        loansTable.draw();       
    });
    
    $.getJSON('webresources/instruments/' + id + '/statuses', function(data) {
        statusesTable.clear();
        for(i=0; i<data.length; i++) {
           statusesTable.row.add([
                data[i].text,
                data[i].date==null?"":new Date(data[i].date).toLocaleDateString(),
                data[i].statusByUser
            ]);
        }
        statusesTable.draw();       
    });
    
    $('#type').empty();
    $.getJSON('webresources/instruments/types', function(data) {
        $.each(data, function(key, value) {   
            $('#type')
                .append($("<option></option>")
                .attr("value", value.id)
                .text(value.name)); 
        });        
    });
    
    $('#makes').empty();
    $.getJSON('webresources/instruments/makes', function(data) {
        $.each(data, function(key, value) {   
            $('#makes')
                .append($("<option></option>")
                .attr("value", value)); 
        });        
    });

    $('#musician').empty();
    $.getJSON('webresources/musicians', function(data) {
        $.each(data, function(key, value) {   
            $('#musician')
                .append($("<option></option>")
                .attr("value", value.id)
                .text(value.firstName + " " + value.lastName)); 
        });        
    });
    
    $.getJSON('webresources/instruments/' + id, function(data) {
        $('#staticType').html(data.type);
        $('#staticMake').html(data.make);
        $('#staticProductNo').html(data.productNo);
        $('#staticSerialNo').html(data.serialNo);
        $('#staticDescription').html(data.description);
        $('#type').select(data.type);
        $('#make').val(data.make);
        $('#productNo').val(data.productNo);
        $('#serialNo').val(data.serialNo);
        $('#description').val(data.description);
        $('#lentTo').html(data.lentTo);   
        $('#lastStatus').html(data.status);
        
        if(data.lentTo == null || data.lentTo == "") {
            $("#endLoan").hide();
            $("#newLoan").show();
        } else {            
            $("#newLoan").hide();
            $("#endLoan").show();
        }
    });
}
    
$(document).ready(function() {
    
    var id = getQueryParam("id");

    var loansTable = $('#loans').DataTable( {
        "paging": false,
        "lengthChange": false,
        "info": false,
        "searching": false,
        "order": [[ 1, "desc" ]]
    });

    var statusesTable = $('#statuses').DataTable( {
        "paging": false,
        "lengthChange": false,
        "info": false,
        "searching": false,
        "order": [[ 1, "desc" ]]
    });

    
    init(id, loansTable, statusesTable);
    
    getLoggedOnUser(function(data) {
        $("#logout").html(data.email + ": logg ut");
    });
    
    $("#editInstrumentForm").submit(function(event) {
        // Stop form from submitting normally
        event.preventDefault();
        
        // Save new user
        $.ajax({
            type: "PUT",
            url: "webresources/instruments",
            data: JSON.stringify({
                id: id, 
                type: $("#type").val(), 
                make: $("#make").val(),
                productNo: $("#productNo").val(),
                serialNo: $("#serialNo").val(),
                description: $("#description").val()
            }),
            contentType: "application/json",
            success: function (data, textStatus, jqXHR) {
                init(id, loansTable, statusesTable);
                $('#editInstrumentModal').modal('hide');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                window.location.href = "error.html";
            }
        });    
    });

    $("#newStatusForm").submit(function(event) {
        // Stop form from submitting normally
        event.preventDefault();
        
        // Save new state
        $.ajax({
            type: "POST",
            url: "webresources/instruments/" + id + "/statuses",
            data: $("#status").val(),
            contentType: "text/plain",
            success: function (data, textStatus, jqXHR) {
                init(id, loansTable, statusesTable);
                $('#newStatusModal').modal('hide');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                window.location.href = "error.html";
            }
        });    
    });
    
    $("#newLoanForm").submit(function(event) {
        // Stop form from submitting normally
        event.preventDefault();
        
        // Save new loan
        $.ajax({
            type: "POST",
            url: "webresources/instruments/" + id + "/loans",
            data: $("#musician").val(),
            contentType: "text/plain",
            success: function (data, textStatus, jqXHR) {
                init(id, loansTable, statusesTable);
                $('#newLoanModal').modal('hide');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                window.location.href = "error.html";
            }
        });    
    });
    
    $("#endLoan").click(function(event) {
        bootbox.confirm("Er du sikker på at du vil avslutte utlån?", function(result) {   
            if(result) {
                $.ajax({
                    url: 'webresources/instruments/' + id + '/loans',
                    type: 'DELETE',
                    success: function(data) {
                        init(id, loansTable, statusesTable);
                    },
                    error: function() {
                        window.location.href = "error.html";
                    }                   
                });
            }
        });                  
    });
});