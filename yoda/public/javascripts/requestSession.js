
$(document).ready(function(){

  var courseId = $("#course option:selected").val();
  //var course = courseFull.split(" - ");
  $(".course-panel").hide();
  $("." + courseId).show();
  
  $("#course").change(function() {
	  var courseId = $("#course option:selected").val();
	  //var course = courseFull.split(" - ");
	  $(".course-panel").hide();
	  $("." + courseId).show();
  })

  $body = $("body");

   $(document).on({
      ajaxStart: function() { $body.addClass("loading");    }//,
      //ajaxStop: function() { $body.removeClass("loading"); }
   });

});
