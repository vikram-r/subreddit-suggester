# subreddit-suggester (for Reddit)
A tool that recommends [subreddits](http://www.reddit.com) for a user based on the behavior of users that post in similar subreddits. This tool is
built using [Akka](http://akka.io/) and [Spray](http://spray.io/). The concept is predicated on the assumption that similar
people subscribe to similar subreddits. It uses the parallel nature of Akka/Spray to quickly analyze other posters in 
subscribed subreddits, to determine new subreddits that the user may be interested in.

# How it works

//TODO

# Usage

In order to run this tool, you need to create a [valid reddit app](https://ssl.reddit.com/prefs/apps/). Make sure to create it as a "Web App". 
Once that's done, the `client id` and `client secret` need to be added to the `gradle.properties` file.

The project first needs to authenticate a user with OAuth2 to gain permission to access the subscribed subreddits for 
a user. To help out, the tool accepts system parameters to authenticate a user.

First simply run:

`gradle run`

This will open up your default web browser with a page asking you for permission to access your subscribed subreddits. Accepting 
the prompt will redirect you to whatever your `redirect uri` was set to when you configured your reddit app. Extract the 
`code` query parameter from that URL, then pass it into the following:

`gradle run -Dcode=<code>`

This command will use the code provided to retreive an authenticated token, then use the token to execute the actual program. 
The authenticated token will be printed out, so save it for future use. To run the program again, you can use: 

`gradle run -Dtoken=<token>`

Eventually your token will expire, which means you will have to repeat the process again to retreive a new valid token.


#TODO

This project should eventually be converted into a website. This would simplify the OAuth authentication process. In addition, the
refresh token should be used, so the user doesn't have to re-authenticate from scratch every time the token expires.
