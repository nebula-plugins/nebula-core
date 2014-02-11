Nebula Plugins
=============
The nebula-plugins organization was setup to facilitate the generation, governance, and releasing of Gradle plugins.

Guidelines:
* Should be named gradle-*-plugin. Can use nebula-*-plugin for opinionated plugins.
* Plugin’s name will be the asterisk above, except for nebula-*-plugin’s which are named nebula-*
* Should have a branch and releases for major Gradle versions, optional
  * Version prefix needs to be the short version of Gradle, e.g. 1.8.0 for Gradle 1.8 support
* All plugins will apply the gradle-plugin-plugin to centralize publishing
  * Plugins will publish to bintray.com
* Plugins will have Cloudbee Jenkins jobs for building and releasing, to allow anyone to publish a release.
* Plugins will use the Apache 2.0 license, enforced via license plugin
* Development should be done via pull requests, which can be done on feature branches
* Pull requests should be reviewed by someone who isn't the author

Creating a new plugin
---------------------
* Create GitHub repo under nebula-plugins, aka REPO
  * Add hook via Settings->"WebHook URLs" and configure it with https://netflixoss.ci.cloudbees.com/github-webhook/, click Update Settings
  * Create a contrib-REPO team for repo, from https://github.com/orgs/nebula-plugins/teams, select Write Acces then click Create team
  * Add REPO repo to the contrib-REPO
  * Add REPO to general contrib team, https://github.com/orgs/nebula-plugins/teams/contrib
* Locally, git clone git@github.com:nebula-plugins/gradle-nothing-plugin.git gradle-MYPLUGIN-plugin
  * cd gradle-MYPLUGIN-plugin
  * rm -fr .git
  * git init
  * git remote add origin git@github.com:nebula-plugins/gradle-MYPLUGIN-plugin.git
  * Change files
    * Wipe out CHANGELOG.md
    * Change README.md
    * Reset version in gradle.properties
    * Edit build.gradle, update dependencies, description, etc
    * Rename src/main/resources/META-INF/gradle-plugins/nebula-project.properties
  * git push origin master
* Create Bintray module
  * From https://bintray.com/nebula/gradle-plugins, click Add Package, name it 
  * Set name, description, license (to Apache-2.0), tags (gradle, plugin, nebula), website (https://github.com/nebula-plugins/REPO), issues (https://github.com/nebula-plugins/REPO/issues), version control (https://github.com/nebula-plugins/REPO), and make download stats public.
  * On next page, fill in Github repo, _nebula-plugins/REPO_, save changes
  * On next page, fill in GitHub release notes file, CHANGELOG.md, save changes
  * From https://bintray.com/nebula/gradle-plugins/REPO, Click "Add to JCenter". Click "Host my snapshots..." and fill in group id as com.netflix.nebula, click Send. This will take a day to process.
* Setup Cloudbees
  * SEED job should make the appropriate jobs, ensure that it ran correctly: https://netflixoss.ci.cloudbees.com/job/nebula-plugins/job/SEED-nebula-plugins/
  * You should see REPO-snapshots, REPO-pull-requests, REPO-release as jobs in https://netflixoss.ci.cloudbees.com/job/nebula-plugins/
  * Do a `git push` to ensure push changes are picked up, job should queue immediately.
Adding Admins
---------------
* from https://bintray.com/nebula/organization/edit, add member.

Build Order
----------------
Since the plugin-plugin uses these plugins, there's some really screwy bootstrapping that has to happen. We need a better way, but the core plugins
will eventually settle down and this won't happen often. But when it does it should be clear what the steps are:
* Release nebula-test, it has no dependencies and the old nebula-plugin-plugin that it uses should be adequate to get it out
* Release nebula-plugin-plugin with a dependency (via the latest conf) to the released nebula-test


Nebula Core
===========
This specific project holds some "helper" classes for testing and interacting with Gradle. It's not meant to get too big, 
but should serve as a central place for all plugins. This project should have no dependency and not contain any specific
plugins.

TODO
----------
Create a test configuration for test code.


