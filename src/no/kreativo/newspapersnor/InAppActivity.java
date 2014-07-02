package no.kreativo.newspapersnor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import no.kreativo.newspapersnor.util.IabHelper;
import no.kreativo.newspapersnor.util.IabResult;
import no.kreativo.newspapersnor.util.Inventory;
import no.kreativo.newspapersnor.util.Purchase;

public class InAppActivity extends ActionBarActivity {

    private static final String TAG = "APP";
    IabHelper mHelper;
    static final String ITEM_SKU = "removeads";

    private Button buyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app);

        buyButton = (Button) findViewById(R.id.knapp);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyClick();
            }
        });

        String base64EncodedPublicKey =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApdCJK4xM08rnNBcWnJNPzYtJtpNvCx+0Gw0bBUigYPq" +
                        "l8fx2KTTCgzPhvSsbrEqfGiEwlvDPRAPSTCqwkN6VR1Kxw/iqL77s/GPIsdvA/l2br/n23ZKhKB5OoZV" +
                        "VrOliy1mykhAlDdIM8V6ZQ/FUt8OjCWgSB8QOTKPe5bNBbYxMm2Ho7ka4IhfRTy1JYoTCPXm9ZnGXVOLbZ" +
                        "2glSiuWpm7PwNA8kZKhS+xA0dfZTU4m9kFgeMQVkGvJYWrz1TtEioLfQiMi" +
                        "ohT/Aa0ScdgphdhBefM/OXZAZtGlMbUd2404FYD" +
                        "ZY07taEgmj2IUotkCFZUel15CgJmMiqZOIlXaKQIDAQAB";

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " +
                            result);
                    Toast.makeText(InAppActivity.this, "In-app blling setup failed", Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");
                    Toast.makeText(InAppActivity.this, "In-app Billing is set up OK", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    public void buyClick() {
        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                mPurchaseFinishedListener, "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase) {
            if (result.isFailure()) {
                // Handle error
                return;
            } else if (purchase.getSku().equals(ITEM_SKU)) {
                consumeItem();
            }

        }
    };

    public void consumeItem() {
        Toast.makeText(InAppActivity.this, "KJØP FULLFØRT", Toast.LENGTH_LONG).show();
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {


            if (result.isFailure()) {
                // Handle failure
            } else {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                        mConsumeFinishedListener);
                Toast.makeText(InAppActivity.this, "ONQUERYINVENTORYFINISHEDLISTENER", Toast.LENGTH_LONG).show();

            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {
                        Toast.makeText(InAppActivity.this, "ONCONSUMEFINISHEDLISTENER", Toast.LENGTH_LONG).show();

                    } else {
                        // handle error
                    }
                }
            };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

}
