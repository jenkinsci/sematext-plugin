package com.sematext.jenkins.plugins.metrics;

import com.sematext.jenkins.plugins.utils.TagUtils;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Metrics {
  TOKEN_CHECK("token.check", "token check", "Special metric we send from plugin when user click 'Test Token' button."),

  RUNS_UNKNOWN("runs.unknown", "runs unknown"),
  RUNS_SUCCESS("runs.success", "runs success"),
  RUNS_UNSTABLE("runs.unstable", "runs unstable"),
  RUNS_FAILURE("runs.failure", "runs failure"),
  RUNS_NOT_BUILT("runs.not_built", "runs not built"),
  RUNS_ABORTED("runs.aborted", "runs aborted"),

  JOBS("jobs", "jobs"),
  JOBS_SCHEDULED("jobs.scheduled", "jobs scheduled",
      "The count at which jobs are scheduled. If a job is already in the queue and an identical request for scheduling the job is received then Jenkins will coalesce the two requests. This metric gives a reasonably pure measure of the load requirements of the Jenkins master as it is unaffected by the number of executors available to the system."),
  JOBS_QUEUING("jobs.queuing", "jobs queuing", "The count of jobs which are queued."),
  JOBS_BLOCKED("jobs.blocked", "jobs blocked", "The count at which jobs in the build queue enter the blocked state."),
  JOBS_BUILDABLE("jobs.buildable", "jobs buildable",
      "The count at which jobs in the build queue enter the buildable state."),
  JOBS_WAITING("jobs.waiting", "jobs waiting", "The count at which jobs enter the quiet period."),

  JOBS_EXECUTION_TIME("jobs.execution.time", "jobs execution time"),
  JOBS_QUEUING_TIME("jobs.queuing.time", "queuing jobs time", "The total time jobs spend in the build queue."),
  JOBS_BLOCKED_TIME("jobs.blocked.time", "blocked jobs time",
      "The amount of time jobs in the build queue enter spend in blocked state."),
  JOBS_BUILDABLE_TIME("jobs.buildable.time", "buildable jobs time",
      "The amount of time jobs in the build queue enter spend in buildable state."),
  JOBS_WAITING_TIME("jobs.waiting.time", "waiting jobs time",
      "The total amount of time that jobs spend in their quiet period."),
  JOBS_TOTAL_TIME("jobs.total.time", "total jobs time",
      "The time jobs spend from entering the build queue to completing building.");

  private final String key;
  private final String label;
  private final String description;

  private final static Map<String, Metrics> KEY_TO_METRIC = Arrays.stream(Metrics.values())
      .collect(Collectors.toMap(Metrics::getKey, metrics -> metrics));

  public Map<String, String> asTags() {
    Map<String, String> tags = new HashMap<>();
    if (getLabel() != null) {
      TagUtils.addTag(tags, "label", getLabel().replace(" ", "\\ "));
    }
    if (getDescription() != null) {
      TagUtils.addTag(tags, "description", getDescription().replace(" ", "\\ "));
    }
    TagUtils.addTag(tags, "numericType", "long");
    TagUtils.addTag(tags, "type", "counter");

    return tags;
  }

  public static Metrics fromKey(String key) {
    return KEY_TO_METRIC.get(key);
  }

  public String getLabel() {
    return label;
  }

  public String getDescription() {
    return description;
  }

  public String getKey() {
    return key;
  }

  Metrics(String key) {
    this(key, null, null);
  }

  Metrics(String key, String label) {
    this(key, label, null);
  }

  Metrics(String key, String label, String description) {
    this.key = key;
    this.label = label;
    this.description = description;
  }
}
