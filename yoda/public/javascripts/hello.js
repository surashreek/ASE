if (window.console) {
  console.log("Welcome to your Play application's JavaScript!");
}

var dateToday = new Date();
$(document).ready(function(){

  $( "#datepicker" ).datepicker({
    minDate:dateToday
  });

  $("#login").click(function() {
    window.location.replace("/login");
  });

  $('#addrequest').click(function () {

    var formValues = $("#requestform").serialize();
      $.ajax({

        data: formValues, //data should look like this "name=John&location=Boston"
        type: "POST", // GET or POST
        url: "/addNewSession", // the url to call
        success: function (response) {
          if (response.status == "success") { //on success
            //alert(response.message);
            $('#modalTitle').text("Success! A new session has been requested");
            $('#modalMessage').text(response.message);
            $('#dialog').foundation('reveal', 'open');
            //$('#closedialog').click(function() {
              window.location.replace("/mysessions"); //refresh page
            //})
          }
          if (response.status == "fail") { // on fail
            //alert(response.message);
            $('#modalTitle').text("Fail to add new session");
            $('#modalMessage').text(response.message);
            $('#dialog').foundation('reveal', 'open');
            //$('#closedialog').click(function() {
              $("body").removeClass("loading");
            //})
          }
          if (response.status == "ban"){
            $('#modalTitle').text("You are forced to logout");
            $('#modalMessage').text("This account has been locked out due to suspicious behavior. If you feel this was done in error, please contact the administrator.");
            $('#dialog').foundation('reveal', 'open');
            window.location.replace("/");
          }
        }
      });
      //return false; // cancel original event to prevent form submitting

  });

});


function setTutorName(name, id){
  $('#TutorName').val(name);
  $('#TutorLabel').text(name);
  $('#TutorId').val(id);
  var coursename = $('#course').find(":selected").text();
  var courseid = $('#course').find(":selected").val();
  $('#coursename').val(coursename);
  $('#courseid').val(courseid);
}