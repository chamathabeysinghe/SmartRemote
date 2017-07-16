package lk.ac.mrt.cse.smartremotecontroller.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by chamath on 7/7/17.
 */

public final class DataContract {

    //avoid instantiating
    private DataContract(){}

    //general detabase Uri s
    public static final String CONTENT_AUTHORITY="lk.ac.mrt.cse.smartremotecontrolle";
    public static final String PATH_DATA="data";
    public static final Uri BASE_CONTENT_URI= Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_DATA).build();


    //data table for remote
    public static class RemoteControllerColumns implements BaseColumns {
        public static final String TABLE_NAME = "remote_controller";
        public static final String COLUMN_NAME_REMOTE_NAME= "remote_name";
        public static final String COLUMN_NAME_BRAND_NAME= "brand_name";

        public static final String COLUMN_NAME_BUTTON_NAME= "button_name";
        public static final String COLUMN_NAME_BUTTON_SIGNAL = "button_signal";

    }


}
