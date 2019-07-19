package com.nicoqueijo.android.currencyconverter.dialogs;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.helpers.Utility;

/**
 * Dialog that allows the user to change the app's theme. Implements OnClickListener to handle the
 * theme option clicks.
 */
public class ThemeDialog extends SettingsDialog implements View.OnClickListener {

    public enum Theme {
        LIGHT(R.style.AppThemeLight),
        DARK(R.style.AppThemeDark);

        private int theme;

        public int getTheme() {
            return this.theme;
        }

        Theme(int theme) {
            this.theme = theme;
        }
    }

    public static final String TAG = ThemeDialog.class.getSimpleName();

    private RadioButton mLightRadioButton;
    private RadioButton mDarkRadioButton;

    /**
     * Factory method to create a new instance of this Fragment using the provided parameters.
     *
     * @return a new instance of Fragment
     */
    public static ThemeDialog newInstance() {
        return new ThemeDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_theme, container, false);
        Utility.roundDialogCorners(this);
        initViews(view);
        restoreSavedTheme();
        return view;
    }

    /**
     * Initializes the Dialog's views and registers the onClickListeners.
     *
     * @param view the root view of the inflated hierarchy
     */
    public void initViews(View view) {
        mLightRadioButton = view.findViewById(R.id.radio_button_light);
        mDarkRadioButton = view.findViewById(R.id.radio_button_dark);
        mLightRadioButton.setOnClickListener(this);
        mDarkRadioButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radio_button_light:
                changeTheme(mLightRadioButton, Theme.LIGHT);
                break;
            case R.id.radio_button_dark:
                changeTheme(mDarkRadioButton, Theme.DARK);
                break;
        }
    }

    /**
     * Retrieves the theme setting from the SharedPreferences file and sets that theme to the
     * appropriate RadioButton. If this is the first time running the app SharedPreferences won't
     * have a theme value and the default value will be the light theme.
     */
    private void restoreSavedTheme() {
        int defaultTheme = R.style.AppThemeLight;
        int savedTheme = mSharedPrefsProperties.getInt("theme", defaultTheme);
        if (savedTheme == R.style.AppThemeDark) {
            mDarkRadioButton.setChecked(true);
            mActiveRadioButton.push(mDarkRadioButton);
        } else {
            mLightRadioButton.setChecked(true);
            mActiveRadioButton.push(mLightRadioButton);
        }
    }

    /**
     * Saves the theme of the pressed RadioButton to SharedPreferences and the app theme is
     * changed to the new theme. The host activity is recreated to update the views with the new
     * theme and this dialog is dismissed.
     *
     * @param selectedRadioButton the button pushed that triggered this method.
     * @param theme               the theme the user selected.
     */
    private void changeTheme(RadioButton selectedRadioButton, Theme theme) {
        if (mActiveRadioButton.pop() == selectedRadioButton) {
            dismiss();
            return;
        }
        saveTheme(theme);
        getActivity().setTheme(theme.getTheme());
        getActivity().recreate();
        dismiss();
    }

    /**
     * Saves the theme the user selected to SharedPreferences.
     *
     * @param theme the theme the user selected.
     */
    private void saveTheme(Theme theme) {
        SharedPreferences.Editor editor = mSharedPrefsProperties.edit();
        editor.putInt("theme", theme.getTheme());
        editor.apply();
    }
}
