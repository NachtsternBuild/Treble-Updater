#!/system/bin/sh

# get the active slot
get_current_slot() {
    getprop ro.boot.slot_suffix
}

# get the other slot
get_other_slot() {
    local current_slot
    current_slot=$(get_current_slot)
    if [ "$current_slot" = "_a" ]; then
        echo "_b"
    else
        echo "_a"
    fi
}

# get the inactive slot
inactive_slot=$(get_other_slot)
echo "Inaktiver Slot: $inactive_slot"

# check if everything is okay
if [ "$#" -ne 3 ]; then
    echo "Fehler: Ungültige Anzahl von Argumenten."
    echo "Verwendung: $0 <Image-Pfad> <Image-Typ> <Clear User Data Option>"
    exit 1
fi

# read the parameters
image_path=$1
image_type=$2
clear_user_data_option=$3

# set the target partition based on the image type
case $image_type in
    boot) target_partition="boot_$inactive_slot";;
    vendor) target_partition="vendor_$inactive_slot";;
    system) target_partition="system_$inactive_slot";;
    *) echo "Ungültiger Image-Typ"; exit 1;;
esac

# check if the image exists
if [ ! -f "$image_path" ]; then
    echo "Bilddatei nicht gefunden: $image_path"
    exit 1
fi

# flash the image
echo "Flashe $image_path in $target_partition..."
dd if="$image_path" of="/dev/block/by-name/$target_partition" bs=4M || { echo "Fehler beim Flashen"; exit 1; }

# set the new slot
echo "Wechsle zu Slot: $inactive_slot"
update_engine_client --switch-slot="$inactive_slot" || { echo "Fehler beim Slot-Wechsel"; exit 1; }

# clear user data if the option is set
if [ "$clear_user_data_option" = "clear_user_data" ]; then
    echo "Lösche Benutzerdaten durch Formatierung der Datenpartition..."
    mkfs.ext4 /dev/block/by-name/userdata || { echo "Fehler beim Löschen der Benutzerdaten"; exit 1; }
fi

# reboot the system
echo "Das Gerät wird neu gestartet..."
reboot
