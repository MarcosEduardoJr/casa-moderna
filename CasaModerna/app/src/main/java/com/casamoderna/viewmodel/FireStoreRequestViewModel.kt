package com.casamoderna.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.casamoderna.model.Order
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_new_order.*

/**
 * Created by marco on 19,Abril,2020
 */

class FireStoreRequestViewModel : ViewModel() {
    var orderMutableLiveData = MutableLiveData<Order>()
    var ordersMutableLiveData = MutableLiveData<ArrayList<Order>>()
    var eventHandlerMutableLiveData = MutableLiveData<Boolean>()

    fun getOrders() {
        FirebaseFirestore.getInstance().collection("orders")
            .whereGreaterThanOrEqualTo("local", "rua")
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    ordersMutableLiveData.value = ArrayList<Order>()
                    for (document in result) {
                        val order = queryToOrder("", document)
                        ordersMutableLiveData.value!!.add(order)
                        Log.d("FIRESTORE_GET_ORDERS", "${document.id} => ${document.data}")
                    }

                } else {
                    ordersMutableLiveData.value = null
                }
            }
            .addOnFailureListener {
                ordersMutableLiveData.value = null
                Log.d("FIRESTORE_GET_ORDERS", "Failure${it.message}")
            }
    }

    fun getOrders(local: String, limitEstimate: Int) {
        FirebaseFirestore.getInstance().collection("orders")
            .whereGreaterThanOrEqualTo("local", local)
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    var orderAux = ArrayList<Order>()

                    for (document in result) {
                        val order = queryToOrder("", document)
                        orderAux.add(order)
                        Log.d("FIRESTORE_GET_ORDERS", "${document.id} => ${document.data}")
                    }

                    val ordersFiltered  = orderAux.filter { it.valueOrder!!.toInt() <= limitEstimate   }
                    orderAux = ArrayList()
                    ordersFiltered.forEach{
                        orderAux.add(it)
                    }
                    ordersMutableLiveData.value = orderAux
                } else {
                    ordersMutableLiveData.value = null
                }
            }
            .addOnFailureListener {
                ordersMutableLiveData.value = null
                Log.d("FIRESTORE_GET_ORDERS", "Failure${it.message}")
            }
    }

    fun getOrder(userId: String) {
        FirebaseFirestore.getInstance().collection("orders")
            .document(userId)
            .get().addOnSuccessListener { document ->
                if (document != null) {
                    val order = queryToOrder(userId, document)
                    orderMutableLiveData.value = order
                    Log.d("FIRESTORE_GET_ORDER", document.id)
                } else {
                    ordersMutableLiveData.value = null
                }
            }.addOnFailureListener {
                ordersMutableLiveData.value = null
                Log.d("FIRESTORE_GET_ORDER", "Failure${it.message}")
            }
    }

    private fun queryToOrder(
        userId: String,
        document: DocumentSnapshot
    ): Order {
        val order = Order(userId)
        order.docId = document.id
        order.local = document.getString("local")
        order.phone = document.getString("phone")
        order.valueOrder = document.getString("valueOrder")
        order.description = document.getString("description")
        order.imgOnePath = document.getString("imgOnePath")
        order.imgTwoPath = document.getString("imgTwoPath")
        order.imgThreePath = document.getString("imgThreePath")
        order.imgFourPath = document.getString("imgFourPath")
        return order
    }

    fun save(
        uuid: String,
        uriOne: Uri?,
        uriTwo: Uri?,
        uriThree: Uri?,
        uriFour: Uri?
    ) {
        val uris = mutableListOf<Uri>()
        val urisIndex = mutableListOf<Int>()

        if (uriOne != null) {
            uris.add(uriOne)
            urisIndex.add(1)
        }
        if (uriTwo != null) {
            uris.add(uriTwo)
            urisIndex.add(2)
        }
        if (uriThree != null) {
            uris.add(uriThree)
            urisIndex.add(3)
        }
        if (uriFour != null) {
            uris.add(uriFour)
            urisIndex.add(4)
        }
        saveImgOrder(uuid, uris, urisIndex)
    }

    private fun saveOrder(uuid: String) {
        FirebaseFirestore.getInstance()
            .collection("orders").document(uuid)
            .set(orderMutableLiveData.value!!)
            .addOnSuccessListener {
                eventHandlerMutableLiveData.value = true
                Log.d("FIRESTORE_GET_ORDERS", "save")
            }
            .addOnFailureListener {
                eventHandlerMutableLiveData.value = false
                Log.d("FIRESTORE_SAVE_ORDERS", "Failure${it.message}")
            }
    }

    private fun saveImgOrder(uuid: String, uris: MutableList<Uri>, imgPosition: MutableList<Int>) {
        if (uris.isNotEmpty()) {
            val uri = uris[0]
            val uriImgPosition = imgPosition[0]
            val filename: String? = uuid + "_" + uriImgPosition
            val reference: StorageReference =
                FirebaseStorage.getInstance().getReference("/images_order_home/$filename")
            reference.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    // Uri: taskSnapshot.downloadUrl
                    // Name: taskSnapshot.metadata!!.name
                    // Path: taskSnapshot.metadata!!.path
                    // Size: taskSnapshot.metadata!!.sizeBytes
                    when (uriImgPosition) {
                        1 -> {
                            reference.downloadUrl.addOnSuccessListener {
                                orderMutableLiveData.value!!.imgOnePath = it.toString()
                                fifo(uris, imgPosition, uuid)
                            }
                        }
                        2 -> {
                            reference.downloadUrl.addOnSuccessListener {
                                orderMutableLiveData.value!!.imgTwoPath = it.toString()
                                fifo(uris, imgPosition, uuid)
                            }
                        }
                        3 -> {

                            reference.downloadUrl.addOnSuccessListener {
                                orderMutableLiveData.value!!.imgThreePath = it.toString()
                                fifo(uris, imgPosition, uuid)
                            }
                        }
                        4 -> {
                            reference.downloadUrl.addOnSuccessListener {
                                orderMutableLiveData.value!!.imgFourPath = it.toString()
                                fifo(uris, imgPosition, uuid)
                            }
                        }
                    }
                    Log.d("SAVE_IMG_ORDERS", taskSnapshot.toString())
                }
                .addOnFailureListener { it ->
                    eventHandlerMutableLiveData.value = false
                    Log.d("SAVE_IMG_ORDERS", "Failure${it.message}")
                }

        } else {
            saveOrder(uuid)
        }
    }

    private fun fifo(
        uris: MutableList<Uri>,
        imgPosition: MutableList<Int>,
        uuid: String
    ) {
        uris.removeAt(0)
        imgPosition.removeAt(0)
        saveImgOrder(uuid, uris, imgPosition)
    }


    /*

     private fun deleteImageFirebaseStorage(locationPath: String, numberOrderImage: Int) {
        var filename: String? = currentUser.uuid + "_" + numberOrderImage
        FirebaseStorage.getInstance().getReference(locationPath).delete()
            .addOnSuccessListener { taskSnapshot ->
            }
            .addOnFailureListener { exception ->
            }
    }
     */
}