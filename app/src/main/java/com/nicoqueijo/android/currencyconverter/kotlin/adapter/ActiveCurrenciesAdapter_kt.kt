package com.nicoqueijo.android.currencyconverter.kotlin.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.databinding.RowActiveCurrencyKtBinding
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.CurrencyDiffUtilCallback
import com.nicoqueijo.android.currencyconverter.kotlin.util.SwipeAndDragHelper_kt
import com.nicoqueijo.android.currencyconverter.kotlin.view.DecimalNumberKeyboard
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.ActiveCurrenciesViewModel_kt
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ActiveCurrenciesAdapter_kt(private val viewModel: ActiveCurrenciesViewModel_kt,
                                 private val keyboard: DecimalNumberKeyboard) :
        ListAdapter<Currency, ActiveCurrenciesAdapter_kt.ViewHolder>(CurrencyDiffUtilCallback()),
        SwipeAndDragHelper_kt.ActionCompletionContract {

    private var decimalFormatter: DecimalFormat
    private var decimalSeparator: String
    private var animBlink = AnimationUtils.loadAnimation(viewModel.getApplication(), R.anim.blink)

    init {
        val numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault())
        val conversionPattern = "###,##0.0000"
        decimalFormatter = numberFormatter as DecimalFormat
        decimalFormatter.applyPattern(conversionPattern)
        decimalSeparator = decimalFormatter.decimalFormatSymbols.decimalSeparator.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowActiveCurrencyKtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        if (viewModel.focusedCurrency == null) {
            val firstCurrency = viewModel.adapterActiveCurrencies.take(1)[0]
            firstCurrency.isFocused = true
            viewModel.focusedCurrency = firstCurrency
        }
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.M) // Find another way to set background color
    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*log("onBindViewHolder() called for $position: ${viewModel.adapterActiveCurrencies[position]}")*/
        holder.binding.currency = viewModel.adapterActiveCurrencies[position]
        if (viewModel.adapterActiveCurrencies[position].isFocused) {
            holder.rowForeground.setBackgroundColor(keyboard.context.getColor(R.color.dark_gray))
            holder.blinkingCursor.startAnimation(animBlink)
        } else {
            holder.rowForeground.background = keyboard.context.getDrawable(R.drawable.background_row_active_currency)
            holder.blinkingCursor.clearAnimation()
        }
    }

    fun setCurrencies(currencies: MutableList<Currency>) {
        setFocusedCurrency(currencies)
        viewModel.adapterActiveCurrencies = currencies
        submitList(currencies)
    }

    private fun setFocusedCurrency(currencies: MutableList<Currency>) {
        val focusedCurrency = viewModel.focusedCurrency
        focusedCurrency?.let {
            viewModel.focusedCurrency = currencies[currencies.indexOf(focusedCurrency)]
            currencies[currencies.indexOf(focusedCurrency)].isFocused = true
        }
    }

    override fun onViewMoved(oldPosition: Int, newPosition: Int) {
        viewModel.handleMove(oldPosition, newPosition)
        notifyItemMoved(oldPosition, newPosition)
    }

    override fun onViewSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        /*val conversionValue = swipedCurrency.conversionValue*/

        // Handles the focus management
        val swipedCurrency = viewModel.adapterActiveCurrencies[position]
        if (viewModel.focusedCurrency == swipedCurrency) {
            val newlyFocusedCurrency: Currency
            if (position == 0) {
                if (viewModel.adapterActiveCurrencies.size >= 2) {
                    newlyFocusedCurrency = viewModel.adapterActiveCurrencies[1]
                    newlyFocusedCurrency.isFocused = true
                    swipedCurrency.isFocused = false
                    viewModel.focusedCurrency = newlyFocusedCurrency
                } else {
                    viewModel.focusedCurrency = null
                }
            } else {
                newlyFocusedCurrency = viewModel.adapterActiveCurrencies[0]
                newlyFocusedCurrency.isFocused = true
                swipedCurrency.isFocused = false
                viewModel.focusedCurrency = newlyFocusedCurrency
            }
        }

        viewModel.handleSwipe(position)
        Snackbar.make(viewHolder!!.itemView, R.string.item_removed, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) {
                    viewModel.handleSwipeUndo()
                }.show()
    }

    override fun onViewDropped() {
        viewModel.handleDrop()
    }

    private fun changeFocusedCurrency(adapterPosition: Int) {
        val clickedCurrency = viewModel.adapterActiveCurrencies[adapterPosition]
        val indexOfPreviouslyFocusedCurrency = viewModel.changeFocusedCurrency(clickedCurrency)
        if (indexOfPreviouslyFocusedCurrency != -1) {
            notifyItemChanged(indexOfPreviouslyFocusedCurrency)
        }
        notifyItemChanged(adapterPosition)
    }

    inner class ViewHolder(val binding: RowActiveCurrencyKtBinding) :
            RecyclerView.ViewHolder(binding.root) {

        val rowForeground: ConstraintLayout = itemView.findViewById(R.id.row_foreground_kt)
        val deleteIconStart: ImageView = itemView.findViewById(R.id.delete_icon_start_kt)
        val deleteIconEnd: ImageView = itemView.findViewById(R.id.delete_icon_end_kt)
        val flag: ImageView = itemView.findViewById(R.id.flag_kt)
        val currencyCode: TextView = itemView.findViewById(R.id.currency_code_kt)
        val conversionValue: TextView = itemView.findViewById(R.id.conversion_value_kt)
        val blinkingCursor: View = itemView.findViewById(R.id.blinking_cursor_kt)

        init {
            conversionValue.hint = "0${decimalSeparator}0000"

            conversionValue.setOnClickListener {
                changeFocusedCurrency(adapterPosition)
            }

            /*keyboard.onKeyClickedListener { button ->
                // Move and encapsulate all this logic into functions outside this inner class
                if (button is Button) {
                    // validate input first
                    val input = button.text.toString()
                    val existingText = conversionValue.text.toString()
                    val replacementText = StringBuilder()
                    replacementText.append(existingText).append(input)
                    conversionValue.text = replacementText
                    // Number or decimal separator
                    log("${conversionValue.text}")
                }
                if (button is ImageButton) {
                    // Backspace
                    conversionValue.text = conversionValue.text.dropLast(1)
                    log("${conversionValue.text}")
                }
            }
            keyboard.onKeyLongClickedListener {
                conversionValue.text = ""
                log("${conversionValue.text}")
            }*/
        }

        /*private fun validateInput(s: CharSequence?): Boolean {
            return validateLength(s) &&
                    validateDecimalPlaces(s) &&
                    validateDecimalSeparator(s) &&
                    validateZeros(s)
        }

        private fun validateLength(s: CharSequence?): Boolean {
            val maxDigitsAllowed = 20
            val input = s.toString()
            if (!input.contains(decimalSeparator) && input.length > maxDigitsAllowed) {
                dropLastWithFeedback(input)
                return false
            }
            return true
        }

        private fun validateDecimalPlaces(s: CharSequence?): Boolean {
            val maxDecimalPlacesAllowed = 4
            val input = s.toString()
            if (input.contains(decimalSeparator) &&
                    input.substring(input.indexOf(decimalSeparator) + 1).length > maxDecimalPlacesAllowed) {
                dropLastWithFeedback(input)
                return false
            }
            return true
        }

        @SuppressLint("SetTextI18n")
        private fun validateDecimalSeparator(s: CharSequence?): Boolean {
            val input = s.toString()
            if (input.length == 1 && input[0] == decimalSeparator.single()) {
                conversionValue.text = "0$decimalSeparator"
                return false
            }
            val decimalSeparatorCount = input.asSequence().count { char ->
                char.toString() == decimalSeparator
            }
            if (decimalSeparatorCount > 1) {
                dropLastWithFeedback(input)
                return false
            }
            return true
        }

        private fun validateZeros(s: CharSequence?): Boolean {
            val input = s.toString()
            if (input.length == 2) {
                if (input == "00") {
                    dropLastWithFeedback(input)
                    return false
                }
                if (input[0] == '0' && input[1] != decimalSeparator.single()) {
                    conversionValue.text = input[1].toString()
                    return false
                }
            }
            return true
        }

        private fun dropLastWithFeedback(input: String) {
            Utils.vibrate(viewModel.getApplication())
            conversionValue.startAnimation(AnimationUtils.loadAnimation(viewModel.getApplication(), R.anim.shake))
            conversionValue.text = input.dropLast(1)
        }*/
    }

    companion object {
        private fun log(message: String) {
            Log.d("ActiveCurrency", message)
        }
    }
}