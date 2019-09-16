package mahiti.org.oelp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.snatik.storage.Storage;


import java.io.File;

import mahiti.org.oelp.R;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.EasyImageConfiguration;


/**
 * Created by RAJ ARYAN on 28/06/19.
 */
public class ImageCaptureChooseFromGallery {

    private final int selectionType;
    private final Fragment activity;
    private Context context;
    private Uri outputFileUri;

    /**
     * @param selectionType     Capture Image Or Select From Gallery
     * @param activity          Context of Calling Activity
     */

    public ImageCaptureChooseFromGallery(int selectionType, Fragment activity, Context context) {
        this.selectionType = selectionType;
        this.activity = activity;
        this.context = context;
        setConfiguration();
        callSpecificIntent();
    }

    private void setConfiguration() {
        EasyImage.clearConfiguration(context);
        String imageSavingBasePath = AppUtils.completePathInSDCard(3).getAbsolutePath();
        if (!new File(imageSavingBasePath).exists()) {
            createDirectory(1, imageSavingBasePath);  // 0- Audio 1 - Video
        }

        EasyImageConfiguration easyImage = EasyImage.configuration(context);
        easyImage.setAllowMultiplePickInGallery(false);
        easyImage.setImagesFolderName(imageSavingBasePath);

    }

    private void callSpecificIntent() {
        if (selectionType == Constants.CAPTURE_IMAGE) {
            openCamera();
        } else if (selectionType == Constants.CHOOSE_GALLERY) {
            openGallery();
        } else {
            openCameraAndGallery();
        }
    }

    public void showImagePickerOptions() {
        // setup the alert builder
        /*AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.choose_option));

        // add a list
        String[] animals = {activity.getString(R.string.lbl_take_camera_picture), activity.getString(R.string.lbl_choose_from_gallery)};
        builder.setItems(animals, (dialog, which) -> {
            switch (which) {
                case 0:
                    openCamera();
                    break;
                case 1:
                    openGallery();
                    break;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();*/
    }

    public Uri getCaptureImageOutputUri() {
        String imageSavingBasePath = AppUtils.completePathInSDCard(0).getAbsolutePath();
        if (!new File(imageSavingBasePath).exists()) {
            createDirectory(1, imageSavingBasePath);  // 0- Audio 1 - Video
        }
        File getImage = new File(imageSavingBasePath);

        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), AppUtils.getDateTime() + ".png"));
        }
        return outputFileUri;
    }

    public void createDirectory(int type, String imageSavingBasePath) {
        Storage storage = new Storage(context);
        File imageSavingFile = new File(imageSavingBasePath);
        imageSavingBasePath = imageSavingFile.getAbsolutePath();
        storage.createDirectory(imageSavingBasePath, true);
    }

    private void openCamera() {
        // collect all camera intents
       /* PackageManager packageManager = activity.getPackageManager();
        List<Intent> allIntents = new ArrayList<>();
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        activity.startActivityForResult(chooserIntent, spoorthy.khptcode.utils.Constants.CAPTURE_IMAGE);*/
        EasyImage.openCameraForImage(activity, Constants.CAPTURE_IMAGE);
    }

    private void openGallery() {
        /*PackageManager packageManager = activity.getPackageManager();
        List<Intent> allIntents = new ArrayList<>();
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (multipleSelection==1)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            allIntents.add(intent);
        }
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        activity.startActivityForResult(chooserIntent, spoorthy.khptcode.utils.Constants.CHOOSE_GALLERY);*/

        EasyImage.openGallery(activity, Constants.CHOOSE_GALLERY);
    }

    private void openCameraAndGallery() {
// Determine Uri of camera image to save.
       /* Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = activity.getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        activity.startActivityForResult(chooserIntent, spoorthy.khptcode.utils.Constants.CAPTURE_IMAGE);*/

        showImagePickerOptions();


    }


}
