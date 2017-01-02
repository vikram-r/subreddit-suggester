import com.vikram.core.{CustomJsonProtocols, RedditDataModel}
import org.junit.Test
import org.junit.Assert._
import spray.json._



class RedditDataModelTest {
  import RedditDataModel._
  import CustomJsonProtocols._

  val listingThingJson =
    """
      |{
      |  "kind": "Listing",
      |  "data": {
      |    "modhash": null,
      |    "children": [
      |      {
      |        "kind": "t5",
      |        "data": {
      |          "banner_img": "",
      |          "submit_text_html": null,
      |          "user_is_banned": false,
      |          "wiki_enabled": true,
      |          "id": "2fwo",
      |          "user_is_contributor": false,
      |          "submit_text": "",
      |          "display_name": "programming",
      |          "header_img": "http://b.thumbs.redditmedia.com/2rTE46grzsr-Ll3Q.png",
      |          "description_html": "&lt;!-- SC_OFF --&gt;&lt;div class=\"md\"&gt;&lt;p&gt;&lt;a href=\"/r/programming\"&gt;/r/programming&lt;/a&gt; is a reddit for discussion and news about &lt;a href=\"http://en.wikipedia.org/wiki/Computer_programming\"&gt;computer programming&lt;/a&gt;&lt;/p&gt;\n\n&lt;hr/&gt;\n\n&lt;p&gt;&lt;strong&gt;Guidelines&lt;/strong&gt;&lt;/p&gt;\n\n&lt;ul&gt;\n&lt;li&gt;Please try to keep submissions on topic and of high quality.&lt;/li&gt;\n&lt;li&gt;Just because it has a computer in it doesn&amp;#39;t make it programming.&lt;/li&gt;\n&lt;li&gt;Memes and image macros are not acceptable forms of content.&lt;/li&gt;\n&lt;li&gt;If there is no code in your link, it probably doesn&amp;#39;t belong here.&lt;/li&gt;\n&lt;li&gt;App demos should include code and/or architecture discussion.&lt;/li&gt;\n&lt;li&gt;Please follow proper &lt;a href=\"https://www.reddit.com/help/reddiquette\"&gt;reddiquette&lt;/a&gt;.&lt;/li&gt;\n&lt;/ul&gt;\n\n&lt;hr/&gt;\n\n&lt;p&gt;&lt;strong&gt;Info&lt;/strong&gt;&lt;/p&gt;\n\n&lt;ul&gt;\n&lt;li&gt;Do you have a question? Check out &lt;a href=\"/r/learnprogramming\"&gt;/r/learnprogramming&lt;/a&gt;, &lt;a href=\"/r/cscareerquestions\"&gt;/r/cscareerquestions&lt;/a&gt;, or &lt;a href=\"https://www.stackoverflow.com\"&gt;stackoverflow&lt;/a&gt;.&lt;/li&gt;\n&lt;li&gt;Do you have something funny to share with fellow programmers? Please take it to &lt;a href=\"/r/ProgrammerHumor/\"&gt;/r/ProgrammerHumor/&lt;/a&gt;.&lt;/li&gt;\n&lt;li&gt;For posting job listings, please visit &lt;a href=\"/r/forhire\"&gt;/r/forhire&lt;/a&gt; or &lt;a href=\"/r/jobbit\"&gt;/r/jobbit&lt;/a&gt;.&lt;/li&gt;\n&lt;li&gt;Check out our &lt;a href=\"http://www.reddit.com/r/pro                                                                                                                           gramming/wiki/faq\"&gt;faq&lt;/a&gt;.  It could use some updating.&lt;/li&gt;\n&lt;li&gt;If you&amp;#39;re an all-star hacker (or even just beginning), why not join the discussion at &lt;a href=\"https://www.reddit.com/r/redditdev\"&gt;/r/redditdev&lt;/a&gt; and steal our &lt;a href=\"https://github.com/reddit/reddit/wiki\"&gt;reddit code&lt;/a&gt;!&lt;/li&gt;\n&lt;/ul&gt;\n\n&lt;hr/&gt;\n\n&lt;p&gt;&lt;strong&gt;Related reddits&lt;/strong&gt;&lt;/p&gt;\n\n&lt;ul&gt;\n&lt;li&gt;&lt;a href=\"/r/technology\"&gt;/r/technology&lt;/a&gt;&lt;/li&gt;\n&lt;li&gt;&lt;a href=\"/r/ProgrammerTIL\"&gt;/r/ProgrammerTIL&lt;/a&gt;&lt;/li&gt;\n&lt;li&gt;&lt;a href=\"/r/learnprogramming\"&gt;/r/learnprogramming&lt;/a&gt;&lt;/li&gt;\n&lt;li&gt;&lt;a href=\"/r/askprogramming\"&gt;/r/askprogramming&lt;/a&gt;&lt;/li&gt;\n&lt;li&gt;&lt;a href=\"/r/coding\"&gt;/r/coding&lt;/a&gt;&lt;/li&gt;\n&lt;li&gt;&lt;a href=\"/r/compsci\"&gt;/r/compsci&lt;/a&gt;&lt;/li&gt;\n&lt;li&gt;&lt;a href=\"/r/dailyprogrammer\"&gt;/r/dailyprogrammer&lt;/a&gt;&lt;/li&gt;\n&lt;li&gt;&lt;a href=\"/r/netsec\"&gt;/r/netsec&lt;/a&gt;&lt;/li&gt;\n&lt;li&gt;&lt;a href=\"/r/webdev\"&gt;/r/webdev&lt;/a&gt;&lt;/li&gt;\n&lt;li&gt;&lt;a href=\"/r/web_design\"&gt;/r/web_design&lt;/a&gt;&lt;/li&gt;\n&lt;li&gt;&lt;a href=\"/r/gamedev\"&gt;/r/gamedev&lt;/a&gt;&lt;/li&gt;\n&lt;li&gt;&lt;a href=\"/r/cscareerquestions\"&gt;/r/cscareerquestions&lt;/a&gt;&lt;/li&gt;\n&lt;li&gt;&lt;a href=\"/r/reverseengineering\"&gt;/r/reverseengineering&lt;/a&gt;&lt;/li&gt;\n&lt;li&gt;&lt;a href=\"/r/startups\"&gt;/r/startups&lt;/a&gt;&lt;/li&gt;\n&lt;li&gt;&lt;a href=\"/r/techsupport\"&gt;/r/techsupport&lt;/a&gt;&lt;/li&gt;\n&lt;/ul&gt;\n\n&lt;p&gt;&lt;strong&gt;&lt;a href=\"https://www.reddit.com/r/programming/wiki/faq#wiki_what_language_reddits_are_there.3F\"&gt;Specific languages&lt;/a&gt;&lt;/strong&gt;&lt;/p&gt;\n&lt;/div&gt;&lt;!-- SC_ON --&gt;",
      |          "title": "programming",
      |          "collapse_deleted_comments": false,
      |          "public_description": "Computer Programming",
      |          "over18": false,
      |          "public_description_ht                                                                                                                            ml": "&lt;!-- SC_OFF --&gt;&lt;div class=\"md\"&gt;&lt;p&gt;Computer Programming&lt;/p&gt;\n&lt;/div&gt;&lt;!-- SC_ON --&gt;",
      |          "icon_size": null,
      |          "suggested_comment_sort": null,
      |          "icon_img": "",
      |          "header_title": null,
      |          "description": "/r/programming is a reddit for discussion and news about [computer programming](http://en.wikipedia.org/wiki/Computer_programming)\n\n****\n**Guidelines**\n\n* Please try to keep submissions on topic and of high quality.\n* Just because it has a computer in it doesn't make it programming.\n* Memes and image macros are not acceptable forms of content.\n* If there is no code in your link, it probably doesn't belong here.\n* App demos should include code and/or architecture discussion.\n* Please follow proper [reddiquette](https://www.reddit.com/help/reddiquette).\n\n****\n**Info**\n\n* Do you have a question? Check out /r/learnprogramming, /r/cscareerquestions, or [stackoverflow](https://www.stackoverflow.com).\n* Do you have something funny to share with fellow programmers? Please take it to /r/ProgrammerHumor/.\n* For posting job listings, please visit /r/forhire or /r/jobbit.\n* Check out our [faq](http://www.reddit.com/r/programming/wiki/faq).  It could use some updating.\n* If you're an all-star hacker (or even just beginning), why not join the discussion at [/r/redditdev](https://www.reddit.com/r/redditdev) and steal our [reddit code](https://github.com/reddit/reddit/wiki)!\n\n****\n**Related reddits**\n\n* /r/technology\n* /r/ProgrammerTIL\n* /r/learnprogramming\n* /r/askprogramming\n* /r/coding\n* /r/compsci\n* /r/dailyprogrammer\n* /r/netsec\n* /r/webdev\n* /r/web_design\n* /r/gamedev\n* /r/cscareerquestions\n* /r/reverseengineering\n* /r/startups\n* /r/techsupport\n\n**[Specific languages](https://www.reddit.com/r/programming/wiki/faq#wiki_what_language_reddits_are_there.3F)**",
      |          "user_is_muted": false,
      |          "submit_link_label": null,
      |          "accounts_active": null,
      |          "public_traffic": false,
      |          "header_size": [
      |            120,
      |            40
      |          ],
      |          "subscribers": 673319,
      |          "submit_text_label": null,
      |          "lang": "en",
      |          "key_color": "",
      |          "name": "t5_2fwo",
      |          "created": 1141179569,
      |          "url": "/r/programming/",
      |          "quarantine": false,
      |          "hide_ads": false,
      |          "created_utc": 1141150769,
      |          "banner_size": null,
      |          "user_is_moderator": false,
      |          "user_sr_theme_enabled": true,
      |          "comment_score_hide_mins": 0,
      |          "subreddit_type": "public",
      |          "submission_type": "link",
      |          "user_is_subscriber": true
      |        }
      |      },
      |      {
      |        "kind": "t5",
      |        "data": {
      |          "banner_img": "",
      |          "submit_text_html": "&lt;!-- SC_OFF --&gt;&lt;div class=\"md\"&gt;&lt;p&gt;Please read &lt;a href=\"/r/pics/about/sidebar\"&gt;the sidebar&lt;/a&gt; before submitting&lt;/p&gt;\n&lt;/div&gt;&lt;!-- SC_ON --&gt;",
      |          "user_is_banned": false,
      |          "wiki_enabled": true,
      |          "id": "2qh0u",
      |          "user_is_contributor": false,
      |          "submit_text": "Please read [the sidebar](/r/pics/about/sidebar) before submitting\n\n",
      |          "display_name": "pics",
      |          "header_img": "http://a.thumbs.redditmedia.com/v6TowjW1Qnev8RYA2CounVgo_Y-B7pu2dyRDFZNTk_8.png",
      |          "description_html": "&lt;!-- SC_OFF --&gt;&lt;div class=\"md\"&gt;&lt;p&gt;A place to share photographs and pictures. Feel free to post your own, but please &lt;strong&gt;read the rules first&lt;/strong&gt; (see below), and note that we are &lt;em&gt;not a catch-all&lt;/em&gt; for general images (of screenshots, comics, etc.)&lt;/p&gt;\n\n&lt;hr/&gt;\n\n&lt;h1&gt;Spoiler code&lt;/h1&gt;\n\n&lt;p&gt;Please mark spoilers like this:&lt;br/&gt;\n&lt;code&gt;[text here](/spoiler)&lt;/code&gt;&lt;/p&gt;\n\n&lt;p&gt;Hover over to &lt;a href=\"/spoiler\"&gt;read&lt;/a&gt;.&lt;/p&gt;\n\n&lt;hr/&gt;\n\n&lt;p&gt;Check out &lt;a href=\"http://nt.reddit.com/r/pics\"&gt;http://nt.reddit.com/r/pics&lt;/a&gt;!&lt;/p&gt;\n\n&lt;hr/&gt;\n\n&lt;h1&gt;&lt;a href=\"/r/pics/about/rules\"&gt;Posting Rules&lt;/a&gt;&lt;/h1&gt;\n\n&lt;ol&gt;\n&lt;li&gt;&lt;p&gt;&lt;strong&gt;No screenshots.  No pictures with added/superimposed text.&lt;/strong&gt; &lt;em&gt;This includes &lt;a href=\"http://en.wikipedia.org/wiki/Image_macro\"&gt;image macros&lt;/a&gt;, comics, maps, infographics, and most diagrams. Text (e.g. a URL) serving to credit the original author is e                                                                                                                            xempt.&lt;/em&gt;&lt;/p&gt;&lt;/li&gt;\n&lt;li&gt;&lt;p&gt;&lt;strong&gt;No porn or gore.&lt;/strong&gt; &lt;em&gt;NSFW content must be tagged.&lt;/em&gt;&lt;/p&gt;&lt;/li&gt;\n&lt;li&gt;&lt;p&gt;&lt;strong&gt;No personal information.&lt;/strong&gt; &lt;em&gt;This includes anything hosted on Facebook&amp;#39;s servers, as they can be traced to the original account holder. Stalking &amp;amp; harassment will not be tolerated.&lt;/em&gt; &lt;strong&gt;&lt;em&gt;No missing-persons requests!&lt;/em&gt;&lt;/strong&gt;&lt;/p&gt;&lt;/li&gt;\n&lt;li&gt;&lt;p&gt;&lt;strong&gt;No post titles soliciting votes&lt;/strong&gt; &lt;em&gt;(e.g. &amp;quot;upvote this&amp;quot;).&lt;/em&gt;&lt;/p&gt;&lt;/li&gt;\n&lt;li&gt;&lt;p&gt;&lt;strong&gt;No DAE, &amp;quot;[FIXED]&amp;quot;, &amp;quot;cake day&amp;quot;, or &amp;quot;photoshop this&amp;quot; posts, nor posts addressed to a specific redditor.&lt;/strong&gt; &lt;em&gt;&amp;quot;[FIXED]&amp;quot; posts should be added as a comment to the original image. Photoshop requests should be directed to &lt;a href=\"/r/PhotoshopRequest\"&gt;/r/PhotoshopRequest&lt;/a&gt;.&lt;/em&gt;&lt;/p&gt;&lt;/li&gt;\n&lt;li&gt;&lt;p&gt;&lt;strong&gt;Submissions must link directly to a specific image file or to a website with minimal ads.&lt;/strong&gt; &lt;em&gt;We do not allow blog hosting of images (&amp;quot;blogspam&amp;quot;), but links to albums on image hosting websites are okay. URL shorteners are prohibited. URLs in image or album descriptions are prohibited.&lt;/em&gt; &lt;/p&gt;&lt;/li&gt;\n&lt;li&gt;&lt;p&gt;&lt;strong&gt;No animated images.&lt;/strong&gt; &lt;em&gt;Please submit them to &lt;a href=\"/r/gif\"&gt;/r/gif&lt;/a&gt;, &lt;a href=\"/r/gifs\"&gt;/r/gifs&lt;/a&gt;, or &lt;a href=\"/r/reactiongifs\"&gt;/r/reactiongifs&lt;/a&gt; instead.&lt;/em&gt;&lt;/p&gt;&lt;/li&gt;\n&lt;li&gt;&lt;p&gt;&lt;strong&gt;Three strikes, you&amp;#39;re out for misleading posts.&lt;/strong&gt; If three of your posts are tagged misleading by moderators, you will be banned.&lt;/p&gt;&lt;/li&gt;\n&lt;/ol&gt;                                                                                                                            \n\n&lt;ul&gt;\n&lt;li&gt;&lt;p&gt;Please be civil when commenting. Racist/sexist/homophobic comments and personal attacks against other redditors do not belong here.&lt;/p&gt;&lt;/li&gt;\n&lt;li&gt;&lt;p&gt;If your submission appears to be filtered, but &lt;strong&gt;definitely&lt;/strong&gt; meets the above rules, &lt;a href=\"/message/compose?to=%23pics\"&gt;please send us a message&lt;/a&gt; with a link to the &lt;strong&gt;comments section&lt;/strong&gt; of your post (not a direct link to the image). &lt;strong&gt;Don&amp;#39;t delete it&lt;/strong&gt;  as that just makes the filter hate you! &lt;/p&gt;&lt;/li&gt;\n&lt;li&gt;&lt;p&gt;If you come across any rule violations please report the submission or  &lt;a href=\"http://www.reddit.com/message/compose?to=%23pics\"&gt;message the mods&lt;/a&gt; and one of us will remove it!&lt;/p&gt;&lt;/li&gt;\n&lt;li&gt;&lt;p&gt;Serial reposters may be filtered. False claims of ownership will result in a ban.&lt;/p&gt;&lt;/li&gt;\n&lt;li&gt;&lt;p&gt;Please try to come up with &lt;strong&gt;original post titles&lt;/strong&gt;. Submissions that use certain clichés/memes will be automatically tagged with a warning.&lt;/p&gt;&lt;/li&gt;\n&lt;li&gt;&lt;p&gt;&lt;strong&gt;Professional photographer or artist?&lt;/strong&gt; Read &lt;a href=\"/r/pics/wiki/professionals\"&gt;these guidelines&lt;/a&gt; for linking to your own site and obtaining &amp;#39;Verified&amp;#39; user flair. &lt;/p&gt;&lt;/li&gt;\n&lt;/ul&gt;\n\n&lt;h1&gt;Links&lt;/h1&gt;\n\n&lt;p&gt;If your post doesn&amp;#39;t meet the above rules, consider submitting it on one of these other subreddits:&lt;/p&gt;\n\n&lt;h1&gt;Subreddits&lt;/h1&gt;\n\n&lt;p&gt;Below is a table of subreddits that you might want to check out!&lt;/p&gt;\n\n&lt;table&gt;&lt;thead&gt;\n&lt;tr&gt;\n&lt;th&gt;Screenshots&lt;/th&gt;\n&lt;th&gt;Advice Animals&lt;/th&gt;\n&lt;/tr&gt;\n&lt;/thead&gt;&lt;tbody&gt;\n&lt;tr&gt;\n&lt;td&gt;&lt;a href=\"/r/images\"&gt;/r/images&lt;/a&gt;&lt;/td&gt;\n&lt;td&gt;&lt;a href=\"/r/adviceanimals\"&gt;/r/a                                                                                                                            dviceanimals&lt;/a&gt;&lt;/td&gt;\n&lt;/tr&gt;\n&lt;tr&gt;\n&lt;td&gt;&lt;a href=\"/r/screenshots\"&gt;/r/screenshots&lt;/a&gt;&lt;/td&gt;\n&lt;td&gt;&lt;a href=\"/r/memes\"&gt;/r/memes&lt;/a&gt;&lt;/td&gt;\n&lt;/tr&gt;\n&lt;tr&gt;\n&lt;td&gt;&lt;a href=\"/r/desktops\"&gt;/r/desktops&lt;/a&gt;&lt;/td&gt;\n&lt;td&gt;&lt;a href=\"/r/memesIRL\"&gt;/r/memesIRL&lt;/a&gt;&lt;/td&gt;\n&lt;/tr&gt;\n&lt;tr&gt;\n&lt;td&gt;&lt;strong&gt;Animals&lt;/strong&gt;&lt;/td&gt;\n&lt;td&gt;&lt;strong&gt;More Animals&lt;/strong&gt;&lt;/td&gt;\n&lt;/tr&gt;\n&lt;tr&gt;\n&lt;td&gt;&lt;a href=\"/r/aww\"&gt;/r/aww&lt;/a&gt;&lt;/td&gt;\n&lt;td&gt;&lt;a href=\"/r/rabbits\"&gt;/r/rabbits&lt;/a&gt;&lt;/td&gt;\n&lt;/tr&gt;\n&lt;tr&gt;\n&lt;td&gt;&lt;a href=\"/r/dogs\"&gt;/r/dogs&lt;/a&gt;&lt;/td&gt;\n&lt;td&gt;&lt;a href=\"/r/catsstandingup\"&gt;/r/catsstandingup&lt;/a&gt;&lt;/td&gt;\n&lt;/tr&gt;\n&lt;tr&gt;\n&lt;td&gt;&lt;a href=\"/r/cats\"&gt;/r/cats&lt;/a&gt;&lt;/td&gt;\n&lt;td&gt;&lt;a href=\"/r/otters\"&gt;/r/otters&lt;/a&gt;&lt;/td&gt;\n&lt;/tr&gt;\n&lt;tr&gt;\n&lt;td&gt;&lt;a href=\"/r/foxes\"&gt;/r/foxes&lt;/a&gt;&lt;/td&gt;\n&lt;td&gt;&lt;a href=\"/r/redpandas\"&gt;/r/redpandas&lt;/a&gt;&lt;/td&gt;\n&lt;/tr&gt;\n&lt;tr&gt;\n&lt;td&gt;&lt;strong&gt;GIFS&lt;/strong&gt;&lt;/td&gt;\n&lt;td&gt;&lt;strong&gt;HQ / Curated&lt;/strong&gt;&lt;/td&gt;\n&lt;/tr&gt;\n&lt;tr&gt;\n&lt;td&gt;&lt;a href=\"/r/gifs\"&gt;/r/gifs&lt;/a&gt;&lt;/td&gt;\n&lt;td&gt;&lt;a href=\"/r/pic\"&gt;/r/pic&lt;/a&gt;&lt;/td&gt;\n&lt;/tr&gt;\n&lt;tr&gt;\n&lt;td&gt;&lt;a href=\"/r/catgifs\"&gt;/r/catgifs&lt;/a&gt;&lt;/td&gt;\n&lt;td&gt;&lt;a href=\"/r/earthporn\"&gt;/r/earthporn&lt;/a&gt;&lt;/td&gt;\n&lt;/tr&gt;\n&lt;tr&gt;\n&lt;td&gt;&lt;a href=\"/r/reactiongifs\"&gt;/r/reactiongifs&lt;/a&gt;&lt;/td&gt;\n&lt;td&gt;&lt;a href=\"/r/spaceporn\"&gt;/r/spaceporn&lt;/a&gt;&lt;/td&gt;\n&lt;/tr&gt;\n&lt;/tbody&gt;&lt;/table&gt;\n\n&lt;h2&gt;Topic subreddits&lt;/h2&gt;\n\n&lt;p&gt;Every now and then, we choose 2 new topics, and find some subreddits about that topic to feature! Go                                                                                                                            t a topic you think we should feature? &lt;a href=\"https://www.reddit.com/r/Fox/comments/3y79pf/rpics_topic_request_thread/\"&gt;Go here&lt;/a&gt;&lt;/p&gt;\n\n&lt;table&gt;&lt;thead&gt;\n&lt;tr&gt;\n&lt;th&gt;Space&lt;/th&gt;\n&lt;th&gt;Interesting&lt;/th&gt;\n&lt;/tr&gt;\n&lt;/thead&gt;&lt;tbody&gt;\n&lt;tr&gt;\n&lt;td&gt;&lt;a href=\"/r/space\"&gt;/r/space&lt;/a&gt;&lt;/td&gt;\n&lt;td&gt;&lt;a href=\"/r/Damnthatsinteresting\"&gt;/r/Damnthatsinteresting&lt;/a&gt;&lt;/td&gt;\n&lt;/tr&gt;\n&lt;tr&gt;\n&lt;td&gt;&lt;a href=\"/r/ISS\"&gt;/r/ISS&lt;/a&gt;&lt;/td&gt;\n&lt;td&gt;&lt;a href=\"/r/mildlyinteresting\"&gt;/r/mildlyinteresting&lt;/a&gt;&lt;/td&gt;\n&lt;/tr&gt;\n&lt;tr&gt;\n&lt;td&gt;&lt;a href=\"/r/Mars\"&gt;/r/Mars&lt;/a&gt;&lt;/td&gt;\n&lt;td&gt;&lt;a href=\"/r/notinteresting\"&gt;/r/notinteresting&lt;/a&gt;&lt;/td&gt;\n&lt;/tr&gt;\n&lt;/tbody&gt;&lt;/table&gt;\n&lt;/div&gt;&lt;!-- SC_ON --&gt;",
      |          "title": "Reddit Pics",
      |          "collapse_deleted_comments": true,
      |          "public_description": "",
      |          "over18": false,
      |          "public_description_html": null,
      |          "icon_size": [
      |            256,
      |            256
      |          ],
      |          "suggested_comment_sort": null,
      |          "icon_img": "http://b.thumbs.redditmedia.com/VZX_KQLnI1DPhlEZ07bIcLzwR1Win808RIt7zm49VIQ.png",
      |          "header_title": "wait what",
      |          "description": "A place to share photographs and pictures. Feel free to post your own, but please **read the rules first** (see below), and note that we are *not a catch-all* for general images (of screenshots, comics, etc.)\n\n---\n\n#Spoiler code#\n\nPlease mark spoilers like this:  \n`[text here](/spoiler)`\n\nHover over to [read](/spoiler).\n\n---\nCheck out http://nt.reddit.com/r/pics!\n\n---\n#[Posting Rules](/r/pics/about/rules)#\n\n\n1. **No screenshots.  No pictures with added/superimposed text.** *This includes [image macros](http://en.wikipedia.org/wiki/Image_macro), comics, maps, infographics, and most diagrams. Text (e.g. a URL) serving to credit the original author is exempt.*\n\n1. **No porn or gore.** *NSFW content must be tagged.*\n\n1. **No personal information.** *This includes anyth                                                                                                                            ing hosted on Facebook's servers, as they can be traced to the original account holder. Stalking &amp; harassment will not be tolerated.* ***No missing-persons requests!***\n\n1. **No post titles soliciting votes** *(e.g. \"upvote this\").*\n\n1. **No DAE, \"[FIXED]\", \"cake day\", or \"photoshop this\" posts, nor posts addressed to a specific redditor.** *\"[FIXED]\" posts should be added as a comment to the original image. Photoshop requests should be directed to /r/PhotoshopRequest.*\n\n1. **Submissions must link directly to a specific image file or to a website with minimal ads.** *We do not allow blog hosting of images (\"blogspam\"), but links to albums on image hosting websites are okay. URL shorteners are prohibited. URLs in image or album descriptions are prohibited.* \n\n1. **No animated images.** *Please submit them to /r/gif, /r/gifs, or /r/reactiongifs instead.*\n\n1. **Three strikes, you're out for misleading posts.** If three of your posts are tagged misleading by moderators, you will be banned.\n\n* Please be civil when commenting. Racist/sexist/homophobic comments and personal attacks against other redditors do not belong here.\n\n* If your submission appears to be filtered, but **definitely** meets the above rules, [please send us a message](/message/compose?to=%23pics) with a link to the **comments section** of your post (not a direct link to the image). **Don't delete it**  as that just makes the filter hate you! \n\n* If you come across any rule violations please report the submission or  [message the mods](http://www.reddit.com/message/compose?to=%23pics) and one of us will remove it!\n\n* Serial reposters may be filtered. False claims of ownership will result in a ban.\n\n* Please try to come up with **original post titles**. Submissions that use certain clichés/memes will be automatically tagged with a warning.\n\n* **Professional photographer or artist?** Read [these guidelines](/r/pics/wiki/professionals) for linking to your own site and obtaining 'Verified' user flair. \n\n#Links#                                                                                                                            \nIf your post doesn't meet the above rules, consider submitting it on one of these other subreddits:\n\n#Subreddits\nBelow is a table of subreddits that you might want to check out!\n\nScreenshots | Advice Animals\n-----------|--------------\n/r/images | /r/adviceanimals\n/r/screenshots | /r/memes\n/r/desktops | /r/memesIRL\n**Animals** | **More Animals**\n/r/aww | /r/rabbits\n/r/dogs | /r/catsstandingup\n/r/cats | /r/otters\n/r/foxes | /r/redpandas\n**GIFS** | **HQ / Curated**\n/r/gifs | /r/pic\n/r/catgifs | /r/earthporn\n/r/reactiongifs | /r/spaceporn\n\n##Topic subreddits\n\nEvery now and then, we choose 2 new topics, and find some subreddits about that topic to feature! Got a topic you think we should feature? [Go here](https://www.reddit.com/r/Fox/comments/3y79pf/rpics_topic_request_thread/)\n\n\n\nSpace | Interesting\n-----|----------\n/r/space | /r/Damnthatsinteresting \n/r/ISS | /r/mildlyinteresting \n/r/Mars | /r/notinteresting ",
      |          "user_is_muted": false,
      |          "submit_link_label": "Submit an image",
      |          "accounts_active": null,
      |          "public_traffic": true,
      |          "header_size": [
      |            160,
      |            64
      |          ],
      |          "subscribers": 11782400,
      |          "submit_text_label": null,
      |          "lang": "en",
      |          "key_color": "",
      |          "name": "t5_2qh0u",
      |          "created": 1201249869,
      |          "url": "/r/pics/",
      |          "quarantine": false,
      |          "hide_ads": false,
      |          "created_utc": 1201221069,
      |          "banner_size": null,
      |          "user_is_moderator": false,
      |          "user_sr_theme_enabled": true,
      |          "comment_score_hide_mins": 60,
      |          "subreddit_type": "public",
      |          "submission_type": "link",
      |          "user_is_subscriber": true
      |        }
      |      }
      |    ],
      |    "after": "t5_2qh0u",
      |    "before": null
      |  }
      |}
      |
    """.stripMargin

  @Test
  def testRedditListingElementToSubredditData(): Unit = {
    val listingThing = listingThingJson.parseJson.convertTo[RedditListingThing]
    val result = listingThing.data.children.map(_.dataAsSubredditData.name)

    val expected = Set("programming", "pics")
    assertEquals(expected.size, result.size)
    assertEquals(expected, result.toSet)
  }


}
