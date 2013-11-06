Twitter-filterstream-service
============================

Demo Scala application from a combination of:

- Twitter Streaming API
- Server-Sent Events

Languages / Libraries / frameworks used:

- Akka
- Play 2
- Twitter4J library
- Javascript (w/ JQuery + Highcharts)

Deployed on Heroku at [twitter-filterstream.herokuapp.com/](http://twitter-filterstream.herokuapp.com/)

On deployment
-------------

At the moment, this app uses one set of credentials taken from the environment variables. As such, please make sure the following are set in your environment:

TWITTER_CONSUMERKEY
TWITTER_CONSUMERSECRET
TWITTER_ACCESSTOKEN
TWITTER_ACCESSTOKENSECRET

They can be found from [your Twitter Dev app page](https://dev.twitter.com/apps)