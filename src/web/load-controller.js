$(function() {
    $("#userRow").hide();
    $("#ratingRow").hide();

    $("#getusers").click(function (e) {
        e.preventDefault();
        jQuery.ajax ({
            url: "/api/users/",
            type: "GET"
        })
            .done(function(data){
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

    });

    $("#getRatings").click(function (e) {
        e.preventDefault();
        jQuery.ajax ({
            url: "/api/places/",
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
                $("#ratingTable").text("Sorry no ratings");
            })

    });

    function loadRatings() {
        jQuery.ajax ({
            url:  "/api/places/" + placeID + "/ratings?offset=" + offset + "&count=" + count + "&sort=rating" ,
            type: "GET"
        })
            .done(function(data){
                total = data.metadata.total;
                $("#page").text("Page " + Math.floor(offset/count+1) + " of " + (Math.ceil(total/count)));
                $("#ratingTable").find(".cloned").remove();
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
})


