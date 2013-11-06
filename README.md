Twitter-filterstream-service
============================

Demo Scala application from a combination of:

- Twitter Streaming API
- Server-Sent Events

Languages / Libraries / frameworks used:

- Akka
- Twitter Bootstrap
- Play 2
- Twitter4J library
- Javascript (w/ JQuery + Highcharts)

Example
-------

Deployed on Heroku at [twitter-filterstream.herokuapp.com/](http://twitter-filterstream.herokuapp.com/). Unfortunately, there may be some kinks with server-sent events and Heroku, so if the deployed app is broken, try cloning this repo locally and running it.

At the main page, enter a comma-separated list of terms, click go and tweets that have any one of those words will appear and be counted in the graph.

On running/deploying this app
-------------

At the moment, this app uses one set of credentials taken from your environment variables. As such, please make sure the following are set in your environment:

TWITTER_CONSUMERKEY
TWITTER_CONSUMERSECRET
TWITTER_ACCESSTOKEN
TWITTER_ACCESSTOKENSECRET

They should correspond to your app's set of credentials can be found on [your Twitter Dev app page](https://dev.twitter.com/apps)
