function init(id) {
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
    });
}
    
$(document).ready(function() {
    
    var id = getQueryParam("id");
    
    init(id);
    
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
                init(id);
                $('#editInstrumentModal').modal('hide');
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
            url: "webresources/instruments/" + id + "/loan",
            data: $("#musician").val(),
            contentType: "text/plain",
            success: function (data, textStatus, jqXHR) {
                init(id);
                $('#newLoanModal').modal('hide');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                window.location.href = "error.html";
            }
        });    
    });
    
    $("#endLoan").click(function(event) {
        bootbox.confirm("Er du sikker på at du vil avslutte utlån?", function(result) {    
            $.ajax({
                url: 'webresources/instruments/' + id + '/loan',
                type: 'DELETE',
                success: function(data) {
                    init(id);
                },
                error: function() {
                    window.location.href = "error.html";
                }                   
            });
        });                  
    });

});