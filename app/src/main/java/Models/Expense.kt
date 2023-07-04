package Models

class Expense(title: String, cost: Double, category_id: Int, id: Int) {
    companion object{
        val NOTE_EDIT_EXTRA = "noteEdit"
    }

    var id: Int = 0
    var title: String = ""
    var cost: Double = 0.00
    var category_id: Int = 0

    init{
        this.id = id
        this.title = title
        this.cost = cost
        this.category_id = category_id
    }
}