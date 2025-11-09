// com.lumiere.app.util.PublicUrlBuilder
package com.lumiere.app.utils;

import org.springframework.stereotype.Component;

@Component
public class PublicUrlBuilder {
  private final String base;

  public PublicUrlBuilder(@org.springframework.beans.factory.annotation.Value("${app.public-base-url}") String base) {
    this.base = base != null ? base.replaceAll("/+$", "") : "";
  }

  public String media(String name) {
    return base + "/api/media/" + name;
  }
}
