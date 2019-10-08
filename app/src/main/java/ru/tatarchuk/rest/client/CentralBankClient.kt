package ru.tatarchuk.rest.client

class CentralBankClient : RestClient() {

    override fun baseUrl() = "http://www.cbr.ru/scripts/"
}