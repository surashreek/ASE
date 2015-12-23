if (window.console) {
  console.log("Welcome to your Play application's JavaScript!");
}

$(document).ready(function(){

  $("#login").click(function() {
    window.location.replace("/login");
  });

});