package com.github.pkp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pkp.databinding.ActivityFilePhotoBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


/**
 * Activity for capture photo and pick files from storage.
 *
 * @author Pintu Kumar Patil
 * @version 1.0
 * @since 3/Mar/2023
 */

public class FilePhotoActivity extends AppCompatActivity {
    public static final String[] FILE_PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final String[] CAMERA_PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    public static final int REQUEST_CODE = 25;
    static final String EXTRA_OPTIONS = "options";
    static FileHandler fileHandler;
    ActivityResultLauncher<Intent> fileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (result.getResultCode() == Activity.RESULT_OK) {
                    ArrayList<String> filePathList = new ArrayList<>();
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        if (data.getClipData() == null && uri != null) {
                            String selectedFilePath = FileUtils.getPath(FilePhotoActivity.this, uri);
                            filePathList.add(selectedFilePath);
                        } else if (data.getClipData() != null) {
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                Uri uri2 = data.getClipData().getItemAt(i).getUri();
                                if (uri2 == null) return;
                                String selectedFilePath = FileUtils.getPath(FilePhotoActivity.this, uri2);
                                filePathList.add(selectedFilePath);
                            }
                        }
                        if (!filePathList.isEmpty() && fileHandler != null) {
                            fileHandler.onSuccess(filePathList);
                            finish();
                        }
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    finish();
                }
            });
    ActivityFilePhotoBinding binding;
    private String mCurrentPhotoPath;
    ActivityResultLauncher<Intent> imageCaptureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    ArrayList<String> filePathList = new ArrayList<>();
                    if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
                        filePathList.add(mCurrentPhotoPath);
                        fileHandler.onSuccess(filePathList);
                        finish();
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    finish();
                }
            });
    private FilePhotoPicker.Options options;

    public void gallery(View view) {
        PermissionsChecker.check(this, REQUEST_CODE, FILE_PERMISSIONS, new PermissionHandler() {
            @Override
            public void onGranted() {
                openGalleryForImageAndVideo();
            }

            @Override
            public void onDeny() {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && (!ActivityCompat.shouldShowRequestPermissionRationale(FilePhotoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(FilePhotoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showAlertForAppInfoScreen(FilePhotoActivity.this, R.drawable.ic_permission_storage, getString(R.string.storage_permission));
                    }
                } else {
                    Toast.makeText(FilePhotoActivity.this, getString(R.string.please_grant_required_permissions), Toast.LENGTH_LONG).show();
                    showAlertForAppInfoScreen(FilePhotoActivity.this, R.drawable.ic_permission_storage, getString(R.string.storage_permission));

                }
            }
        });
    }

    public void documents(View view) {
        PermissionsChecker.check(this, REQUEST_CODE, FILE_PERMISSIONS, new PermissionHandler() {
            @Override
            public void onGranted() {
                openAttachments();
            }

            @Override
            public void onDeny() {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && (!ActivityCompat.shouldShowRequestPermissionRationale(FilePhotoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(FilePhotoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showAlertForAppInfoScreen(FilePhotoActivity.this, R.drawable.ic_permission_storage, getString(R.string.storage_permission));
                    }
                } else {
                    Toast.makeText(FilePhotoActivity.this, getString(R.string.please_grant_required_permissions), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void camera(View view) {
        PermissionsChecker.check(this, REQUEST_CODE, CAMERA_PERMISSIONS, new PermissionHandler() {
            @Override
            public void onGranted() {
                getCamera();
            }

            @Override
            public void onDeny() {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && (!ActivityCompat.shouldShowRequestPermissionRationale(FilePhotoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) || !PermissionsChecker.isCameraAccessed(FilePhotoActivity.this))) {
                    if (!PermissionsChecker.isStorageAccessed(FilePhotoActivity.this)) {
                        showAlertForAppInfoScreen(FilePhotoActivity.this, R.drawable.ic_permission_storage, getString(R.string.storage_permission));
                    } else if (!PermissionsChecker.isCameraAccessed(FilePhotoActivity.this)) {
                        showAlertForAppInfoScreen(FilePhotoActivity.this, R.drawable.ic_permission_camera, getString(R.string.camera_video_permission));
                    }
                } else {
                    Toast.makeText(FilePhotoActivity.this, getString(R.string.please_grant_required_permissions), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void cancel(View view) {
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.transparent)));
        binding = ActivityFilePhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        options = (FilePhotoPicker.Options) intent.getSerializableExtra(EXTRA_OPTIONS);
        if (options == null) {
            options = new FilePhotoPicker.Options();
        }
        binding.tvCamera.setVisibility(options.pickUsingCamera ? View.VISIBLE : View.GONE);
        binding.tvDocuments.setVisibility(options.pickFromDocuments ? View.VISIBLE : View.GONE);
        binding.tvGallery.setVisibility(options.pickFromGallery ? View.VISIBLE : View.GONE);
        binding.tvCamera.setText(options.cameraTitle);
        binding.tvDocuments.setText(options.documentsTitle);
        binding.tvGallery.setText(options.galleryTitle);
    }

    /**
     * get request for image capture from camera
     */
    @SuppressLint("QueryPermissionsNeeded")
    public void getCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = FileUtils.createImageFile(getApplicationContext());
            // Continue only if the File was successfully created
            mCurrentPhotoPath = photoFile.getAbsolutePath();
            Uri uri = FileProvider.getUriForFile(this, FileUtils.getAuthority(getApplicationContext()), photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            imageCaptureLauncher.launch(takePictureIntent);
        }
    }

    /**
     * Handling Get doc,pdf,xlx,apk,et., from user to send throw Attachments <br/>
     */
    public void openAttachments() {
        Intent chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("*/*");
        chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, options.multipleSelect);
        chooseFile.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        fileLauncher.launch(chooseFile);
    }

    /**
     * Declaration openGalleryForImageAndVideo()
     * Method is used to open the Image And Video
     */
    private void openGalleryForImageAndVideo() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "*/*");
            intent.setType("image/* video/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, options.multipleSelect);
            fileLauncher.launch(intent);
        } catch (Exception exception) {
            Log.e("openAttachments", "Exception-------->" + exception.getMessage());
        }
    }

    /**
     * @param context - Dialog call from
     * @param vector  - icon related to permission
     * @param message - message related to permission
     */
    public void showAlertForAppInfoScreen(Context context, int vector, String message) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.alert_popup_permission);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp;
        if (window != null) {
            wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
        }
        ImageView permissionAlertTypeImageView = dialog.findViewById(R.id.permissionAlertTypeImageView);
        TextView permissionAlertContentTextView = dialog.findViewById(R.id.permissionAlertContentTextView);
        TextView permissionAlertNotNowTextView = dialog.findViewById(R.id.permissionAlertNotNowTextView);
        TextView permissionAlertSettingsTextView = dialog.findViewById(R.id.permissionAlertSettingsTextView);
        permissionAlertContentTextView.setText(message);
        permissionAlertTypeImageView.setImageResource(vector);
        permissionAlertNotNowTextView.setOnClickListener(v -> dialog.dismiss());
        permissionAlertSettingsTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}