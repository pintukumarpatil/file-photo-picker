# Android-file-photo-picker

# Description

This app can make the file picking process easy, which allows you to select Pictures, Videos, and Documents. Also, that has Capturing Photo option.
For using this library, you need to migrate your project to AndroidX(If your project is not migrated to AndroidX).

This works without MANAGE_EXTERNAL_STORAGE permission Since it's very useful when you publish the app on Google Play Store.


<img src="app_demo_picture.png" width="30%" height="30%"/>


# Getting Started

To add this library to your project, please follow below steps

Add this in your root `build.gradle` file (project level gradle file):

```gradle
allprojects {
    repositories {
        maven { url "https://www.jitpack.io" }
    }
}

buildscript {
    repositories {
        maven { url "https://www.jitpack.io" }
    }
}
```

Then, Add this in your root `build.gradle` file (app level gradle file):

for example:

  ```
  dependencies {
    implementation 'com.github.pintukumarpatil:file-photo-picker:1.4'
  }
  ```

# Key Features :

- Fully Handled Android's Dangerous Permissions
- Compressing option for selected images included,
- Can pick any non-media files like PDF,Doc,Txt files without using MANAGE_EXTERNAL_STORAGE permission
- Dangerous Permissions & FILE_PROVIDER_PATHS settings are already included, No need to put extra effort.
- Supports upto Android 13

This library compatible with Android 6 and above

## How to use the file & photo picker:

  ```
    FilePhotoPicker.Options options = new FilePhotoPicker.Options()
    .setPickUsingCamera(true)
    .setCameraTitle("CAMERA")
    .setPickFromDocuments(true)
    .setDocumentsTitle("DOCUMENTS")
    .setPickFromGallery(true)
    .setGalleryTitle("GALLERY")
    .setMultipleSelect(true);
    FilePhotoPicker.pick(getBaseContext(), options, pathHandler);
  ```
### Handling the PickerResult

You can use the included utility method to parse the result:

  ```
    FileHandler pathHandler = new FileHandler() {
    @Override
    public void onSuccess(ArrayList<String> files) {
    
            }
            };
  ```

## Want to customize further?

Sample project is here(https://github.com/pintukumarpatil/file-photo-picker)