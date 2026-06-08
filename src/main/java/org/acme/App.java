package org.acme;

import java.util.List;

import org.acme.gitlab.GitLabApiException;
import org.acme.gitlab.GitlabSchemas.FileCommitResponse;
import org.acme.gitlab.GitlabService;
import org.acme.graph.GraphService;
import org.acme.graph.GraphService.Sharepoint.FileInfo;
import org.acme.powerautomate.PowerAutomateEmailData;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jsoup.Jsoup;

@QuarkusMain
public class App implements QuarkusApplication {
  private static final Logger LOG = Logger.getLogger(App.class);
  @Inject
  GitlabService gitlab;

  @Inject
  GraphService.Sharepoint sharepoint;

  @Override
  public int run(String... args) throws Exception {
    List<PowerAutomateEmailData> powerAutomateEmailData = sharepoint.getFiles();
    powerAutomateEmailData.forEach(emailData -> {
      try {
        gitlab.filesCreateFiles(emailData);
      } catch (GitLabApiException e) {
        LOG.error("Error creating file: " + "Status " + e.getStatus() + " Message: " + e.getMessage());
      }
    });
    return 0;
  }
}
