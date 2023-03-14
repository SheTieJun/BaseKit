

package shetj.me.base.bean

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class MusicBean : Serializable {
    var url: String? = null
    var title: String? = null
    var duration: String? = null
    var imgUrl: String? = null
}