package com.toystore.app.data.local

import com.toystore.app.data.model.Toy

object ToyMapper {

    fun toCached(toy: Toy): CachedToy {
        return CachedToy(
            id = toy.id,
            name = toy.name,
            description = toy.description,
            price = toy.price,
            imageUrl = toy.imageUrl,
            stock = toy.stock,
            category = toy.category,
            createdAt = toy.createdAt,
            updatedAt = toy.updatedAt,
            lastUpdated = System.currentTimeMillis()
        )
    }

    fun toDomain(cached: CachedToy): Toy {
        return Toy(
            id = cached.id,
            name = cached.name,
            description = cached.description,
            price = cached.price,
            imageUrl = cached.imageUrl,
            stock = cached.stock,
            category = cached.category,
            createdAt = cached.createdAt,
            updatedAt = cached.updatedAt
        )
    }

    fun toCachedList(toys: List<Toy>): List<CachedToy> {
        return toys.map { toCached(it) }
    }

    fun toDomainList(cached: List<CachedToy>): List<Toy> {
        return cached.map { toDomain(it) }
    }
}