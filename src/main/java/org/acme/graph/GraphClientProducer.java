package org.acme.graph;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.azure.identity.ClientCertificateCredential;
import com.azure.identity.ClientCertificateCredentialBuilder;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

public class GraphClientProducer {

  @ConfigProperty(name = "graph.tenant-id")
  String tenantId;

  @ConfigProperty(name = "graph.client-id")
  String clientId;

  @ConfigProperty(name = "graph.certificate-base64")
  String certificateBase64;

  @ConfigProperty(name = "graph.certificate-password")
  String clientCertificatePassword;

  // client-credentials flow uses the .default scope
  private static final String[] SCOPES = {"https://graph.microsoft.com/.default"};

  @Produces
  @Singleton
  public GraphServiceClient graphServiceClient() {
    byte[] pfxBytes = Base64.getDecoder().decode(certificateBase64);

    ClientCertificateCredential tokenCredential = new ClientCertificateCredentialBuilder()
        .tenantId(tenantId)
        .clientId(clientId)
        .pfxCertificate(new ByteArrayInputStream(pfxBytes))
        .clientCertificatePassword(clientCertificatePassword)
        .build();
    return new GraphServiceClient(tokenCredential, SCOPES);
  }
}
