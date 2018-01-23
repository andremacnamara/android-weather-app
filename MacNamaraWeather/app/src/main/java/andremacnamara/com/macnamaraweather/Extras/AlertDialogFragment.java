package andremacnamara.com.macnamaraweather.Extras;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import andremacnamara.com.macnamaraweather.R;


//This claas provides a message that displays if that is not found by the API
//It's used in TodaysWeather class
public class AlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString( R.string.errorTitle))
                .setMessage(context.getString(R.string.error_Message))
                .setPositiveButton("Ok", null);

        AlertDialog dialog = builder.create();
        return dialog;
    }
}
