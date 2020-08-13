package com.casamoderna

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.casamoderna.model.User
import kotlinx.android.synthetic.main.fragment_category_profile.*


/**
 * A simple [Fragment] subclass.
 */
class CategoryProfileFragment : Fragment() {

    private lateinit var user: User
    val args: SignFragmentArgs by navArgs()
    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_category_profile, container, false)
        return v
    }

    override fun onStart() {
        super.onStart()
        archt.setOnClickListener { nextPageSearch() }
        modernHome.setOnClickListener { nextPageNewOrder() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        user = args.currentUser
        var hello = getString(R.string.hello_category_profile, user.name)
        nameUser.text = hello
    }

    private fun nextPageSearch() {
        val action = CategoryProfileFragmentDirections.nextactionSearch()
        Navigation.findNavController(v).navigate(action)
    }

    private fun nextPageNewOrder() {
        val action = CategoryProfileFragmentDirections.nextactionNewOrder(currentUser = user)
        Navigation.findNavController(v).navigate(action)
    }


}
