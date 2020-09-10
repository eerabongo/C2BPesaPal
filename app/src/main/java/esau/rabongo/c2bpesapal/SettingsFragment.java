package esau.rabongo.c2bpesapal;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;


public class SettingsFragment extends Fragment {
    private String TAG = this.getClass().getCanonicalName();
    private AppSharedPreferences appSharedPreferences;
    private static SettingsFragment SettingsFragmentInstance;

    private TextInputEditText consumerKeyTextInputEditText;
    private TextInputEditText consumerSecretTextInputEditText;
    private Spinner currencySpinner;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        SettingsFragmentInstance = fragment;
        return fragment;
    }

    public static SettingsFragment getFragmentInstance() {
        return SettingsFragmentInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_settings, container, false);
        // initialize shared preferences
        appSharedPreferences = AppSharedPreferences.getClassInstance();
        appSharedPreferences.init(getActivity());
        // initialize and populate fields
        consumerKeyTextInputEditText = fragment_view.findViewById(R.id.consumerKeyTextInputEditText);
        consumerKeyTextInputEditText.setText(appSharedPreferences.getConsumerKey());
        consumerSecretTextInputEditText = fragment_view.findViewById(R.id.consumerSecretTextInputEditText);
        consumerSecretTextInputEditText.setText(appSharedPreferences.getConsumerSecret());
        currencySpinner = fragment_view.findViewById(R.id.currencySpinner);
        ArrayAdapter<CharSequence> currencySpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.currency_array, android.R.layout.simple_spinner_item);
        currencySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(currencySpinnerAdapter);
        int currencySpinnerPosition = currencySpinnerAdapter.getPosition(appSharedPreferences.getCurrency());
        currencySpinner.setSelection(currencySpinnerPosition);
        // add change listeners to fields
        consumerKeyTextInputEditText.addTextChangedListener(consumerKeyTextInputEditTextListener);
        consumerSecretTextInputEditText.addTextChangedListener(consumerSecretTextInputEditTextListener);
        currencySpinner.setOnItemSelectedListener(currencySpinnerListener);

        return fragment_view;
    }

    private TextWatcher consumerKeyTextInputEditTextListener = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            appSharedPreferences.setConsumerKey(s.toString());
        }
    };

    private TextWatcher consumerSecretTextInputEditTextListener = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            appSharedPreferences.setConsumerSecret(s.toString());
        }
    };

    private AdapterView.OnItemSelectedListener currencySpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            appSharedPreferences.setCurrency(currencySpinner.getSelectedItem().toString());
            //update currency value in HomeFragment view
            HomeFragment.getFragmentInstance().setCurrencyFromSettings(appSharedPreferences.getCurrency());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
            Log.d(TAG, "currencySpinnerListener onNothingSelected");
        }
    };
}