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

Deployed on Heroku at [twitter-filterstream.herokuapp.com/](http://twitter-filterstream.herokuapp.com/)

Enter a comma-separated list of terms and tweets that have any one of those words will appear and be counted in the graph.

On deployment
-------------

At the moment, this app uses one set of credentials taken from the environment variables. As such, please make sure the following are set in your environment:

TWITTER_CONSUMERKEY
TWITTER_CONSUMERSECRET
TWITTER_ACCESSTOKEN
TWITTER_ACCESSTOKENSECRET

They can be found from [your Twitter Dev app page](https://dev.twitter.com/apps)