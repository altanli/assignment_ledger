package com.jobget.ledger

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jobget.ledger.adapter.TransAdapter
import com.jobget.ledger.database.UserDatabase
import com.jobget.ledger.model.TransItem
import com.jobget.ledger.util.NumUtils.Companion.convertToCurrency
import com.jobget.ledger.util.Prefs
import com.jobget.ledger.viewmodel.TransViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    val transViewModel : TransViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // test data input
        inputTextData()

        var recyclerView: RecyclerView = findViewById(R.id.recycler_view);
        var expenseTxt: TextView = findViewById(R.id.expenses_txt);
        var incomeTxt: TextView = findViewById(R.id.income_txt);
        var balanceTxt: TextView = findViewById(R.id.balance_txt);
        var progressBar: ProgressBar = findViewById(R.id.balance_progress)
        var addButton: Button = findViewById(R.id.add_btn);

        addButton.setOnClickListener{
            val fm: FragmentManager = supportFragmentManager
            val editNameDialogFragment = InputFragment()
            editNameDialogFragment.show(fm, "fragment_edit_name")
        }

        var transAdapter = TransAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = transAdapter
        transAdapter.onItemClickListener = object : TransAdapter.OnItemClickListener {
            override fun onLongClick(transItem: TransItem) {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setPositiveButton(R.string.delete_dialog_yes, DialogInterface.OnClickListener { dialog, id ->
                    transViewModel.deleteTransItem(transItem)
                    dialog.dismiss()
                })
                builder.setNegativeButton(R.string.delete_dialog_no, null)
                val alert = builder.create()
                alert.setTitle(R.string.delete_dialog_title)
                alert.setMessage(applicationContext.getString(R.string.delete_dialog_msg))
                alert.show()
            }
        }

        transViewModel.setContext(this)
        transViewModel.getTransData().observe(this, Observer<List<TransItem>>{ transDataList ->
            transAdapter.setDataList(transDataList)

            var income_sum : Int = 0
            var expense_sum : Int = 0
            var balance : Int = 0
            for (transItem in transDataList) {
                if (transItem.value > 0) {
                    income_sum += transItem.value
                } else {
                    expense_sum += transItem.value
                }
            }
            balance = income_sum + expense_sum

            expenseTxt.text = convertToCurrency(Math.abs(expense_sum))
            incomeTxt.text = convertToCurrency(income_sum)
            balanceTxt.text = convertToCurrency(balance)
            progressBar.progress = (100 * (Math.abs(expense_sum).toDouble() / income_sum.toDouble())).toInt()
        })
    }

    override fun onDestroy() {
        transViewModel.closeDB()
        super.onDestroy()
    }

    /**
     *  Test Data Input
     */
    fun inputTextData() {
        val sharedPreferences = getSharedPreferences(Prefs.APP_PREF_INT, Context.MODE_PRIVATE)
        if (!sharedPreferences.getBoolean(Prefs.APP_PREF_TESTSET, false)) {
            lifecycleScope.launch(Dispatchers.Default) {
                val userDatabase = UserDatabase.getDatabase(this@MainActivity)
                userDatabase.userDao().insertToRoomDatabase(TransItem("May 16, 2022", "Coffee from StarBucks", -7))
                userDatabase.userDao().insertToRoomDatabase(TransItem("May 16, 2022", "Grocery from Nestor's", -56))
                userDatabase.userDao().insertToRoomDatabase(TransItem("May 16, 2022", "Salary", 1000))
                userDatabase.userDao().insertToRoomDatabase(TransItem("May 16, 2022", "Food take out", -57))
                userDatabase.userDao().insertToRoomDatabase(TransItem("May 15, 2022", "Phone bill", -90))
                userDatabase.userDao().insertToRoomDatabase(TransItem("May 14, 2022", "Phone bill", -90))
                userDatabase.userDao().insertToRoomDatabase(TransItem("May 13, 2022", "Phone bill", -90))
                userDatabase.userDao().insertToRoomDatabase(TransItem("May 12, 2022", "Phone bill", -90))
                withContext(Dispatchers.Main) {
                }
            }

            var editor = sharedPreferences.edit()
            editor.putBoolean(Prefs.APP_PREF_TESTSET, true)
            editor.commit()
        }
    }
}