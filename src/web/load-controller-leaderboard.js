$(function() {
    var token = null;
    var userID = null;

    $("#loadLeaderboard").click(function (e) {
        e.preventDefault();
        jQuery.ajax({
            url: "/api/places/",
            type: "GET"
        })
            .done(function (data) {
                console.log(data);
                data.content.forEach(function (item) {
                    $("#ratingRow").clone().prop("id", item.id).appendTo("#leaderBoardTable");
                    $("#" + item.id).find("#placeName").text(item.placeName);
                    $("#" + item.id).find("#avgRating").text(item.avgRating);
                    $("#" + item.id).find("#cityName").text(item.cityName);
                    $("#" + item.id).find("#categoryName").text(item.categoryName);
                    $("#" + item.id).prop("class", "cloned");
                    $("#" + item.id).show();
                });

            })
            .fail(function (data) {
                $("#leaderBoardTable").text("Sorry! No data fetched.");
            })

    });
})