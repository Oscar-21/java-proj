package org.acme.powerautomate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InternetMessageHeader(String name, String value) {}
