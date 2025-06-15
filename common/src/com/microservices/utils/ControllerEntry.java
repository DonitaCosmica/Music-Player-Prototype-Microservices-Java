package com.microservices.utils;

import com.microservices.interfaces.IGenericController;

public class ControllerEntry<T> {
  private final IGenericController<T> controller;
  private final Class<T> type;
  
  public ControllerEntry(IGenericController<T> controller, Class<T> type) {
    this.controller = controller;
    this.type = type;
  }

  public IGenericController<T> getController() {
    return controller;
  }

  public Class<T> getType() {
    return type;
  }
}
