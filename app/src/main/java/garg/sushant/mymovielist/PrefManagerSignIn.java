package garg.sushant.mymovielist;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sushant on 4/5/2017.
 */

public class PrefManagerSignIn {

    private static final String MY_PREFERENCES_SIGNIN = "signin_preferences";

    public static boolean isFirst(Context context){
        final SharedPreferences readerSignIn = context.getSharedPreferences(MY_PREFERENCES_SIGNIN, Context.MODE_PRIVATE);
        final boolean first = readerSignIn.getBoolean("is_first", true);
        if(first){
            final SharedPreferences.Editor editor = readerSignIn.edit();
            editor.putBoolean("is_first", false);
            editor.commit();
        }
        return first;
    }

}
