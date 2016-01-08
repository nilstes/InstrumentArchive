function init(id, loansTable, statusesTable) {  
    $.when(
        $.ajax({
            url: 'webresources/instruments/' + id + '/statuses',
            type: 'GET',
            dataType: 'json'
        }),  
        $.ajax({
            url: 'webresources/instruments/' + id + '/loans',
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
        }),
        $.ajax({
            url: 'webresources/musicians',
            type: 'GET',
            dataType: 'json'               
        }),
        $.ajax({
            url: 'webresources/instruments/' + id,
            type: 'GET',
            dataType: 'json'               
        })
    ).then(function(statuses, loans, types, makes, musicians, instrument) {
        // Init loans
        loansTable.clear();
        for(i=0; i<loans[0].length; i++) {
           loansTable.row.add([
                loans[0][i].musicianName,
                loans[0][i].outAt==null?"":new Date(loans[0][i].outAt).toLocaleDateString(),
                loans[0][i].outByUser,
                loans[0][i].inAt==null?"":new Date(loans[0][i].inAt).toLocaleDateString(),
                loans[0][i].inByUser
            ]);
        }
        loansTable.draw();       
    
        // Init statuses
        statusesTable.clear();
        for(i=0; i<statuses[0].length; i++) {
           statusesTable.row.add([
                statuses[0][i].text,
                statuses[0][i].date==null?"":new Date(statuses[0][i].date).toLocaleDateString(),
                statuses[0][i].statusByUser
            ]);
        }
        statusesTable.draw();       
    
        // Init types dropdown
        $('#type').empty();
        $.each(types[0], function(key, value) {   
            var selected = value.name===instrument[0].type?"selected":null;
            $('#type')
                .append($("<option></option>")
                    .attr("value", value.id)
                    .attr("selected", selected)
                    .text(value.name)); 
        });        
    
        // Init makes dropdown
        $('#makes').empty();
        $.each(makes[0], function(key, value) {   
            $('#makes')
                .append($("<option></option>")
                .attr("value", value)); 
        });        

        // Init musicians dropdown
        $('#musician').empty();
        $.each(musicians[0], function(key, value) {   
            $('#musician')
                .append($("<option></option>")
                .attr("value", value.id)
                .text(value.firstName + " " + value.lastName)); 
        });        

        // Init instrument details
        $('#staticType').html(instrument[0].type);
        $('#staticMake').html(instrument[0].make);
        $('#staticProductNo').html(instrument[0].productNo);
        $('#staticSerialNo').html(instrument[0].serialNo);
        $('#staticDescription').html(instrument[0].description);
//        $('#type').select(instrument[0].type);
        $('#make').val(instrument[0].make);
        $('#productNo').val(instrument[0].productNo);
        $('#serialNo').val(instrument[0].serialNo);
        $('#description').val(instrument[0].description);
        $('#lentTo').html(instrument[0].lentTo);   
        $('#lastStatus').html(instrument[0].status);
        
        // Show/hide new loan/end loan-buttons
        if(instrument[0].lentTo == null || instrument[0].lentTo == "") {
            $("#endLoan").hide();
            $("#newLoan").show();
        } else {            
            $("#newLoan").hide();
            $("#endLoan").show();
        }
    }, function(xhr, status, error) {
        handleError(xhr, status, error);
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
   
    getLoggedOnUser(function(data) {
        $("#logout").html(data.email + ": logg ut");
        init(id, loansTable, statusesTable);
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
            success: function () {
                init(id, loansTable, statusesTable);
                $('#editInstrumentModal').modal('hide');
            },
            error: function(xhr, status, error) {
                handleError(xhr, status, error);
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
            success: function () {
                init(id, loansTable, statusesTable);
                $('#newStatusModal').modal('hide');
            },
            error: function(xhr, status, error) {
                handleError(xhr, status, error);
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
            success: function () {
                init(id, loansTable, statusesTable);
                $('#newLoanModal').modal('hide');
            },
            error: function(xhr, status, error) {
                handleError(xhr, status, error);
            }
        });    
    });
    
    $("#endLoan").click(function(event) {
        bootbox.confirm("Er du sikker på at du vil avslutte utlån?", function(result) {   
            if(result) {
                $.ajax({
                    url: 'webresources/instruments/' + id + '/loans',
                    type: 'DELETE',
                    success: function() {
                        init(id, loansTable, statusesTable);
                    },
                    error: function(xhr, status, error) {
                        handleError(xhr, status, error);
                    }
                });
            }
        });                  
    });
});