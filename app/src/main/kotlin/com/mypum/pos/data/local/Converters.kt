package com.mypum.pos.data.local
import androidx.room.TypeConverter
import java.math.BigDecimal
import java.time.Instant
import com.mypum.pos.domain.model.enum.*
class Converters {
 @TypeConverter fun instantToLong(v:Instant?):Long?=v?.toEpochMilli()
 @TypeConverter fun longToInstant(v:Long?):Instant?=v?.let(Instant::ofEpochMilli)
 @TypeConverter fun bigDecimalToString(v:BigDecimal?):String?=v?.toPlainString()
 @TypeConverter fun stringToBigDecimal(v:String?):BigDecimal?=v?.toBigDecimalOrNull()
 @TypeConverter fun unidadToString(v:UnidadMedida?)=v?.name
 @TypeConverter fun stringToUnidad(v:String?)=v?.let(UnidadMedida::valueOf)
 @TypeConverter fun pagoToString(v:MetodoPago?)=v?.name
 @TypeConverter fun stringToPago(v:String?)=v?.let(MetodoPago::valueOf)
 @TypeConverter fun tipoToString(v:TipoServicio?)=v?.name
 @TypeConverter fun stringToTipo(v:String?)=v?.let(TipoServicio::valueOf)
}
