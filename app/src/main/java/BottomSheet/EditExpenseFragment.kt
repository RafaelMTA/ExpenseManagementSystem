package BottomSheet

import Database.BudgetDBHandler
import Database.CategoryDBHandler
import Database.ExpenseDBHandler
import Models.Category
import Models.Expense
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.expensemanagementsystem.R
import com.example.expensemanagementsystem.databinding.FragmentAddExpenseBinding
import com.example.expensemanagementsystem.databinding.FragmentEditExpenseBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditExpenseFragment: BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentEditExpenseBinding
    private lateinit var expenseDBHandler : ExpenseDBHandler
    private lateinit var categoryDBHandler : CategoryDBHandler
    private lateinit var budgetDBHandler: BudgetDBHandler
    private lateinit var categoryList : ArrayList<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = requireActivity()
        expenseDBHandler = ExpenseDBHandler(activity, "EMS.db", null, 1)
        categoryDBHandler = CategoryDBHandler(activity, "EMS.db", null, 1)
        budgetDBHandler = BudgetDBHandler(activity, "EMS.db", null, 1)
        categoryList = categoryDBHandler.readAll()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditExpenseBinding.inflate(inflater, container, false)

        binding.title.setText(arguments?.getString("title"))
        binding.cost.setText(arguments?.getDouble("cost").toString())

        binding.saveButton.setOnClickListener{
            saveAction()
        }

        if(categoryList.isEmpty()){
            Toast.makeText(requireActivity(), "No categories entries found", Toast.LENGTH_LONG).show()
        }else{
            val listAdapter = ArrayAdapter(requireActivity(), R.layout.category_item, R.id.category_title, categoryList.map {"Title: " + it.title + " - " + it.id})
            //categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            //binding.category.onItemSelectedListener = this

            binding.category.adapter = listAdapter
        }

        return binding.root
    }

    private fun saveAction()
    {
        if(binding.title.text.toString().isNotEmpty()){
            val categoryId = getCategoryID(binding.category.selectedItemId.toString().toInt())
            val expenseId = arguments?.getInt("id")

            //On add expense check if there is enough budget
            if(categoryId?.let { expenseId?.let { it1 -> hasBudget(it, it1) } } == true){
                val success = categoryId?.let {
                    Expense(binding.title.text.toString(), binding.cost.text.toString().toDouble(),
                        it, 0)
                }?.let { expenseDBHandler.update(expenseId.toString(), it) }

                if(success == null){
                    Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_LONG).show()
                }else{
                    binding.title.setText("")

                    dismiss()
                    Toast.makeText(requireActivity(), "Successfully registered", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(requireActivity(), "Budget has been reached", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(requireActivity(), "Title must be informed", Toast.LENGTH_LONG).show()
        }
    }

    private fun getCategoryID(i : Int): Int? {
        return categoryList[i].id
    }

    private fun displayData(category: Category){
        Toast.makeText(requireActivity(),  category.title, Toast.LENGTH_LONG).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val category = parent?.selectedItem
        displayData(category as Category)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun hasBudget(categoryID : Int, expenseID: Int) : Boolean{
        val budgetList = budgetDBHandler.getCategoryBudget(categoryID.toString())
        val expenseTotal = expenseDBHandler.getTotalCostOnUpdate(categoryID, expenseID) + binding.cost.text.toString().toDouble()

        if(budgetList.isNotEmpty()){
            if(expenseTotal > budgetList[0].budget){
                return false
            }
        }else{
            return false
        }

        return true
    }
}