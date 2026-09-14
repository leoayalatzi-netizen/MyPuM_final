package com.mypum.pos.domain.util
object WeightFormatter { fun format(grams:Double)=if(grams>=1000) "%.3f kg".format(grams/1000) else "%.0f g".format(grams) }
