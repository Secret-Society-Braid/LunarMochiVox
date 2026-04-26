# LunarMochiVox

日本語版は[こちら](./docs/README_ja.md)。

LunarMochiVox is a bot application for Discord, enabling text-chatters in voice channels to
communicate with each other using text-to-speech (TTS) technology.

It provides a simple, yet powerful interface for users to interact with the bot.

## Features

### Text-to-speech (TTS)

Have you ever encountered a situation where you can't use your microphone but your friends are in
the voice channel?

With LunarMochiVox, It is now possible to communicate with your friends using text-to-speech!

Join a voice channel, then type `/vc` or use application command in the text channel that you
want to use for chatting, and the bot will read your messages aloud in the voice channel!

LunarMochiVox uses `VOICEVOX` as the default TTS engine, which provides middle-range quality speech.

...Of course, it's free!

## Contributing

Contributions are welcome!
Please see [CONTRIBUTING.md](./CONTRIBUTING.md) for detailed information.

This section is simple build instructions.

1. clone this repository.
2. change the codes that you desire.
3. execute `./gradlew shadowJar` to build the application.
4. the built application is located in `build/libs/LunarMochiVox.jar`.

> !Note: This application does not provide executable files. only `executable jar` will be built.
> You will need to set up Java 25 or higher to run this application.

It's simple, isn't it?

If you have any questions regarding this application, please feel free to ask in the
[discussion section]() of this repository.

### Using Docker and Docker Compose

experienced with Docker and Docker Compose? then we have good news for you!
You can use the `Dockerfile` to build and run the application.

Also, if you use `docker-compose.yml`, It will automatically set up Text-to-speech (TTS) engine!
You can start the application by simply executing `docker-compose up -d`.

Of course, if you have any questions regarding this application, please feel free to ask!

### Issues, Questions, and Suggestions

Issuing tickets or asking questions is also a great way to contribute to this project!

If you:

- have encountered a bug or feel inconvenient with the application?
- have a question regarding the application?
- come up with a nice idea for the application?

All of these are welcome!

- Report issues or bugs in
  the [issue section](https://github.com/Secret-Society-Braid/LunarMochiVox/issues) and select a
  template for the bugs.
- Ask questions in
  the [discussion section](https://github.com/Secret-Society-Braid/LunarMochiVox/discussions).
- Suggest improvements or fresh ideas in
  the [issue section](https://github.com/Secret-Society-Braid/LunarMochiVox/issues) and select a
  template for the feature request
  or [discussion section](https://github.com/Secret-Society-Braid/LunarMochiVox/discussions) to post
  your thoughts.

## License

This project is licensed under

## Open Source Licenses

This project is made possible by a lot of open source projects.

Please visit [Open Source Licenses](./docs/clauses/OPEN_SOURCE_LICENSES.md) for more information,
or simply run `licenses` application command in the Discord text channel.
