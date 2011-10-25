package edu.purdue.tanks.universe.server;

import java.io.IOException;

public interface ServerConnection {
	public void send(String obj) throws IOException;
	public void connectToHost() throws IOException;
	public String receive() throws IOException;
}
