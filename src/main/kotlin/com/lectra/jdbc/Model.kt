package com.lectra.jdbc

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.util.Assert


data class Brick(val description: String, @Id val Id: Long? = null)

data class LegoModel(val name: String, val brickContent: MutableSet<BrickContentItem> = mutableSetOf(), @Id val id: Long? = null) {

    fun add(brick: Brick, amount: Int) {
        Assert.notNull(brick.Id, "Save the brick before adding it to a model so that it has a valid ID")
        brickContent.add(BrickContentItem(brick.Id!!, amount))
    }

    fun totalBrickCount(): Int {
        return brickContent.asSequence().map { it.amount }.sum()
    }

    fun distinctBrickCount(): Int {
        return brickContent.size
    }

}

@Table("BRICKCONTENT")
class BrickContentItem(val brickId: Long?, val amount: Int) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BrickContentItem

        if (brickId != other.brickId) return false

        return true
    }

    override fun hashCode(): Int {
        return brickId.hashCode()
    }
}
