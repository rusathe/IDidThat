$(function() {
    var token = null;
    var userId = null;
    var offset = 0;
    var count = 20;
    var total = -1;

    //put hide statements here
    //$("#userRow").hide();
    //$("#ratingRow").hide();

    $("#getUsers").click(function (e) {
        e.preventDefault();
        getUsers();
    })

    $("#getRatings").click(function (e) {
        e.preventDefault();
        getRatings();
    })
})

    function getUsers() {
        jQuery.ajax ({
            url: "/api/users",
            type: "GET"
        })
            .done(function (data) {
                console.log(data);
                data.forEach(function(item){
                    $( "#userRow" ).clone().prop("id",item.id).appendTo( "#userTable" );
                    $("#"+item.id).find("#userName").text(item.userName);
                    $("#"+item.id).find("#emailAddress").text(item.emailAddress);
                    $("#"+item.id).find("#placeID").text(item.placeID);
                    $("#"+item.id).find("#checkinID").text(item.checkinID);
                    $("#"+item.id).find("#ratingID").text(item.ratingID);
                    $("#"+item.id).prop("class","cloned");
                    $("#"+item.id).show();
                });
            })
            .fail(function(data){
                $("#userTable").text("Sorry no users");
            })
    }

    function getRatings() {
        jQuery.ajax ({
            url:  "/api/place",
            type: "GET"
        })
            .done(function(data){
                console.log(data);
                data.content.forEach(function(item){
                    $( "#ratingRow" ).clone().prop("id",item.id).appendTo( "#ratingTable" );
                    $("#"+item.id).find("#rating").text(item.rating);
                    $("#"+item.id).find("#avgRating").text(item.avgRating);
                    $("#"+item.id).find("#placeID").text(item.placeID);
                    $("#"+item.id).prop("class","cloned");
                    $("#"+item.id).show();
                });
            })
            .fail(function(data){
                $("#ratingRow").text("Sorry no ratings");
            })
    }
