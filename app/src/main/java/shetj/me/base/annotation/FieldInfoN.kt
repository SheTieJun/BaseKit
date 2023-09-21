package shetj.me.base.annotation

class FieldInfoN internal constructor(var descriptor: String, var name: String, var value: Any) {
    override fun toString(): String {
        return "{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}'
    }

    override fun equals(obj: Any?): Boolean {
        return if (obj is FieldInfoN) {
            obj.descriptor == descriptor && obj.name == name && obj.value == value
        } else {
            false
        }
    }
}