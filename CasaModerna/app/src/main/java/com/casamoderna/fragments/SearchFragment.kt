package com.casamoderna.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.casamoderna.MainActivity
import com.casamoderna.R
import com.casamoderna.adapter.OrderListAdapter
import com.casamoderna.viewmodel.FireStoreRequestViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.filter_order.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.doFilter
import java.text.NumberFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var v: View
    private lateinit var viewAdapter: OrderListAdapter
    private lateinit var viewManager: LinearLayoutManager
    var estimate = 10000
    private val firestoreViewModel: FireStoreRequestViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_search, container, false)
        return v
    }

    override fun onStart() {
        super.onStart()
        initializeComponents()
        initializeViewModel()
    }

    private fun initializeViewModel() {
        firestoreViewModel.ordersMutableLiveData.observe(
            this,
            androidx.lifecycle.Observer { response ->
                if (response != null) {
                    initializeRecycleView()
                }
                progressSearch.visibility = View.INVISIBLE
            })
        progressSearch.visibility = View.VISIBLE
        firestoreViewModel.getOrders()

    }

    private fun initializeRecycleView() {
        viewManager = LinearLayoutManager(requireContext())
        viewAdapter = OrderListAdapter(firestoreViewModel.ordersMutableLiveData.value!!)

        recyclerview.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
    }

    private fun initializeComponents() {
        back_search.setOnClickListener { findNavController().navigateUp() }
        filter.setOnClickListener { filterOrder() }

        doFilter.setOnClickListener {
            filter()
        }
    }

    private fun filter() {
        firestoreViewModel.getOrders(
            localFilter.text.toString(), fetchLimitEstimate()
        )
    }

    private fun fetchLimitEstimate(): Int {
        return if (!this::bottomSheetDialog.isInitialized) {
            999999
        } else if (bottomSheetDialog.btdEstimate.text.toString() == "") {
            999999
        } else {
            bottomSheetDialog.btdEstimate.text.toString().toInt()
        }
    }

    private fun filterOrder() {
        bottomSheetDialog = BottomSheetDialog(activity as MainActivity)
        val view = layoutInflater.inflate(R.layout.filter_order, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
        bottomSheetDialog.btdEstimate.setText("10000")
        bottomSheetDialog.btdMinus.setOnClickListener {
            if (bottomSheetDialog.btdEstimate.text.toString().toInt() > 1) {
                bottomSheetDialog.btdEstimate.setText(
                    (bottomSheetDialog.btdEstimate.text.toString().toInt() - 1).toString()
                )
            }
        }

        bottomSheetDialog.btdPlus.setOnClickListener {
            bottomSheetDialog.btdEstimate.setText(
                ( bottomSheetDialog.btdEstimate.text.toString().toInt() + 1).toString()
            )
        }
        bottomSheetDialog.btdDoFilter.setOnClickListener {
            filter()
        }

    }


    private fun formatterFilterValueOrder(value: Float): String {
        val format = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 0
        format.currency = Currency.getInstance("BRL")
        return format.format(value.toDouble())
    }
}
