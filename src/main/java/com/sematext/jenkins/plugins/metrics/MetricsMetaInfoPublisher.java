package com.sematext.jenkins.plugins.metrics;

import com.sematext.jenkins.plugins.client.SematextHttpClient;
import com.sematext.jenkins.plugins.utils.LogUtils;
import hudson.Extension;
import hudson.model.PeriodicWork;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Extension
public class MetricsMetaInfoPublisher extends PeriodicWork {
  private static final Logger logger = Logger.getLogger(MetricsMetaInfoPublisher.class.getName());

  private static final long PERIOD = TimeUnit.HOURS.toMillis(3);
  private static final long INITIAL_DELAY = TimeUnit.MINUTES.toMillis(1);

  @Override
  public long getRecurrencePeriod() {
    return PERIOD;
  }

  @Override
  public long getInitialDelay() {
    return INITIAL_DELAY;
  }

  @Override
  protected void doRun() throws Exception {
    try {
      logger.fine("doRun called: Publishing Sematext metrics metainfo.");

      SematextHttpClient.getInstance()
          .postMetaInfo(Arrays.stream(Metrics.values()).filter(metric -> metric != Metrics.TOKEN_CHECK)
              .collect(Collectors.toList()));

    } catch (Exception e) {
      LogUtils.severe(logger, e, null);
    }
  }
}
