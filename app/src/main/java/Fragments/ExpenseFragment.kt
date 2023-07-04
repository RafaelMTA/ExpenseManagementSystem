package Fragments

import BottomSheet.AddExpenseFragment
import BottomSheet.EditBudgetFragment
import BottomSheet.EditCategoryFragment
import BottomSheet.EditExpenseFragment
import Database.BudgetDBHandler
import Database.CategoryDBHandler
import Database.ExpenseDBHandler
import Models.Expense
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.expensemanagementsystem.databinding.FragmentExpenseBinding
import com.example.expensemanagementsystem.R
import com.example.expensemanagementsystem.databinding.FragmentCategoryBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExpenseFragment : Fragment() {
    private lateinit var binding : FragmentExpenseBinding
    private lateinit var expenseDBHandler : ExpenseDBHandler
    private lateinit var categoryDBHandler: CategoryDBHandler
    private lateinit var arrayList : ArrayList<Expense>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        expenseDBHandler = ExpenseDBHandler(requireActivity(), "EMS.db", null, 1)
        categoryDBHandler = CategoryDBHandler(requireActivity(), "EMS.db", null, 1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)

        fab.isVisible = true

        fab.setOnClickListener(null)

        fab.setOnClickListener{
            AddExpenseFragment().show(requireActivity().supportFragmentManager, "addExpenseTag")
        }

        binding = FragmentExpenseBinding.inflate(inflater, container, false)

        binding.back.setOnClickListener{
            requireActivity().onBackPressed()
        }
        //Load list
        arrayList = expenseDBHandler.readAll()

        if(arrayList.isEmpty()){
            Toast.makeText(requireActivity(), "No entries found", Toast.LENGTH_LONG).show()
        }else{
            val listAdapter = ArrayAdapter(requireActivity(), R.layout.expense_item, R.id.expense_item_title, arrayList.map{"Title: " + it.title + " | $: " + it.cost + " - " + getCategoryName(it.category_id.toString())})

            binding.listView.adapter = listAdapter

            //Edit item from list
            binding.listView.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    var bundle = Bundle()
                    bundle.putInt("id", arrayList[position].id)
                    bundle.putString("title", arrayList[position].title)
                    bundle.putDouble("cost", arrayList[position].cost)
                    bundle.putInt("categoryId", arrayList[position].category_id)

                    val frg = EditExpenseFragment()
                    frg.arguments = bundle
                    frg.show(requireActivity().supportFragmentManager, "editExpenseTag")

                    listAdapter.notifyDataSetChanged()
                    true
                }

            //Remove item from list
            binding.listView.onItemLongClickListener =
                AdapterView.OnItemLongClickListener { _, _, i , _ ->
                    arrayList[i].id?.let { onLongClick(requireActivity(), this, i, it) }
                    arrayList.removeAt(i - 1)
                    listAdapter.notifyDataSetChanged()
                    true
                }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroy() {
        expenseDBHandler.close()
        super.onDestroy()
    }

    private fun onLongClick(parent: FragmentActivity, view: ExpenseFragment, position: Int, id: Int){
        expenseDBHandler.delete(id.toString())
        Toast.makeText(requireActivity(), "Successfully deleted", Toast.LENGTH_LONG).show()
    }

    private fun getCategoryName(id: String) : String {
        return categoryDBHandler.read(id)[0].title
    }
}