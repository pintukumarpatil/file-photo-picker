package com.github.pkp;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

/**
 * @author Pintu Kumar Patil
 * @version 1.0
 * @since 20/Nov/19
 */

public class FileUtils {
    private static final String TAG = "FileUtils";

    /**
     * @param fileName of that file, from you want to get file extension
     * @return the file extension
     */
    public static String getExtension(String fileName) {
        String[] filenameArray = fileName.split("\\.");
        return filenameArray[filenameArray.length - 1].toLowerCase();
    }

    /**
     * @param extension of a file
     * @return the new file name of the basis of main file
     */
    public static String getRandomFileName(String extension) {
        return random() + "-" + Calendar.getInstance().getTimeInMillis() + "." + extension;
    }

    static String random() {
        int MAX_LENGTH = 6;
        String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(MAX_LENGTH);
        for (int i = 0; i < MAX_LENGTH; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    /**
     * @param appContext of an application
     * @return the file
     */
    public static File createImageFile(Context appContext) {
        final String fileName = getRandomFileName(".jpg");
        return getFile(appContext, fileName);
    }

    /**
     * @param appContext of the application
     * @param child      in which subdirectory you want to create file
     * @return the file
     */
    public static File getFile(Context appContext, String child) {
        return new File(getDocumentCacheDir(appContext), child);
    }

    /**
     * @param file,          where you want to get the download percentage
     * @param actualFileSize is the actual file size
     * @return download file percentage
     */
    public static int getFileWritePercentage(File file, Long actualFileSize) {
        if (file.exists()) {
            Long fileSize = file.length();
            //Do action
            if (fileSize.equals(actualFileSize) || fileSize > actualFileSize) {
                return 100;
            } else {
                return (int) (((float) fileSize / (float) actualFileSize) * 100);
            }
        }
        return -1;
    }

    /**
     * @param size of file
     * @return file size in specific format
     */
    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    /**
     * @param file, that you want to get file size
     * @return file size in bytes
     */
    public static long getFileSizeInBytes(File file) {
        return file.length();
    }

    /**
     * @param file, that you want to get file size
     * @return file size in MB
     */
    public static long getFileSizeInMB(File file) {
        long fileSizeInBytes = file.length();
// Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long fileSizeInKB = fileSizeInBytes / 1024;
// Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        return fileSizeInKB / 1024;
    }


    /**
     * @param context The context to check Authority.
     * @param uri     The Uri to check.
     * @return Whether the Uri authority is local.
     */
    private static boolean isLocalStorageDocument(Context context, Uri uri) {
        return getAuthority(context).equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGoogleOldPhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isConversationPhotosUri(Uri uri) {
        return "com.gwl.conversation.provider".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Storage.
     */
    private static boolean isGoogleDrivePhotosUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority());
    }

    private static boolean isNewGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }

    private static boolean isPicassoPhotoUri(Uri uri) {

        return uri != null
                && !TextUtils.isEmpty(uri.getAuthority())
                && (uri.getAuthority().startsWith("com.android.gallery3d")
                || uri.getAuthority().startsWith("com.google.android.gallery3d"));
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs, Uri filePathUri) {

        Cursor cursor = null;
        final String column = MediaStore.Files.FileColumns.DATA;
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return saveFileFromUri(context, filePathUri);//if any problem then please comment only this line
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return saveFileFromUri(context, filePathUri);//if any problem then please comment only this line;
    }

    /**
     * @param context of an application
     * @param uri     of a file
     * @return path of that file uri
     */
    public static String getPath(final Context context, final Uri uri) {

        Log.d(TAG + " File -",
                "Authority: " + uri.getAuthority() +
                        ", Fragment: " + uri.getFragment() +
                        ", Port: " + uri.getPort() +
                        ", Query: " + uri.getQuery() +
                        ", Scheme: " + uri.getScheme() +
                        ", Host: " + uri.getHost() +
                        ", Segments: " + uri.getPathSegments().toString());

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (isLocalStorageDocument(context, uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri);
            }
            // ExternalStorageProvider
            else if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4);
                }

                String[] contentUriPrefixesToTry = new String[]{
                        "content://downloads/public_downloads",
                        "content://downloads/my_downloads",
                        "content://downloads/all_downloads"
                };

                for (String contentUriPrefix : contentUriPrefixesToTry) {
                    if (id != null) {
                        try {
                            Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.parseLong(id));
                            String path = getDataColumn(context, contentUri, null, null, uri);
                            if (path != null) {
                                return path;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                return saveFileFromUri(context, uri);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs, uri);
            } else if (isGoogleDrivePhotosUri(uri)) {
                // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                return saveFileFromUri(context, uri);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGoogleOldPhotosUri(uri))
                return uri.getLastPathSegment();
            else if (isNewGooglePhotosUri(uri)) {
                // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                return saveFileFromUri(context, uri);
            } else if (isPicassoPhotoUri(uri)) {
                // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                return saveFileFromUri(context, uri);
            } else if (isConversationPhotosUri(uri)) {
                // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                return saveFileFromUri(context, uri);
            }
            return getDataColumn(context, uri, null, null, uri);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return saveFileFromUri(context, uri);
    }

    /**
     * @param context on an application
     * @param uri     of the file
     * @return file name
     */
    private static String getFileName(@NonNull Context context, Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);
        String filename = null;

        if (mimeType == null) {
            String path = getPath(context, uri);
            if (path == null) {
                filename = getName(uri.toString());
            } else {
                File file = new File(path);
                filename = file.getName();
            }
        } else {
            Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
            if (returnCursor != null) {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                filename = returnCursor.getString(nameIndex);
                returnCursor.close();
            }
        }

        return filename;
    }

    /**
     * @param context in an application
     * @param uri     of a file
     */
    private static String saveFileFromUri(Context context, Uri uri) {
        String fileName = getFileName(context, uri);
        File cacheDir = getDocumentCacheDir(context);
        File file = generateFileName(fileName, cacheDir);
        String destinationPath = null;
        if (file != null) {
            destinationPath = file.getAbsolutePath();
            saveFileFromUri(context, uri, destinationPath);
        }
        return destinationPath;
    }

    /**
     * @param context         in an application
     * @param uri             of a file
     * @param destinationPath where you want to save file
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void saveFileFromUri(Context context, Uri uri, String destinationPath) {
        InputStream is = null;
        BufferedOutputStream bos = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            bos = new BufferedOutputStream(new FileOutputStream(destinationPath, false));
            byte[] buf = new byte[1024];
            if (is != null)
                is.read(buf);
            do {
                bos.write(buf);
            } while (is != null && is.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param filepath of a file
     * @return file name that you want
     */
    public static String getName(String filepath) {
        if (TextUtils.isEmpty(filepath)) {
            return "";
        }
        if (!filepath.contains("/")) {
            return filepath;
        }
        int index = filepath.lastIndexOf('/');
        return filepath.substring(index + 1);
    }

    /**
     * @param context of the application
     * @return file from cache directory
     */
    private static File getDocumentCacheDir(@NonNull Context context) {
        File dir = context.getFilesDir();
        if (!dir.exists()) {
            boolean isDirectoryCreated = dir.mkdirs();
            System.out.println(isDirectoryCreated ? "Directory created" : "Directory Creation issue");
        }
        logDir(context.getFilesDir());
        logDir(dir);
        return dir;
    }

    /**
     * @param dir is the directory that you want to log aa the contains files
     */
    private static void logDir(File dir) {
        //if (BuildConfig.DEBUG) {
        Log.d(TAG, "Dir=" + dir);
        File[] files = dir.listFiles();
        for (File file : Objects.requireNonNull(files)) {
            Log.d(TAG, "File=" + file.getPath());
        }
        // }
    }

    /**
     * @param name      of the file
     * @param directory of the file in which you want to create a file
     * @return file
     */
    @Nullable
    private static File generateFileName(@Nullable String name, File directory) {
        if (name == null) {
            return null;
        }

        File file = new File(directory, name);

        if (file.exists()) {
            String fileName = name;
            String extension = "";
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex > 0) {
                fileName = name.substring(0, dotIndex);
                extension = name.substring(dotIndex);
            }

            int index = 0;

            while (file.exists()) {
                index++;
                name = fileName + '(' + index + ')' + extension;
                file = new File(directory, name);
            }
        }

        try {
            if (!file.createNewFile()) {
                return null;
            }
        } catch (IOException e) {
            Log.w(TAG, e);
            return null;
        }

        logDir(directory);

        return file;
    }

    /**
     * @param appContext of an application
     */
    @SuppressWarnings("unused")
    public static void deleteFiles(Context appContext) {
        File dir = getDocumentCacheDir(appContext);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    deleteFile(new File(dir, child));
                    appContext.deleteFile(child);
                }
            }
        }
    }

    /**
     * @param file that you want to delete
     */
    public static void deleteFile(File file) {
        if (file != null && file.exists()) {
            if (file.delete()) {
                System.out.println("file Deleted");
            } else {
                System.out.println("file not Deleted");
            }
        }
    }

    /**
     * @param context of an application
     * @param file    of the file that you want to view
     */
    public static void openFile(Context context, File file) {
        try {

            Uri uri = FileProvider.getUriForFile(context, getAuthority(context), file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (file.toString().contains(".doc") || file.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (file.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (file.toString().contains(".ppt") || file.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (file.toString().contains(".xls") || file.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (file.toString().contains(".zip") || file.toString().contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            } else if (file.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (file.toString().contains(".wav") || file.toString().contains(".mp3") || file.toString().contains(".m4a")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/*");
            } else if (file.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (file.toString().contains(".jpg") || file.toString().contains(".jpeg") || file.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (file.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (file.toString().contains(".3gp") || file.toString().contains(".mpg") ||
                    file.toString().contains(".mpeg") || file.toString().contains(".mpe") || file.toString().contains(".mp4") || file.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                intent.setDataAndType(uri, "*/*");
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (file.isFile()) {
                @SuppressLint("QueryPermissionsNeeded") List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
            }

            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getAuthority(Context context) {
        Log.e("Authority",context.getPackageName() + ".provider");
        return context.getPackageName() + ".provider";
    }
}
