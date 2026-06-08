package org.acme.gitlab;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v4/projects/{project_id}")
@RegisterRestClient(configKey = "gitlab-api")
@RegisterProvider(GitLabExceptionMapper.class)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ClientHeaderParam(name="PRIVATE-TOKEN", value="${gitlab.token}")
public interface IGitLabService {

  @POST
  @Path("/repository/files/{file_path}")
  RestResponse<GitlabSchemas.FileCommitResponse> filesCreateFile(
      @PathParam("project_id") String projectId,
      @PathParam("file_path") String filePath,
      GitlabSchemas.CommitFilePayload payload
  ) throws GitLabApiException;

  @PUT
  @Path("/repository/files/{branch_name}")
  RestResponse<GitlabSchemas.FileCommitResponse> filesUpdateFile(
      @PathParam("project_id") String projectId,
      @PathParam("branch_name") String branchName,
      GitlabSchemas.CommitFilePayload payload
  );
  
  @GET
  @Path("/repository/branches/{branch_name}")
  RestResponse<GitlabSchemas.GetBranchResponse> branchesGetBranch(
      @PathParam("project_id") String projectId,
      @PathParam("branch_name")String branchName
  );

  @POST
  @Path("/issues")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  RestResponse<GitlabSchemas.Issue> issuesCreateIssue(@PathParam("project_id") String projectId, GitlabSchemas.IssueRequest request);
}
