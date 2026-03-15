package dev.vhoyd.blockworks.internal

import dev.vhoyd.blockworks.api.model.Wrapper

internal class InternalWrapper<T>(override val delegate: T) : Wrapper<T>