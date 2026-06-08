package org.acme.gitlab;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jboss.logging.Logger;
import org.acme.gitlab.GitlabSchemas.CommitFilePayload;
import org.acme.gitlab.GitlabSchemas.GetBranchResponse;
import org.acme.gitlab.GitlabSchemas.Issue;
import org.acme.gitlab.GitlabSchemas.IssueRequest;
import org.acme.powerautomate.Attachment;
import org.acme.powerautomate.PowerAutomateEmailData;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jsoup.Jsoup;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped
public class GitlabService {
  private static final Logger LOG = Logger.getLogger(GitlabService.class);
  private final Pattern techSpecIdPattern = Pattern.compile("Tech Spec Id:\\s*(\\S+)");
  @RestClient
  @Inject
  IGitLabService gitlab;

  @Inject
  ObjectMapper objectMapper;

  @ConfigProperty(name = "gitlab.projectid")
  String projectId;

  @ConfigProperty(name = "gitlab.targetdir")
  String targetdir;

  @ConfigProperty(name = "gitlab.timeout")
  Integer timeout;


  public void filesCreateFiles(PowerAutomateEmailData powerAutomateEmailData)
      throws GitLabApiException {
    String branch = getTechSpecId(powerAutomateEmailData);
    String content = Base64.getEncoder().encodeToString(
        Jsoup.parse(powerAutomateEmailData.getBody()).toString().getBytes(StandardCharsets.UTF_8));
    String fileName = powerAutomateEmailData.getFileName();
    if (!doesBranchExist(branch)) {
      fileIssue("Tech Spec Id: " + branch + " does not have an existing branch.");
      return;
    }
    try {
      CommitFilePayload payload = createPayload(branch, content, fileName, "base64");
      gitlab.filesCreateFile(projectId, targetdir + "/" + powerAutomateEmailData.getFileName(),
          payload);
      if (!powerAutomateEmailData.getAttachments().isEmpty()) {
        for (Attachment attachment : powerAutomateEmailData.getAttachments()) {
          content = attachment.contentBytes();
          fileName = attachment.name();
          payload = createPayload(branch, content, fileName, "base64");
          gitlab.filesCreateFile(projectId, targetdir + "/" + attachment.name(), payload);
        }
      }
    } catch (GitLabApiException e) {
      throw e;
    }
  }

  public GitlabSchemas.GetBranchResponse branchesGetBranch(String branchName)
      throws GitLabApiException {
    try (var resp = gitlab.branchesGetBranch(projectId, branchName)) {
      return resp.getEntity();
    } catch (GitLabApiException e) {
      switch (e.getStatus()) {
        case 400:
          LOG.error("Bad Request: " + e.getMessage());
        case 404:
          LOG.error("Branch not found");
        default:
          LOG.error("Unexpected error: Status " + e.getStatus() + "Message: " + e.getMessage());
      }
      throw e;
    }
  }

  private CommitFilePayload createPayload(String branch, String content, String fileName,
      String encoding) {
    String commitMessage = "Auto-commit: Syncing " + fileName + " from SharePoint";
    return new GitlabSchemas.CommitFilePayload(branch, content, commitMessage, encoding);
  }

  private String getTechSpecId(PowerAutomateEmailData powerAutomateEmailData) {
    String subject = powerAutomateEmailData.getSubject();
    String techSpecId = "";
    if (subject == null || subject.isBlank() || subject.isEmpty()) {
      return techSpecId;
    }
    try {
      Matcher m = techSpecIdPattern.matcher(subject);
      if (m.find()) {
        techSpecId = m.group(1);
      }
    } catch (Exception e) {
      LOG.error(e.getStackTrace());
    }
    return techSpecId;
  }

  private boolean doesBranchExist(String branchName) {
    try {
      GetBranchResponse getBranchResponse = branchesGetBranch(branchName);
      if (getBranchResponse != null
          && !(getBranchResponse.name().isBlank() && getBranchResponse.name().isEmpty())) {
        return true;
      }
    } catch (GitLabApiException e) {
      LOG.error(e.getMessage());
    }
    return false;
  }

  public void fileIssue(String message) {
    IssueRequest req = new IssueRequest(message, "Missing branch");
    try(var resp = gitlab.issuesCreateIssue(projectId, req)) {
      Issue created = resp.getEntity();
      LOG.info("Trying to create issue for project: " + projectId);
      LOG.info("Created: " + created.webUrl());
    } catch (GitLabApiException e) {
      throw e;
    }
  }
}
