Twitter-filterstream-service
============================

Demo Scala application from a combination of:

- Twitter Streaming API
- Server-Sent Events

Languages / Libraries / frameworks used:

- Akka
- Play 2
- Twitter Bootstrap
- [twitter-filterstream](https://github.com/lloydmeta/twitter-filterstream), which inherently wraps Twitter4J
- Javascript (w/ JQuery + Highcharts)

Description
-------

Deployed on Heroku at [twitter-filterstream.herokuapp.com](http://twitter-filterstream.herokuapp.com/)

At the main page, enter a comma-separated list of terms, click go and tweets that have any one of those words will appear and be counted in the graph. Anyone visiting the site is looking at the same stream. Changing the terms will change the stream for all visitors.

This app assumes that there is only 1 app server - there is no message bus / pub-sub mechanism to push updates to and from a feed across nodes.

On running/deploying this app
-------------

At the moment, this app uses one set of credentials taken from your environment variables. As such, please make sure the following are set in your environment:

* TWITTER_CONSUMERKEY
* TWITTER_CONSUMERSECRET
* TWITTER_ACCESSTOKEN
* TWITTER_ACCESSTOKENSECRET

They should correspond to your app's set of credentials can be found on [your Twitter Dev app page](https://dev.twitter.com/apps).

If you don't want to set the above environment variables, you can fill in the values in `conf/application.conf`, but keep in mind that you'll be committing your credentials into version control, which may or may not be sub-optimal.
