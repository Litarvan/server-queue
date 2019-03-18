# Server queue

[![Version](https://img.shields.io/github/release/Litarvan/server-queue.svg?style=flat-square)](https://github.com/Litarvan/server-queue/releases)
[![GitHub All Releases](https://img.shields.io/github/downloads/Litarvan/server-queue/total.svg?style=flat-square)](https://github.com/Litarvan/server-queue/releases)

A Minecraft server connection queue.

## Server

The server is the player queue. Player will connect to it using a Minecraft client which has been modded to implement the client API, and it will send back their position in the queue, and then when they can connect.

### Usage

Download the last server-queue-x.x.x.zip release from https://github.com/Litarvan/server-queue/releases, then

```
$ unzip server-queue-x.x.x.zip
$ cd server-queue-x.x.x/
$ bin/server-queue <port> <minecraftServerAddress> <minecraftServerPort>
```

### Building

```
$ ./gradlew distZip # Or just gradlew distZip on Windows
```

You can take server-queue-x.x.x.zip in build/distributions/

## Client

### Import

```
repositories {
    url {
        maven 'https://litarvan.github.io/maven'
    }
}

dependencies {
    implementation 'fr.litarvan:server-queue-client:1.0.0'
}
```

### Usage

```
QueueClient client = new QueueClient("127.0.0.1", 1234); // Puts your server-queue address and port

client.setOnPositionUpdate(position -> {
    System.out.println("Position updated : " + position);
});
client.setOnConnect(() -> {
    System.out.println("Done ! We can connect !");
    // Do whatever you want to connect
    
    // Connection is closed automatically after this callback
});

client.start(); // ou client.startInSeparatedThread();
```

### Building

```
$ ./gradlew build # Or just gradlew build on Windows
```

You can take server-queue-client-x.x.x.jar in queue-client/build/libs/