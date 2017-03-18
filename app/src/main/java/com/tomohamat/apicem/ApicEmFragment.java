package com.tomohamat.apicem;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Robert on 3/15/2017.
 */

public class ApicEmFragment extends Fragment {

    private static final String TAG = "ApicEmFragment";

    protected ArrayList<Button> buttons;
    protected View.OnClickListener listener;

    protected TextView mResultTextView;
    protected ProgressDialog progressDialog;

    public void disableButtons() {
        setButtonsStatus(false);
    }

    public void enableButtons() {
        setButtonsStatus(true);
    }

    protected void setButtonsStatus(boolean enabled) {
        for (Button button : buttons) {
            button.setEnabled(enabled);
        }
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void showPleaseWait() {
        mResultTextView.setText(getString(R.string.please_wait));
    }

    public void showProgressDialog(boolean visible) {
        if (visible) {
            progressDialog = ProgressDialog.show(this.getContext(), "",
                    getString(R.string.please_wait), true);
            new Thread() {
                public void run() {
                    while (true) {
                        try {
                            sleep(500);
                        } catch (Exception e) {
                        }
                    }
                }
            }.start();
        } else {
            if (null != progressDialog) {
                progressDialog.dismiss();
            }
        }
    }

    public void showResult(String result) {
        mResultTextView.setText(result);
    }

}
