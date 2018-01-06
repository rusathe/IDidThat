var MongoClient = require('mongodb').MongoClient;

var dbConnection = null;

var lockCount = 0;

function getDbConnection(callback){
    MongoClient.connect("mongodb://localhost/ididthatDB", function(err, db){
        if(err){
            console.log("Unable to connect to Mongodb");
        }else{
            dbConnection = db;
            callback();
        }
    });
};

function closeConnection() {
    if (dbConnection)
        dbConnection.close();

}

getDbConnection(function(){
    dbConnection.dropDatabase(function(err,doc){
        if (err)
            console.log("Could not drop database");
        else
            addUser();
            addPlace();
    });
});

function addUser() {
    d = [{
        "userName": "Jerry",
        "emailAddress": "jerry@tom.com",
        "password": "i+g6Eeh4jpRB0mkloTDdnA=="
    },
        {
            "userName": "Tom",
            "emailAddress": "tom@jerry.com",
            "password": "i+g6Frh4jpTB4mkloTDdnJ=="
        }];
    var users = dbConnection.collection('users');
    users.insertOne(d[0], function(err,doc){
        if (err){
            console.log("Could not add user 1");
        }
        else {
            addCheckinstoUser(doc.ops[0]._id.toString(),45);
        }
    })
    users.insertOne(d[1], function(err,doc){
        if (err){
            console.log("Could not add user 1");
        }
        else {
            addCheckinstoUser(doc.ops[0]._id.toString(),120);
        }
    })
}

//Building data automatically here
placeNameList = ['Stevens Creek', 'Levis Stadium', 'NASA Ames', 'Great America', 'GooglePlex', 'Madras Cafe'];

function addCheckinstoUser(userID,count) {
    sequence = Array(count);
    console.log("sequence",sequence);
    var c = [];
    for (i=0;i<count;i++){
        console.log("Trying")
        var placeName = placeNameList[Math.floor(Math.random() * placeNameList.length)];
        var placeID = userID + i;
        var number = Math.floor(Math.random() * 100);
        var userID = userID;
        var rating=rating;
        c.push ({
            placeID : placeID,
            userID : userID,
            placeName : placeName,
            isExisting : true,
            rating : rating
        });

    }

    c.forEach(function(checkins){
        var checkin = dbConnection.collection('checkin');
        checkin.insertOne(checkins);
    })

}

function addPlace() {
    d = [{
        "placeName":    "Shana",
        "placeLat":     37.4,
        "placeLong":     122.1,
        "placeCategoryType":        "Restaurant",
        "numberCheckins":        2,
        "avgRating":        4.1,
        "latestRankingbyCategory": 1

    },

        {
            "placeName":    "Park1",
            "placeLat":     37.5,
            "placeLong":     122.1,
            "placeCategoryType": "Park",
            "numberCheckins":        2,
            "avgRating":        4.1,
            "latestRankingbyCategory": 2
        }];
    var places = dbConnection.collection('places');
    places.insertOne(d[0], function(err,doc){
        if (err){
            console.log("Could not add place 1");
        }
        else {
            addRatingtoPlace(doc.ops[0]._id.toString(),150);
        }
    })
    places.insertOne(d[1], function(err,doc){
        if (err){
            console.log("Could not add place 1");
        }
        else {
            addRatingtoPlace(doc.ops[0]._id.toString(),100);
        }
    })
}

function addRatingtoPlace(placeID,count) {
    sequence = Array(count);
    console.log("sequence",sequence);
    var c = [];
    for (i=0;i<count;i++){
        var rating = Math.floor(((Math.random() * 5)));
        var avgRating = Math.floor(((Math.random() * 5)));


        var placeID = placeID;


        c.push ({
            rating : rating,
            avgRating : avgRating,
            placeID : placeID
        });

    }

    c.forEach(function(rating){
        var ratings = dbConnection.collection('ratings');
        ratings.insertOne(rating);
    })

}


setTimeout(closeConnection,5000);