package com.casamoderna

import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.casamoderna.model.Order
import com.casamoderna.model.User
import com.casamoderna.viewmodel.FireStoreRequestViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_new_order.*


/**
 * A simple [Fragment] subclass.
 */
class NewOrderFragment : Fragment() {
    private var initData: Boolean = true
    private lateinit var order: Order
    private lateinit var currentUser: User
    private lateinit var v: View
    private var uriOne: Uri? = null
    private var uriTwo: Uri? = null
    private var uriThree: Uri? = null
    private var uriFour: Uri? = null
    private var REQUEST_PICK_IMAGE: Int = 0
    private val args: SignFragmentArgs by navArgs()
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private val firestoreViewModel: FireStoreRequestViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_new_order, container, false)
        return v
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
    }

    override fun onStart() {
        super.onStart()
        if (initData) {
            currentUser = args.currentUser
            order = Order(uuid = currentUser.uuid!!)
            initializeComponents()
            initializeViewModel()
        }
    }

    private fun initializeComponents() {
        phone.addTextChangedListener(MaskEditUtil.mask(phone, MaskEditUtil.FORMAT_FONE))
        valueOrder.addTextChangedListener(MaskEditUtil.mask(valueOrder, MaskEditUtil.FORMAT_MONEY));
        updateUi()
        back.setOnClickListener { findNavController().navigateUp() }
        imgHouseOne.setOnClickListener { view -> selectPhoto(view, 1) }
        imgHouseTwo.setOnClickListener { view -> selectPhoto(view, 2) }
        imgHouseThree.setOnClickListener { view -> selectPhoto(view, 3) }
        imgHouseFour.setOnClickListener { view -> selectPhoto(view, 4) }


        deleteImgHouseOne.setOnClickListener {
            if (imgHouseOne.drawable != ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_camera_outline
                )
            ) {
                if (deleteImgHouseOne.colorFilter == null) {
                    deleteImgHouseOne.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        ), PorterDuff.Mode.SRC_IN
                    )
                } else {

                    deleteImgHouseOne.colorFilter = null
                    imgHouseOne.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_camera_outline
                        )
                    )
                }
            }
        }

        deleteImgHouseTwo.setOnClickListener {
            if (imgHouseTwo.drawable != ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_camera_outline
                )
            ) {
                if (deleteImgHouseTwo.colorFilter == null) {
                    deleteImgHouseTwo.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        ), PorterDuff.Mode.SRC_IN
                    )
                } else {
                    deleteImgHouseTwo.colorFilter = null
                    imgHouseTwo.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_camera_outline
                        )
                    )
                }
            }
        }

        deleteImgHouseThree.setOnClickListener {
            if (imgHouseThree.drawable != ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_camera_outline
                )
            ) {
                if (deleteImgHouseThree.colorFilter == null) {
                    deleteImgHouseThree.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        ), PorterDuff.Mode.SRC_IN
                    )
                } else {
                    deleteImgHouseThree.colorFilter = null
                    imgHouseThree.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_camera_outline
                        )
                    )
                }
            }
        }

        deleteImgHouseFour.setOnClickListener {
            if (imgHouseFour.drawable != ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_camera_outline
                )
            ) {
                if (deleteImgHouseFour.colorFilter == null) {
                    deleteImgHouseFour.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        ), PorterDuff.Mode.SRC_IN
                    )
                } else {
                    deleteImgHouseThree.colorFilter = null
                    imgHouseThree.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_camera_outline
                        )
                    )
                }
            }
        }

        save.setOnClickListener {
            validateComponents()
        }
    }


    private fun initializeViewModel() {
        firestoreViewModel.orderMutableLiveData.observe(this, Observer<Order> { response ->
            if (response != null && initData) {
                progress.visibility = View.INVISIBLE
                initData = false
                order = response
                updateUi()
            }
        })
        progress.visibility = View.VISIBLE
        firestoreViewModel.getOrder(currentUser.uuid!!)

        firestoreViewModel.eventHandlerMutableLiveData.observe(this, Observer<Boolean> { response ->
            progress.visibility = View.INVISIBLE
            if (response) {
                msgSave()
            } else {
                Snackbar.make(v, R.string.save_fail, Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun msgSave() {
        var bottomSheetDialog = BottomSheetDialog(activity as MainActivity)
        val view = layoutInflater.inflate(R.layout.save_order_sucess, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun updateUi() {
        local.setText(order.local)
        phone.setText(order.phone)
        valueOrder.setText(order.valueOrder)
        description.setText(order.description)

        if (order.imgOnePath != null)
            Picasso.get().load(order.imgOnePath)
                .into(imgHouseOne, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {}
                    override fun onError(e: java.lang.Exception?) {
                        Picasso.get().load(R.drawable.ic_camera_outline).into(imgHouseOne)
                    }
                })
        if (order.imgTwoPath != null)
            Picasso.get().load(order.imgTwoPath)
                .into(imgHouseTwo, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {}
                    override fun onError(e: java.lang.Exception?) {
                        Picasso.get().load(R.drawable.ic_camera_outline).into(imgHouseTwo)
                    }
                })
        if (order.imgThreePath != null)
            Picasso.get().load(order.imgThreePath)
                .into(imgHouseThree, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {}
                    override fun onError(e: java.lang.Exception?) {
                        Picasso.get().load(R.drawable.ic_camera_outline).into(imgHouseThree)
                    }
                })

        if (order.imgFourPath != null)
            Picasso.get().load(order.imgFourPath)
                .into(imgHouseFour, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {}
                    override fun onError(e: java.lang.Exception?) {
                        Picasso.get().load(R.drawable.ic_camera_outline).into(imgHouseFour)
                    }
                })
    }


    private fun selectPhoto(pView: View?, numberImgHome: Int) {
        progress.visibility = View.VISIBLE
        var intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        REQUEST_PICK_IMAGE = numberImgHome
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        progress.visibility = View.INVISIBLE
        if (REQUEST_PICK_IMAGE == requestCode) {
            if (data != null) {
                if (REQUEST_PICK_IMAGE == 1) {
                    uriOne = data.data!!
                    Picasso.get().load(uriOne).into(imgHouseOne)
                }
                if (REQUEST_PICK_IMAGE == 2) {
                    uriTwo = data.data!!
                    Picasso.get().load(uriTwo).into(imgHouseTwo)
                }
                if (REQUEST_PICK_IMAGE == 3) {
                    uriThree = data.data!!
                    Picasso.get().load(uriThree).into(imgHouseThree)
                }
                if (REQUEST_PICK_IMAGE == 4) {
                    uriFour = data.data!!
                    Picasso.get().load(uriFour).into(imgHouseFour)
                }
            }
        }

    }

    private fun validateComponents() {
        var fieldsValid = true
        if (TextUtils.isEmpty(local.text.toString())) {
            local.error = getString(R.string.error_field_required)
            fieldsValid = false
        }
        if (TextUtils.isEmpty(phone.text.toString())) {
            phone.error = getString(R.string.error_field_required)
            fieldsValid = false
        }
        if (TextUtils.isEmpty(valueOrder.text.toString())) {
            valueOrder.error = getString(R.string.error_field_required)
            fieldsValid = false
        }

        if (imgHouseOne.drawable == ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_camera_outline
            )
            && imgHouseTwo.drawable == ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_camera_outline
            )
            && imgHouseThree.drawable == ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_camera_outline
            )
            && imgHouseFour.drawable == ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_camera_outline
            )
        ) {
            fieldsValid = false
            Snackbar.make(v, R.string.error_field_img_required, Snackbar.LENGTH_LONG).show()
        }
        if (fieldsValid) {
            saveFirebase()
        }
    }

    private fun saveFirebase() {
        progress.visibility = View.VISIBLE

        firestoreViewModel.orderMutableLiveData.value!!.local = local.text.toString()
        firestoreViewModel.orderMutableLiveData.value!!.valueOrder = valueOrder.text.toString()
        firestoreViewModel.orderMutableLiveData.value!!.phone = phone.text.toString()
        firestoreViewModel.orderMutableLiveData.value!!.description = description.text.toString()

        firestoreViewModel.save(currentUser.uuid!!, uriOne, uriTwo, uriThree, uriFour)
    }


}
