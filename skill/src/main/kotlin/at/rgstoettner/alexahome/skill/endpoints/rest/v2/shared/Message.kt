package at.rgstoettner.alexahome.skill.endpoints.rest.v2.shared

open class Message(header: Header, payload: Any) {
    constructor(namespace: String, name: String, payload: Any) : this(Header(namespace, name), payload)


}