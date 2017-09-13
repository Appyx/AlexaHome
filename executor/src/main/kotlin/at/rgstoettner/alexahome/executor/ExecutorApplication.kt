package at.rgstoettner.alexahome.executor

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate


@SpringBootApplication
class ExecutorApplication {

}

fun main(args: Array<String>) {
    val pass = ClassPathResource("tls/pass.txt").inputStream.bufferedReader(Charsets.UTF_8).readText()
    System.setProperty("javax.net.ssl.trustStore", ExecutorApplication::class.java.classLoader.getResource("tls/client-truststore.jks").file)
    System.setProperty("javax.net.ssl.trustStorePassword", pass);
    System.setProperty("javax.net.ssl.keyStore", ExecutorApplication::class.java.classLoader.getResource("tls/client-keystore.jks").file)
    System.setProperty("javax.net.ssl.keyStorePassword", pass);

    SpringApplication.run(ExecutorApplication::class.java, *args)
}

@Component
class Runner : CommandLineRunner {
    override fun run(vararg args: String?) {
        val template = RestTemplate()
        println(template.postForObject("https://localhost:8443", "hello world", String::class.java))
    }
}


