#!/system/bin/sh

# Print Module Info
ui_print "*****************************************"
ui_print "   Treble Updater"
ui_print "   Version: 1.0"
ui_print "   @NachtsternBuild"
ui_print " https://github.com/NachtsternBuild/"
ui_print "*****************************************"

# Copy APK to /system/priv-app
ui_print "Copying APK to /system/priv-app..."
cp -f $MODPATH/system/priv-apps/treble_updater.apk /system/priv-app/treble_updater.apk

# Set permissions for the APK
ui_print "Set permissions..."
set_perm /system/priv-app/treble_updater.apk 0 0 0644
ui_print "***Installed APK***"

# Copy script to /system/bin
ui_print "Copying script to /system/bin..."
cp -f $MODPATH/system/bin/slot_changer.sh /system/bin/slot_changer.sh

# Set permissions for the script
ui_print "Set permissions..."
set_perm /system/bin/slot_changer.sh 0 0 0755

# Print success message
ui_print "Installation complete!"
