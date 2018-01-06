$(function() {
    var count = 30;
    var placeId = null;
    var offset = 0;
    var total = -1;

    $("#ratingRow").hide();

    $("#loadLeaderboard").click(function (e) {
        e.preventDefault();
        jQuery.ajax({
            url: "/place/",
            type: "GET"
        }).done(function(data){
            console.log(data);
            data.forEach(function(item){
                if (document.getElementById("getCity").value == item.cityName) {
                        placeId = item.id;
                        loadRatings();
                }
            });
        }).fail(function (data) {
            $("#lbTable").text("Sorry no ratings");
        })

    });

    function loadRatings() {
        jQuery.ajax({
            url: "/place/",
            type: "GET"
        }).done(function(data){
                //total = data.metadata.total;
                //$("#page").text("Page " + Math.floor(offset/count+1) + " of " + (Math.ceil(total/count)));
                $("#lbTable").find(".cloned").remove();
                data.content.forEach(function(item) {
                    $("#lbRow").clone().prop("id", item.id).appendTo("#lbTable");
                    $("#" + item.id).find("#city").text(item.cityName);
                    $("#" + item.id).find("#place").text(item.placeName);
                    $("#" + item.id).find("#category").text(item.placeCategoryType);
                    $("#"+ item.id).find("#placeId").text(item.placeId);
                    $("#" + item.id).prop("class", "cloned");
                    $("#" + item.id).show();
                });
            }).fail(function (data) {
                $("#lbRow").text("Sorry nothing found");
            })
    }

})