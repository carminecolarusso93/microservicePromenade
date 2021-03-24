'use strict';

// var baseURIRest = '/monolith-otm/rest/otm';

document.getElementById("buttonTopCriticalNode").addEventListener("click", restTopCriticalNode)
document.getElementById("buttonThresholdCriticalNode").addEventListener("click", restThresholdCriticalNode)
document.getElementById("buttonNodeId").addEventListener("click", restNodeId)
document.getElementById("buttonShortestPath").addEventListener("click", restShortestPath)

var markerIcon = L.icon({
    iconUrl: 'Image/markerIcon.png',
    iconSize: [38, 50],
    iconAnchor: [19, 50],
    popupAnchor: [0, -50]
});

var markerBandiera = L.icon({
    iconUrl: 'Image/markerFlag.png',
    iconSize: [38, 50],
    iconAnchor: [0, 40],
});

var markerHospital = L.icon({
    iconUrl: 'Image/markerHospital.png',
    iconSize: [38, 50],
    iconAnchor: [19, 50],
    popupAnchor: [0, -50]
});

var markerP = L.icon({
    iconUrl: 'Image/markerParking.png',
    iconSize: [38, 50],
    iconAnchor: [19, 50],
    popupAnchor: [0, -50]
});
var markerBus = L.icon({
    iconUrl: 'Image/markerBus.png',
    iconSize: [38, 38],
    iconAnchor: [19, 50],
    popupAnchor: [0, -50]
});

var markerMuseum = L.icon({
    iconUrl: 'Image/MarkerMuseum.png',
    iconSize: [38, 50],
    iconAnchor: [19, 50],
    popupAnchor: [0, -50]
});

function restTopCriticalNode() {
    // var resp = null;
    // var param = null;
    //console.log("performCall");
    var xhttp = new XMLHttpRequest(); // jshint ignore: line
    xhttp.contentType = "application/json";
    xhttp.onreadystatechange = function () {

        if (xhttp.readyState === XMLHttpRequest.DONE) {
            if (xhttp.status === 200) {
                console.log("response: " + this.responseText);
                var myArr = JSON.parse(this.responseText);
                var i = 1;
                myArr.forEach(function (value) {
                    var marker = new L.marker(new L.LatLng(value.coordinate.latitude, value.coordinate.longitude), {icon: markerIcon});
                    marker.bindPopup("<b>Betweenness: </b>" + value.betweenness + "<br><b>OSMID: </b>" + value.osmid + "<br> <b>Top: </b>" + i);
                    markers.push(marker);
                    i++;
                });
                centerMap(myArr[0].coordinate.latitude, myArr[0].coordinate.longitude);
                showMarkers();
            } else {
                cleanMap();
                alert("Server cannot satisfy this request!");
            }
        }
    };
    console.log("open");
    var top = document.getElementById("topInput").value
    if (top === '') {
        alert("WARNING! insert a valid input");
    } else {
        cleanMap();
        var serverLocation = document.getElementById("serverLocationRest").value;
        console.log(baseURIRest);
        xhttp.open('GET', 'http://' + serverLocation + baseURIRest + '/criticalNodes?top=' + top, true);
        xhttp.send();
    }
}

function restThresholdCriticalNode() {
    // console.log("performCall");
    var xhttp = new XMLHttpRequest(); // jshint ignore: line
    xhttp.contentType = "application/json";
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState === XMLHttpRequest.DONE) {
            if (xhttp.status === 200) {
                //console.log("response: " + this.responseText);
                var myArr = JSON.parse(this.responseText);
                myArr.forEach(function (value) {
                    var marker = new L.marker(new L.LatLng(value.coordinate.latitude, value.coordinate.longitude), {icon: markerIcon});
                    marker.bindPopup("<b>Betweenness: </b>" + value.betweenness + "<br><b>OSMID: </b>" + value.osmid);
                    markers.push(marker);
                });
                centerMap(myArr[0].coordinate.latitude, myArr[0].coordinate.longitude);
                showMarkers();
            } else {
                cleanMap();
                alert("Server cannot satisfy this request!");
            }
        }
    };
    var threshold = document.getElementById("thresholdInput").value
    if (threshold === '') {
        alert("WARNING! insert a valid input");
    } else {
        cleanMap();
        var serverLocation = document.getElementById("serverLocationRest").value;
        xhttp.open('GET', 'http://' + serverLocation + baseURIRest + '/criticalNodes?threshold=' + threshold, false);
        xhttp.send();
    }
}

function restNodeId() {
    // console.log("performCall");
    var xhttp = new XMLHttpRequest(); // jshint ignore: line
    xhttp.contentType = "application/json";
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState === XMLHttpRequest.DONE) {
            if (xhttp.status === 200) {
                //console.log("response: " + this.responseText);
                var value = JSON.parse(this.responseText);
                // $scope.resp = "Flow: " + value.betweenness;
                var marker = new L.marker(new L.LatLng(value.coordinate.latitude, value.coordinate.longitude), {icon: markerIcon});
                marker.bindPopup("<b>Betweenness: </b>" + value.betweenness + "<br><b>OSMID: </b>" + value.osmid);
                markers.push(marker);
                centerMap(value.coordinate.latitude, value.coordinate.longitude);
                showMarkers();
                marker.openPopup();
            } else {
                cleanMap();
                $scope.resp = null;
                alert("Server cannot satisfy this request!");
            }
        }
    };
    // console.log("open");
    var nodeId = document.getElementById("nodeidInput").value
    if (nodeId === '') {
        alert("WARNING! insert a valid input");
    } else {
        cleanMap();
        var serverLocation = document.getElementById("serverLocationRest").value;
        xhttp.open('GET', 'http://' + serverLocation + baseURIRest + '/nodesFlow/' + nodeId, false);
        // console.log("send");
        xhttp.send();
    }
}

function restShortestPath() {
    var xhttp = new XMLHttpRequest(); // jshint ignore: line
    xhttp.contentType = "application/json";
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState === XMLHttpRequest.DONE) {
            if (xhttp.status === 200) {
                var myArr = JSON.parse(this.responseText);
                myArr.forEach(function (value) {
                    var vertex = [value.latitude, value.longitude];
                    pathVertexs.push(vertex);
                });
                var primo = pathVertexs[0];
                var markerStart = new L.marker(new L.LatLng(primo[0], primo[1]));
                //markerStart.bindPopup("<b> START </b>").openPopup();
                markers.push(markerStart);

                var ultimo = pathVertexs[pathVertexs.length - 1];
                var markerEnd = new L.marker(new L.LatLng(ultimo[0], ultimo[1]), {icon: markerBandiera});
                //markerEnd.bindPopup("<b> END </b>").openPopup();
                markers.push(markerEnd);
                showMarkers();
                showPath();
            } else {
                cleanMap();
                alert("Server cannot satisfy this request!");
            }
        }
    };
    console.log("open");
    var startNodeId = document.getElementById("startNodeidInput").value
    var endNodeId = document.getElementById("endNodeidInput").value
    if (startNodeId === '' || endNodeId === '') {
        alert("WARNING! insert a valid input");
    } else {
        cleanMap();
        var serverLocation = document.getElementById("serverLocationRest").value;
        xhttp.open('GET', 'http://' + serverLocation + baseURIRest + '/shortestPaths?source=' + startNodeId + '&destination=' + endNodeId, false);
    }
    // console.log("send");
    xhttp.send();
}

