package de.dsi8.dsi8acl.connection.contract;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ISocket extends Closeable {
	InputStream getInputStream() throws IOException;
	OutputStream getOutputStream() throws IOException;
	boolean isClosed();
}
