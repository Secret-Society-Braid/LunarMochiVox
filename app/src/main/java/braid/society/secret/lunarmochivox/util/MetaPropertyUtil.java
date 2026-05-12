package braid.society.secret.lunarmochivox.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class MetaPropertyUtil {

  private MetaPropertyUtil() {}

  public static String getDevUserId() {
    try {
      Properties prop = Objects.requireNonNull(getMetaProperties());
      String devUserId = prop.getProperty("DEV_USER", null);
      if (devUserId == null || devUserId.isBlank()) {
        return null;
      }
      return devUserId;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Properties getMetaProperties() throws IOException {
    Properties prop = new Properties();
    try (InputStream input = MetaPropertyUtil.class.getClassLoader()
      .getResourceAsStream("meta.properties")) {
      if (input == null) {
        return null;
      }
      prop.load(input);
    }
    return prop;
  }
}
