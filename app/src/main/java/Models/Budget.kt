package Models

class Budget(title: String, description: String, budget: Double, category_id: Int, id: Int) {
    var id: Int = 0
    var title: String = ""
    var description: String = ""
    var budget: Double = 0.00
    var category_id: Int = 0

    init{
        this.id = id
        this.title = title
        this.description = description
        this.budget = budget
        this.category_id = category_id
    }
}