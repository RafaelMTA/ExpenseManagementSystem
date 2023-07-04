package Fragments

import BottomSheet.AddBudgetFragment
import BottomSheet.AddExpenseFragment
import BottomSheet.EditBudgetFragment
import Database.BudgetDBHandler
import Database.CategoryDBHandler
import Models.Budget
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
import com.example.expensemanagementsystem.R
import com.example.expensemanagementsystem.databinding.FragmentBudgetBinding
import com.example.expensemanagementsystem.databinding.FragmentExpenseBinding
import com.example.expensemanagementsystem.databinding.FragmentSupportBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BudgetFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding : FragmentBudgetBinding
    private lateinit var budgetDBHandler : BudgetDBHandler
    private lateinit var categoryDBHandler: CategoryDBHandler
    private lateinit var arrayList : ArrayList<Budget>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        budgetDBHandler = BudgetDBHandler(requireActivity(), "EMS.db", null, 1)
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
            AddBudgetFragment().show(requireActivity().supportFragmentManager, "addBudgetTag")
        }

        binding = FragmentBudgetBinding.inflate(inflater, container, false)

        binding.back.setOnClickListener{
            requireActivity().onBackPressed()
        }

        //Load list
        arrayList = budgetDBHandler.readAll()

        if(arrayList.isEmpty()){
            Toast.makeText(requireActivity(), "No entries found", Toast.LENGTH_LONG).show()
        }else{
            val listAdapter = ArrayAdapter(requireActivity(), R.layout.budget_item, R.id.budget_item_title, arrayList.map {"Title: " + it.title + " | $:" + it.budget + " - " + getCategoryName(it.category_id.toString())})

            binding.budgetListView.adapter = listAdapter
            binding.budgetListView.onItemSelectedListener = this

            //Edit item from list
            binding.budgetListView.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    var bundle = Bundle()
                    bundle.putInt("id", arrayList[position].id)
                    bundle.putString("title", arrayList[position].title)
                    bundle.putString("description", arrayList[position].description)
                    bundle.putDouble("budget", arrayList[position].budget)
                    bundle.putInt("categoryId", arrayList[position].category_id)

                    val frg = EditBudgetFragment()
                    frg.arguments = bundle
                    frg.show(requireActivity().supportFragmentManager, "editBudgetTag")

                    listAdapter.notifyDataSetChanged()
                    true
                }

            //Remove item from list
            binding.budgetListView.onItemLongClickListener =
                AdapterView.OnItemLongClickListener { _, _, i, _ ->
                    arrayList[i].id?.let { onLongClick(requireActivity(), this, i, it) }
                    arrayList.removeAt(i)
                    listAdapter.notifyDataSetChanged()

                    true
                }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroy() {
        budgetDBHandler.close()
        super.onDestroy()
    }

    private fun onLongClick(parent: FragmentActivity, view: BudgetFragment, position: Int, id: Int){
        budgetDBHandler.delete(id.toString())
        Toast.makeText(requireActivity(), "Successfully deleted", Toast.LENGTH_LONG).show()
    }

    private fun displayData(budget: Budget){
        Toast.makeText(requireActivity(),  budget.title, Toast.LENGTH_LONG).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val budget = parent?.selectedItem
        displayData(budget as Budget)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun getCategoryName(id: String) : String {
        return categoryDBHandler.read(id)[0].title
    }
}