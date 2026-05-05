package braid.society.secret.lunarmochivox.voice;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record Speaker(String name, @JsonProperty("speaker_uuid") String speakerUuid,
                      List<Style> styles, String version,
                      @JsonProperty("supported_features") SupportedFeatures supportedFeatures) {

  public static class SupportedFeatures {

    @JsonProperty("permitted_synthesis_morphing")
    private final PermittedSynthesisMorphing permittedSynthesisMorphing;

    private SupportedFeatures(PermittedSynthesisMorphing permittedSynthesisMorphing) {
      this.permittedSynthesisMorphing = permittedSynthesisMorphing;
    }

    PermittedSynthesisMorphing getPermittedSynthesisMorphing() {
      return permittedSynthesisMorphing;
    }

    @SuppressWarnings("unused")
    enum PermittedSynthesisMorphing {
      ALL,
      SELF_ONLY,
      NOTHING
    }
  }

  public record Style(int id, String name, String type) {}
}
