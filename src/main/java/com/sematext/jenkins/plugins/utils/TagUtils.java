package com.sematext.jenkins.plugins.utils;

import java.util.Map;

public final class TagUtils {

  public static void addTag(Map<String, String> tags, String key, String value) {
    if (value != null && key != null) {
      tags.put(key, value);
    }
  }
}
