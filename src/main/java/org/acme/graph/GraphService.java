package org.acme.graph;

import java.io.IOException;
import java.io.InputStream;
import org.jboss.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import org.acme.powerautomate.PowerAutomateEmailData;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.graph.models.Drive;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.DriveItemCollectionResponse;
import com.microsoft.graph.models.Site;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

public class GraphService {

  @Dependent
  public static class Sharepoint {
    private static final Logger LOG = Logger.getLogger(Sharepoint.class);
    @Inject
    GraphServiceClient graph;
    @Inject
    ObjectMapper objectMapper;
    @ConfigProperty(name = "sharepoint.host-name")
    String hostName;
    @ConfigProperty(name = "sharepoint.site-name")
    String siteName;
    @ConfigProperty(name = "sharepoint.target-dir")
    String targetDir;

    public List<PowerAutomateEmailData> getFiles() {
      final List<PowerAutomateEmailData> powerAutomateEmailData = new ArrayList<>();
      final String sitePathBase = hostName + ":/sites/"; // contoso.sharepoint.com:/sites/marketing
      final String sitePath = sitePathBase + siteName;
      Site site = graph.sites().bySiteId(sitePath).get();

      // 2. Get the default document library (drive) for that site
      Drive drive = graph.sites().bySiteId(site.getId()).drive().get();
      String driveId = drive.getId();

      // 3. List children of the folder addressed by its path
      DriveItemCollectionResponse response = graph.drives().byDriveId(drive.getId()).items()
          .byDriveItemId("root:/" + targetDir + ":").children().get();

      for (DriveItem item : response.getValue()) {
        String itemId = item.getId();
        if (item.getFolder() != null) {
          continue;
        }
        try (InputStream in = graph.drives().byDriveId(driveId).items().byDriveItemId(itemId).content().get()) {
          PowerAutomateEmailData emailData = objectMapper.readValue(in, PowerAutomateEmailData.class);
          emailData.setFileName(item.getName());
          powerAutomateEmailData.add(emailData);
        } catch (IOException e) {
          LOG.error(e.getStackTrace());
        }
      }
      // return fileInfos
      return powerAutomateEmailData;
    }


    public record FileInfo(String content, String name) {
    }
  }

}
