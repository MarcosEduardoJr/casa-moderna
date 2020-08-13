package com.casamoderna.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by marco on 13,July,2020
 */
@Parcelize
data class User(var uuid: String?, var name: String?, var email: String?, var phoneNumber: String?)  :
    Parcelable