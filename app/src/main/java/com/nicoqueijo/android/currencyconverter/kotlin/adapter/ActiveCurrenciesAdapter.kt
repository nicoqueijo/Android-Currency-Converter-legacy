package com.nicoqueijo.android.currencyconverter.kotlin.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.databinding.RowActiveCurrencyBinding
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.CurrencyDiffUtilCallback
import com.nicoqueijo.android.currencyconverter.kotlin.util.SwipeAndDragHelper
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isValid
import com.nicoqueijo.android.currencyconverter.kotlin.view.DecimalNumberKeyboard
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.ActiveCurrenciesViewModel
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ActiveCurrenciesAdapter(private val viewModel: ActiveCurrenciesViewModel,
                              private val keyboard: DecimalNumberKeyboard) :
        ListAdapter<Currency, ActiveCurrenciesAdapter.ViewHolder>(CurrencyDiffUtilCallback()),
        SwipeAndDragHelper.ActionCompletionContract {

    private var decimalFormatter: DecimalFormat
    private var decimalSeparator: String
    private var groupingSeparator: String
    private var animBlink = AnimationUtils.loadAnimation(viewModel.getApplication(), R.anim.blink)

    init {
        val numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault())
        val conversionPattern = "###,##0.0000"
        decimalFormatter = numberFormatter as DecimalFormat
        decimalFormatter.applyPattern(conversionPattern)
        groupingSeparator = decimalFormatter.decimalFormatSymbols.groupingSeparator.toString()
        decimalSeparator = decimalFormatter.decimalFormatSymbols.decimalSeparator.toString()
    }

    inner class ViewHolder(private val binding: RowActiveCurrencyBinding) :
            RecyclerView.ViewHolder(binding.root) {

        internal val rowForeground: ConstraintLayout = itemView.findViewById(R.id.row_foreground)
        internal val deleteIconStart: ImageView = itemView.findViewById(R.id.delete_icon_start)
        internal val deleteIconEnd: ImageView = itemView.findViewById(R.id.delete_icon_end)
        internal val currencyCode: TextView = itemView.findViewById(R.id.currency_code)
        private val conversionValue: TextView = itemView.findViewById(R.id.conversion_value)
        private val blinkingCursor: View = itemView.findViewById(R.id.blinking_cursor)

        init {
            conversionValue.hint = "0${decimalSeparator}0000"
            conversionValue.setOnClickListener {
                changeFocusedCurrency(adapterPosition)
            }

            keyboard.onKeyClickedListener { button ->
                // Move and encapsulate all this logic into functions outside this inner class?

                // If the view passed in is a Button we know that belongs to chars 0-9 or the
                // decimal separator as those were declared as Button objects.
                // If the view passed in is an ImageButton that can only mean that it is the
                // backspace key as that was the only one declared as an ImageButton
                if (button is Button) {
                    // validate input first

                    viewModel.focusedCurrency.value = viewModel.focusedCurrency.value // This seems stupid but this is so the RecyclerView scrolls to the focused position when any key is clicked.
                    val focusedCurrency = viewModel.focusedCurrency
                    val keyValue = button.text.toString()
                    val existingText = focusedCurrency.value?.conversion?.conversionText?.replace(groupingSeparator, "")
                    val replacementText = StringBuilder()
                    replacementText.append(existingText).append(keyValue)
                    focusedCurrency.value?.conversion?.conversionValue = BigDecimal(replacementText.toString())
                    conversionValue.text = replacementText
                    notifyItemChanged(viewModel.adapterActiveCurrencies.indexOf(focusedCurrency.value) + 1)
                    // Number or decimal separator
                }
                if (button is ImageButton) {
                    // Backspace
                    conversionValue.text = conversionValue.text.dropLast(1)
                }
            }

            keyboard.onKeyLongClickedListener {
                val focusedCurrency = viewModel.focusedCurrency
                focusedCurrency.value?.conversion?.conversionValue = BigDecimal.ZERO
                notifyItemChanged(viewModel.adapterActiveCurrencies.indexOf(focusedCurrency.value))
            }
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

        fun bind(position: Int) {
            try {
                binding.currency = viewModel.adapterActiveCurrencies[position]
                styleIfFocused()
            } catch (e: IndexOutOfBoundsException) {
                // Create issue on Github and post link here
                // This error is caused by reassignFocusedCurrency()
            }
        }

        private fun styleIfFocused() {
            if (viewModel.adapterActiveCurrencies[adapterPosition].isFocused) {
                rowForeground.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.dark_gray))
                blinkingCursor.startAnimation(animBlink)
            } else {
                rowForeground.background = ContextCompat.getDrawable(binding.root.context,
                        R.drawable.background_row_active_currency)
                blinkingCursor.clearAnimation()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowActiveCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        setFocusToFirstCurrency()
        return ViewHolder(binding)
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setCurrencies(currencies: MutableList<Currency>) {
        setFocusedCurrency(currencies)
        viewModel.adapterActiveCurrencies = currencies
        submitList(currencies)
    }

    private fun setFocusToFirstCurrency() {
        if (viewModel.focusedCurrency.value == null) {
            viewModel.focusedCurrency.value = viewModel.adapterActiveCurrencies.take(1)[0].also { firstCurrency ->
                firstCurrency.isFocused = true
            }
        }
    }

    private fun setFocusedCurrency(currencies: MutableList<Currency>) {
        viewModel.focusedCurrency.value?.let { focusedCurrency ->
            viewModel.focusedCurrency.value = currencies[currencies.indexOf(focusedCurrency)]
            currencies[currencies.indexOf(focusedCurrency)].isFocused = true
        }
    }

    private fun changeFocusedCurrency(adapterPosition: Int) {
        val clickedCurrency = viewModel.adapterActiveCurrencies[adapterPosition]
        val indexOfPreviouslyFocusedCurrency = viewModel.changeFocusedCurrency(clickedCurrency)
        if (indexOfPreviouslyFocusedCurrency.isValid()) {
            notifyItemChanged(indexOfPreviouslyFocusedCurrency)
        }
        notifyItemChanged(adapterPosition)
    }

    override fun onViewMoved(oldPosition: Int, newPosition: Int) {
        viewModel.handleMove(oldPosition, newPosition)
        notifyItemMoved(oldPosition, newPosition)
    }

    override fun onViewSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {
        /*val conversionValue = swipedCurrency.conversionValue*/
        viewModel.handleSwipe(position).let {
            if (it.isValid()) {
                notifyItemChanged(it)
            }
        }
        Snackbar.make(viewHolder!!.itemView, R.string.item_removed, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) {
                    viewModel.handleSwipeUndo()
                }.show()
    }

    override fun onViewDropped() {
        viewModel.handleDrop()
    }
}