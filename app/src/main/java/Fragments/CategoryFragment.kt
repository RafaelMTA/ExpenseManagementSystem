package Fragments

import BottomSheet.AddBudgetFragment
import BottomSheet.AddCategoryFragment
import BottomSheet.EditCategoryFragment
import BottomSheet.EditExpenseFragment
import Database.CategoryDBHandler
import Models.Category
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.example.expensemanagementsystem.R
import com.example.expensemanagementsystem.databinding.FragmentCategoryBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoryFragment : Fragment() {
    private lateinit var binding : FragmentCategoryBinding
    private lateinit var categoryDBHandler : CategoryDBHandler
    private lateinit var arrayList : ArrayList<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            AddCategoryFragment().show(requireActivity().supportFragmentManager, "addCategoryTag")
        }

        binding = FragmentCategoryBinding.inflate(inflater, container, false)

        binding.back.setOnClickListener{
            requireActivity().onBackPressed()
        }

        arrayList = categoryDBHandler.readAll()

        if(arrayList.isEmpty()){
            Toast.makeText(requireActivity(), "No entries found", Toast.LENGTH_LONG).show()
        }else{
            val listAdapter = ArrayAdapter(requireActivity(), R.layout.category_item, arrayList)

            binding.listView.adapter = listAdapter

            //Edit item from list
            binding.listView.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    var bundle = Bundle()
                    bundle.putInt("id", arrayList[position].id)
                    bundle.putString("title", arrayList[position].title)
                    bundle.putString("description", arrayList[position].description)

                    val frg = EditCategoryFragment()
                    frg.arguments = bundle
                    frg.show(requireActivity().supportFragmentManager, "editCategoryTag")

                    listAdapter.notifyDataSetChanged()
                    true
                }

            //Remove item from list
            binding.listView.onItemLongClickListener =
                OnItemLongClickListener { _, _, i, _ ->
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
        categoryDBHandler.close()
        super.onDestroy()
    }
    private fun onLongClick(parent: FragmentActivity, view: CategoryFragment, position: Int, id: Int){
        categoryDBHandler.delete(id.toString())
        Toast.makeText(requireActivity(), "Successfully deleted", Toast.LENGTH_LONG).show()
    }
}