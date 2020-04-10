package com.nicoqueijo.android.currencyconverter.kotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
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
import com.nicoqueijo.android.currencyconverter.kotlin.util.CurrencyConversion
import com.nicoqueijo.android.currencyconverter.kotlin.util.CurrencyDiffUtilCallback
import com.nicoqueijo.android.currencyconverter.kotlin.util.SwipeAndDragHelper
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isValid
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.vibrate
import com.nicoqueijo.android.currencyconverter.kotlin.view.DecimalNumberKeyboard
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.ActiveCurrenciesViewModel
import java.math.BigDecimal

class ActiveCurrenciesAdapter(private val viewModel: ActiveCurrenciesViewModel,
                              private val keyboard: DecimalNumberKeyboard) :
        ListAdapter<Currency, ActiveCurrenciesAdapter.ViewHolder>(CurrencyDiffUtilCallback()),
        SwipeAndDragHelper.ActionCompletionContract {

    inner class ViewHolder(private val binding: RowActiveCurrencyBinding) :
            RecyclerView.ViewHolder(binding.root) {

        val rowForeground: ConstraintLayout = itemView.findViewById(R.id.row_foreground)
        val deleteIconStart: ImageView = itemView.findViewById(R.id.delete_icon_start)
        val deleteIconEnd: ImageView = itemView.findViewById(R.id.delete_icon_end)
        val currencyCode: TextView = itemView.findViewById(R.id.currency_code)
        private val conversion: TextView = itemView.findViewById(R.id.conversion)
        private val blinkingCursor: View = itemView.findViewById(R.id.blinking_cursor)

        init {
            initListeners()
        }

        private fun initListeners() {
            initTextViewListener()
            initKeyboardListener()
        }

        private fun initTextViewListener() {
            conversion.setOnClickListener {
                changeFocusedCurrency(adapterPosition)
            }
        }

        private fun initKeyboardListener() {
            /**
             * If the [button] is a Button we know that belongs to chars 0-9 or the decimal
             * separator as those were declared as Buttons.
             * If the [button] passed in is an ImageButton that can only mean that it is the backspace
             * key as that was the only one declared as an ImageButton.
             *
             * On each key click event, we want to validate the input against what already is in the
             * TextView. If it is valid we want to run the conversion of that value against all other
             * currencies and update the TextView of all other currencies.
             */
            keyboard.onKeyClickedListener { button ->
                /**
                 * notifyItemChanged for this item (to draw input on TextView if it's valid)
                 * If input was valid convert all other items and then notifyItemChanged for them.
                 * Problem is that when you backspace the last element, the algo tries to convert an empty String to a BigDecimal (exception).
                 *      Handle this scenario
                 *          Also need to update all the hints when everything is backspaced
                 * Btw, added currencies aren't being converted. They appear with empty fields. (This also applies to added currencies from swipe undos)
                 */
                val isInputValid = viewModel.handleKeyPressed(button)
                if (isInputValid) {
                    val focusedCurrency = viewModel.focusedCurrency.value
                    viewModel.adapterActiveCurrencies
                            .filter { it != focusedCurrency }
                            .forEach {
                                val fromRate = focusedCurrency!!.exchangeRate
                                val toRate = it.exchangeRate
                                if (focusedCurrency.conversion.conversionString.isNotEmpty()) {
                                    val conversionValue = CurrencyConversion.convertCurrency(BigDecimal(focusedCurrency
                                            .conversion.conversionString), fromRate, toRate)
                                    it.conversion.conversionValue = conversionValue
                                } else {
                                    it.conversion.conversionString = ""
                                }
                                notifyItemChanged(viewModel.adapterActiveCurrencies.indexOf(it))
                            }
                }
                notifyItemChanged(viewModel.adapterActiveCurrencies.indexOf(viewModel.focusedCurrency.value))
            }

            keyboard.onKeyLongClickedListener {
                viewModel.handleKeyLongPressed()
                val focusedCurrency = viewModel.focusedCurrency.value
                viewModel.adapterActiveCurrencies
                        .filter { it != focusedCurrency }
                        .forEach {
                            it.conversion.conversionString = ""
                            notifyItemChanged(viewModel.adapterActiveCurrencies.indexOf(it))
                        }
                notifyItemChanged(viewModel.adapterActiveCurrencies.indexOf(viewModel.focusedCurrency.value))
            }
        }

        private fun vibrateAndShake() {
            keyboard.context.vibrate()
            conversion.startAnimation(AnimationUtils.loadAnimation(viewModel.getApplication(), R.anim.shake))
        }

        fun bind(position: Int) {
            try {
                if (viewModel.adapterActiveCurrencies[position].conversion.hasInvalidInput) {
                    vibrateAndShake()
                    viewModel.focusedCurrency.value?.conversion?.hasInvalidInput = false
                }
                binding.currency = viewModel.adapterActiveCurrencies[position]
                styleIfFocused()
            } catch (e: IndexOutOfBoundsException) {
                /**
                 * Create issue on Github and post link here.
                 * This error is caused by reassignFocusedCurrency() and the observe callback of
                 * the focusedCurrency in ActiveCurrenciesFragment.
                 */
            }
        }

        private fun styleIfFocused() {
            if (viewModel.adapterActiveCurrencies[adapterPosition].isFocused) {
                rowForeground.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.dark_gray))
                blinkingCursor.startAnimation(AnimationUtils.loadAnimation(viewModel.getApplication(), R.anim.blink))
            } else {
                rowForeground.background = ContextCompat.getDrawable(binding.root.context,
                        R.drawable.background_row_active_currency)
                blinkingCursor.clearAnimation()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowActiveCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        viewModel.setFocusToFirstCurrency()
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    /**
     * When currencies are added or removed, the upsert() function is called to modify the underlying
     * db table that stores the state of the currencies. [currencies] provides the currencies as they
     * are in the db (without the volatile fields).
     * Need to make a copy of the volatile fields and pass it back to the adapter.
     */
    fun setCurrencies(currencies: MutableList<Currency>) {
        viewModel.reconcileCurrencies(currencies)
        viewModel.adapterActiveCurrencies = currencies
        submitList(currencies)
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