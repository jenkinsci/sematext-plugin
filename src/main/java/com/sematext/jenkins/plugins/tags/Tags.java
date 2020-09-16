/*
 * Copyright (c) Sematext Group, Inc.
 * All Rights Reserved
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Sematext Group, Inc.
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 *
 */
package com.sematext.jenkins.plugins.tags;

public enum Tags {
  OS_HOST("os.host"),
  JOB_NAME("jenkins.job.name"),
  REPO_NAME("jenkins.repo.name"),
  BRANCH_NAME("jenkins.branch.name"),
  TASK_NAME("jenkins.task.name");
  private final String key;

  public String getKey() {
    return key;
  }

  Tags(String key) {
    this.key = key;
  }
}
