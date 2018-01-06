$(function() {
    var count = 30;
    var placeID = null;
    var offset = 0;
    var total = -1;
    var city=null;
    var category=document.getElementById("getCategory").value;
    var location=document.getElementById("getLocation").value;
    $("#lbRow").hide();


    $("#sbcategory").click(function (e) {
        e.preventDefault();
        var category=document.getElementById("getCategory").value;
        var location=document.getElementById("getLocation").value;
        jQuery.ajax ({
            url: "/place",
            type: "GET"
        })
            .done(function(data){
                console.log(data);
                $("#lbTable").find(".cloned").remove();
                data.forEach(function(item){
                    if(document.getElementById("getCategory").value == item.placeCategoryType){
                        placeID = item.id;
                        loadTable();
                    }
                })
                postSearch();
            })
            .fail(function(data){
                $("#lbTable").text("Sorry no places found");
            })

    });

    $("#sblocation").click(function (e) {
        e.preventDefault();
        var category=document.getElementById("getCategory").value;
        var location=document.getElementById("getLocation").value;
        jQuery.ajax ({
            url: "/place",
            type: "GET"
        })
            .done(function(data){
                console.log(data);
                $("#lbTable").find(".cloned").remove();
                data.forEach(function(item){
                    if(document.getElementById("getLocation").value == item.cityName){
                        placeID = item.id;
                        loadTable();
                    }
                })
                postSearch();
            })
            .fail(function(data){
                $("#lbTable").text("Sorry no places found");
            })

    });

    $("#sbboth").click(function (e) {
        e.preventDefault();
        var category=document.getElementById("getCategory").value;
        var location=document.getElementById("getLocation").value;
        jQuery.ajax ({
            url: "/place",
            type: "GET"
        })
            .done(function(data){
                console.log(data);
                $("#lbTable").find(".cloned").remove();
                data.forEach(function(item){
                    if(document.getElementById("getLocation").value == item.cityName && document.getElementById("getCategory").value == item.placeCategoryType){
                        placeID = item.id;
                        loadTable();
                    }
                })
                postSearch();
            })
            .fail(function(data){
                $("#lbTable").text("Sorry no places found");
            })

    });



    function loadRatings() {
        jQuery.ajax ({
            url:  "/place/"+ placeId + "/rating?offset=" + offset + "&count=" + count + "&sort=rating" ,
            type: "GET"
        })
            .done(function(data){
                //total = data.metadata.total;
                //$("#page").text("Page " + Math.floor(offset/count+1) + " of " + (Math.ceil(total/count)));
                $("#lbTable").find(".cloned").remove();
                data.content.forEach(function(item){
                    $( "#ratingRow" ).clone().prop("id",item.id).appendTo( "#lbTable" );
                    //$("#"+item.id).find("#rating").text(item.rating);
                    //$("#"+item.id).find("#avgRating").text(item.avgRating);
                    $("#"+item.id).find("#placeId").text(item.placeID);
                    $("#"+item.id).prop("class","cloned");
                    $("#"+item.id).show();
                });
                alert("fetched data");
            })
            .fail(function(data){
                $("#lbRow").text("Sorry no ratings");
            })

    }
    function loadTable(){
            jQuery.ajax ({
                url:  "/place/"+ placeID + "?offset=" + offset + "&count=" + count + "&sort=rating" ,
                type: "GET"
            }).done(function(data){
                //total = data.metadata.total;
                //$("#page").text("Page " + Math.floor(offset/count+1) + " of " + (Math.ceil(total/count)));
                data.content.forEach(function(item){
                    $( "#lbRow" ).clone().prop("id",item.id).appendTo("#lbTable");
                    $("#"+item.id).find("#placeName").text(item.placeName);
                    $("#"+item.id).find("#categoryName").text(item.placeCategoryType);
                    $("#"+item.id).find("#cityName").text(item.cityName);
                    $("#"+item.id).find("#placeId").text(item.placeID);
                    $("#"+item.id).prop("class","cloned");
                    $("#"+item.id).show();
                });
            })
            .fail(function(data){
                $("#lbRow").text("Sorry no ratings");
            })

    }
    function postSearch(){
        jQuery.ajax({
            url: "/searchdata",
            type: "POST",
            data: JSON.stringify({
                placeLocation :location,
                placeType : category
            }),
            dataType: "json",
            contentType: "application/json; charset=utf-8"
        }).done(function (data) {

        })
            .fail(function (xhr) {
                alert('Request Status: ' + xhr.status + ' Status Text: ' + xhr.statusText + ' ' + xhr.responseText);
            });
    }
})

