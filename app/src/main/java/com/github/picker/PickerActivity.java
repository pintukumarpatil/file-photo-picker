package com.github.picker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.picker.databinding.ActivityPickerBinding;
import com.github.pkp.FilePhotoPicker;
import com.github.pkp.FileUtils;
import com.github.pkp.FileHandler;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;


/**
 * @author Pintu Kumar Patil
 * @version 2.0
 * @since 30/Dec/22
 */
public class PickerActivity extends AppCompatActivity {
    private ActivityPickerBinding binding;
    String TAG = PickerActivity.class.getName();
    FileHandler pathHandler = new FileHandler() {
        @Override
        public void onSuccess(ArrayList<String> files) {
            binding.tvPath.setText("");
            for (String path:files) {
                binding.tvPath.setText(FileUtils.getName(path));
            }
            Log.e(TAG, files.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPickerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void chooseFile(View view) {
        FilePhotoPicker.Options options = new FilePhotoPicker.Options()
                /*.setPickUsingCamera(true)
                .setCameraTitle("CAM")
                .setPickFromDocuments(false)
                .setDocumentsTitle("DOC")
                .setPickFromGallery(false)
                .setGalleryTitle("GAL")
                .setMultipleSelect(true)*/;
        FilePhotoPicker.pick(getBaseContext(), options, pathHandler);
    }
}