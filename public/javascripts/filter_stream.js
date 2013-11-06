$(function() {

Highcharts.setOptions({
    global: {
        useUTC: false
    }
});

var currentEventSource = undefined;
var relevantIds = {
    termsList: "#termsList",
    statusList: "#statusList",
    graph: "#graph",
    goForm: "#goForm"
};

var prependToStatusList = function (userName, userImage, text, createdAt) {
  var statusList = $(relevantIds.statusList);
  statusList.prepend("<li><blockquote>" + text + "<small>"+ userName + "</small></blockquote></li>");
  if ($(relevantIds.statusList + " li").length > 15) {
    $(relevantIds.statusList + ' li:last').remove();
  };
};

var currentMinuteRounded = function (nearestXMinutes) {
  var coeff = 1000 * 60 * nearestXMinutes;
  var date = new Date();  //or use any other date
  return new Date(Math.round(date.getTime() / coeff) * coeff);
};

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

$(relevantIds.goForm).submit(function(event) {
  if(typeof(EventSource)!=="undefined")
  {
    var termsList = $(relevantIds.termsList).val();
    if (typeof(currentEventSource)!=="undefined"){
      // Cleanup
      $(relevantIds.statusList).empty();
      currentEventSource.close();
    }
    setUpChart(termsList);

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
      incrementChartData($(relevantIds.graph).highcharts());
    }, false);

  }
  else
  {
    alert("Sorry it doesn't look like your browser supports server-sent events");
  }
  event.preventDefault();
});

})