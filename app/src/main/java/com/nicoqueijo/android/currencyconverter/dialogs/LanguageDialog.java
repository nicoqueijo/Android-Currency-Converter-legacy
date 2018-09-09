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

/**
 * DialogFragment that allows the user to change the app's language. Implements OnClickListener to
 * handle the language option clicks.
 */
public class LanguageDialog extends DialogFragment implements View.OnClickListener {

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

    private SharedPreferences mSharedPreferences;
    private Stack<RadioButton> mActiveRadioButton = new Stack<>();
    private LinearLayout mEnglishOption;
    private LinearLayout mSpanishOption;
    private RadioButton mEnglishRadioButton;
    private RadioButton mSpanishRadioButton;

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @return a new instance of fragment
     */
    public static LanguageDialog newInstance() {
        return new LanguageDialog();
    }

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
        initViews(view);
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
                changeLanguage(mEnglishRadioButton, Language.ENGLISH);
                break;
            case R.id.container_language_spanish:
                changeLanguage(mSpanishRadioButton, Language.SPANISH);
                break;
        }
    }

    /**
     * Initializes the Dialog's views.
     *
     * @param view the root view of the inflated hierarchy
     */
    private void initViews(View view) {
        mEnglishOption = view.findViewById(R.id.container_language_english);
        mSpanishOption = view.findViewById(R.id.container_language_spanish);
        mEnglishRadioButton = view.findViewById(R.id.choice_english);
        mSpanishRadioButton = view.findViewById(R.id.choice_spanish);
    }

    /**
     * Retrieves the language setting from the SharedPreferences file and sets that language to the
     * appropriate RadioButton. If this is the first time running the app SharedPreferences won't
     * have a language value and the default value will be the system language. If the system
     * language is not a supported language in this app it defaults to English.
     */
    private void restoreSavedLanguage() {
        String systemLanguage = Locale.getDefault().getLanguage();
        String savedLanguage = mSharedPreferences.getString("language", systemLanguage);
        if (savedLanguage.equals(Language.SPANISH.getLanguage())) {
            mSpanishRadioButton.setChecked(true);
            mActiveRadioButton.push(mSpanishRadioButton);
        } else {
            mEnglishRadioButton.setChecked(true);
            mActiveRadioButton.push(mEnglishRadioButton);
        }
    }

    /**
     * Sets all RadioButtons to be unclickable because their clicks are handled by their parent
     * view.
     */
    private void disableRadioButtons() {
        mEnglishRadioButton.setClickable(false);
        mSpanishRadioButton.setClickable(false);
    }

    /**
     * All the RadioButtons are grouped manually using a stack. Since only one radio button can be
     * pressed at a time, a stack is used to pop a button and push another when the user presses
     * buttons. When a button is pressed it pops the previous button (if any) and pushes the pressed
     * button. The language of the pressed button is saved to SharedPreferences and the locale is
     * changed to the new language. The host activity is recreated to update the views with the new
     * language and this dialog is dismissed.
     *
     * @param languageRadioButton the RadioButton pressed.
     * @param language            the language the user selected.
     */
    private void changeLanguage(RadioButton languageRadioButton, Language language) {
        if (!mActiveRadioButton.isEmpty()) {
            mActiveRadioButton.pop().setChecked(false);
        }
        mActiveRadioButton.push(languageRadioButton);
        languageRadioButton.setChecked(true);
        saveLanguage(language);
        setLocale(language.getLanguage());
        getActivity().recreate();
        dismiss();
    }

    /**
     * Saves the language that the user selected to SharedPreferences.
     *
     * @param language the language the user selected.
     */
    private void saveLanguage(Language language) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("language", language.getLanguage());
        editor.apply();
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
}
