package com.sematext.jenkins.plugins.metrics;

import com.sematext.jenkins.plugins.client.SematextHttpClient;
import com.sematext.jenkins.plugins.tags.JobTags;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import jenkins.metrics.impl.TimeInQueueAction;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Extension
public class JobMetricProviderImpl extends MetricProvider {
  private static final String RUNS_PREFIX = "runs.";

  public static JobMetricProviderImpl instance() {
    return ExtensionList.lookup(MetricProvider.class).get(JobMetricProviderImpl.class);
  }

  private static Map.Entry<String, Object> metric(String key, Object value) {
    return new AbstractMap.SimpleEntry<>(key, value);
  }

  private static void sendMetrics(JobTags tags, Map.Entry<String, Object>... metrics) {
    SematextHttpClient.getInstance()
        .postMetrics(tags.asTags(), Arrays.stream(metrics)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
  }

  @Extension
  public static class RunListenerImpl extends RunListener<Run> {
    @Override
    public synchronized void onStarted(Run run, TaskListener listener) {
      JobMetricProviderImpl instance = instance();
      if (instance != null) {
        JobTags tags;
        try {
          tags = new JobTags(run, listener);
        } catch (IOException | InterruptedException e) {
          return;
        }

        sendMetrics(tags, metric(Metrics.JOBS_SCHEDULED.getKey(), 1));
      }
    }

    @Override
    public synchronized void onCompleted(Run run, TaskListener listener) {
      JobMetricProviderImpl instance = instance();
      TimeInQueueAction action = run.getAction(TimeInQueueAction.class);
      if (action != null && instance != null) {

        JobTags tags;
        try {
          tags = new JobTags(run, listener);
        } catch (IOException | InterruptedException e) {
          return;
        }

        String runResult = String.valueOf(run.getResult()).toLowerCase(Locale.ENGLISH).replace("_", ".");

        Metrics runResultMetric = Metrics.fromKey(RUNS_PREFIX + runResult);

        sendMetrics(tags,
            metric(runResultMetric == null ? Metrics.RUNS_UNKNOWN.getKey() : runResultMetric.getKey(), 1),

            metric(Metrics.JOBS_EXECUTION_TIME.getKey(), action.getExecutingTimeMillis()),
            metric(Metrics.JOBS_QUEUING_TIME.getKey(), action.getQueuingTimeMillis()),
            metric(Metrics.JOBS_BLOCKED_TIME.getKey(), action.getBlockedTimeMillis()),
            metric(Metrics.JOBS_BUILDABLE_TIME.getKey(), action.getBuildableTimeMillis()),
            metric(Metrics.JOBS_WAITING_TIME.getKey(), action.getWaitingTimeMillis()),
            metric(Metrics.JOBS_TOTAL_TIME.getKey(), action.getTotalDurationMillis()),

            metric(Metrics.JOBS.getKey(), 1),
            metric(Metrics.JOBS_QUEUING.getKey(), 1),
            metric(Metrics.JOBS_BLOCKED.getKey(), 1),
            metric(Metrics.JOBS_BUILDABLE.getKey(), 1),
            metric(Metrics.JOBS_WAITING.getKey(), 1)
        );
      }
    }
  }
}
