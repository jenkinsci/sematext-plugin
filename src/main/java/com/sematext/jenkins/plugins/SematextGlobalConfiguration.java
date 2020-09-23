package com.sematext.jenkins.plugins;

import com.sematext.jenkins.plugins.client.SematextHttpClient;
import com.sematext.jenkins.plugins.metrics.Metrics;
import com.sematext.jenkins.plugins.utils.LogUtils;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import hudson.util.Secret;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.interceptor.RequirePOST;

import java.util.Collections;
import java.util.logging.Logger;

@Extension
public class SematextGlobalConfiguration extends GlobalConfiguration {

  private static final Logger logger = Logger.getLogger(
      SematextGlobalConfiguration.class.getName());
  private static final String DISPLAY_NAME = "Sematext Plugin";

  private static final String METRICS_RECEIVER_US_URL = "https://spm-receiver.sematext.com";
  private static final String METRICS_RECEIVER_EU_URL = "https://spm-receiver.eu.sematext.com";
  private static final String DATA_HOUSE = "dataHouse";
  private static final String METRICS_RECEIVER_URL = "metricsReceiverUrl";
  private static final String METRICS_TOKEN = "metricsToken";

  private String dataHouse = "US";
  private Secret metricsToken = null;
  private String metricsReceiverUrl = null;

  public SematextGlobalConfiguration() {
    load();
  }

  public static SematextGlobalConfiguration get() {
    Descriptor configuration = Jenkins.getInstance().getDescriptor(SematextGlobalConfiguration.class);
    return (SematextGlobalConfiguration) configuration;
  }

  @RequirePOST
  public FormValidation doTestMetricsReceiverUrl(@QueryParameter("metricsReceiverUrl") final String metricsReceiverUrl,
      @QueryParameter("dataHouse") final String dataHouse) {
    Jenkins.get().checkPermission(Jenkins.ADMINISTER);
    try {
      String url = buildMetricsReceiverUrl(metricsReceiverUrl, dataHouse);
      boolean urlCheckResult = SematextHttpClient.checkHealth(url);
      if (urlCheckResult) {
        return FormValidation.ok("Great! Your URL is valid.");
      } else {
        return FormValidation.error("Oops! Your URL does not seem to be valid.");
      }
    } catch (IllegalStateException e) {
      return FormValidation.error(e.getMessage());
    }
  }

  @RequirePOST
  public FormValidation doTest(@QueryParameter("metricsReceiverUrl") final String metricsReceiverUrl,
      @QueryParameter("metricsToken") final String metricsToken, @QueryParameter("dataHouse") final String dataHouse) {
    Jenkins.get().checkPermission(Jenkins.ADMINISTER);
    if (metricsToken.length() != 36) {
      return FormValidation.error("Your TOKEN must have 36 characters.");
    }

    FormValidation metricsReceiverTestResult = doTestMetricsReceiverUrl(metricsReceiverUrl, dataHouse);
    if (metricsReceiverTestResult.kind == FormValidation.Kind.ERROR) {
      return metricsReceiverTestResult;
    }

    try {
      String metricsReceiver = buildMetricsReceiverUrl(metricsReceiverUrl, dataHouse);

      boolean tokenCheckResult = SematextHttpClient.newInstance(metricsReceiver, metricsToken)
          .postMetrics(null, Collections.singletonMap(Metrics.TOKEN_CHECK.getKey(), 1));

      if (tokenCheckResult) {
        return FormValidation.ok("Great! Your TOKEN is valid.");
      } else {
        return FormValidation.error("Your TOKEN does not seem to be valid.");
      }
    } catch (IllegalStateException e) {
      return FormValidation.error(e.getMessage());
    }
  }

  @Override
  public String getDisplayName() {
    return DISPLAY_NAME;
  }

  @Override
  public boolean configure(final StaplerRequest req, final JSONObject formData) throws FormException {
    try {
      if (!super.configure(req, formData)) {
        return false;
      }

      String dataHouse = formData.getString(DATA_HOUSE);
      String metricsReceiverUrl = formData.getString(METRICS_RECEIVER_URL);
      String metricsToken = formData.getString(METRICS_TOKEN);
      String metricsReceiver = buildMetricsReceiverUrl(metricsReceiverUrl, dataHouse);
      SematextHttpClient.initInstance(metricsReceiver, metricsToken);

      save();
      return true;
    } catch (Exception e) {
      // Intercept all FormException instances.
      if (e instanceof Descriptor.FormException) {
        throw (FormException) e;
      }

      LogUtils.severe(logger, e, null);
      return false;
    }
  }

  public String buildMetricsReceiverUrl() {
    return buildMetricsReceiverUrl(metricsReceiverUrl, dataHouse);
  }

  private String buildMetricsReceiverUrl(String metricsReceiverUrl, String dataHouse) {
    if (dataHouse.equals("US")) {
      return METRICS_RECEIVER_US_URL;
    }
    if (dataHouse.equals("EU")) {
      return METRICS_RECEIVER_EU_URL;
    }
    if (dataHouse.equals("CUSTOM")) {
      if (metricsReceiverUrl.length() == 0) {
        throw new IllegalStateException("Please add a custom URL...");
      }
      return metricsReceiverUrl;
    }
    throw new IllegalStateException("Select any valid Sematext Region or input a custom URL...");
  }

  public Secret getMetricsToken() {
    return metricsToken;
  }

  @DataBoundSetter
  public void setMetricsToken(Secret metricsToken) {
    this.metricsToken = metricsToken;
  }

  public String getMetricsReceiverUrl() {
    return metricsReceiverUrl;
  }

  @DataBoundSetter
  public void setMetricsReceiverUrl(String metricsReceiverUrl) {
    this.metricsReceiverUrl = metricsReceiverUrl;
  }

  public String getDataHouse() {
    return dataHouse;
  }

  @DataBoundSetter
  public void setDataHouse(String dataHouse) {
    this.dataHouse = dataHouse;
  }
}
