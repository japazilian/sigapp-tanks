package edu.purdue.tanks.universe.server;

import java.io.IOException;

public interface Connection {
	public void send(String obj) throws IOException;
	public String receive() throws IOException;
}
