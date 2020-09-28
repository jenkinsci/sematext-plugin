# Jenkins Sematext Plugin

A Jenkins plugin for monitoring Jenkins with [Sematext][8].

![Jenkins Sematext Dashboard][3]

**Note**: The [Jenkins CI plugin page][1] for this plugin references this documentation.

## Setup

### Sematext App Creation

To be able to see your Jenkins metrics you have to create [Sematext Account][10] and [Sematext Jenkins App][9]
if you didn't do this already. 

### Sematext Agent Installation

To be able to see your Infrastructure and Jenkins JVM metrics you have to install [Sematext Agent][12] 

### Plugin Installation

_This plugin requires Java 8 or newer._

This plugin can be installed from the [Update Center][2] (found at `Manage Jenkins -> Manage Plugins`) in your Jenkins installation:

1. Select the `Available` tab, search for `Sematext`, and select the checkbox next to `Sematext Plugin`.
2. Install the plugin by using one of the two install buttons at the bottom of the screen.
3. To verify the plugin is installed, search for `Sematext Plugin` on the `Installed` tab. 

  Continue below for configuration.

### Plugin Configuration

To configure your Sematext Plugin, navigate to the `Manage Jenkins -> Configure System` page in your Jenkins installation. Once there, scroll down to find the `Sematext Plugin` section:

![Jenkins Sematext Plugin UI][7]

#### Select Region to house your data 

1. Select the radio button with **Sematext Region which you will use to house your data**. Which Region you specify here depends on the Region in which you created the Sematext Jenkins App - US or EU.
2. (advanced) Paste your Receiver URL in the `Sematext Jenkins App Receiver URL`.  You can find the Receiver URL in the integration instructions for your Jenkins App in Sematext.  Test your Receiver URL by using the `Test URL` button.
3. Paste your App Token, also found in the intgration instructions for your Jenkins App in Sematext, in the `Sematext Jenkins App Token` textbox on the Jenkins configuration screen.
4. Test your Sematext Jenkins App Token by using the `Test App Token` button on the Jenkins configuration screen directly below the App Token textbox.
5. Save your configuration.


## Data collected

This plugin collects the following [metrics](#metrics), more metrics will be collected by Sematext agent, full list [see here][11] :

### Metrics

| Metric Name                            | Description                                                               | Tags                                                                     |
|----------------------------------------|---------------------------------------------------------------------------|--------------------------------------------------------------------------|
| `jenkins.runs.success`                 | The count of success job runs.                                               | `os.host`, `jenkins.job.name`,`jenkins.repo.name`, `jenkins.branch.name` |
| `jenkins.runs.unstable`                | The count of unstable job runs.                                              | `os.host`, `jenkins.job.name`,`jenkins.repo.name`, `jenkins.branch.name` |
| `jenkins.runs.failure`                 | The count of failure job runs.                                               | `os.host`, `jenkins.job.name`,`jenkins.repo.name`, `jenkins.branch.name` |
| `jenkins.runs.not_built`               | The count of not build job runs.                                             | `os.host`, `jenkins.job.name`,`jenkins.repo.name`, `jenkins.branch.name` |
| `jenkins.runs.aborted`                 | The count of aborted job runs.                                               | `os.host`, `jenkins.job.name`,`jenkins.repo.name`, `jenkins.branch.name` |
| `jenkins.jobs`                         | The count of jobs.                                                           | `os.host`, `jenkins.job.name`,`jenkins.repo.name`, `jenkins.branch.name` |
| `jenkins.jobs.scheduled`               | The count at which jobs are scheduled. If a job is already in the queue and an identical request for scheduling the job is received then Jenkins will coalesce the two requests. This metric gives a reasonably pure measure of the load requirements of the Jenkins master as it is unaffected by the number of executors available to the system.                                                                                                                                                       | `os.host`, `jenkins.job.name`,`jenkins.repo.name`, `jenkins.branch.name` |
| `jenkins.jobs.queuing`                 | The count of queued jobs.                                       | `os.host`, `jenkins.job.name`,`jenkins.repo.name`, `jenkins.branch.name` |
| `jenkins.jobs.blocked`                 | The count at which jobs in the build queue enter the blocked state.       | `os.host`, `jenkins.job.name`,`jenkins.repo.name`, `jenkins.branch.name` |
| `jenkins.jobs.buildable`               | The count at which jobs in the build queue enter the buildable state.     | `os.host`, `jenkins.job.name`,`jenkins.repo.name`, `jenkins.branch.name` |
| `jenkins.jobs.waiting`                 | The count at which jobs enter the quiet period.                           | `os.host`, `jenkins.job.name`,`jenkins.repo.name`, `jenkins.branch.name` |
| `jenkins.jobs.execution.time`          | The amount of time jobs spend in execution state.                         | `os.host`, `jenkins.job.name`,`jenkins.repo.name`, `jenkins.branch.name` |
| `jenkins.jobs.queuing.time`            | The total time jobs spend in the build queue.                             | `os.host`, `jenkins.job.name`,`jenkins.repo.name`, `jenkins.branch.name` |
| `jenkins.jobs.blocked.time`            | The amount of time jobs in the build queue enter spend in blocked state.  | `os.host`, `jenkins.job.name`,`jenkins.repo.name`, `jenkins.branch.name` |
| `jenkins.jobs.buildable.time`          | The amount of time jobs in the build queue enter spend in buildable state.| `os.host`, `jenkins.job.name`,`jenkins.repo.name`, `jenkins.branch.name` |
| `jenkins.jobs.waiting.time`            | The total amount of time that jobs spend in their quiet period.           | `os.host`, `jenkins.job.name`,`jenkins.repo.name`, `jenkins.branch.name` |
| `jenkins.jobs.total.time`              | The time jobs spend from entering the build queue to completing building. | `os.host`, `jenkins.job.name`,`jenkins.repo.name`, `jenkins.branch.name` |

## Issue Tracking

GitHub's built-in issue tracking system is used to track all issues relating to this plugin: [jenkinsci/sematext-plugin/issues][4]. 

## Changes

See the [CHANGELOG.md][5].

## How to contribute code

First of all and most importantly, **thank you** for sharing.  

Checkout the [contributing guidelines][6] before you submit an issue or a pull request.  

[1]: https://plugins.jenkins.io/sematext
[2]: https://wiki.jenkins-ci.org/display/JENKINS/Plugins#Plugins-Howtoinstallplugins
[3]: https://raw.githubusercontent.com/jenkinsci/sematext-plugin/master/images/sematext-jenkins-dashboard.png
[4]: https://github.com/jenkinsci/sematext-plugin/issues
[5]: https://github.com/jenkinsci/sematext-plugin/blob/master/CHANGELOG.md
[6]: https://github.com/jenkinsci/.github/blob/master/CONTRIBUTING.md
[7]: https://raw.githubusercontent.com/jenkinsci/sematext-plugin/master/images/sematext-jenkins-plugin-ui.png
[8]: https://sematext.com/
[9]: https://sematext.com/docs/monitoring/#creating-monitoring-apps
[10]: https://apps.sematext.com/ui/registration
[11]: https://sematext.com/docs/integration/jenkins/
[12]: https://apps.sematext.com/ui/howto/Jenkins/overview
