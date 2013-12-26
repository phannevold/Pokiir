package network;

import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

/**
 * @author Petter Hannevold
 */
public class ServerTest {

	@Test
	public void serverShouldAcceptAPeer() {
		Server.listen();

		try ( Socket socket = new Socket("localhost", 53999); ) {
			MatcherAssert.assertThat(socket.isConnected(), is(true));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
}
