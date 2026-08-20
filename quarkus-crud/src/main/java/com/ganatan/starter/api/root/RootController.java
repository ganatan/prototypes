package com.ganatan.starter.api.root;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class RootController {

  public record ApiInfo(
      String application,
      String status,
      String java
  ) {
  }

  @GET
  public Map<String, Object> root() {

    return Map.of(
        "application",
        "quarkus-starter",
        "status",
        "running",
        "java",
        System.getProperty(
            "java.version"
        )
    );

  }

  @GET
  @Path("info")
  public ApiInfo rootWithRecord() {

    return new ApiInfo(
        "quarkus-starter",
        "running",
        System.getProperty(
            "java.version"
        )
    );

  }

  @GET
  @Path("status")
  public Map<String, Object> rootWithHashMap() {

    Map<String, Object> response =
        new HashMap<>();

    response.put(
        "application",
        "quarkus-starter"
    );

    response.put(
        "status",
        "running"
    );

    response.put(
        "java",
        System.getProperty(
            "java.version"
        )
    );

    return response;

  }

}