package com.nicoqueijo.android.currencyconverter.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.algorithms.CurrencyConversion;
import com.nicoqueijo.android.currencyconverter.helpers.Constants;
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

    private NumberFormat numberFormatter = NumberFormat.getNumberInstance(Locale.US);
    private DecimalFormat decimalFormatter = (DecimalFormat) numberFormatter;

    public ActiveExchangeRatesRecyclerViewAdapter(Context context,
                                                  List<Currency> activeCurrencies) {
        mContext = context;
        mActiveCurrencies = activeCurrencies;
        String pattern = "###,###.##";
        decimalFormatter.applyPattern(pattern);
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
        holder.mCurrencyCodeTextView.setText(currentCurrency.getCurrencyCode()
                .substring(Constants.CURRENCY_CODE_STARTING_INDEX));
        holder.mFlagImage.setImageResource(Utility.getDrawableResourceByName(currentCurrency
                .getCurrencyCode().toLowerCase(), mContext));
        BigDecimal conversionValue = currentCurrency.getConversionValue();
        String formattedConversionValue = decimalFormatter.format(conversionValue);
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
            mConversionValueEditText.addTextChangedListener(this);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!isInputAboveTwoDecimalPlaces(s)) {
                processTextChange(s);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            cleanInput(s);
        }

        /**
         * Checks whether the input string has above two decimal places.
         * Source: https://stackoverflow.com/a/33548446/5906793
         *
         * @param s input string entered by user
         * @return whether input string has above two decimal places
         */
        private boolean isInputAboveTwoDecimalPlaces(CharSequence s) {
            String inputString = s.toString();
            if (inputString.contains(".") && inputString.substring(inputString.indexOf(".") + 1)
                    .length() > 2) {
                mConversionValueEditText.setText(inputString.substring(0, inputString.length() - 1));
                mConversionValueEditText.setSelection(mConversionValueEditText.getText().length());
                return true;
            }
            return false;
        }

        /**
         * Performs the currency conversion of all exchange rates in the list and updates the UI.
         * On every new input from the EditText, that number is taken, converted against the other
         * currencies and updated on the UI. The exceptions are if what was entered is an empty
         * string or a sole decimal point. Skips itself as it doesn't need to do any conversion
         * on the active EditText.
         *
         * @param s input string entered by user
         */
        private void processTextChange(CharSequence s) {
            if (s.length() > 0 && !s.toString().equals(".")) {
                Currency focusedCurrency = mActiveCurrencies.get(getAdapterPosition());
                Number number = null;
                try {
                    number = decimalFormatter.parse(s.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                double numberAsDouble = number.doubleValue();
                focusedCurrency.setConversionValue(new BigDecimal(numberAsDouble));
                for (int i = 0; i < mActiveCurrencies.size(); i++) {
                    if (!onBind) {
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
//            else {
//                if (!onBind) {
//                    for (int i = 0; i < mActiveCurrencies.size(); i++) {
//                        Currency ithCurrency = mActiveCurrencies.get(i);
//                        ithCurrency.setConversionValue(new BigDecimal(0.0));
//                        notifyItemChanged(i);
//                    }
//                }
//            }
        }

        /**
         * Appends a leading zero if user starts input with a decimal point.
         * Clears the input to en empty string if user starts input with a zero.
         *
         * @param s a handle to the contents of the EditText
         */
        private void cleanInput(Editable s) {
            if (s.toString().length() == 1) {
                if (s.toString().startsWith(".")) {
                    s.insert(0, "0");
                } else if (s.toString().startsWith("0")) {
                    s.clear();
                }
            }
        }
    }
}
