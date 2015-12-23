var id_count = 0;

$(document).ready(function() {

    $body = $("body");

    $(document).on({
        ajaxStart: function() { $body.addClass("loading");    }//,
        //ajaxStop: function() { $body.removeClass("loading"); }
    });

    $('#remove').click(function() {
    });

    $('#addmore').click(function () {
        id_count++;
        //alert(id_count);
        var div = $(this).prev().clone()
        div.find(".courselist").attr("id",function(i,id){
            return id.replace(/\d+/,id_count)
        });
        div.find(".courselist").attr("name",function(i,name){
            console.log(name.replace(/\d+/,id_count));
            return name.replace(/\d+/,id_count)
        });

        $(this).before(div);

        if (id_count > 0) {
            $('#remove').on('click');
        }

        if (id_count > 3) {
            $("#addmore").off('click');
        }
    });

    $('#addtutor').click(function(){

        var formvalues = $('#tutorform').serialize() + "&id_count=" + (id_count + 1);
        console.log(formvalues);

        $.ajax({

            data: formvalues, //data should look like this "name=John&location=Boston"
            type: "POST", // GET or POST
            url: "/addTutor", // the url to call
            success: function (response) {
                if (response.status == "success") { //on success
                    $('#tutorConfirm').foundation('reveal', 'open');
                    $('#closeTutorConfirm').click(function() {
                        window.location.replace("/beatutor"); //refresh page
                    })
                }
                if (response.status == "fail") { // on fail
                    if (response.message == "duplicate-course") {
                        $('#tutorFailure1').foundation('reveal', 'open');
                        $('#closeTutorFailure1').click(function() {
                            $('#addTutor').prop('disabled',true);
                            window.location.replace("/beatutor"); //refresh page
                        })
                    }

                    if (response.message == "null-course") {
                        $('#tutorFailure2').foundation('reveal', 'open');
                        $('#closeTutorFailure2').click(function() {
                            window.location.replace("/beatutor"); //refresh page
                        })
                    }
                }
            }
        });
    });

});