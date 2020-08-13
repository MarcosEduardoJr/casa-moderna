package com.casamoderna.model

import java.util.*

/**
 * Created by marco on 18,July,2020
 */
data class Order(
    var uuid: String

) {
    var docId: String? = null
    var local: String? = null
    var phone: String? = null
    var valueOrder: String? = null
    var description: String? = null
    var imgOnePath: String? = null
    var imgTwoPath: String? = null
    var imgThreePath: String? = null
    var imgFourPath: String? = null

}
