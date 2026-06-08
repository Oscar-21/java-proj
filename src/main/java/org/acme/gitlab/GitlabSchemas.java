package org.acme.gitlab;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GitlabSchemas {

  /**
   * RequestBody for
   * POST "/files/{branch_name}")
   * PUT "/files/{branch_name}")
   */
  public record CommitFilePayload(
    String branch,
    String content,
    @JsonProperty("commit_message")
    String commitMessage,
    String encoding
  ) {}


  /**
   * ResponseBody for
   * POST "/files/{branch_name}")
   * PUT "/files/{branch_name}")
   */
  public record FileCommitResponse(
    @JsonProperty("file_path")
    String filePath,
    String branch
  ) {}

  /**
   * ResponseBody for
   * POST "/branches/{branch_name}")
   * PUT "/branches/{branch_name}")
   */
  public record GetBranchResponse(
    String name,
    @JsonProperty("web_url") String webUrl,
    @JsonProperty("protected") boolean isProtected,
    @JsonProperty("can_push") boolean canPush,
    Commit commit,
    @JsonProperty("default") boolean isDefault,
    @JsonProperty("developers_can_merge") boolean developersCanMerge,
    @JsonProperty("developers_can_push") boolean developersCanPush,
    boolean merged
  ) {}

  public record Commit(
    String id,
    @JsonProperty("short_id")
    String shortId,
    String title,
    String message,
    @JsonProperty("web_url")
    String webUrl,
    @JsonProperty("author_email")
    String authorEmail,
    @JsonProperty("author_name")
    String authorName,
    @JsonProperty("authored_date")
    String authoredDate,
    @JsonProperty("committer_email")
    String committerEmail,
    @JsonProperty("committer_name")
    String committerName,
    @JsonProperty("committed_date")
    String committedDate,
    @JsonProperty("created_at")
    String createdAt,
    @JsonProperty("parent_ids")
    List<String> parentIds
  ) {}


  /**
   * RequestBody for
   * POST "/projects/{id}/issues"
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)  // don't send null optional fields
  public record IssueRequest(
    String title,                 // required
    String description,
    String labels,                // comma-separated, e.g. "bug,urgent"
    @JsonProperty("assignee_ids") List<Long> assigneeIds,
    @JsonProperty("milestone_id") Long milestoneId
  ) {
    public IssueRequest(String title, String description) {
      this(title, description, null, null, null);
    }
  }

  public record Issue(
    Long id,
    Long iid,                     // the per-project issue number
    String title,
    String state,
    @JsonProperty("web_url") String webUrl
  ) {}
}
