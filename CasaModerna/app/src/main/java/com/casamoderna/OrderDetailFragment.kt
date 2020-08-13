package com.casamoderna

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.casamoderna.adapter.SliderAdapter
import com.casamoderna.model.Order
import com.casamoderna.model.SliderItem
import com.casamoderna.viewmodel.FireStoreRequestViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.fragment_order_detail.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class OrderDetailFragment : Fragment() {
    private lateinit var adapter: SliderAdapter
    private val args: OrderDetailFragmentArgs by navArgs()
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private val firestoreViewModel: FireStoreRequestViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_detail, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
    }

    override fun onStart() {
        super.onStart()
        back.setOnClickListener { findNavController().navigateUp() }
        call.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_DIAL,
                    Uri.fromParts(
                        "tel",
                        firestoreViewModel.orderMutableLiveData.value!!.phone,
                        null
                    )
                )
            )
        }
        initializeViewModel()
    }

    private fun initializeViewModel() {
        firestoreViewModel.orderMutableLiveData.observe(this, Observer<Order> { response ->
            if (response != null) {
                progress.visibility = View.INVISIBLE
                updateUi(response)
            }
        })
        progress.visibility = View.VISIBLE
        firestoreViewModel.getOrder(args.idOrder)
    }

    private fun updateUi(order: Order) {
        local.text = order.local
        phone.text = order.phone
        estimate.text = getString(R.string.msg_detail_order_value_order, order.valueOrder)
        description.text = order.description
        initializeSlideImgs()
        renewItems()
    }

    private fun initializeSlideImgs() {
        adapter = SliderAdapter(requireContext())
        imageSlider.setSliderAdapter(adapter)
        imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM) //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!

        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        imageSlider.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        imageSlider.indicatorSelectedColor = Color.WHITE
        imageSlider.indicatorUnselectedColor = Color.GRAY
        imageSlider.scrollTimeInSec = 3
        imageSlider.isAutoCycle = true
        imageSlider.startAutoCycle()
    }

    private fun renewItems( ) {
        val sliderItemList: MutableList<SliderItem> =
            ArrayList<SliderItem>()

        for (i in 0..3) {
            val sliderItem = SliderItem()
            sliderItem.description = firestoreViewModel.orderMutableLiveData.value!!.description
            when (i) {
                0 -> {
                    sliderItem.imageUrl = firestoreViewModel.orderMutableLiveData.value!!.imgOnePath
                }
                1 -> {
                    sliderItem.imageUrl = firestoreViewModel.orderMutableLiveData.value!!.imgTwoPath
                }
                2 -> {
                    sliderItem.imageUrl = firestoreViewModel.orderMutableLiveData.value!!.imgThreePath
                }
                3 -> {
                    sliderItem.imageUrl = firestoreViewModel.orderMutableLiveData.value!!.imgFourPath
                }
            }
            sliderItemList.add(sliderItem)
        }
        adapter.renewItems(sliderItemList)
    }
}
