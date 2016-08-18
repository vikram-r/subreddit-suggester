# subreddit-suggester (for Reddit)
A tool that recommends [subreddits](http://www.reddit.com) for a user based on the behavior of users that post in similar subreddits. This tool is
built using [Akka](http://akka.io/) and [Spray](http://spray.io/). The concept of the algorithm is based on the assumption that similar people subscribe to similar subreddits. It uses the parallel nature of Akka/Spray to quickly analyze other posters in subscribed subreddits, to determine new subreddits that the user may be interested in.

# Usage

This tool can be run by either manually providing a list of subreddits, or by authenticating an account via OAuth2 and using the subreddits that user is subscribed to. 

### Manually Setting Subreddits

The simpler method is to manually provide a list of subreddits to use. Simply provide a comma delimited list to the `subreddits` java system property. For example: 

`gradle run -Dsubreddits=askreddit,pics`

This command will run the program as if you were logged in as a user who is subscribed to only `askreddit`, and `pics`.

### Using a User's Subscribed Subreddits

Alternatively, you can login to a Reddit account, and use that user's subscribed subreddits as input. To do this, you need to create a [valid reddit app](https://ssl.reddit.com/prefs/apps/). Make sure to create it as a "Web App". 
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

You can keep using this command for a while, but eventually your token will expire. You will need to repeat the process to login again.


#TODO

This project should eventually be converted into a website. This would simplify the OAuth authentication process. In addition, the
refresh token should be used, so the user doesn't have to re-authenticate from scratch every time the token expires.
