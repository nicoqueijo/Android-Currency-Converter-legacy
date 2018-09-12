package com.nicoqueijo.android.currencyconverter.adapters;

import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.algorithms.CurrencyConversion;
import com.nicoqueijo.android.currencyconverter.helpers.CustomEditText;
import com.nicoqueijo.android.currencyconverter.helpers.SwipeAndDragHelper;
import com.nicoqueijo.android.currencyconverter.helpers.Utility;
import com.nicoqueijo.android.currencyconverter.models.Currency;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for the RecyclerView that displays the exchange rates the user is interacting with.
 * Implements SwipeAndDragHelper.IActionCompletionContract to remove and reorder items in the list.
 */
public class ActiveExchangeRatesAdapter extends
        RecyclerView.Adapter<ActiveExchangeRatesAdapter.ViewHolder>
        implements SwipeAndDragHelper.IActionCompletionContract {

    public static final String TAG = ActiveExchangeRatesAdapter.class.getSimpleName();

    private Context mContext;
    private List<Currency> mActiveCurrencies;
    private FloatingActionButton mFloatingActionButton;
    private boolean mOnBind;

    private NumberFormat mNumberFormatter;
    private DecimalFormat mDecimalFormatter;
    private String mDecimalSeparator;
    private String mConversionPattern = "###,##0.00";
    private Animation mAnimShake;
    private Vibrator mVibrator;

    /**
     * Constructor for the adapter.
     *
     * @param context              hosting context
     * @param activeCurrencies     the currencies the user is interacting with
     * @param floatingActionButton view to be used as the first param of the Snackbar's make method
     */
    public ActiveExchangeRatesAdapter(Context context, List<Currency> activeCurrencies,
                                      FloatingActionButton floatingActionButton) {
        mContext = context;
        mActiveCurrencies = activeCurrencies;
        mFloatingActionButton = floatingActionButton;
        mNumberFormatter = NumberFormat.getNumberInstance(Locale.getDefault());
        mDecimalFormatter = (DecimalFormat) mNumberFormatter;
        mDecimalFormatter.applyPattern(mConversionPattern);
        mDecimalSeparator = String.valueOf(mDecimalFormatter
                .getDecimalFormatSymbols()
                .getDecimalSeparator());
        mAnimShake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_active_exchange_rate, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mOnBind = true;
        Currency currentCurrency = mActiveCurrencies.get(position);
        holder.mCurrencyCodeTextView.setText(currentCurrency.getTrimmedCurrencyCode());
        holder.mFlagImage.setImageResource(Utility.getDrawableResourceByName(currentCurrency
                .getCurrencyCode().toLowerCase(), mContext));
        BigDecimal conversionValue = currentCurrency.getConversionValue();
        String formattedConversionValue = mDecimalFormatter.format(conversionValue).equals
                ("0" + mDecimalSeparator + "00") ? "0" : mDecimalFormatter.format(conversionValue);
        holder.mConversionValueEditText.setText(formattedConversionValue);
        mOnBind = false;
    }

    @Override
    public int getItemCount() {
        return mActiveCurrencies.size();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        Currency targetCurrency = mActiveCurrencies.get(oldPosition);
        mActiveCurrencies.remove(oldPosition);
        mActiveCurrencies.add(newPosition, targetCurrency);
        notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void onViewSwiped(RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        final Currency swipedCurrency = mActiveCurrencies.get(position);
        final BigDecimal conversionValue = swipedCurrency.getConversionValue();
        swipedCurrency.setSelected(false);
        swipedCurrency.setConversionValue(new BigDecimal(0.0));
        mActiveCurrencies.remove(position);
        notifyItemRemoved(position);
        Snackbar snackbar = Snackbar.make(mFloatingActionButton, R.string.item_removed,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipedCurrency.setConversionValue(conversionValue);
                swipedCurrency.setSelected(true);
                mActiveCurrencies.add(position, swipedCurrency);
                notifyItemInserted(position);
            }
        });
        snackbar.show();
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements TextWatcher {

        public RelativeLayout mItemBackground;
        public LinearLayout mItemForeground;
        public ImageView mDeleteIconStart;
        public ImageView mDeleteIconEnd;
        public ImageView mFlagImage;
        public TextView mCurrencyCodeTextView;
        public CustomEditText mConversionValueEditText;

        ViewHolder(View itemView) {
            super(itemView);
            mItemBackground = itemView.findViewById(R.id.item_background);
            mItemForeground = itemView.findViewById(R.id.item_foreground);
            mDeleteIconStart = itemView.findViewById(R.id.delete_icon_start);
            mDeleteIconEnd = itemView.findViewById(R.id.delete_icon_end);
            mFlagImage = itemView.findViewById(R.id.flag);
            mCurrencyCodeTextView = itemView.findViewById(R.id.currency_code);
            mConversionValueEditText = itemView.findViewById(R.id.conversion_value);

            String hint = "0" + mDecimalSeparator + "00";
            mConversionValueEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            mConversionValueEditText.setHint(hint);
            mConversionValueEditText.setKeyListener(DigitsKeyListener
                    .getInstance("0123456789" + mDecimalSeparator));
            mConversionValueEditText.addTextChangedListener(this);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isInputValid(s)) {
                processTextChange(s);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            cleanInput(s);
        }

        /**
         * Performs the currency conversion of all exchange rates in the list and updates the UI.
         * <p>
         * First scenario is when a new currency is received after selecting it from the dialog
         * that the FAB pops up.
         * <p>
         * Second scenario is when all the characters are removed from the focused currency. This
         * sets the other currencies to 0 and updates the UI.
         * <p>
         * Third scenario is on every new input from the EditText, that number is taken, converted
         * against the other currencies and updated on the UI. The exceptions are if what was
         * entered is an empty string or a sole decimal separator. Skips itself as it doesn't need
         * to do any conversion on the active EditText.
         *
         * @param s input string entered by user
         */
        private void processTextChange(CharSequence s) {
            Currency focusedCurrency = mActiveCurrencies.get(getAdapterPosition());
            if (mOnBind) {
                Currency newlyAddedCurrency = mActiveCurrencies.get(getItemCount() - 1);
                BigDecimal amount = focusedCurrency.getConversionValue();
                double fromRate = focusedCurrency.getExchangeRate();
                double toRate = newlyAddedCurrency.getExchangeRate();
                BigDecimal convertedCurrency = CurrencyConversion
                        .currencyConverter(amount, fromRate, toRate);
                convertedCurrency = Utility.roundBigDecimal(convertedCurrency);
                newlyAddedCurrency.setConversionValue(convertedCurrency);
                return;
            }
            if (s.length() == 0) {
                for (int i = 0; i < mActiveCurrencies.size(); i++) {
                    mActiveCurrencies.get(i).setConversionValue(new BigDecimal("0"));
                    notifyItemChanged(i);
                }
            }
            if (s.length() > 0 && !s.toString().equals(mDecimalSeparator)) {
                Number number = null;
                try {
                    number = mDecimalFormatter.parse(s.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                double numberAsDouble = number.doubleValue();
                focusedCurrency.setConversionValue(new BigDecimal(numberAsDouble));
                for (int i = 0; i < mActiveCurrencies.size(); i++) {
                    if (i == getAdapterPosition()) {
                        continue;
                    }
                    Currency ithCurrency = mActiveCurrencies.get(i);
                    BigDecimal amount = focusedCurrency.getConversionValue();
                    double fromRate = focusedCurrency.getExchangeRate();
                    double toRate = ithCurrency.getExchangeRate();
                    BigDecimal convertedCurrency = CurrencyConversion
                            .currencyConverter(amount, fromRate, toRate);
                    convertedCurrency = Utility.roundBigDecimal(convertedCurrency);
                    ithCurrency.setConversionValue(convertedCurrency);
                    notifyItemChanged(i);
                }
            }
        }

        /**
         * Checks if the input string that is retrieved from the EditText is eligible
         * for further processing by testing it against three methods that:
         * 1) assures user didn't enter more than two decimal places e.g. 12.567
         * 2) assures user didn't enter two decimal separators e.g. 12.56.7
         * 3) assures adding an extra digit doesn't potentially cause overflow
         *
         * @param s input string entered by user
         * @return whether the s is valid and can be processed
         */
        private boolean isInputValid(CharSequence s) {
            return (!inputAboveTwoDecimalPlaces(s)
                    && !inputHasMoreThanOneSeparators(s)
                    && !causesOverflow(s));
        }

        /**
         * Checks whether the input string has two or more digits after the decimal separator.
         * If it does it removes the last one entered.
         * Credit: https://stackoverflow.com/a/33548446/5906793
         *
         * @param s input string entered by user
         * @return whether input string has above two decimal places
         */
        private boolean inputAboveTwoDecimalPlaces(CharSequence s) {
            String inputString = s.toString();
            if (inputString.contains(mDecimalSeparator) &&
                    inputString.substring(inputString.indexOf(mDecimalSeparator) + 1)
                            .length() > 2) {
                mVibrator.vibrate(25L);
                itemView.findViewById(R.id.conversion_value).startAnimation(mAnimShake);
                mConversionValueEditText.setText(inputString.substring(0, inputString.length() - 1));
                mConversionValueEditText.setSelection(mConversionValueEditText.getText().length());
                return true;
            }
            return false;
        }

        /**
         * Checks whether the input string has more than one decimal separator.
         * If it does it removes the last one entered.
         *
         * @param s input string entered by user
         * @return whether there is more than one decimal separators
         */
        private boolean inputHasMoreThanOneSeparators(CharSequence s) {
            String inputString = s.toString();
            int occurrences = 0;
            for (int i = 0; i < inputString.length(); i++) {
                if (String.valueOf(inputString.charAt(i)).equals(mDecimalSeparator)) {
                    occurrences++;
                }
            }
            if (occurrences > 1) {
                mVibrator.vibrate(25L);
                itemView.findViewById(R.id.conversion_value).startAnimation(mAnimShake);
                mConversionValueEditText.setText(inputString.substring(0, inputString.length() - 1));
                mConversionValueEditText.setSelection(mConversionValueEditText.getText().length());
                return true;
            }
            return false;
        }

        /**
         * Checks whether the input string can cause overflow. To be on the safe side we are not
         * allowing more than 10 digits to be entered. This includes the two decimal places.
         * If user entered a 11th digit it is removed.
         *
         * @param s input string entered by user
         * @return whether adding an additional digit may cause overflow
         */
        private boolean causesOverflow(CharSequence s) {
            final int MAX_DIGITS_ALLOWED = 10;
            final long VIBRATION_LENGTH = 1L;
            final String ANY_NON_DIGIT_REGEX = "\\D";
            String inputString = s.toString();
            inputString = inputString.replaceAll(ANY_NON_DIGIT_REGEX, "");
            if (inputString.length() > MAX_DIGITS_ALLOWED && !mOnBind) {
                mVibrator.vibrate(VIBRATION_LENGTH);
                itemView.findViewById(R.id.conversion_value).startAnimation(mAnimShake);
                mConversionValueEditText.setText(inputString.substring(0, inputString.length() - 1));
                mConversionValueEditText.setSelection(mConversionValueEditText.getText().length());
                return true;
            }
            return false;
        }

        /**
         * Appends a leading zero if user starts input with a decimal separator.
         * Clears the input to en empty string if user starts input with a zero.
         *
         * @param s a handle to the contents of the EditText
         */
        private void cleanInput(Editable s) {
            if (s.toString().length() == 1) {
                if (s.toString().startsWith(mDecimalSeparator)) {
                    s.insert(0, "0");
                } else if (s.toString().startsWith("0")) {
                    s.clear();
                }
            }
        }
    }
}
