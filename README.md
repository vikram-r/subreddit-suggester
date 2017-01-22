# subreddit-suggester (for Reddit)
A tool that recommends [subreddits](http://www.reddit.com) for a user based on the behavior of users that post in similar subreddits. The engine is built using [Akka](http://akka.io/). The web server is built using the [Play Framework](https://www.playframework.com/). The entire project/dependencies are managed by [sbt](http://www.scala-sbt.org/). The premise of the algorithm is based on the assumption that similar people subscribe to similar subreddits. It uses the parallel nature of Akka to quickly analyze other posters in subscribed subreddits in order to determine new subreddits that the user may be interested in.

# Usage (Web Server)

To launch the web server, first run
```
sbt
```
then 
```
run
```

Once the web server is started, navigate to `http://localhost:9000/` to get started.

# Configuring the Reddit App

In order to take advantage of all the features in this project, you should link a Reddit application. This will allow users to log in with their Reddit accounts and receive suggestions based on their subscribed subreddits. To configure your Reddit application, you need to create a [valid reddit app](https://ssl.reddit.com/prefs/apps/). Make sure to create it as a "Web App". Once that's done, the `client id`, `client secret`, and `redirect_uri` need to be provided as system properties. For example: 

```
export REDDIT_CLIENT_ID=awesome_client_id
export REDDIT_CLIENT_SECRET=yeah_right
export REDDIT_REDIRECT_URI=http://localhost:9000/oauth2-callback
```
(Make sure the `redirect_uri` matches the OAuth2 callback url defined in `routes`. Currently it is `http://localhost:9000/oauth2-callback`).

### Testing (while in development)
While this project is in development, there are a couple endpoints that can be used for testing. Currently: 

```
/debug                  runs the engine with a preset list of subreddits
/oauthDebugRun          runs the engine for the user that is currently logged in
```
Note: These routes are purely for development purposes, and are subject to change.

# Using the Engine CLI

The Engine itself has a CLI you can use to test if you don't want to setup the full web server/Reddit application. To use the CLI, you must manually provide a list of subreddits to use (no OAuth2 support). This list should be passed as Java arguments. For example:

```
sbt "project engine" "run askreddit pics"
```
This command will run the program as if you were logged in as a user who is subscribed to only `askreddit`, and `pics`.

#To Do
- The engine is a bit unreliable. There are a few edge cases that cause it to stall indefinitely.
- Implement caching
- A lot of front-end work remaining
