Welcome to Milmaps!

Milmaps aims to provide a pure javascript (via GWT) library for displaying tiled maps from geowebcache or other tiled sources. Some of the features we are targeting are animation like that provided by modest maps a flash based tile map display library.

Build instructions for **non-members** (Must have Subversion and Maven):

  * `svn checkout http://milmaps.googlecode.com/svn/trunk/ milmaps-read-only`
  * `cd milmaps-read-only`
  * `mvn install`

Checkout instructions for **members**:
  * `svn checkout https://milmaps.googlecode.com/svn/trunk/ milmaps --username <username@gmail.com>`
  * When prompted, enter your [googlecode.com](https://code.google.com/hosting/settings) generated password. (Must be signed in to generate your password).
  * Commit new projects to https://milmaps.googlecode.com/svn/trunk

**To see a live demo, check out this [link](http://www.milmaps.com)**


---

**Maven License Plugin**

When adding a new source file, the appropriate license header must be appended to the file by running the following command:
  * `mvn license:format`