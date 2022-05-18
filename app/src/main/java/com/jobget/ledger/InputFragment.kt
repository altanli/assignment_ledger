package com.jobget.ledger

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.jobget.ledger.model.TransItem
import com.jobget.ledger.util.NumUtils
import com.jobget.ledger.viewmodel.InputViewModel
import com.jobget.ledger.viewmodel.TransViewModel
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TRANSACTION_EXPENSE = "Expense"
private const val TRANSACTION_INCOME = "Income"

/**
 * A simple [Fragment] subclass.
 * Use the [InputFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InputFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val inputViewModel:InputViewModel by activityViewModels()

        val rootView = inflater.inflate(R.layout.fragment_input, container, false)
        val spinner : Spinner = rootView.findViewById(R.id.transaction_spinner)

        val plantsList: List<String> = listOf(TRANSACTION_EXPENSE, TRANSACTION_INCOME)
        val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, plantsList)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.adapter = spinnerArrayAdapter

        val desc_edt : EditText = rootView.findViewById(R.id.transaction_desc_edt)
        desc_edt.addTextChangedListener {
            inputViewModel.setTitleStr(it.toString())
        }

        val price_edt : EditText = rootView.findViewById(R.id.value_edt)
        price_edt.addTextChangedListener {
            if (spinner.selectedItem.equals(TRANSACTION_EXPENSE)) {
                inputViewModel.setPriceValue(-Math.abs(it.toString().toInt()))
            } else {
                inputViewModel.setPriceValue(Math.abs(it.toString().toInt()))
            }
        }

        val addButton : Button = rootView.findViewById(R.id.add_btn)
        inputViewModel.transData.observe(requireActivity(), Observer<TransItem> { transItem ->
            addButton.setEnabled(transItem.title.length > 0 && transItem.value != 0)
        })

        addButton.setOnClickListener {
            val transItem = inputViewModel.transData.value!!
            transItem.dateStr = SimpleDateFormat(NumUtils.Default_Date_Format).format(Date())
            (activity as MainActivity).transViewModel.addTransItem(transItem)
            (activity as MainActivity).supportFragmentManager.beginTransaction().remove(this).commit()
        }

        return rootView;
    }

    override fun onStart() {
        super.onStart()
        dialog?:return
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)
    }
}