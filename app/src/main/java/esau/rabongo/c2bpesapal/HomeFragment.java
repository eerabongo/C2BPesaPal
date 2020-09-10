package esau.rabongo.c2bpesapal;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.pesapal.pesapalandroid.data.Payment;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import static esau.rabongo.c2bpesapal.MainActivity.SETTINGS_ACTIVITY_REQUEST_CODE;

public class HomeFragment extends Fragment {
    private String TAG = this.getClass().getCanonicalName();
    private AppSharedPreferences appSharedPreferences;
    private static HomeFragment HomeFragmentInstance;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private TextView currencyTextView;
    private EditText amountEditText;
    private EditText paymentDescriptionEditText;
    private Button submitButton;

    private PaypalAPITask paypalAPITask;


    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        HomeFragmentInstance = fragment;
        return fragment;
    }

    public static HomeFragment getFragmentInstance() {
        return HomeFragmentInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_home, container, false);
        // initialize shared preferences
        appSharedPreferences = AppSharedPreferences.getClassInstance();
        appSharedPreferences.init(getActivity());
        // initialize fields
        firstNameEditText = fragment_view.findViewById(R.id.firstNameEditText);
        lastNameEditText = fragment_view.findViewById(R.id.lastNameEditText);
        emailEditText = fragment_view.findViewById(R.id.emailEditText);
        phoneEditText = fragment_view.findViewById(R.id.phoneEditText);
        currencyTextView = fragment_view.findViewById(R.id.currencyTextView);
        amountEditText = fragment_view.findViewById(R.id.amountEditText);
        paymentDescriptionEditText = fragment_view.findViewById(R.id.paymentDescriptionEditText);
        submitButton = fragment_view.findViewById(R.id.submitButton);
        // populate currency field
        currencyTextView.setText(appSharedPreferences.getCurrency());
        // add click listeners to button
        submitButton.setOnClickListener(submitButtonListener);

        return fragment_view;
    }

    public void setCurrencyFromSettings(String currency) {
        Log.d(TAG, "setCurrencyFromSettings:" + currency);
        currencyTextView.setText(currency);
    }

    public void clearFields() {
        firstNameEditText.setText("");
        lastNameEditText.setText("");
        emailEditText.setText("");
        phoneEditText.setText("");
        currencyTextView.setText("");
        amountEditText.setText("");
        paymentDescriptionEditText.setText("");
    }

    private View.OnClickListener submitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String first_name = firstNameEditText.getText().toString();
            String last_name = lastNameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
            String currency = currencyTextView.getText().toString();
            String amount = amountEditText.getText().toString();
            String payment_description = paymentDescriptionEditText.getText().toString();

            // validate input
            if (TextUtils.isEmpty(first_name)) {
                firstNameEditText.setError("First name is required");
                return;
            }
            if (TextUtils.isEmpty(last_name)) {
                lastNameEditText.setError("Last name is required");
                return;
            }
            if (TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Valid Email address is required");
                return;
            }
            Log.e(TAG, "phone:" + phone);
            if (phone.length() < 10) {
                phoneEditText.setError("Valid phone number is required");
                return;
            }
            if (TextUtils.isEmpty(currency)) {
                currencyTextView.setError("Currency is required");
                return;
            }
            if (TextUtils.isEmpty(amount) && !amount.matches("([0-9]{4})(\\.)([0-2]{2})")) {
                amountEditText.setError("Valid phone number is required");
                return;
            }

            // check network connectivity and alert where appropriate
            if (isNetworkAvailable(getActivity())) {
                // cancel network task if already running
                if (paypalAPITask != null && !paypalAPITask.isCancelled()) {
                    paypalAPITask.cancel(true);
                }
                // start network task on a separate thread
                paypalAPITask = new PaypalAPITask();
                paypalAPITask.execute(first_name, last_name, email, phone, currency, amount, payment_description);
            } else {
                String title = getResources().getString(R.string.no_internet_connection_title);
                String message = getResources().getString(R.string.no_internet_connection_message);

                ((MainActivity) getActivity()).showAlertDialog(title, message);
            }
        }

        ;
    };

    public class PaypalAPITask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String first_name = strings[0];
            String last_name = strings[1];
            String email = strings[2];
            String phone = strings[3];
            String currency = strings[4];
            String amount = strings[5];
            String payment_description = strings[6];

            Timer timer = new Timer();
            timer.schedule(new TaskKiller(this, TAG), MainActivity.ASYNC_TASK_TIMEOUT);
            try {

//                ComponentName cn1 = new ComponentName(getActivity(), "com.pesapal.pesapalandroid.PesapalSettingsActivity");
//                Intent intent1 = new Intent().setComponent(cn1);
//                intent1.putExtra("pkg","your package name");
//                startActivityForResult(intent1,SETTINGS_ACTIVITY_REQUEST_CODE);

                String unique_reference = (String) android.text.format.DateFormat.format("yyyyMMddhhmmss", new java.util.Date());

                double d_amount = Double.parseDouble(amount);
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String s_amount = decimalFormat.format(d_amount);
                d_amount = Double.parseDouble(s_amount);
                Log.e(TAG, "d_amount:" + d_amount);

                Payment payment = new Payment();
                payment.setReference(unique_reference);//transaction unique reference
                payment.setAmount(d_amount);
                payment.setAccount("merchant_id 20 payment");
                payment.setDescription(payment_description);
                payment.setEmail(email);
                payment.setCurrency(currency);
                payment.setFirstName(first_name);
                payment.setLastName(last_name);
                payment.setPhoneNumber(phone);
                ComponentName cn2 = new ComponentName(getActivity(), "com.pesapal.pesapalandroid.PesapalPayActivity");

                Intent intent2 = new Intent().setComponent(cn2);
                intent2.putExtra("payment", payment);
                startActivityForResult(intent2, MainActivity.PAYMENT_ACTIVITY_REQUEST_CODE);
            } catch (Exception e) {
                Log.d(TAG, "PaypalAPITask e:" + e.toString());
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            paypalAPITask.cancel(true);
        }
    }

    class TaskKiller extends TimerTask {
        private AsyncTask<?, ?, ?> mTask;
        private String TAG;

        public TaskKiller(AsyncTask<?, ?, ?> task, String TAG) {
            this.mTask = task;
            this.TAG = TAG;
        }

        public void run() {
            try {
                mTask.cancel(true);
                Log.v(TAG, "TaskKiller called");

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        String title = "ErrorTitle";
                        String message = "ErrorMessage";
                        Log.d(TAG, "");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}