package no.kreativo.newspapersnor;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import no.kreativo.newspapersnor.preferences.SettingsActivity;
import no.kreativo.newspapersnor.util.IabHelper;
import no.kreativo.newspapersnor.util.IabResult;
import no.kreativo.newspapersnor.util.Inventory;
import no.kreativo.newspapersnor.util.Purchase;
import no.kreativo.newspapersnor.utils.Utils;

public class InAppActivity extends ActionBarActivity {

    private IabHelper iabHelper;
    private Button buyButton, sjekkInventoryButton;
    private SuperActivityToast superToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app);

        // SuperToast style
        superToast = new SuperActivityToast(InAppActivity.this);
        superToast.setDuration(SuperToast.Duration.LONG);
        superToast.setTextColor(Color.WHITE);
        superToast.setTouchToDismiss(true);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        buyButton = (Button) findViewById(R.id.buyButton);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyRemoveAds();
            }
        });

        iabHelper = new IabHelper(this, Utils.PUBLIC_KEY);

        iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    superToast.setText(getResources().getString(R.string.setup_failed));
                    superToast.setBackground(SuperToast.Background.RED);
                    superToast.show();
                }
            }
        });
    }

    IabHelper.OnIabPurchaseFinishedListener purchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                superToast.setText(getResources().getString(R.string.problem_purchasing));
                superToast.setBackground(SuperToast.Background.RED);
                superToast.show();
                return;
            } else if (purchase.getSku().equals(Utils.SKU_REMOVEADS)) {
                // Purchase successful
                superToast.setText(getResources().getString(R.string.ad_remove_success));
                superToast.setBackground(SuperToast.Background.GREEN);
                superToast.show();
            }
        }
    };

    public void buyRemoveAds() {
        iabHelper.launchPurchaseFlow(this, Utils.SKU_REMOVEADS, 10001, purchaseFinishedListener, "");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iabHelper != null) iabHelper.dispose();
        iabHelper = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!iabHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_activity_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(InAppActivity.this, SettingsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}