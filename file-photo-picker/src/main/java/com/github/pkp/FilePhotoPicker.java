package com.github.pkp;

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

/**
 * A public class for capture photo and pick files from storage.
 *
 * @author Pintu Kumar Patil
 * @version 1.0
 * @since 3/Mar/2023
 */
public class FilePhotoPicker {

    /**
     * Check/Request permissions and call the callback methods of permission handler accordingly.
     *
     * @param context Android context.
     * @param options The options for handling permissions.
     * @param handler The permission handler object for handling callbacks of various user
     *                actions such as permission granted, permission denied, etc.
     */
    public static void pick(final Context context, Options options, final FileHandler handler) {
        FilePhotoActivity.fileHandler = handler;
        Intent intent = new Intent(context, FilePhotoActivity.class);
        intent.putExtra(FilePhotoActivity.EXTRA_OPTIONS, options);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    /**
     * Options to customize while requesting permissions.
     */
    public static class Options implements Serializable {

        String cameraTitle = "Camera";
        String galleryTitle = "Gallery";
        String documentsTitle = "Documents";
        boolean pickUsingCamera = true;
        boolean pickFromGallery = true;
        boolean pickFromDocuments = true;
        boolean multipleSelect = false;

        /**
         * Sets the button text for "camera" while capture image using camera.
         *
         * @param title The text for "camera".
         * @return same instance.
         */
        public Options setCameraTitle(String title) {
            this.cameraTitle = title;
            return this;
        }

        /**
         * Sets the button text for "Gallery" while capture image using Gallery.
         *
         * @param title The text for "Gallery".
         * @return same instance.
         */
        public Options setGalleryTitle(String title) {
            this.galleryTitle = title;
            return this;
        }

        /**
         * Sets the button text for "Documents" while capture image using Documents.
         *
         * @param title The text for "Documents".
         * @return same instance.
         */
        public Options setDocumentsTitle(String title) {
            this.documentsTitle = title;
            return this;
        }

        /**
         * Sets the "Pick Using Camera" flag in Intent, for when we're
         *
         * @param pickUsingCamera true if we need camera option for image capture
         * @return same instance.
         */
        public Options setPickUsingCamera(boolean pickUsingCamera) {
            this.pickUsingCamera = pickUsingCamera;
            return this;
        }

        /**
         * Sets the "Pick From Gallery" flag in Intent, for when we're
         *
         * @param pickFromGallery true if we need camera option for image capture
         * @return same instance.
         */
        public Options setPickFromGallery(boolean pickFromGallery) {
            this.pickFromGallery = pickFromGallery;
            return this;
        }

        /**
         * Sets the "Pick From Documents" flag in Intent, for when we're
         *
         * @param pickFromDocuments true if we need camera option for image capture
         * @return same instance.
         */
        public Options setPickFromDocuments(boolean pickFromDocuments) {
            this.pickFromDocuments = pickFromDocuments;
            return this;
        }

        /**
         * Sets the "Multiple select" flag in Intent, for enable multiple file select
         *
         * @param multipleSelect true if we need multiple file select
         * @return same instance.
         */
        public Options setMultipleSelect(boolean multipleSelect) {
            this.multipleSelect = multipleSelect;
            return this;
        }
    }
}
