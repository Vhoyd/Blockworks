package dev.vhoyd.blockworks.mining

interface BlockworksAttributable {
    /**
     * Assigns the value of the given [MiningAttribute].
     */
    fun <P : Any, C : Any> setAttribute(miningAttribute: MiningAttribute<P, C>, value : C)

    /**
     * @return the value of the given [MiningAttribute]
     */
    fun <P : Any, C : Any> getAttribute(miningAttribute: MiningAttribute<P, C>) : C

    operator fun <P : Any, C : Any> set(miningAttribute: MiningAttribute<P, C>, value : C) = setAttribute(miningAttribute, value)
    operator fun <P : Any, C : Any> get(miningAttribute: MiningAttribute<P, C>) : C = getAttribute(miningAttribute)
}