package Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.expensemanagementsystem.R
import com.example.expensemanagementsystem.databinding.FragmentCategoryBinding
import com.example.expensemanagementsystem.databinding.FragmentLogoutBinding

class LogoutFragment : Fragment() {
    private lateinit var binding: FragmentLogoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogoutBinding.inflate(inflater, container, false)

        binding.back.setOnClickListener{
            requireActivity().onBackPressed()
        }
        // Inflate the layout for this fragment
        return binding.root
    }
}