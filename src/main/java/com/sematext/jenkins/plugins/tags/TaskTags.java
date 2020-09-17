package com.sematext.jenkins.plugins.tags;

import com.sematext.jenkins.plugins.utils.TagUtils;
import hudson.model.Queue;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class TaskTags extends BaseTags {
  private String taskName;

  public TaskTags(Queue.Task task) throws IOException, InterruptedException {
    if (task == null) {
      return;
    }

    setTaskName(task.getName());
  }

  public String getTaskName() {
    return taskName;
  }

  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  public Map<String, String> asTags() {
    Map<String, String> tags = super.asTags();
    TagUtils.addTag(tags, "jenkins.task.name", getTaskName());
    return tags;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;
    TaskTags taskTags = (TaskTags) o;
    return Objects.equals(taskName, taskTags.taskName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), taskName);
  }
}
