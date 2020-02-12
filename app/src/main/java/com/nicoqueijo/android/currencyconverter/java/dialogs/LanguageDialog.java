package com.nicoqueijo.android.currencyconverter.java.dialogs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.java.activities.MainActivity;
import com.nicoqueijo.android.currencyconverter.java.helpers.Utility;

import java.util.Locale;

/**
 * Dialog that allows the user to change the app's language. Implements OnClickListener to handle
 * the language option clicks.
 */
public class LanguageDialog extends SettingsDialog implements View.OnClickListener {

    public enum Language {
        ENGLISH("en"),
        SPANISH("es");

        private String language;

        public String getLanguage() {
            return this.language;
        }

        Language(String language) {
            this.language = language;
        }
    }

    public static final String TAG = DialogFragment.class.getSimpleName();

    private RadioButton mEnglishRadioButton;
    private RadioButton mSpanishRadioButton;

    /**
     * Factory method to create a new instance of this Fragment using the provided parameters.
     *
     * @return a new instance of Fragment
     */
    public static LanguageDialog newInstance() {
        return new LanguageDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_language, container, false);
        Utility.roundDialogCorners(this);
        initViews(view);
        restoreSavedLanguage();
        return view;
    }

    /**
     * Initializes the Dialog's views and registers the onClickListeners.
     *
     * @param view the root view of the inflated hierarchy
     */
    public void initViews(View view) {
        mEnglishRadioButton = view.findViewById(R.id.radio_button_english);
        mSpanishRadioButton = view.findViewById(R.id.radio_button_spanish);
        mEnglishRadioButton.setOnClickListener(this);
        mSpanishRadioButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radio_button_english:
                changeLanguage(mEnglishRadioButton, Language.ENGLISH);
                break;
            case R.id.radio_button_spanish:
                changeLanguage(mSpanishRadioButton, Language.SPANISH);
                break;
        }
    }

    /**
     * Saves the language of the pressed RadioButton to SharedPreferences and the locale is
     * changed to the new language. The host activity is recreated to update the views with the
     * new language and this dialog is dismissed.
     *
     * @param selectedRadioButton the button pushed that triggered this method.
     * @param language            the language the user selected.
     */
    private void changeLanguage(RadioButton selectedRadioButton, Language language) {
        if (mActiveRadioButton.pop() == selectedRadioButton) {
            dismiss();
            return;
        }
        saveLanguage(language);
        MainActivity hostActivity = (MainActivity) getActivity();
        hostActivity.setLocale(language.getLanguage());
        hostActivity.recreate();
        dismiss();
    }

    /**
     * Retrieves the language setting from the SharedPreferences file and sets that language to the
     * appropriate RadioButton. If this is the first time running the app SharedPreferences won't
     * have a language value and the default value will be the system language. If the system
     * language is not a supported language in this app it defaults to English.
     */
    private void restoreSavedLanguage() {
        String systemLanguage = Locale.getDefault().getLanguage();
        String savedLanguage = mSharedPrefsProperties.getString("language", systemLanguage);
        if (Language.SPANISH.getLanguage().equals(savedLanguage)) {
            mSpanishRadioButton.setChecked(true);
            mActiveRadioButton.push(mSpanishRadioButton);
        } else {
            mEnglishRadioButton.setChecked(true);
            mActiveRadioButton.push(mEnglishRadioButton);
        }
    }

    /**
     * Saves the language the user selected to SharedPreferences.
     *
     * @param language the language the user selected.
     */
    private void saveLanguage(Language language) {
        SharedPreferences.Editor editor = mSharedPrefsProperties.edit();
        editor.putString("language", language.getLanguage());
        editor.apply();
    }
}
