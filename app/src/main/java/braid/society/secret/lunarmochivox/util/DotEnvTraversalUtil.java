package braid.society.secret.lunarmochivox.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public class DotEnvTraversalUtil {

  public static Optional<String> getFromDotEnvFile(String key, Path dotEnvFilePath) {
    try (Stream<String> lines = Files.lines(dotEnvFilePath)) {
      return lines
        .map(String::trim)
        .filter(line -> !line.startsWith("#") && line.contains("="))
        .map(line -> line.split("=", 2))
        .filter(parts -> parts.length == 2 && parts[0].trim().equalsIgnoreCase(key))
        .map(parts -> parts[1].trim())
        .findFirst();
    } catch (IOException e) {
      return Optional.empty();
    }
  }
}
