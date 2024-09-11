package com.nachtstern_build.trebleupdater;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import android.content.Context;
import android.database.Cursor;
import android.provider.OpenableColumns;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private Uri selectedFileUri;
    private String selectedImageType;
    private CheckBox clearUserDataCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView selectedFileTextView = findViewById(R.id.selected_file_textview);
        Spinner imageTypeSpinner = findViewById(R.id.image_type_spinner);
        Button selectFileButton = findViewById(R.id.select_file_button);
        Button flashButton = findViewById(R.id.flash_button);
        clearUserDataCheckbox = findViewById(R.id.clear_user_data_checkbox);

        // Setup spinner for image type selection
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.image_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imageTypeSpinner.setAdapter(adapter);

        imageTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedImageType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedImageType = "boot"; // Default type
            }
        });

        selectFileButton.setOnClickListener(v -> {
            // Open file picker
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*"); // Allows all file types, filter later if needed
            startActivityForResult(Intent.createChooser(intent, "Wähle eine Datei"), PICK_FILE_REQUEST);
        });

        flashButton.setOnClickListener(v -> {
            if (selectedFileUri != null && selectedImageType != null) {
                // Convert URI to file path
                String filePath = FileUtils.getPath(this, selectedFileUri);
                if (filePath != null) {
                    // Determine if user wants to clear data
                    String clearUserData = clearUserDataCheckbox.isChecked() ? "clear_user_data" : "no_action";
                    // Run script with selected file, type, and clear data option
                    runScript(filePath, selectedImageType, clearUserData);
                } else {
                    Toast.makeText(this, "Fehler: Dateipfad ungültig.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Bitte eine Datei und einen Image-Typ auswählen.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                // Display the selected file path
                TextView selectedFileTextView = findViewById(R.id.selected_file_textview);
                selectedFileTextView.setText(selectedFileUri.getPath());
            }
        }
    }

    private void runScript(String filePath, String imageType, String clearUserData) {
        // Build the command with parameters
        String command = "/system/bin/slot_changer.sh " + filePath + " " + imageType + " " + clearUserData;
        try {
            // Execute the command with superuser privileges
            Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            Toast.makeText(this, "Befehl ausgeführt: " + command, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Fehler beim Ausführen des Skripts: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    // FileUtils-Klasse als innere Klasse
    public static class FileUtils {
        public static String getPath(Context context, Uri uri) {
            String result = null;
            if ("content".equals(uri.getScheme())) {
                try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (index >= 0) {
                            result = cursor.getString(index);
                        }
                    }
                }
            } else {
                result = uri.getPath();
            }
            return result;
        }
    }
}
