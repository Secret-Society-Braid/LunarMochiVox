package braid.society.secret.lunarmochivox.voice;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoxAudioHandler implements AudioSendHandler {

  private byte[] out;
  private int position;
  private ByteBuffer lastFrame;
  private boolean isSpeaking;
  private final VOICEVOXTtsEngine engine;
  private static final Logger log = LoggerFactory.getLogger(VoxAudioHandler.class);
  private final Lock lock = new ReentrantLock();
  private final Condition speakingCondition = lock.newCondition();

  public VoxAudioHandler(VOICEVOXTtsEngine engine) {
    this.engine = engine;
  }

  public VoxAudioHandler(String webApiHost, int webApiPort) {
    this(VOICEVOXTtsEngine.getInstance(webApiHost, webApiPort));
  }

  void setSpeaking(boolean speaking) {
    lock.lock();
    try {
      isSpeaking = speaking;
      speakingCondition.signalAll();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean canProvide() {
    boolean canProvide = position < out.length;

    if(canProvide) {
      lastFrame = ByteBuffer.wrap(out, position, engine.getAudioFrame());
      position += engine.getAudioFrame();

      if(position >= out.length) {
        log.info("Bot may finish speaking.");
        setSpeaking(false);
      }
    }
    return canProvide;
  }

  public void speak(String phrase, User author) throws IOException {
    lock.lock();
    try {
      while(!this.isSpeaking) {
        speakingCondition.await();
      }
    } catch (InterruptedException e) {
      log.error("Someone interrupted me while waiting for speaking condition", e);
    } finally {
      lock.unlock();
    }
    setSpeaking(true);
    this.position = Integer.MAX_VALUE;
    this.out = engine.say(phrase, author);
    this.position = 0;
  }

  @Nullable
  @Override
  public ByteBuffer provide20MsAudio() {
    return this.lastFrame;
  }
}
