package com.nicoqueijo.android.currencyconverter.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.nicoqueijo.android.currencyconverter.R;

import java.util.Locale;
import java.util.Stack;

public class LanguageDialog extends DialogFragment implements View.OnClickListener {

    public enum Language {
        en, // English
        es  // Spanish
    }

    public static final String TAG = DialogFragment.class.getSimpleName();

    private SharedPreferences mSharedPreferences;
    private Stack<RadioButton> mActiveRadioButton = new Stack<>();
    private LinearLayout mEnglishOption;
    private LinearLayout mSpanishOption;
    private RadioButton mEnglishRadioButton;
    private RadioButton mSpanishRadioButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName()
                .concat(".settings"), Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_language, container, false);

        mEnglishOption = view.findViewById(R.id.container_language_english);
        mEnglishRadioButton = view.findViewById(R.id.choice_english);
        mSpanishOption = view.findViewById(R.id.container_language_spanish);
        mSpanishRadioButton = view.findViewById(R.id.choice_spanish);

        restoreSavedLanguage();
        disableRadioButtons();

        mEnglishOption.setOnClickListener(this);
        mSpanishOption.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.container_language_english:
                changeLanguage(mEnglishRadioButton, Language.en);
                break;
            case R.id.container_language_spanish:
                changeLanguage(mSpanishRadioButton, Language.es);
                break;
        }
    }

    private void disableRadioButtons() {
        mEnglishRadioButton.setClickable(false);
        mSpanishRadioButton.setClickable(false);
    }

    private void changeLanguage(RadioButton languageRadioButton, Language language) {
        if (!mActiveRadioButton.isEmpty()) {
            mActiveRadioButton.pop().setChecked(false);
        }
        languageRadioButton.setChecked(true);
        mActiveRadioButton.push(languageRadioButton);
        saveLanguage(language);
        setLocale(language.name());
//        mCommunicator.onDialogMessage(language.name());
        dismiss();
    }

    private void saveLanguage(Language language) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("language", language.name());
        editor.apply();
    }

    private void restoreSavedLanguage() {
        String systemLanguage = Locale.getDefault().getLanguage();
        String savedLanguage = mSharedPreferences.getString("language", systemLanguage);
        if (savedLanguage.equals(Language.es.name())) {
            mSpanishRadioButton.setChecked(true);
            mActiveRadioButton.push(mSpanishRadioButton);
        } else {
            mEnglishRadioButton.setChecked(true);
            mActiveRadioButton.push(mEnglishRadioButton);
        }
    }

    /**
     * Sets the locale to a new language.
     *
     * @param lang the new language to set the app to.
     */
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(myLocale);
        resources.updateConfiguration(configuration, displayMetrics);
    }

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @return a new instance of fragment
     */
    public static LanguageDialog newInstance() {
        return new LanguageDialog();
    }

}
