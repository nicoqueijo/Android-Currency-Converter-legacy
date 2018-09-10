package com.nicoqueijo.android.currencyconverter.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.nicoqueijo.android.currencyconverter.R;

import java.util.Stack;

/**
 * DialogFragment that allows the user to change the app's theme. Implements OnClickListener to
 * handle the theme option clicks.
 */
public class ThemeDialog extends DialogFragment implements View.OnClickListener {

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

    private SharedPreferences mSharedPreferences;
    private Stack<RadioButton> mActiveRadioButton = new Stack<>();
    private LinearLayout mLightOption;
    private LinearLayout mDarkOption;
    private RadioButton mLightRadioButton;
    private RadioButton mDarkRadioButton;

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @return a new instance of fragment
     */
    public static ThemeDialog newInstance() {
        return new ThemeDialog();
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
        View view = inflater.inflate(R.layout.dialog_theme, container, false);
        initViews(view);
        restoreSavedTheme();
        disableRadioButtons();
        mLightOption.setOnClickListener(this);
        mDarkOption.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.container_theme_light:
                changeTheme(mLightRadioButton, Theme.LIGHT);
                break;
            case R.id.container_theme_dark:
                changeTheme(mDarkRadioButton, Theme.DARK);
                break;
        }
    }

    /**
     * Initializes the Dialog's views.
     *
     * @param view the root view of the inflated hierarchy
     */
    private void initViews(View view) {
        mLightOption = view.findViewById(R.id.container_theme_light);
        mDarkOption = view.findViewById(R.id.container_theme_dark);
        mLightRadioButton = view.findViewById(R.id.choice_light);
        mDarkRadioButton = view.findViewById(R.id.choice_dark);
    }

    /**
     * Retrieves the theme setting from the SharedPreferences file and sets that theme to the
     * appropriate RadioButton. If this is the first time running the app SharedPreferences won't
     * have a theme value and the default value will be the light theme.
     */
    private void restoreSavedTheme() {
        int defaultTheme = R.style.AppThemeLight;
        int savedTheme = mSharedPreferences.getInt("theme", defaultTheme);
        if (savedTheme == R.style.AppThemeDark) {
            mDarkRadioButton.setChecked(true);
            mActiveRadioButton.push(mDarkRadioButton);
        } else {
            mLightRadioButton.setChecked(true);
            mActiveRadioButton.push(mLightRadioButton);
        }
    }

    /**
     * Sets all RadioButtons to be unclickable because their clicks are handled by their parent
     * view.
     */
    private void disableRadioButtons() {
        mLightRadioButton.setClickable(false);
        mDarkRadioButton.setClickable(false);
    }

    /**
     * All the RadioButtons are grouped manually using a stack. Since only one radio button can be
     * pressed at a time, a stack is used to pop a button and push another when the user presses
     * buttons. When a button is pressed it pops the previous button (if any) and pushes the pressed
     * button. The theme of the pressed button is saved to SharedPreferences and the app theme is
     * changed to the new theme. The host activity is recreated to update the views with the new
     * theme and this dialog is dismissed.
     *
     * @param selectedRadioButton the RadioButton pressed.
     * @param theme               the theme the user selected.
     */
    private void changeTheme(RadioButton selectedRadioButton, Theme theme) {
        if (mActiveRadioButton.peek() == selectedRadioButton) {
            dismiss();
            return;
        }
        if (!mActiveRadioButton.isEmpty()) {
            mActiveRadioButton.pop().setChecked(false);
        }
        mActiveRadioButton.push(selectedRadioButton);
        selectedRadioButton.setChecked(true);
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
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("theme", theme.getTheme());
        editor.apply();
    }
}
