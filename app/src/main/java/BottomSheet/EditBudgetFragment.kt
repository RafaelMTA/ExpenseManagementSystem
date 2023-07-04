package BottomSheet

import Database.BudgetDBHandler
import Database.CategoryDBHandler
import Models.Budget
import Models.Category
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.expensemanagementsystem.R
import com.example.expensemanagementsystem.databinding.FragmentEditBudgetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditBudgetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentEditBudgetBinding
    private lateinit var budgetDBHandler: BudgetDBHandler
    private lateinit var categoryDBHandler: CategoryDBHandler
    private lateinit var categoryList: ArrayList<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = requireActivity()
        budgetDBHandler = BudgetDBHandler(activity, "EMS.db", null, 1)
        categoryDBHandler = CategoryDBHandler(activity, "EMS.db", null, 1)
        categoryList = categoryDBHandler.readAll()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditBudgetBinding.inflate(inflater, container, false)

        binding.title.setText(arguments?.getString("title"))
        binding.description.setText(arguments?.getString("description"))
        binding.budget.setText(arguments?.getDouble("budget").toString())

        binding.saveButton.setOnClickListener {
            saveAction()
        }

        if (categoryList.isEmpty()) {
            Toast.makeText(requireActivity(), "No categories entries found", Toast.LENGTH_LONG)
                .show()
        } else {
            val listAdapter = ArrayAdapter(
                requireActivity(),
                R.layout.category_item,
                categoryList.map { "Title: " + it.title + " - " + it.id })

            binding.category.adapter = listAdapter
        }

        return binding.root
    }

    private fun saveAction() {
        if (binding.title.text.toString().isNotEmpty()) {

            val categoryId = getCategoryID(binding.category.selectedItemId.toString().toInt())
            val budgetId = arguments?.getInt("id")

            //Check if budget with the category already exists

            if (budgetDBHandler.exists(categoryId.toString()) && categoryId != arguments?.getInt("categoryId")) {
                Toast.makeText(
                    requireActivity(),
                    "Budget already registered with this category",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val success = categoryId?.let {
                    Budget(
                        binding.title.text.toString(),
                        binding.description.text.toString(),
                        binding.budget.text.toString().toDouble(),
                        it,
                        0
                    )
                }?.let { budgetDBHandler.update(budgetId.toString(), it) }

                if (success == null) {
                    Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_LONG)
                        .show()
                } else {
                    binding.title.setText("")
                    binding.description.setText("")

                    dismiss()
                    Toast.makeText(
                        requireActivity(),
                        "Successfully registered",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        } else {
            Toast.makeText(requireActivity(), "Title must be informed", Toast.LENGTH_LONG)
                .show()
        }
    }


    private fun getCategoryID(i: Int): Int? {
        return categoryList[i].id
    }

    private fun loadData(i: Int): ArrayList<Budget> {
        return budgetDBHandler.read(i.toString())
    }
}
