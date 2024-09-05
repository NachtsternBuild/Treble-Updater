# Build
## Build the APK
- install Android Studio
- configure Android Studio
- build and test the APK with Android Studio

## The Modul
- start the build.sh in the terminal and follow the instructions of the build script

```
TrebleUpdater/
├── common/
│   └── post-fs-data.sh          # after boot prozesses
├── install.sh                   # install skript
├── module.prop                  # the modul info
└── system/
    ├── bin/
    |    └── treble_updater.sh      # the main skript startet by the apk
    └── priv-apps/
          └── treble_updater.apk
```

