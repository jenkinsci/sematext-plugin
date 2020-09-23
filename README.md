# Jenkins Sematext Plugin

A Jenkins plugin for automatically forwarding metrics to a Sematext account.

![Jenkins Sematext Dashboard][3]

**Note**: The [Jenkins CI plugin page][1] for this plugin references this documentation.

## Setup

### Installation

_This plugin requires Java 8 or newer._

This plugin can be installed from the [Update Center][2] (found at `Manage Jenkins -> Manage Plugins`) in your Jenkins installation:

1. Select the `Available` tab, search for `Sematext`, and select the checkbox next to `Sematext Plugin`.
2. Install the plugin by using one of the two install buttons at the bottom of the screen.
3. To verify the plugin is installed, search for `Sematext Plugin` on the `Installed` tab. 

  Continue below for configuration.

#### Plugin user interface

To configure your Sematext Plugin, navigate to the `Manage Jenkins -> Configure System` page on your Jenkins installation. Once there, scroll down to find the `Sematext Plugin` section:

##### Select Region to house your data 

1. Select the radio button with **Sematext Region which you will use to house your data** (Depends on which Region you created Sematext Jenkins App).
2. (advanced) Paste your Receiver URL in the `Sematext Jenkins App Receiver URL`.  Test your Receiver URL by using the `Test URL` button
3. Paste your App Token in the `Sematext Jenkins App Token` textbox on the Jenkins configuration screen.
4. Test your Sematext Jenkins App Token by using the `Test App Token` button on the Jenkins configuration screen directly below the App Token textbox.

5. Save your configuration.

## Data collected

This plugin is collecting the following [metrics](#metrics):

### Metrics

| Metric Name                            | Description                                                    | Tags                                                                     |
|----------------------------------------|----------------------------------------------------------------|--------------------------------------------------------------------------|
| `jenkins.runs.success`      | Number of success job runs .                                              | `os.host`, `jenkins.job.name`,`jenkins.repo.name`, `jenkins.branch.name` |

## Issue Tracking

GitHub's built-in issue tracking system is used to track all issues relating to this plugin: [jenkinsci/sematext-plugin/issues][4]. 

## Changes

See the [CHANGELOG.md][5].

## How to contribute code

First of all and most importantly, **thank you** for sharing.  

Checkout the [contributing guidelines][6] before you submit an issue or a pull request.  

[1]: https://plugins.jenkins.io/sematext
[2]: https://wiki.jenkins-ci.org/display/JENKINS/Plugins#Plugins-Howtoinstallplugins
[3]: https://raw.githubusercontent.com/sematext/sematext-jenkins-plugin/master/images/sematext-jenkins-dashboard.png
[4]: https://github.com/jenkinsci/sematext-plugin/issues
[5]: https://github.com/jenkinsci/sematext-plugin/blob/master/CHANGELOG.md
[6]: https://github.com/jenkinsci/.github/blob/master/CONTRIBUTING.md
