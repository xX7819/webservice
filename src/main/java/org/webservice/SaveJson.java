package org.webservice;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.logging.Logger;

@Path("/save")
public class SaveJson {
  private static final Logger LOGGER = Logger.getLogger(SaveJson.class.getName());
  private static final String BASE_DIR = "saved-json";

  @Context
  SecurityContext securityContext;

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("App")
  public Response saveJson(String jsonPayload) {
    try {
      // Get username from auth context
      String username = securityContext.getUserPrincipal().getName();

      // Ensure user-specific directory exists
      String userDir = BASE_DIR + "/" + username;
      Files.createDirectories(Paths.get(userDir));

      // Filename with timestamp
      String filename = "json_" + LocalDateTime.now().toString().replace(":", "-") + ".json";

      // Save JSON
      try (FileWriter writer = new FileWriter(userDir + "/" + filename)) {
        writer.write(jsonPayload);
      }

      return Response.ok("Saved JSON for user '" + username + "' as " + filename).build();
    } catch (IOException e) {
      LOGGER.warning(Arrays.toString(e.getStackTrace()));
      return Response.serverError().entity("Failed to save JSON: " + e.getMessage()).build();
    }
  }
}
