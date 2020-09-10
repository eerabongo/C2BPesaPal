package esau.rabongo.c2bpesapal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private String TAG = this.getClass().getCanonicalName();
    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;

    public static final int ASYNC_TASK_TIMEOUT = 1000 * 120;
    public static final int PAYMENT_ACTIVITY_REQUEST_CODE = 1;
    public static final int SETTINGS_ACTIVITY_REQUEST_CODE = 2;

    private static String title = "";
    private static String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance(), "HomeFragment").commit();
                        break;

                    case R.id.navigation_settings:
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, SettingsFragment.newInstance(), "SettingsFragment").commit();
                        break;
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d(TAG, "onActivityResult requestCode:" + requestCode);
        try {
            String payment = data.getStringExtra("payment");
            Log.d(TAG, "onActivityResult payment:" + payment);
            String[] data_parts = payment.split(",");
            //6d48197e-68bb-4b20-ad94-57c39f90f39c,MPESA,PENDING,100,
            if (data_parts.length > 0) {
                String payment_id = data_parts[0];
                String method_of_payment = data_parts[1];
                final String status = data_parts[2];
                String amount_processed = data_parts[3];

                switch (status) {
                    case "COMPLETED":
                        HomeFragment.getFragmentInstance().clearFields();
                        title = "Payment Completed";
                        message = "Your payment has been processed, you will receive an Email/SMS notification and your payment settled instantly. Thank you for using PesaPal";
                        break;
                    case "PENDING":
                        HomeFragment.getFragmentInstance().clearFields();
                        title = "Payment Submitted";
                        message = "Your payment is being processed. Once confirmed, you will receive an Email/SMS notification and your payment settled instantly. Thank you for using PesaPal";
                        break;
                    default:
                        title = "Payment Failed";
                        message = "Please try again later";
                }

                this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showAlertDialog(title, message);
                    }
                });
            } else {
                this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        String title = "Payment Failed";
                        String message = "Please try again later";
                        showAlertDialog(title, message);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "onActivityResult e:" + e.toString());
            e.printStackTrace();
        }
    }

    public void showAlertDialog(String title, String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(R.drawable.ic_baseline_error_24);
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.show();
    }


}