package br.com.easyshop.domain

/** Erro de regra de negócio, exibido ao usuário pela camada de apresentação. */
class ValidationException(message: String) : Exception(message)
