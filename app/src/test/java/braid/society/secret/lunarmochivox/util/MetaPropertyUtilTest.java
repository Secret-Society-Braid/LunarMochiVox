package braid.society.secret.lunarmochivox.util;

import com.google.common.truth.Truth;
import org.junit.jupiter.api.Test;

public class MetaPropertyUtilTest {

  @Test
  void testGetDevUser() {
    String actual = MetaPropertyUtil.getDevUserId();
    Truth.assertThat(actual).isEqualTo("idol_ranfa_master");
  }
}
