package com.sematext.jenkins.plugins.tags;

import hudson.EnvVars;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.LogTaskListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseTags {
  private static final Logger LOGGER = Logger.getLogger(BaseTags.class.getName());
  private static final Integer MAX_HOSTNAME_LEN = 255;

  private String host;

  public BaseTags() throws IOException, InterruptedException {
    this(null, null);
  }

  public BaseTags(Run run, TaskListener listener) throws IOException, InterruptedException {
    EnvVars envVars = null;
    if (run != null) {
      if (listener != null) {
        envVars = run.getEnvironment(listener);
      } else {
        envVars = run.getEnvironment(new LogTaskListener(LOGGER, Level.INFO));
      }
    }

    setHost(getHost(envVars));
  }

  public static String getHost(EnvVars envVars) {
    String[] UNIX_OS = { "mac", "linux", "freebsd", "sunos" };

    String host = null;
    if (envVars != null) {
      host = envVars.get("HOSTNAME");
    }
    if (isValidHostname(host)) {
      return host;
    }

    String os = getOS();
    if (Arrays.asList(UNIX_OS).contains(os)) {
      // Attempt to grab unix hostname
      try {
        String[] cmd = { "/bin/hostname", "-f" };
        Process proc = Runtime.getRuntime().exec(cmd);
        InputStream in = proc.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          out.append(line);
        }
        reader.close();

        host = out.toString();
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, e, null);
      }

      if (isValidHostname(host)) {
        return host;
      }
    }

    try {
      host = Inet4Address.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      //nothing
    }
    if (isValidHostname(host)) {
      return host;
    }

    // Never found the hostname
    if (host == null || "".equals(host)) {
      LOGGER.warning("Unable to find host name.");
    }

    return null;
  }

  private static String getOS() {
    String out = System.getProperty("os.name");
    String os = out.split(" ")[0];
    return os.toLowerCase();
  }

  private static Boolean isValidHostname(String hostname) {
    if (hostname == null) {
      return false;
    }

    String[] localHosts = { "localhost", "localhost.localdomain",
        "localhost6.localdomain6", "ip6-localhost" };
    String VALID_HOSTNAME_RFC_1123_PATTERN = "^(([a-zA-Z0-9]|"
        + "[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*"
        + "([A-Za-z0-9]|"
        + "[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";
    String host = hostname.toLowerCase();

    if (Arrays.asList(localHosts).contains(host)) {
      return false;
    }

    if (hostname.length() > MAX_HOSTNAME_LEN) {
      return false;
    }

    Pattern r = Pattern.compile(VALID_HOSTNAME_RFC_1123_PATTERN);
    Matcher m = r.matcher(hostname);

    return m.find();
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Map<String, String> asTags() {
    if (getHost() != null) {
      return Stream.of(
          new AbstractMap.SimpleEntry<>("os.host", getHost()))
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    } else {
      return new HashMap<>();
    }
  }

  public void addTag(Map<String, String> tags, String key, String value) {
    if (value != null && key != null) {
      tags.put(key, value);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    BaseTags baseTags = (BaseTags) o;
    return Objects.equals(host, baseTags.host);
  }

  @Override
  public int hashCode() {
    return Objects.hash(host);
  }
}
