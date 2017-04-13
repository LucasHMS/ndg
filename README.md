# ndg
A frequent problem when you want to record a conference call is that you depend on the quality of the audio transmited to the person who is recording the call. We know for a fact, that Skype or Hangouts are not good at all on delivering good audio, even on good connections. This tool was developed to attack this. Each peer on the call can record your own audio in a way that it is synchronized with the others, thus the person who edit it all together doesn't need to worry about the multiple tracks beeing out of sync.

## Functioning principle
You start the server on a remote machine with a valid IP (IP placed on the config.properties) with `java -jar server.jar n` with `n` being the number of people that will be on the call. Then each client must start the aplication, selects the path to store the audio file and hit record. Now the server waits for n connections before it send's a menssage to all clients start the audio capturing.
