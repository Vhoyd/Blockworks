package dev.vhoyd.blockworks.impl.core

import dev.vhoyd.blockworks.api.model.Wrapper

internal class ProvidedWrapper<T>(override val delegate: T) : Wrapper<T>