package com.sematext.jenkins.plugins.tags;

import com.sematext.jenkins.plugins.utils.TagUtils;
import hudson.EnvVars;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.LogTaskListener;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobTags extends BaseTags {
  private static final Logger LOGGER = Logger.getLogger(JobTags.class.getName());

  private String jobName;
  private String repoName;
  private String branch;

  public JobTags(Run run, TaskListener listener) throws IOException, InterruptedException {
    if (run == null) {
      return;
    }
    EnvVars envVars;
    if (listener != null) {
      envVars = run.getEnvironment(listener);
    } else {
      envVars = run.getEnvironment(new LogTaskListener(LOGGER, Level.INFO));
    }

    if (envVars != null) {
      if (envVars.get("GIT_BRANCH") != null) {
        setBranch(envVars.get("GIT_BRANCH"));
      } else if (envVars.get("CVS_BRANCH") != null) {
        setBranch(envVars.get("CVS_BRANCH"));
      } else if (envVars.get("BRANCH") != null) {
        setBranch(envVars.get("BRANCH"));
      } else if (envVars.get("GIT_LOCAL_BRANCH") != null) {
        setBranch(envVars.get("GIT_LOCAL_BRANCH"));
      }else if (envVars.get("BRANCH_NAME") != null) {
        setBranch(envVars.get("BRANCH_NAME"));
      }
    }

    String jobName = run.getParent().getFullName();
    setJobName(jobName == null ? null : jobName.replaceAll("»", "/").replaceAll(" ", ""));

    String repoName = StringUtils.substringBetween(getJobName(), "/");
    if (repoName == null) {
      repoName = "NA";
    }
    setRepoName(repoName);
  }

  public String getJobName() {
    return jobName;
  }

  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  public String getRepoName() {
    return repoName;
  }

  public void setRepoName(String repoName) {
    this.repoName = repoName;
  }

  public String getBranch() {
    return branch;
  }

  public void setBranch(String branch) {
    this.branch = branch;
  }

  public Map<String, String> asTags() {
    Map<String, String> tags = super.asTags();
    TagUtils.addTag(tags, "jenkins.job.name", getJobName());
    TagUtils.addTag(tags, "jenkins.repo.name", getRepoName());
    TagUtils.addTag(tags, "jenkins.branch.name", getBranch());
    return tags;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    JobTags jobTags = (JobTags) o;
    return Objects.equals(jobName, jobTags.jobName) &&
        Objects.equals(repoName, jobTags.repoName) &&
        Objects.equals(branch, jobTags.branch);
  }

  @Override
  public int hashCode() {
    return Objects.hash(jobName, repoName, branch);
  }

  @Override
  public String toString() {
    return "JobTags{" +
        "jobName='" + jobName + '\'' +
        ", repoName='" + repoName + '\'' +
        ", branch='" + branch + '\'' +
        "} " + super.toString();
  }
}
