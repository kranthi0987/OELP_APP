package mahiti.org.oelp.fileandvideodownloader;

/**
 * Created by sandeep HR on 06/05/19.
 */

import android.content.Context;


public class TedPermission extends TedPermissionBase {
    public static final String TAG= TedPermission.class.getSimpleName();

    public static Builder with(Context context) {
        return new Builder(context);
    }

    public static class Builder extends PermissionBuilder<Builder> {

        private Builder(Context context) {
            super(context);
        }

        public void check() {
            checkPermissions();
        }

    }
}