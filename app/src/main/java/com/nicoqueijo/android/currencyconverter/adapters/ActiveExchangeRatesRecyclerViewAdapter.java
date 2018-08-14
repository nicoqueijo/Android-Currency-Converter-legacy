package com.nicoqueijo.android.currencyconverter.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.algorithms.CurrencyConversion;
import com.nicoqueijo.android.currencyconverter.helpers.CustomEditText;
import com.nicoqueijo.android.currencyconverter.helpers.Utility;
import com.nicoqueijo.android.currencyconverter.models.Currency;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class ActiveExchangeRatesRecyclerViewAdapter extends
        RecyclerView.Adapter<ActiveExchangeRatesRecyclerViewAdapter.ViewHolder> {

    public static final String TAG = ActiveExchangeRatesRecyclerViewAdapter.class.getSimpleName();

    private Context mContext;
    private List<Currency> mActiveCurrencies;
    private boolean onBind;
    private boolean newItemAdded;

    private NumberFormat numberFormatter;
    private DecimalFormat decimalFormatter;
    private String decimalSeparator;

    public ActiveExchangeRatesRecyclerViewAdapter(Context context,
                                                  List<Currency> activeCurrencies) {
        mContext = context;
        mActiveCurrencies = activeCurrencies;
        String pattern = "###,##0.00";
        numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault());
        decimalFormatter = (DecimalFormat) numberFormatter;
        decimalFormatter.applyPattern(pattern);
        decimalSeparator = String.valueOf(decimalFormatter
                .getDecimalFormatSymbols()
                .getDecimalSeparator());
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_active_exchange_rate, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        onBind = true;
        Currency currentCurrency = mActiveCurrencies.get(position);
        holder.mCurrencyCodeTextView.setText(currentCurrency.getTrimmedCurrencyCode());
        holder.mFlagImage.setImageResource(Utility.getDrawableResourceByName(currentCurrency
                .getCurrencyCode().toLowerCase(), mContext));
        BigDecimal conversionValue = currentCurrency.getConversionValue();
        String formattedConversionValue = decimalFormatter.format(conversionValue).equals
                ("0" + decimalSeparator + "00") ? "0" : decimalFormatter.format(conversionValue);
        holder.mConversionValueEditText.setText(formattedConversionValue);
        onBind = false;
    }

    @Override
    public int getItemCount() {
        return mActiveCurrencies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements TextWatcher {

        ImageView mFlagImage;
        TextView mCurrencyCodeTextView;
        CustomEditText mConversionValueEditText;

        ViewHolder(View itemView) {
            super(itemView);
            mFlagImage = itemView.findViewById(R.id.flag);
            mCurrencyCodeTextView = itemView.findViewById(R.id.currency_code);
            mConversionValueEditText = itemView.findViewById(R.id.conversion_value);
            String hint = "0" + decimalSeparator + "00";
            mConversionValueEditText.setHint(hint);
            mConversionValueEditText.setKeyListener(DigitsKeyListener
                    .getInstance("0123456789" + decimalSeparator));
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
         * Second scenario is on every new input from the EditText, that number is taken, converted
         * against the other currencies and updated on the UI. The exceptions are if what was
         * entered is an empty string or a sole decimal separator. Skips itself as it doesn't need
         * to do any conversion on the active EditText.
         *
         * @param s input string entered by user
         */
        private void processTextChange(CharSequence s) {
            Currency focusedCurrency = mActiveCurrencies.get(getAdapterPosition());
            if (onBind) {
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
            if (s.length() > 0 && !s.toString().equals(decimalSeparator)) {
                Number number = null;
                try {
                    number = decimalFormatter.parse(s.toString());
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
         * for further processing by testing it against two methods that:
         * 1) assures user didn't enter more than two decimal places e.g. 12.567
         * 2) assures user didn't enter two decimal separators e.g. 12.56.7
         *
         * @param s input string entered by user
         * @return whether the s is valid and can be processed
         */
        private boolean isInputValid(CharSequence s) {
            return (!inputAboveTwoDecimalPlaces(s) && !inputHasMoreThanOneSeparators(s));
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
            if (inputString.contains(decimalSeparator) &&
                    inputString.substring(inputString.indexOf(decimalSeparator) + 1)
                            .length() > 2) {
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
                if (String.valueOf(inputString.charAt(i)).equals(decimalSeparator)) {
                    occurrences++;
                }
            }
            if (occurrences > 1) {
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
                if (s.toString().startsWith(decimalSeparator)) {
                    s.insert(0, "0");
                } else if (s.toString().startsWith("0")) {
                    s.clear();
                }
            }
        }
    }
}
