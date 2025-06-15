package com.microservices.request;

import java.util.Map;

public class Request<T> {
  private final String method;
  private final String path;
  private final Map<String, String> params;
  private final T body;
  private final String className;
  
  public Request(String method, String path, Map<String, String> params, T body, String className) {
    this.method = method;
    this.path = path;
    this.params = params;
    this.body = body;
    this.className = className;
  }
  
  public String getMethod() { return method; }
  public String getPath() { return path; }
  public Map<String, String> getParams() { return params; }
  public T getBody() { return body; }
  public String getClassName() { return className; }
}
