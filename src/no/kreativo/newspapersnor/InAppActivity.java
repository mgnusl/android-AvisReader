package no.kreativo.newspapersnor;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import no.kreativo.newspapersnor.util.IabHelper;
import no.kreativo.newspapersnor.util.IabResult;


public class InAppActivity extends ActionBarActivity {

    private IabHelper mHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app);

        String piece1 = "";
        String piece2 = "";
        String piece3 = "";
        String piece4 = "";

        mHelper = new IabHelper(this, piece1 + piece2 + piece3 + piece4);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d("APP", "Problem setting up In-app Billing: " + result);
                }else {Log.d("APP", "SUCCESSSS");}

            }
        });


    }


}
