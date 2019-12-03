package com.example.myrunningapp.Utils;

import android.content.Context;

public class DataController {

    //used to access UserID within all classes for GET/POST Calls
    public int DCuserID;

    private static DataController mInstance;
    private final Context mContext;

    public DataController(Context ctx) {
        mContext = ctx;
    }

    public static synchronized DataController getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DataController(ctx);
        }
        return mInstance;
    }
}
