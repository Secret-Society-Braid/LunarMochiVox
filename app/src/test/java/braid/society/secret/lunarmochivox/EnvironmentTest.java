package braid.society.secret.lunarmochivox;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class EnvironmentTest {

  @Test
  void testJunit() {
    final String actual = "Hello";
    assertEquals(actual, "Hello");
  }

  @Test
  void testTruth() {
    final String actual = "Hello";
    assertThat(actual).isEqualTo("Hello");
  }
}
