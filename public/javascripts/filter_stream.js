$(function() {

var currentEventSource = undefined;
var relevantIds = {
    termsList: "#termsList",
    statusList: "#statusList",
    graph: "#graph",
    goForm: "#goForm"
};

var prependToStatusList = function (userName, userImage, text, createdAt) {
  var statusList = $(relevantIds.statusList);
  statusList.prepend("<li>" + userName + " said: " + text + "</li>");
  if ($(relevantIds.statusList + " li").length > 20) {
    $(relevantIds.statusList + ' li:last').remove();
  };
};


$(relevantIds.goForm).submit(function(event) {
  if(typeof(EventSource)!=="undefined")
  {
    var termsList = $(relevantIds.termsList).val();
    if (currentEventSource){
      // Cleanup
      currentEventSource.close;
    } else {
      currentEventSource = new EventSource("/filterStream/" + termsList);
      currentEventSource.addEventListener('open', function (e) {
        console.log("connection opened");
      }, false);
      currentEventSource.addEventListener('message', function (e) {
        var data = JSON.parse(e.data);
        var tweet = data.status;
        console.log("new status received");
        console.log(tweet);
        prependToStatusList(tweet.userName, tweet.userImage, tweet.text, tweet.createdAt);
      }, false);
    }
  }
  else
  {
    alert("Sorry it doesn't look like your browser supports server-sent events");
  }
  event.preventDefault();
});

})