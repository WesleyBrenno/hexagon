package co.there4.hexagon.web.integration

import co.there4.hexagon.web.Client
import org.testng.annotations.Test

import co.there4.hexagon.web.Server
import co.there4.hexagon.web.jetty.JettyServer
import org.asynchttpclient.Response
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import java.net.URL
import java.net.InetAddress.getByName as address

@Test(threadPoolSize = 1, invocationCount = 1) abstract class ItTest {
    val servers = listOf (
        JettyServer (bindAddress = address("localhost"), bindPort = 5060)
    )

    val clients = servers.map { Client (URL ("http://localhost:${it.bindPort}")) }

    protected abstract fun initialize (server: Server)

    @BeforeClass fun startServers () {
        servers.forEach {
            initialize (it)
            it.run ()
        }
    }

    @AfterClass fun stopServers () {
        servers.forEach { it.stop () }
    }

    protected fun withClients(lambda: Client.() -> Unit) {
        clients.forEach {
            it.cookies.clear()
            it.(lambda) ()
        }
    }

    protected fun assertResponseEquals(response: Response?, status: Int, content: String) {
        assert (response?.statusCode == status)
        assert (response?.responseBody == content)
    }

    protected fun assertResponseContains(response: Response?, status: Int, vararg content: String) {
        assert (response?.statusCode == status)
        content.forEach {
            assert (response?.responseBody?.contains (it) ?: false)
        }
    }
}