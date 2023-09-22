package shetj.me.base.annotation

class FieldInfoN internal constructor(var descriptor: String, var name: String, var value: Any) {
    override fun toString(): String {
        return "{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}'
    }

    override fun equals(other: Any?): Boolean {
        return if (other is FieldInfoN) {
            other.descriptor == descriptor && other.name == name && other.value == value
        } else {
            false
        }
    }
}