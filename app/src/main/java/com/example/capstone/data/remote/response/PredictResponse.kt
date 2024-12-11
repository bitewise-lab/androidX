package com.example.capstone.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

class PredictResponse(

	@field:SerializedName("healthRisk")
	val healthRisk: String? = null,

	@field:SerializedName("Ing")
	val ing: Ing? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("recommendations")
	val recommendations: List<RecommendationsItem>? = null
)

@Parcelize
 class RecommendationsItem(

	@field:SerializedName("carbo")
	val carbo: String? = null,

	@field:SerializedName("healthy_packaged_food_brands")
	val healthyPackagedFoodBrands: String? = null,

	@field:SerializedName("protein")
	val protein: String? = null,

	@field:SerializedName("fat")
	val fat: String? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("sugar")
	val sugar: String? = null
) : Parcelable

 class Ing(

	@field:SerializedName("Carbohydrates")
	val carbohydrates: Int? = null,

	@field:SerializedName("Sodium")
	val sodium: Int? = null,

	@field:SerializedName("Fat")
	val fat: Int? = null,

	@field:SerializedName("Serving Size")
	val servingSize: String? = null,

	@field:SerializedName("Calories")
	val calories: Int? = null,

	@field:SerializedName("Sugars")
	val sugars: Int? = null,

	@field:SerializedName("Protein")
	val protein: Int? = null
)
