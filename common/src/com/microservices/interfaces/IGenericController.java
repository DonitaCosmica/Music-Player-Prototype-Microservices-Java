package com.microservices.interfaces;

import com.microservices.response.ApiResponse;
import java.util.List;
import java.util.Map;

public interface IGenericController<T> {
  ApiResponse<List<T>> getAll();
  ApiResponse<T> getById(String id);
  ApiResponse<List<T>> getByParams(Map<String, String> params);
  ApiResponse<Void> create(T entity);
  ApiResponse<Void> update(T entity);
  ApiResponse<Void> delete(String id);
}
