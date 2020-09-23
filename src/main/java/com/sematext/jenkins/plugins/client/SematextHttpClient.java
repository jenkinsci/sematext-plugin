package com.sematext.jenkins.plugins.client;

import com.sematext.jenkins.plugins.SematextGlobalConfiguration;
import com.sematext.jenkins.plugins.metrics.Metrics;
import com.sematext.jenkins.plugins.utils.LogUtils;
import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SematextHttpClient {
  private static final String METRICS_RECEIVER_HEALTH_ENDPOINT = "health";
  private static final String METRICS_RECEIVER_ENDPOINT = "write?db=metrics";
  private static final String METRICS_RECEIVER_META_INFO_ENDPOINT = "write?db=metainfo";
  private static final int TIMEOUT_MS = 60 * 1000;
  public static final String LINE_DELIMITER = "\n";
  private static SematextHttpClient instance = null;
  private static final Logger logger = Logger.getLogger(SematextHttpClient.class.getName());

  private String metricsReceiverUrl;
  private String metricsToken;
  private boolean connectionValid = false;

  private void init(String metricsReceiverUrl, String metricsToken) {
    this.metricsReceiverUrl = metricsReceiverUrl;
    this.metricsToken = metricsToken;
    this.connectionValid = checkHealth(metricsReceiverUrl);
  }

  public static SematextHttpClient getInstance() {
    // no need to use lazy double check, 2 initialization is ok too
    if (instance == null) {
      SematextGlobalConfiguration configuration = SematextGlobalConfiguration.get();
      instance = newInstance(configuration.buildMetricsReceiverUrl(), configuration.getMetricsToken().getEncryptedValue());
    }

    return instance;
  }

  public static void initInstance(String metricsReceiverUrl, String metricsToken) {
    instance = newInstance(metricsReceiverUrl, metricsToken);
  }

  public static SematextHttpClient newInstance(String metricsReceiverUrl, String metricsToken) {
    SematextHttpClient instance = new SematextHttpClient();
    instance.init(metricsReceiverUrl, metricsToken);
    return instance;
  }

  public boolean postMetaInfo(List<Metrics> metrics) {
    if (!connectionValid) {
      logger.severe("Your client is not initialized properly");
      return false;
    }

    List<String> metaInfoLines = metrics.stream()
        .map(m -> buildMetricsMetaInfoLine(metricsToken, m.asTags(), m.getKey())).collect(Collectors.toList());

    return post(buildEndpoint(metricsReceiverUrl, METRICS_RECEIVER_META_INFO_ENDPOINT),
        String.join(LINE_DELIMITER, metaInfoLines));
  }

  public boolean postMetrics(Map<String, String> tags, Map<String, Object> metrics) {
    if (!connectionValid) {
      logger.severe("Your client is not initialized properly");
      return false;
    }

    String payload = buildMetricsLine(metricsToken, tags, metrics);

    return post(buildEndpoint(metricsReceiverUrl, METRICS_RECEIVER_ENDPOINT), payload);
  }

  public static boolean checkHealth(String url) {
    return get(buildEndpoint(url, METRICS_RECEIVER_HEALTH_ENDPOINT), null);
  }

  public static boolean get(String url, String payload) {
    return send(url, payload, "GET");
  }

  public static boolean post(String url) {
    return post(url, null);
  }

  public static boolean post(String url, String payload) {
    return send(url, payload, "POST");
  }

  private static boolean send(String url, String payload, String method) {
    HttpURLConnection conn = null;
    boolean status = true;

    try {
      logger.fine("Setting up HttpURLConnection...");
      conn = getHttpURLConnection(new URL(url));
      conn.setRequestMethod(method);
      conn.setRequestProperty("Content-Type", "text/plain");
      conn.setUseCaches(false);
      conn.setDoInput(true);
      conn.setDoOutput(true);

      if (payload != null) {
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
        logger.fine("Writing to OutputStreamWriter...");
        wr.write(payload);
        wr.close();
      }

      // Get response
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
      rd.close();
      if (conn.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT ||
          conn.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED ||
          conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
        logger.fine(String.format("API call with payload '%s' was sent successfully!", payload));
      } else {
        logger.severe(String.format("API call with payload '%s' failed!", payload));
        status = false;
      }
    } catch (Exception e) {
      try {
        if (conn != null && conn.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN) {
          logger.severe("Your API key may be invalid. We received a 403 error.");
          LogUtils.severe(logger, e, null);
        } else {
          LogUtils.severe(logger, e, "Unknown client error, please check your config");
        }
      } catch (IOException ex) {
        LogUtils.severe(logger, e, null);
      }
      status = false;
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return status;
  }

  private static String buildEndpoint(String url, String endpoint) {
    return url + "/" + endpoint;
  }

  private static String buildMetricsLine(String token, Map<String, String> tags, Map<String, Object> metrics) {
    Map<String, String> fullTags = tags != null ? tags : new HashMap<>();
    fullTags.put("token", token);
    return String.format("jenkins,%s %s", toString(fullTags), toString(metrics));
  }

  private static String buildMetricsMetaInfoLine(String token, Map<String, String> tags, String metricKey) {
    Map<String, String> fullTags = tags != null ? tags : new HashMap<>();
    fullTags.put("token", token);
    return String.format("jenkins,%s %s", toString(fullTags), metricKey);
  }

  private static String toString(Map<String, ? extends Object> tags) {
    return tags == null ? "" : tags.entrySet().stream().map(t -> t.getKey() + "=" + t.getValue()).collect(
        Collectors.joining(","));
  }

  private static HttpURLConnection getHttpURLConnection(final URL url) throws IOException {
    HttpURLConnection conn = null;
    ProxyConfiguration proxyConfig = null;

    Jenkins jenkins = Jenkins.getInstance();
    if (jenkins != null) {
      proxyConfig = jenkins.proxy;
    }

    if (proxyConfig != null) {
      Proxy proxy = proxyConfig.createProxy(url.getHost());
      if (proxy != null && proxy.type() == Proxy.Type.HTTP) {
        logger.fine("Attempting to use the Jenkins proxy configuration");
        conn = (HttpURLConnection) url.openConnection(proxy);
      }
    } else {
      logger.fine("Jenkins proxy configuration not found");
    }

    if (conn == null) {
      conn = (HttpURLConnection) url.openConnection();
      logger.fine("Using HttpURLConnection, without proxy");
    }

    conn.setConnectTimeout(TIMEOUT_MS);
    conn.setReadTimeout(TIMEOUT_MS);

    return conn;
  }
}
