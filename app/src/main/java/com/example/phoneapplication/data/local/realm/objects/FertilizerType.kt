package com.example.phoneapplication.data.local.realm.objects

import io.github.xilinjia.krdb.types.RealmObject
import io.github.xilinjia.krdb.types.annotations.PrimaryKey

class FertilizerType : RealmObject {
    @PrimaryKey
    var id: String = ""
    var name: String = ""
    var iconName: String = ""
}