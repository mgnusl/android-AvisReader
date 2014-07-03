package no.kreativo.newspapersnor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import no.kreativo.newspapersnor.util.IabHelper;
import no.kreativo.newspapersnor.util.IabResult;
import no.kreativo.newspapersnor.util.Inventory;
import no.kreativo.newspapersnor.util.Purchase;

public class InAppActivity extends ActionBarActivity {

    private static final String TAG = "APP";
    private IabHelper iabHelper;
    private static final String SKU_REMOVEADS = "removeads";
    private String base64EncodedPublicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApdCJK4xM08rnNBcWnJNPzYtJtpNvCx+0Gw0bBUigYPq" +
                    "l8fx2KTTCgzPhvSsbrEqfGiEwlvDPRAPSTCqwkN6VR1Kxw/iqL77s/GPIsdvA/l2br/n23ZKhKB5OoZV" +
                    "VrOliy1mykhAlDdIM8V6ZQ/FUt8OjCWgSB8QOTKPe5bNBbYxMm2Ho7ka4IhfRTy1JYoTCPXm9ZnGXVOLbZ" +
                    "2glSiuWpm7PwNA8kZKhS+xA0dfZTU4m9kFgeMQVkGvJYWrz1TtEioLfQiMi" +
                    "ohT/Aa0ScdgphdhBefM/OXZAZtGlMbUd2404FYD" +
                    "ZY07taEgmj2IUotkCFZUel15CgJmMiqZOIlXaKQIDAQAB";

    private Button buyButton, sjekkInventoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app);

        buyButton = (Button) findViewById(R.id.knapp);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyRemoveAds();
            }
        });

        sjekkInventoryButton = (Button) findViewById(R.id.knapp2);
        sjekkInventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iabHelper.queryInventoryAsync(gotInventoryListener);
            }
        });


        iabHelper = new IabHelper(this, base64EncodedPublicKey);

        iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Toast.makeText(InAppActivity.this, "In-app blling setup failed", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(InAppActivity.this, "In-app Billing is set up OK", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void buyRemoveAds() {
        iabHelper.launchPurchaseFlow(this, SKU_REMOVEADS, 10001, purchaseFinishedListener, "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!iabHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener purchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                Toast.makeText(InAppActivity.this, "There was a problem processing the purchase.", Toast.LENGTH_LONG).show();
                return;
            } else if (purchase.getSku().equals(SKU_REMOVEADS)) {
                consumeItem();
            }
        }
    };

    public void consumeItem() {
        Toast.makeText(InAppActivity.this, "KJØP FULLFØRT", Toast.LENGTH_LONG).show();
    }

    IabHelper.QueryInventoryFinishedListener gotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                // handle error here
            } else {
                // does the user have the premium upgrade?
                boolean hasRemoveAds = inventory.hasPurchase(SKU_REMOVEADS);
                Toast.makeText(InAppActivity.this, "Har tidligere kjøpt removeads: " + hasRemoveAds, Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iabHelper != null) iabHelper.dispose();
        iabHelper = null;
    }

}