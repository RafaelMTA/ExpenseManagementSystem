package Models

class Category(title: String, description: String, id: Int){
    var id: Int = 0
    var title: String = ""
    var description: String = ""

    init{
        this.id = id
        this.title = title
        this.description = description
    }

    override fun toString(): String {
        return this.title
    }
}