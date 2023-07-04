package BottomSheet

import Database.CategoryDBHandler
import Models.Category
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.expensemanagementsystem.databinding.FragmentAddCategoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddCategoryFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddCategoryBinding
    private lateinit var categoryDBHandler : CategoryDBHandler

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val activity = requireActivity()
        categoryDBHandler = CategoryDBHandler(activity, "EMS.db", null, 1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCategoryBinding.inflate(inflater, container, false)
        binding.saveButton.setOnClickListener{
            saveAction()
        }
        return binding.root
    }

    private fun saveAction()
    {
        if(binding.title.text.toString().isNotEmpty()){
            val success = categoryDBHandler.insert(Category(binding.title.text.toString(), binding.description.text.toString(), 0))

            if(success == null){
                Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_LONG).show()
            }else{
                binding.title.setText("")
                binding.description.setText("")
                dismiss()
                Toast.makeText(requireActivity(), "Successfully registered", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(requireActivity(), "Title must be informed", Toast.LENGTH_LONG).show()
        }
    }
}