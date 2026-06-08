package org.acme.powerautomate;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PowerAutomateEmailData {
  private String id;
  private String receivedDateTime;
  private String hasAttachments;
  private String internetMessageId;
  private String subject;
  private String importance;
  private String conversationId;
  private Boolean isRead;
  private List<InternetMessageHeader> internetMessageHeaders;
  private boolean isHtml;
  private String body;
  private String from;
  private String toRecipients;
  private List<Attachment> attachments;
  private String fileName;
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getReceivedDateTime() {
    return receivedDateTime;
  }
  public void setReceivedDateTime(String receivedDateTime) {
    this.receivedDateTime = receivedDateTime;
  }
  public String getHasAttachments() {
    return hasAttachments;
  }
  public void setHasAttachments(String hasAttachments) {
    this.hasAttachments = hasAttachments;
  }
  public String getInternetMessageId() {
    return internetMessageId;
  }
  public void setInternetMessageId(String internetMessageId) {
    this.internetMessageId = internetMessageId;
  }
  public String getSubject() {
    return subject;
  }
  public void setSubject(String subject) {
    this.subject = subject;
  }
  public String getImportance() {
    return importance;
  }
  public void setImportance(String importance) {
    this.importance = importance;
  }
  public String getConversationId() {
    return conversationId;
  }
  public void setConversationId(String conversationId) {
    this.conversationId = conversationId;
  }
  public Boolean getIsRead() {
    return isRead;
  }
  public void setIsRead(Boolean isRead) {
    this.isRead = isRead;
  }
  public List<InternetMessageHeader> getInternetMessageHeaders() {
    return internetMessageHeaders;
  }
  public void setInternetMessageHeaders(List<InternetMessageHeader> internetMessageHeaders) {
    this.internetMessageHeaders = internetMessageHeaders;
  }
  public boolean isHtml() {
    return isHtml;
  }
  public void setHtml(boolean isHtml) {
    this.isHtml = isHtml;
  }
  public String getBody() {
    return body;
  }
  public void setBody(String body) {
    this.body = body;
  }
  public String getFrom() {
    return from;
  }
  public void setFrom(String from) {
    this.from = from;
  }
  public String getToRecipients() {
    return toRecipients;
  }
  public void setToRecipients(String toRecipients) {
    this.toRecipients = toRecipients;
  }
  public List<Attachment> getAttachments() {
    return attachments;
  }
  public void setAttachments(List<Attachment> attachments) {
    this.attachments = attachments;
  }
  public String getFileName() {
    return fileName;
  }
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
  
}
