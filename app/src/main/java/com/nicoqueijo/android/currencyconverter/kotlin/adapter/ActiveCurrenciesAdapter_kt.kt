package com.nicoqueijo.android.currencyconverter.kotlin.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.databinding.RowActiveCurrencyKtBinding
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.*
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.ActiveCurrenciesViewModel_kt
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ActiveCurrenciesAdapter_kt(private val viewModel: ActiveCurrenciesViewModel_kt) :
        ListAdapter<Currency, ActiveCurrenciesAdapter_kt.ViewHolder>(CurrencyDiffUtilCallback()),
        SwipeAndDragHelper_kt.ActionCompletionContract {

    private var decimalFormatter: DecimalFormat
    private var decimalSeparator: String
    private var animShake: Animation

    init {
        val numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault())
        val conversionPattern = "###,##0.0000"
        decimalFormatter = numberFormatter as DecimalFormat
        decimalFormatter.applyPattern(conversionPattern)
        decimalSeparator = decimalFormatter.decimalFormatSymbols.decimalSeparator.toString()
        animShake = AnimationUtils.loadAnimation(viewModel.getApplication(), R.anim.shake)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowActiveCurrencyKtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.currency = viewModel.adapterActiveCurrencies[position]
        holder.conversionValue.hint = viewModel.adapterActiveCurrencies[position].hintValue.toString()
        Log.d("ActiveCurrency", "${viewModel.adapterActiveCurrencies[position].hintValue}")
    }

    fun setCurrencies(currencies: MutableList<Currency>) {
        viewModel.adapterActiveCurrencies = currencies
        submitList(currencies)
    }

    override fun onViewMoved(oldPosition: Int, newPosition: Int) {
        viewModel.handleMove(oldPosition, newPosition)
        notifyItemMoved(oldPosition, newPosition)
    }

    override fun onViewSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
//        val conversionValue = swipedCurrency.conversionValue
        viewModel.handleSwipe(position)
        Snackbar.make(viewHolder!!.itemView, R.string.item_removed, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) {
                    viewModel.handleSwipeUndo()
                }.show()
    }

    override fun onViewDropped() {
        viewModel.handleDrop()
    }

    fun updateHints() {
        val focusedCurrency = viewModel.focusedCurrency.value
        viewModel.adapterActiveCurrencies.filter { currency ->
            currency != viewModel.focusedCurrency.value
        }.forEach { currency ->
            currency.hintValue = CurrencyConversion.convertCurrency(BigDecimal("1"),
                    focusedCurrency!!.exchangeRate,
                    currency.exchangeRate)
        }
    }

    inner class ViewHolder(val binding: RowActiveCurrencyKtBinding) :
            RecyclerView.ViewHolder(binding.root),
            TextWatcher {

        val rowForeground: ConstraintLayout = itemView.findViewById(R.id.row_foreground_kt)
        val deleteIconStart: ImageView = itemView.findViewById(R.id.delete_icon_start_kt)
        val deleteIconEnd: ImageView = itemView.findViewById(R.id.delete_icon_end_kt)
        val flag: ImageView = itemView.findViewById(R.id.flag_kt)
        val currencyCode: TextView = itemView.findViewById(R.id.currency_code_kt)
        val conversionValue: CustomEditText_kt = itemView.findViewById(R.id.conversion_value_kt)

        init {
            conversionValue.imeOptions = EditorInfo.IME_ACTION_DONE
            conversionValue.keyListener = DigitsKeyListener.getInstance("0123456789$decimalSeparator")
            conversionValue.hint = "0${decimalSeparator}0000"
            conversionValue.addTextChangedListener(this)
            conversionValue.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    viewModel.focusedCurrency.postValue(viewModel.adapterActiveCurrencies[adapterPosition])
                    conversionValue.hint = "1"
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            isInputValid(s)
        }

        override fun afterTextChanged(s: Editable?) {
            ///////////////////////////////////////////////////////////////////////////////////////////////
            /*try {
                conversionValue.removeTextChangedListener(this)
                val value: String = conversionValue.text.toString()
                if (value != "") {
                    if (value.startsWith(".")) {
                        conversionValue.setText("0.")
                    }
                    if (value.startsWith("0") && !value.startsWith("0.")) {
                        conversionValue.setText("")
                    }
                    val str: String = conversionValue.text.toString().replace(",", "")
                    if (value != "") conversionValue.setText(getDecimalFormattedString(str))
                    conversionValue.setSelection(conversionValue.text.toString().length)
                }
                conversionValue.addTextChangedListener(this)
                return
            } catch (ex: Exception) {
                ex.printStackTrace()
                conversionValue.addTextChangedListener(this)
            }*/
            ///////////////////////////////////////////////////////////////////////////////////////////////
        }

        fun getDecimalFormattedString(value: String): String? {
            val lst = StringTokenizer(value, ".")
            var str1 = value
            var str2 = ""
            if (lst.countTokens() > 1) {
                str1 = lst.nextToken()
                str2 = lst.nextToken()
            }
            var str3 = ""
            var i = 0
            var j = -1 + str1.length
            if (str1[-1 + str1.length] == '.') {
                j--
                str3 = "."
            }
            var k = j
            while (true) {
                if (k < 0) {
                    if (str2.length > 0) str3 = "$str3.$str2"
                    return str3
                }
                if (i == 3) {
                    str3 = ",$str3"
                    i = 0
                }
                str3 = str1[k].toString() + str3
                i++
                k--
            }
        }

        fun trimCommaOfString(string: String): String? {
//        String returnString;
            return if (string.contains(",")) {
                string.replace(",", "")
            } else {
                string
            }
        }

        private fun isInputValid(s: CharSequence?): Boolean {
            return (!isInputTooLarge(s) &&
                    !isInputAboveFourDecimalPlaces(s) &&
                    !isInputAboveOneDecimalSeparator(s) &&
                    isInputLeadingZero(s))
        }

        private fun isInputAboveFourDecimalPlaces(s: CharSequence?): Boolean {
            val input = s.toString()
            if (input.contains(decimalSeparator) &&
                    input.substring(input.indexOf(decimalSeparator) + 1).length > 4) {
                dropLastWithFeedback(input)
                return true
            }
            return false
        }

        private fun isInputAboveOneDecimalSeparator(s: CharSequence?): Boolean {
            val input = s.toString()
            val decimalSeparatorCount = input.asSequence().count { char ->
                char.toString() == decimalSeparator
            }
            if (decimalSeparatorCount > 1) {
                dropLastWithFeedback(input)
                return true
            }
            return false
        }

        private fun isInputTooLarge(s: CharSequence?): Boolean {
            val maxDigitsAllowed = 20
            val input = s.toString()
            if (!input.contains(decimalSeparator) && input.length > maxDigitsAllowed) {
                dropLastWithFeedback(input)
                return true
            }
            return false
        }

        private fun isInputLeadingZero(s: CharSequence?): Boolean {
            val input = s.toString()
            if (input.length == 2) {
                if (input == "00") {
                    dropLastWithFeedback(input)
                    return true
                }
                if (input[0] == '0' && input[1] != decimalSeparator.single()) {
                    conversionValue.setText(input[1].toString())
                    return true
                }
            }
            return false
        }

        private fun dropLastWithFeedback(input: String) {
            Utils.vibrate(viewModel.getApplication())
            conversionValue.startAnimation(animShake)
            conversionValue.setText(input.dropLast(1))
        }
    }
}
