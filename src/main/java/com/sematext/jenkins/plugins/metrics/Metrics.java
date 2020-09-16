package com.sematext.jenkins.plugins.metrics;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Metrics {
  TOKEN_CHECK("token.check"),

  RUNS_UNKNOWN("runs.unknown"),
  RUNS_SUCCESS("runs.success"),
  RUNS_UNSTABLE("runs.unstable"),
  RUNS_FAILURE("runs.failure"),
  RUNS_NOT_BUILT("runs.not.built"),
  RUNS_ABORTED("runs.aborted"),

  JOBS("jobs"),
  JOBS_SCHEDULED("jobs.scheduled"),
  JOBS_QUEUING("jobs.queuing"),
  JOBS_BLOCKED("jobs.blocked"),
  JOBS_BUILDABLE("jobs.buildable"),
  JOBS_WAITING("jobs.waiting"),
  JOBS_TOTAL("jobs.total"),

  JOBS_EXECUTION_TIME("jobs.execution.time"),
  JOBS_QUEUING_TIME("jobs.queuing.time"),
  JOBS_BLOCKED_TIME("jobs.blocked.time"),
  JOBS_BUILDABLE_TIME("jobs.buildable.time"),
  JOBS_WAITING_TIME("jobs.waiting.time"),
  JOBS_TOTAL_TIME("jobs.total.time");

  private final String key;

  private final static Map<String, Metrics> KEY_TO_METRIC = Arrays.stream(Metrics.values())
      .collect(Collectors.toMap(Metrics::getKey, metrics -> metrics));

  public static Metrics fromKey(String key) {
    return KEY_TO_METRIC.get(key);
  }

  public String getKey() {
    return key;
  }

  Metrics(String key) {
    this.key = key;
  }
}
