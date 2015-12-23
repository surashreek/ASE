function showHideSections(tabNo, container) {
    	
    	$(".tab-title").removeClass('active');
    	$("#panel"+tabNo).addClass('active');
    	
    	$(".main-content").hide();
        $("#" + container).show();
}

function acceptClicked(sessionId) {
    var reply = document.getElementById("reply").value;
	var formData = "sessionId=" + sessionId + "&status=upcoming" + "&reply=" + encodeURIComponent(reply);

	$.ajax({
        data: formData, //data should look like this "name=John&location=Boston"
        type: "POST", // GET or POST
        url: "/confirmRejectSession", // the url to call
        success: function (response) {
            if (response.status == "success") { //on success
                //alert("Request accepted; page refreshing");
                $('#requestAccept').foundation('reveal', 'open');
                $('#closeRequestAccept').click(function() {
                    window.location.reload(); //refresh page
                })
            }
            if (response.status == "fail") { // on fail
            	//alert("Request failed...something is wrong.");
                $('#requestFail').foundation('reveal', 'open');
                $('#closeRequestFail').click(function() {
                    window.location.reload(); //refresh page
                })
            }
        }
    });

        //window.location.reload();
}

function rejectClicked(sessionId) {
    var reply = document.getElementById("reply").value;
	var formData = "sessionId=" + sessionId + "&status=rejected" + "&reply=" + encodeURIComponent(reply);
	$.ajax({

        data: formData, //data should look like this "name=John&location=Boston"
        type: "POST", // GET or POST
        url: "/confirmRejectSession", // the url to call
        success: function (response) {
            if (response.status == "success") { //on success
                //alert("Request rejected; refreshing page");
                $('#requestReject').foundation('reveal', 'open');
                $('#closeRequestReject').click(function() {
                    window.location.reload(); //refresh page
                })
            }
            if (response.status == "fail") { // on fail
            	//alert("Request failed...something is wrong.");
            	$('#requestFail').foundation('reveal', 'open');
                $('#closeRequestFail').click(function() {
                    window.location.reload(); //refresh page
                })
            }
        }
    });

        //window.location.reload();
}

function submitRating(sessionId) {
    var rating = document.getElementById("rating"+sessionId).value;
    var comment = document.getElementById("comment"+sessionId).value;
    var formData = "sessionId=" + sessionId + "&rating=" + rating + "&comment=" + comment;

    $.ajax({
        data: formData,
        type: "POST",
        url: "/addRating",
        success: function(response) {
            if (response.status == 'success') {
                //alert("Successfully saved rating, thank you!");
                $('#ratingSuccess').foundation('reveal', 'open');
                $('#closeRatingSuccess').click(function() {
                    window.location.reload(); //refresh page
                })
            }
            else if (response.status == 'fail') {
                //alert("Request failed... something went wrong.")
                $('#requestFail').foundation('reveal', 'open');
                $('#closeRequestFail').click(function() {
                    window.location.reload(); //refresh page
                })
            }
            else if (response.status == 'ban') {
                $('#bandialog').foundation('reveal', 'open');
                window.location.replace("/login");
            }
        }
    });

    //window.location.reload();
}

$(document).ready(function() {

    $(".main-content").hide();
    $("#upcoming-sessions").show();
    
    $('#acceptButton').click(function(){

        var formvalues = $('#tutorform').serialize();
        
    });

    $body = $("body");

    $(document).on({
        ajaxStart: function() { $body.addClass("loading");    }//,
         //ajaxStop: function() { $body.removeClass("loading"); }
    });
    
});
