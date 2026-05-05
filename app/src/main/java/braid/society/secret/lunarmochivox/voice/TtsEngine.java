package braid.society.secret.lunarmochivox.voice;

import java.io.IOException;
import java.io.InterruptedIOException;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.User;

public interface TtsEngine {

  byte[] say(String phrase, User author) throws IOException;
}
