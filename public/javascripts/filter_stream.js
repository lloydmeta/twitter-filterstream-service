$(function() {

Highcharts.setOptions({
    global: {
        useUTC: false
    }
});

var relevantIds = {
    status: "#status",
    termsList: "#termsList",
    statusList: "#statusList",
    graph: "#graph",
    goForm: "#goForm"
};

// Clears the status list
var clearStatusList = function() {
 $(relevantIds.statusList).empty();
}

// current terms being looked at
var currentTerms = undefined;

// Handles prepending of newly received tweets to the Tweets/status list
var prependToStatusList = function (userName, userImage, text, createdAt) {
  var statusList = $(relevantIds.statusList);
  statusList.prepend("<li><blockquote>" + text + "<small>"+ userName + "</small></blockquote></li>");
  if ($(relevantIds.statusList + " li").length > 15) {
    $(relevantIds.statusList + ' li:last').remove();
  };
};

// Rounds the current time to the nearst X minutes
var currentMinuteRounded = function (nearestXMinutes) {
  var coeff = 1000 * 60 * nearestXMinutes;
  var date = new Date();  //or use any other date
  return new Date(Math.round(date.getTime() / coeff) * coeff);
};

// Handles incrementing of chart data
var incrementChartData = function (chart) {
  var currentInterval = currentMinuteRounded(1);
  var currentLastDataPoint = chart.series[0].data[chart.series[0].data.length - 1];
  if (currentLastDataPoint && Math.abs(currentLastDataPoint.x - currentInterval) < 1000 ) {
    var currentYValueAtInterval = currentLastDataPoint.y;
    currentLastDataPoint.update(currentYValueAtInterval + 1);
  } else {
    chart.series[0].addPoint({ x: currentInterval, y: 1 }, true, false);
  }
};

// Resets the status list and the chart
var cleanSlateWithTerms = function (newTerms) {
  console.log("new terms in cleanslatewith terms: " + newTerms);
  currentTerms = newTerms;
  setUpChart(newTerms.join(", "));
  clearStatusList();
};

// Handles setting a quick "flash" message that fades out
var flashStatus = function (message) {
  var statusDiv = $(relevantIds.status);
  statusDiv.find('span#msg').html(message);
  statusDiv.show(250).fadeOut(4000);
}

// Handle a new message pushed from the EventSource
var handleNewMessage = function (message) {
  if (message.event == "newTerms") {
    var flashMessage = "The new terms being filtered on are: " + message.terms.join(", ");
    console.log(flashMessage);
    flashStatus(flashMessage);
    cleanSlateWithTerms(message.terms);
  } else {
    console.log("New status received");
    // Initial bootstrap in case there already is a stream
    if (typeof(currentTerms) === "undefined") {
      cleanSlateWithTerms(message.currentTerms);
    }
    var tweet = message.status;
    prependToStatusList(tweet.userName, tweet.userImage, tweet.text, tweet.createdAt);
    incrementChartData($(relevantIds.graph).highcharts());
  }
};

var setUpChart = function (terms){
  $(relevantIds.graph).highcharts({
    chart: {
      type: 'spline',
      animation: Highcharts.svg, // don't animate in old IE
      marginRight: 10
    },
    title: {
      text: 'Tweets with: ' + terms
    },
    xAxis: {
      type: 'datetime'
    },
    yAxis: {
      title: {
        text: 'Number of Tweets'
      },
      plotLines: [{
        value: 0,
        width: 1,
        color: '#808080'
      }]
    },
    tooltip: {
      formatter: function() {
              return '<b>'+ this.series.name +'</b><br/>'+
              Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) +'<br/>'+
              Highcharts.numberFormat(this.y, 2);
      }
    },
    legend: {
      enabled: false
    },
    exporting: {
      enabled: false
    },
    series: [{
      name: 'Number of Tweets',
      data: [ { x: currentMinuteRounded(1), y: 0 } ]
    }]
  });
};

// Connect to the event source if the browser supports it
if (typeof(EventSource) !== "undefined") {
  var currentEventSource = new EventSource("/connect");

  // Setup listeners on the EventSource
  currentEventSource.addEventListener('open', function (e) {
    console.log("Connection opened");
  }, false);

  currentEventSource.addEventListener('message', function (e) {
    var message = JSON.parse(e.data);
    handleNewMessage(message);
  }, false);

  currentEventSource.addEventListener('error', function (e) {
    if (e.readyState == EventSource.CLOSED) {
      $(relevantIds.statusList).empty();
      alert("Your connection was closed");
    } else {
      alert("An unknown error occured");
    }
  }, false);

  // Just some small cleanup
  window.onbeforeunload = function(){
    console.log('Closing connection on reload');
    currentEventSource.close();
  };
} else {
  alert("Sorry this site requires a real browser. Please try Chrome, Firefox, or Safari.");
}

// Override the submit form and prevent default behaviour
$(relevantIds.goForm).submit(function(event) {
  var termsList = $(relevantIds.termsList).val();
  $.ajax({
    url: '/new_terms/' + termsList,
    type: 'PUT',
    success: function(data) {
      console.log("New terms PUT successfully")
    },
    error: function () {
      console.log("Failed to PUT new terms")
    }
  });
  event.preventDefault();
});

})